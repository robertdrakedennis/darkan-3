package org.darkan.core.net

import io.ktor.utils.io.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.isActive
import org.darkan.core.EnvVars
import org.darkan.core.Logger.logInfo
import org.darkan.core.Logger.logTrace
import org.darkan.core.Logger.logWarn
import world.gregs.voidps.buffer.*
import world.gregs.voidps.cache.file.FileProvider

class JS5Server(val provider: FileProvider, val prefetchKeys: IntArray) {
    val limiter = MultilogLimiter()

    suspend fun init(input: ByteReadChannel, output: ByteWriteChannel, ip: String) {
        if (!limiter.add(ip)) {
            logWarn("JS5 connection rejected (limit): $ip")
            output.finish(ResponseOpcode.LOGIN_LIMIT_EXCEEDED)
            return
        }
        logInfo("JS5 connection from $ip")
        try {
            // Read handshake: size(1) + major(4) + minor(4) + token(N+1) + platform(1)
            val size = input.readByte().toInt()

            val major = input.readInt()
            val minor = input.readInt()
            if (major != EnvVars.majorVersion || minor != EnvVars.minorVersion) {
                logWarn("JS5 version mismatch from $ip — got $major.$minor, expected ${EnvVars.majorVersion}.${EnvVars.minorVersion}. Accepting anyway for development.")
            }
            val token = input.readRSString()
            if (token != EnvVars.js5ServerToken) {
                logWarn("JS5 invalid token from $ip: $token")
                output.writeByte(ResponseOpcode.BAD_SESSION_ID)
                output.flushAndClose()
                return
            }
            // Consume the trailing platform/language byte (NXT appends this after the null-terminated token)
            input.readByte()

            logInfo("JS5 handshake complete for $ip — version $major.$minor")

            // Send SYNC response — NXT TCP JS5 does NOT send prefetch keys after SYNC
            // (confirmed: working cache downloader connects to Jagex without reading any keys)
            output.writeByte(ResponseOpcode.JS5_SYNC)
            output.flush()

            // Read client ACK sequence. NXT sends two 10-byte control messages:
            //   Opcode 6 (ENCRYPTION_ACK): opcode(1) + medium(3) + int(4) + short(2) = 10 bytes
            //   Opcode 3 (CONNECTION_READY): opcode(1) + medium(3) + int(4) + short(2) = 10 bytes
            // Confirmed via raw byte dump: both use the same format.
            suspend fun readControlPayload() { input.readMedium(); input.readInt(); input.readShort() }

            val ackOpcode = input.readByte().toInt() and 0xFF
            if (ackOpcode != RequestOpcode.ACKNOWLEDGE) {
                logInfo("Expected ACK opcode 6, got: $ackOpcode from $ip")
                output.writeByte(ResponseOpcode.LOGIN_SERVER_REJECTED_SESSION)
                output.flushAndClose()
                return
            }
            readControlPayload()

            val readyOpcode = input.readByte().toInt() and 0xFF
            if (readyOpcode != RequestOpcode.STATUS_LOGGED_OUT) {
                logWarn("Expected CONNECTION_READY (3), got: $readyOpcode from $ip")
            }
            readControlPayload()

            logInfo("JS5 ACK + CONNECTION_READY complete for $ip")

            // Enter file request loop
            // All NXT JS5 client messages are 10 bytes (6 meaningful + 4 padding):
            //   File requests: flags(1) + index(1) + group(4) + padding(4) = 10
            //   Control msgs:  opcode(1) + medium(3) + int(4) + short(2) = 10
            // flags byte: (priority << 4) | isUrgent; file request if (flags & 0x0E) == 0
            coroutineScope {
                while (isActive) {
                    val opcode = input.readByte().toInt() and 0xFF
                    when {
                        // NXT file request: low nibble is 0 or 1 (urgency flag only)
                        (opcode and 0x0E) == 0 -> {
                            val urgent = (opcode and 1) != 0
                            val index = input.readByte().toInt() and 0xFF
                            val group = input.readInt()
                            input.readShort() // padding
                            input.readShort() // padding
                            logInfo("JS5 request: index=$index group=$group flags=0x${"%02x".format(opcode)} (${if (urgent) "urgent" else "prefetch"}) from $ip")
                            val ref = (index.toLong() shl 32) or (group.toLong() and 0xFFFFFFFFL)
                            if (!provider.serve(output, ref, prefetch = !urgent)) {
                                logWarn("JS5 cache miss: index=$index group=$group (${if (urgent) "urgent" else "prefetch"}) from $ip")
                            } else {
                                logInfo("JS5 served: index=$index group=$group prefetch=${!urgent} to $ip")
                            }
                        }
                        opcode == RequestOpcode.STATUS_LOGGED_IN || opcode == RequestOpcode.STATUS_LOGGED_OUT -> {
                            logTrace("JS5 status opcode $opcode from $ip")
                            readControlPayload()
                        }
                        opcode == RequestOpcode.ENCRYPTION_KEY_UPDATE -> {
                            logTrace("JS5 encryption key update from $ip")
                            readControlPayload()
                        }
                        opcode == RequestOpcode.ACKNOWLEDGE -> {
                            logTrace("JS5 acknowledge from $ip")
                            readControlPayload()
                        }
                        else -> {
                            logWarn("JS5 unknown opcode $opcode from $ip")
                            break
                        }
                    }
                }
            }
        } finally {
            logTrace("JS5 connection closed: $ip")
            limiter.remove(ip)
        }
    }
}

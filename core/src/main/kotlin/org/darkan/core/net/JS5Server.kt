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

            // Send SYNC + prefetch keys
            output.writeByte(ResponseOpcode.JS5_SYNC)
            for (key in prefetchKeys)
                output.writeInt(key)
            output.flush()

            // Read client ACK sequence:
            //   Opcode 6 (ENCRYPTION_ACK): 1 byte opcode + 3 bytes padding + 4 bytes major version = 8 bytes
            //   Opcode 3 (CONNECTION_READY): 1 byte opcode
            val ackOpcode = input.readByte().toInt()
            if (ackOpcode != RequestOpcode.ACKNOWLEDGE) {
                logInfo("Expected ACK opcode 6, got: $ackOpcode")
                output.writeByte(ResponseOpcode.LOGIN_SERVER_REJECTED_SESSION)
                output.flushAndClose()
                return
            }
            // Read 3-byte padding (00 00 05) + 4-byte major version
            input.readMedium()  // padding: 0x000005
            input.readInt()     // major version (validated above, ignored here)

            val readyOpcode = input.readByte().toInt()
            if (readyOpcode != RequestOpcode.STATUS_LOGGED_OUT) {
                logInfo("Expected READY opcode 3, got: $readyOpcode")
                output.writeByte(ResponseOpcode.BAD_SESSION_ID)
                output.flushAndClose()
                return
            }

            // Enter file request loop (State 3: CONNECTED)
            coroutineScope {
                while (isActive) {
                    val opcode = input.readByte().toInt() and 0xFF
                    when (opcode) {
                        RequestOpcode.JS5_FILE -> {
                            // Prefetch request: archive(1) + group(4)
                            val ref = input.read40BitULong()
                            val idx = (ref ushr 32).toInt()
                            val arc = (ref and 0xFFFFFFFFL).toInt()
                            if (!provider.serve(output, ref, prefetch = true)) {
                                logWarn("JS5 cache miss: index=$idx archive=$arc (prefetch) from $ip")
                            } else {
                                logTrace("JS5 serve: index=$idx archive=$arc prefetch=true to $ip")
                            }
                        }
                        RequestOpcode.JS5_FILE_HIGH_PRIORITY -> {
                            // Urgent request: archive(1) + group(4)
                            val ref = input.read40BitULong()
                            val idx = (ref ushr 32).toInt()
                            val arc = (ref and 0xFFFFFFFFL).toInt()
                            if (!provider.serve(output, ref, prefetch = false)) {
                                logWarn("JS5 cache miss: index=$idx archive=$arc (urgent) from $ip")
                            } else {
                                logTrace("JS5 serve: index=$idx archive=$arc prefetch=false to $ip")
                            }
                        }
                        RequestOpcode.STATUS_LOGGED_IN, RequestOpcode.STATUS_LOGGED_OUT -> {
                            // Status update: 3 bytes padding (consumed and ignored)
                            logTrace("JS5 status opcode $opcode from $ip")
                            input.readMedium()
                        }
                        RequestOpcode.ENCRYPTION_KEY_UPDATE -> {
                            // XTEA key update: 1 byte index + 4x4 byte keys = 17 bytes
                            logTrace("JS5 encryption key update from $ip")
                            input.readByte()  // index
                            repeat(4) { input.readInt() } // 4 XTEA key ints
                        }
                        RequestOpcode.ACKNOWLEDGE -> {
                            // Acknowledge: 3 bytes (consumed and ignored)
                            logTrace("JS5 acknowledge from $ip")
                            input.readMedium()
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

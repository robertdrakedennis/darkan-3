package org.darkan.core.net

import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import kotlinx.io.Source
import org.darkan.core.EnvVars
import org.darkan.core.Logger.logError
import org.darkan.core.Logger.logTrace
import org.darkan.core.Logger.logWarn
import org.darkan.core.net.prot.*
import world.gregs.voidps.buffer.readUByte
import world.gregs.voidps.buffer.readUShort
import world.gregs.voidps.buffer.writeByte
import world.gregs.voidps.buffer.writeSmart
import java.io.EOFException
import java.io.IOException
import java.net.SocketException
import java.nio.channels.ClosedChannelException
import java.util.concurrent.ConcurrentLinkedQueue

open class Session(
    private val write: ByteWriteChannel,
    val isaacIn: Isaac,
    private val isaacOut: Isaac?,
    val ip: String,
    val codec: Codec,
) {
    enum class State { CONNECTED, LOST_CONNECTION, DISCONNECTED }

    val readChannel = Channel<ClientProt>(capacity = EnvVars.packetQueueCapacity)
    private val pendingPackets = ConcurrentLinkedQueue<ServerProt>()

    var disconnected: Boolean = false
    private var disconnect: (() -> Unit)? = null
    private var disconnecting: (() -> Unit)? = null
    private var state: State = State.CONNECTED

    fun onDisconnected(block: () -> Unit) {
        disconnect = block
    }

    fun onDisconnecting(block: () -> Unit) {
        disconnecting = block
    }

    suspend fun disconnect(reason: Int) {
        if (disconnected) return
        write.writeByte(reason)
        disconnect()
    }

    suspend fun disconnect() {
        if (disconnected) return
        disconnected = true
        write.flushAndClose()
        state = State.DISCONNECTED
        disconnect?.invoke()
    }

    fun exit() {
        if (state == State.CONNECTED) {
            state = State.LOST_CONNECTION
            disconnecting?.invoke()
        }
    }

    /**
     * Queues a packet to be sent on the next [flush]. Safe to call from non-suspend contexts.
     */
    fun queuePacket(packet: ServerProt) {
        if (disconnected) return
        pendingPackets.add(packet)
    }

    open suspend fun flush() {
        if (disconnected) return
        var packet = pendingPackets.poll()
        while (packet != null) {
            send(packet)
            packet = pendingPackets.poll()
        }
        write.flush()
    }

    /**
     * Blocking wrapper for [flush], safe to call from Java / non-suspend contexts
     * such as the world thread's tick loop.
     */
    fun flushBlocking() {
        if (disconnected) return
        runBlocking { flush() }
    }

    /**
     * Non-suspend poll for the next decoded packet.
     * Returns null if channel is empty. Safe to call from Java.
     */
    fun pollPacket(): ClientProt? = readChannel.tryReceive().getOrNull()

    suspend fun readPackets(input: ByteReadChannel) {
        try {
            while (!disconnected) {
                val cipher = isaacIn.nextInt()
                val opcode = (input.readUByte() - cipher) and 0xff
                val clientProt = codec.clientProtsByOpcode[opcode]
                if (clientProt == null) {
                    logError("Missing ClientProt with opcode $opcode")
                    return
                }
                val size = when (clientProt.size) {
                    is ProtSize.Fixed -> clientProt.size.length
                    ProtSize.VarByte -> input.readUByte()
                    ProtSize.VarShort -> input.readUShort()
                }
                val packet = input.readPacket(size)

                val packetData = try {
                    clientProt.decoder?.invoke(packet, opcode)
                        ?: if (clientProt.protClass == UnhandledClientProt::class) {
                            UnhandledClientProt(opcode, codec.clientProtName(opcode), size)
                        } else {
                            codec.createInstanceForOpcode<ClientProt>(opcode)
                        }
                } catch (e: Exception) {
                    logError("Decoder exception for opcode $opcode (${clientProt.protClass.simpleName}, size=$size): ${e::class.simpleName}: ${e.message}")
                    continue
                }
                if (packetData == null) {
                    logError("Failed to create packet instance for opcode $opcode")
                    continue
                }
                readChannel.send(packetData)
            }
        } catch (e: Exception) {
            if (isExpectedDisconnect(e)) {
                logTrace("Session read ended: ${e::class.simpleName}: ${e.message}")
            } else {
                throw e
            }
        } finally {
            if (!disconnected) {
                disconnected = true
                state = State.LOST_CONNECTION
                disconnecting?.invoke()
            }
        }
    }

    open suspend fun send(serverProt: ServerProt, noIsaac: Boolean = false) {
        if (disconnected) return
        try {
            val encoder = codec.serverProts[serverProt::class]
            if (encoder == null) {
                logWarn("Missing ServerProt encoder: ${serverProt::class}")
                return
            }

            when (encoder.size) {
                is ProtSize.Fixed -> {
                    if (EnvVars.packetValidateSizes && encoder.encoder != null) {
                        val dataChannel = ByteChannel()
                        encoder.encoder.invoke(serverProt, dataChannel)
                        val packetBytes = dataChannel.toByteArray()
                        val packetLength = packetBytes.size
                        val expectedLength = encoder.size.length
                        if (packetLength != expectedLength) {
                            logWarn("Fixed packet size mismatch for ${serverProt::class.simpleName}: expected=$expectedLength actual=$packetLength opcode=${encoder.opcode}")
                        }
                        writeOpcode(encoder.opcode, if (noIsaac) null else isaacOut)
                        write.writeFully(packetBytes)
                    } else {
                        writeOpcode(encoder.opcode, if (noIsaac) null else isaacOut)
                        encoder.encoder?.invoke(serverProt, write)
                    }
                }
                ProtSize.VarByte, ProtSize.VarShort -> {
                    val dataChannel = ByteChannel()
                    encoder.encoder?.invoke(serverProt, dataChannel)
                    val packetData = dataChannel.toByteReadPacket()
                    val packetLength = packetData.remaining

                    if (encoder.size == ProtSize.VarByte && packetLength > 255)
                        logWarn("Packet length exceeds VarByte maximum (${packetLength} > 255)")
                    else if (encoder.size == ProtSize.VarShort && packetLength > 65535)
                        logWarn("Packet length exceeds VarShort maximum (${packetLength} > 65535)")

                    writeOpcode(encoder.opcode, if (noIsaac) null else isaacOut)

                    if (encoder.size == ProtSize.VarByte)
                        write.writeByte(packetLength.toByte())
                    else
                        write.writeShort(packetLength.toShort())

                    write.writePacket(packetData)
                }
            }
        } catch (e: Exception) {
            logWarn("Client error:", e)
            runBlocking { disconnect() }
        }
    }

    private suspend fun writeOpcode(opcode: Int, cipher: Isaac?) {
        if (opcode < 0) return
        if (cipher != null) {
            if (opcode >= 128) {
                write.writeByte(((opcode shr 8) + 128) + cipher.nextInt())
                write.writeByte(opcode + cipher.nextInt())
            } else
                write.writeByte(opcode + cipher.nextInt())
        } else
            write.writeSmart(opcode)
    }

    private suspend fun ByteChannel.toByteArray(): ByteArray {
        flush()
        val bytes = ByteArray(availableForRead)
        readFully(bytes)
        close()
        return bytes
    }

    private suspend fun ByteChannel.toByteReadPacket(): Source {
        flush()
        val packet = ByteReadPacket(ByteArray(availableForRead).also { readFully(it) })
        close()
        return packet
    }

    companion object {
        /**
         * Returns true if the exception represents an expected client disconnect
         * (connection reset, EOF, closed channel) rather than a real error.
         */
        fun isExpectedDisconnect(e: Throwable): Boolean {
            return e is ClosedByteChannelException
                || e is EOFException
                || e is SocketException
                || e is ClosedChannelException
                || (e is IOException && e.message?.let {
                    it.contains("reset", ignoreCase = true)
                    || it.contains("broken pipe", ignoreCase = true)
                    || it.contains("closed", ignoreCase = true)
                } == true)
                || (e.cause != null && isExpectedDisconnect(e.cause!!))
        }
    }
}

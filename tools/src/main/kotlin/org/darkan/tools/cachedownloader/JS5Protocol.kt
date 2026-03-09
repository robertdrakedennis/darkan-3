package org.darkan.tools.cachedownloader

import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket

data class JS5Response(val index: Int, val archive: Int, val container: ByteArray)

object JS5Protocol {
    const val BLOCK_SIZE = 102400

    /**
     * Opens a connected socket, performs handshake + connection init.
     * Returns the socket, input stream, and output stream.
     */
    fun connect(
        host: String, port: Int,
        major: Int, minor: Int, token: String,
        soTimeout: Int = 30_000
    ): Triple<Socket, DataInputStream, DataOutputStream> {
        val sock = Socket(host, port)
        sock.soTimeout = soTimeout
        val out = DataOutputStream(sock.getOutputStream())
        val inp = DataInputStream(sock.getInputStream())

        val response = handshake(out, inp, major, minor, token)
        if (response != 0) {
            sock.close()
            throw IllegalStateException("Handshake failed: $response")
        }

        sendConnectionInit(out, major)
        return Triple(sock, inp, out)
    }

    /**
     * Sends JS5 handshake and returns the server response byte.
     */
    fun handshake(
        out: DataOutputStream, inp: DataInputStream,
        major: Int, minor: Int, token: String
    ): Int {
        val tokenBytes = token.toByteArray(Charsets.US_ASCII)
        out.writeByte(15) // JS5_INIT
        out.writeByte(tokenBytes.size + 10)
        out.writeInt(major)
        out.writeInt(minor)
        out.write(tokenBytes)
        out.writeByte(0) // null terminator
        out.writeByte(0) // platform/language byte
        out.flush()
        return inp.readUnsignedByte()
    }

    /**
     * Sends ConnectionInitialized(6) + LoggedOut(3) after successful handshake.
     */
    fun sendConnectionInit(out: DataOutputStream, major: Int) {
        out.writeByte(6)
        out.writeByte(0); out.writeByte(0); out.writeByte(5)
        out.writeShort(0); out.writeShort(major); out.writeShort(0)
        out.writeByte(3)
        out.writeByte(0); out.writeByte(0); out.writeByte(5)
        out.writeShort(0); out.writeShort(major); out.writeShort(0)
        out.flush()
    }

    /**
     * Sends a high-priority file request (opcode 33).
     */
    fun sendFileRequest(out: DataOutputStream, index: Int, archive: Int, major: Int) {
        out.writeByte(33)
        out.writeByte(index)
        out.writeInt(archive)
        out.writeShort(major)
        out.writeShort(0)
        out.flush()
    }

    /**
     * Reads a complete JS5 response (handling block boundaries).
     */
    fun readResponse(inp: DataInputStream): JS5Response {
        val idx = inp.readUnsignedByte()
        val hash = inp.readInt()
        val archive = hash and 0x7FFFFFFF

        val compression = inp.readUnsignedByte()
        val compressedSize = inp.readInt()
        val totalDataLen = compressedSize + if (compression != 0) 4 else 0

        val container = ByteArray(5 + totalDataLen)
        container[0] = compression.toByte()
        container[1] = (compressedSize shr 24).toByte()
        container[2] = (compressedSize shr 16).toByte()
        container[3] = (compressedSize shr 8).toByte()
        container[4] = compressedSize.toByte()

        var offset = 10 // header(5) + compression header(5)
        var dataRead = 0
        while (dataRead < totalDataLen) {
            val blockRemaining = BLOCK_SIZE - offset
            val toRead = blockRemaining.coerceAtMost(totalDataLen - dataRead)
            inp.readFully(container, 5 + dataRead, toRead)
            dataRead += toRead
            offset += toRead
            if (offset == BLOCK_SIZE && dataRead < totalDataLen) {
                inp.readUnsignedByte() // continuation index
                inp.readInt()          // continuation hash
                offset = 5
            }
        }

        return JS5Response(idx, archive, container)
    }
}

package org.darkan.tools.recorder

import org.darkan.core.EnvVars
import org.darkan.core.net.Isaac
import org.darkan.core.net.prot.Codec
import org.darkan.core.net.prot.revision.rev948.register948
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.Base64
import kotlin.system.exitProcess

fun main() {
    val codec = register948()
    val seeds = intArrayOf(0x11111111, 0x22222222, 0x33333333, 0x44444444)
    val s2cOpcodes = intArrayOf(81, 54, 5, 212)
    val c2sOpcodes = intArrayOf(107, 70, 38)

    val s2cPost = encodePackets(codec, seeds, isServer = true, s2cOpcodes)
    val c2sPost = encodePackets(codec, seeds, isServer = false, c2sOpcodes)
    val lobbyS2c = lobbyS2c(s2cPost)
    val worldS2c = worldS2c(s2cPost)
    val lobbyC2s = c2s(loginOpcode = 19, c2sPost)
    val worldC2s = c2s(loginOpcode = 16, c2sPost)

    val eventsFile = File.createTempFile("undercut-socket-selftest-", ".jsonl")
    eventsFile.deleteOnExit()
    eventsFile.writeText(
        buildString {
            appendLine(sessionStart())
            appendLine(socketConnect(fd = 7, state = "LOGIN_SCREEN", port = 43596))
            appendLine(socketIo(fd = 7, state = "LOGIN_SCREEN", dir = "C", bytes = lobbyC2s))
            appendSplitSocketIo(fd = 7, state = "LOGIN_SCREEN", dir = "S", bytes = lobbyS2c)
            appendLine(socketClose(fd = 7, state = "LOBBY_SCREEN"))
            appendLine(socketConnect(fd = 8, state = "LOBBY_SCREEN", port = 43597))
            appendLine(socketIo(fd = 8, state = "LOBBY_SCREEN", dir = "C", bytes = worldC2s))
            appendSplitSocketIo(fd = 8, state = "LOBBY_SCREEN", dir = "S", bytes = worldS2c)
            appendLine(socketClose(fd = 8, state = "LOGGED_IN"))
        }
    )

    val outFile = File.createTempFile("undercut-socket-selftest-", ".jsonl")
    outFile.deleteOnExit()
    UndercutSocketDeframe.main(
        arrayOf(
            eventsFile.absolutePath,
            "--out",
            outFile.absolutePath,
            "--seeds",
            seeds.joinToString(","),
            "--strict",
        )
    )

    val lines = outFile.readLines()
    val packetLines = lines.filter { it.contains("\"seq\":") && it.contains("\"opcode\":") }
    val desyncs = lines.count { it.contains("\"record\":\"desync\"") }
    val truncations = lines.count { it.contains("\"record\":\"truncated\"") }
    val expectedPackets = (s2cOpcodes.size + c2sOpcodes.size) * 2

    var failures = 0
    if (packetLines.size != expectedPackets) {
        System.err.println("[undercut-selftest] FAIL: decoded ${packetLines.size} packets, expected $expectedPackets")
        failures++
    }
    if (desyncs != 0) {
        System.err.println("[undercut-selftest] FAIL: $desyncs desync line(s)")
        failures++
    }
    if (truncations != 0) {
        System.err.println("[undercut-selftest] FAIL: $truncations truncation line(s)")
        failures++
    }
    if (lines.none { it.contains("\"role\":\"world\"") }) {
        System.err.println("[undercut-selftest] FAIL: world connection header missing")
        failures++
    }

    if (failures == 0) {
        println("[undercut-selftest] PASS: decoded $expectedPackets packets across lobby+world from Undercut JSONL")
        exitProcess(0)
    }
    System.err.println("[undercut-selftest] FAILED with $failures problem(s).")
    exitProcess(1)
}

private fun encodePackets(codec: Codec, seeds: IntArray, isServer: Boolean, opcodes: IntArray): ByteArray {
    val isaacSeed = if (isServer) IntArray(4) { seeds[it] + EnvVars.ISAAC_DELTA } else seeds.copyOf()
    val isaac = Isaac(isaacSeed)
    val out = ByteArrayOutputStream()
    for (opcode in opcodes) {
        val sizeMode = if (isServer) codec.serverProtSize(opcode) else codec.clientProtSize(opcode)
        val payloadLen = when (sizeMode) {
            -1 -> 5
            -2 -> 260
            0 -> 0
            else -> sizeMode
        }
        if (opcode >= 128) {
            out.write((((opcode shr 8) + 128) + isaac.nextInt()) and 0xFF)
            out.write((opcode + isaac.nextInt()) and 0xFF)
        } else {
            out.write((opcode + isaac.nextInt()) and 0xFF)
        }
        when (sizeMode) {
            -1 -> out.write(payloadLen and 0xFF)
            -2 -> {
                out.write((payloadLen shr 8) and 0xFF)
                out.write(payloadLen and 0xFF)
            }
        }
        repeat(payloadLen) { out.write((0x80 + it) and 0xFF) }
    }
    return out.toByteArray()
}

private fun lobbyS2c(post: ByteArray): ByteArray = ByteArrayOutputStream().apply {
    val loginData = ByteArray(20) { (0xA0 + it).toByte() }
    write(0)
    repeat(8) { write(0xDE) }
    write(2)
    write(loginData.size)
    write(loginData)
    write(post)
}.toByteArray()

private fun worldS2c(post: ByteArray): ByteArray = ByteArrayOutputStream().apply {
    val loginData = ByteArray(28) { (0x60 + it).toByte() }
    write(0)
    repeat(8) { write(0xEF) }
    write(2)
    write(0)
    write(1)
    write(1)
    write(2)
    write(loginData.size)
    write(loginData)
    write(post)
}.toByteArray()

private fun c2s(loginOpcode: Int, post: ByteArray): ByteArray = ByteArrayOutputStream().apply {
    write(14)
    write(loginOpcode)
    write(0)
    write(4)
    repeat(4) { write(0xBB) }
    write(post)
}.toByteArray()

private fun StringBuilder.appendSplitSocketIo(fd: Int, state: String, dir: String, bytes: ByteArray) {
    val split = bytes.size / 2
    appendLine(socketIo(fd, state, dir, bytes.copyOfRange(0, split)))
    appendLine(socketIo(fd, state, dir, bytes.copyOfRange(split, bytes.size)))
}

private fun sessionStart(): String = Json.obj(
    "type" to "session_start",
    "epoch_ms" to 1L,
    "session_id" to "undercut-selftest",
)

private fun socketConnect(fd: Int, state: String, port: Int): String = Json.obj(
    "type" to "socket_connect",
    "epoch_ms" to 2L,
    "client_cycle" to 1,
    "main_state_name" to state,
    "fd" to fd,
    "family" to 2,
    "address" to "127.0.0.1",
    "port" to port,
    "result" to 0,
)

private fun socketIo(fd: Int, state: String, dir: String, bytes: ByteArray): String = Json.obj(
    "type" to "socket",
    "epoch_ms" to 3L,
    "client_cycle" to 2,
    "main_state_name" to state,
    "direction" to dir,
    "fd" to fd,
    "syscall" to if (dir == "S") "read" else "write",
    "size" to bytes.size,
    "captured_size" to bytes.size,
    "truncated" to false,
    "payload_base64" to Base64.getEncoder().encodeToString(bytes),
)

private fun socketClose(fd: Int, state: String): String = Json.obj(
    "type" to "socket_close",
    "epoch_ms" to 4L,
    "client_cycle" to 3,
    "main_state_name" to state,
    "fd" to fd,
)

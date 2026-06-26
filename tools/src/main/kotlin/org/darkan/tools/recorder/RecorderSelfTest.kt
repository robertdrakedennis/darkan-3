package org.darkan.tools.recorder

import org.darkan.core.EnvVars
import org.darkan.core.net.Isaac
import org.darkan.core.net.prot.ProtSize
import org.darkan.core.net.prot.revision.rev948.register948
import java.io.ByteArrayOutputStream
import java.io.File
import kotlin.system.exitProcess

/**
 * Synthetic end-to-end self-test for the recorder pipeline — proves the capture
 * format + deframer reconstruct a KNOWN ServerProt/ClientProt sequence WITHOUT
 * needing the client or the dylib. This is the deframer's "byte-diff against
 * known output" gate in miniature: we encode a sequence exactly the way the
 * server does (opcode obfuscation via [org.darkan.core.net.Session.writeOpcode]:
 * `opcode + isaac.nextInt()`, 2-byte form for opcode ≥ 128; sizes per the
 * codec), wrap it in a synthetic capture file with a realistic login prelude,
 * run the real [RecorderDeframe], and assert the decoded opcodes match the input.
 *
 * Run: ./gradlew :tools:recorderSelfTest
 * Exit 0 = pipeline correct; nonzero = a deframer bug.
 */
fun main() {
    val codec = register948()
    val seeds = intArrayOf(0x11111111, 0x22222222, 0x33333333, 0x44444444)

    // A representative S2C sequence using real 948 ServerProts (mix of fixed,
    // varByte, varShort, and a 2-byte opcode ≥ 128 to exercise the wide-opcode
    // path). Opcodes/sizes resolved from the codec so this tracks register948().
    val s2cOpcodes = intArrayOf(
        // pick opcodes that exist in the 948 server table with known sizes:
        81,  // RebuildNormalSimple  (VarShort)  — wide-ish payload
        54,  // HashedWorldToken     (VarByte)
        5,   // ResetClientVarcache  (fixed 0)
        212, // SetWorldTarget       (VarByte, opcode ≥ 128 → 2-byte encoded)
        213, // SwitchWorld          (VarByte, opcode ≥ 128 → 2-byte encoded)
        216, // WorldListPacket      (VarShort, opcode ≥ 128 → 2-byte encoded)
    )

    val c2sOpcodes = intArrayOf(
        54,  // RequestWorldList      (fixed)
        107, // MapBuildComplete      (fixed zero)
        70,  // FriendListDel         (VarByte)
        38,  // MessagePrivateSend    (VarShort)
    )

    println("[selftest] building synthetic S2C stream with opcodes ${s2cOpcodes.toList()}")
    println("[selftest] building synthetic C2S stream with opcodes ${c2sOpcodes.toList()}")

    // Encode the post-login S2C stream exactly as Session.writeOpcode would.
    val s2cIsaac = Isaac(IntArray(4) { seeds[it] + EnvVars.ISAAC_DELTA }) // S2C = +50
    val post = ByteArrayOutputStream()
    val expectedS2c = ArrayList<Triple<Int, String, Int>>() // opcode, name, payloadSize
    for (op in s2cOpcodes) {
        val sizeMode = codec.serverProtSize(op)
        // Synthesize a payload whose length is valid for the size mode.
        val payloadLen = when (sizeMode) {
            -1 -> 5         // varByte: small
            -2 -> 300       // varShort: > 255 to prove the 2-byte length path
            0 -> 0
            else -> sizeMode
        }
        val payload = ByteArray(payloadLen) { (it and 0xFF).toByte() }

        // opcode obfuscation (mirror Session.writeOpcode)
        if (op >= 128) {
            post.write((((op shr 8) + 128) + s2cIsaac.nextInt()) and 0xFF)
            post.write((op + s2cIsaac.nextInt()) and 0xFF)
        } else {
            post.write((op + s2cIsaac.nextInt()) and 0xFF)
        }
        // size field (cleartext)
        when (sizeMode) {
            -1 -> post.write(payloadLen and 0xFF)
            -2 -> { post.write((payloadLen shr 8) and 0xFF); post.write(payloadLen and 0xFF) }
        }
        post.write(payload)
        expectedS2c.add(Triple(op, codec.serverProtName(op), payloadLen))
    }
    val postBytes = post.toByteArray()

    val c2sIsaac = Isaac(seeds.copyOf()) // C2S = raw seeds
    val c2sPost = ByteArrayOutputStream()
    val expectedC2s = ArrayList<Triple<Int, String, Int>>()
    for (op in c2sOpcodes) {
        val sizeMode = codec.clientProtSize(op)
        val payloadLen = when (sizeMode) {
            -1 -> 5
            -2 -> 260
            0 -> 0
            else -> sizeMode
        }
        val payload = ByteArray(payloadLen) { ((0xC0 + it) and 0xFF).toByte() }

        if (op >= 128) {
            c2sPost.write((((op shr 8) + 128) + c2sIsaac.nextInt()) and 0xFF)
            c2sPost.write((op + c2sIsaac.nextInt()) and 0xFF)
        } else {
            c2sPost.write((op + c2sIsaac.nextInt()) and 0xFF)
        }
        when (sizeMode) {
            -1 -> c2sPost.write(payloadLen and 0xFF)
            -2 -> {
                c2sPost.write((payloadLen shr 8) and 0xFF)
                c2sPost.write(payloadLen and 0xFF)
            }
        }
        c2sPost.write(payload)
        expectedC2s.add(Triple(op, codec.clientProtName(op), payloadLen))
    }

    // Build the full lobby S2C stream with a realistic login prelude:
    //   [9B first-response: 1B code=0 + 8B session key][1B result=2][1B dataLen][N data][POST]
    val lobbyLoginData = ByteArray(20) { (0xA0 + it).toByte() }
    val lobbyS2cBytes = ByteArrayOutputStream().apply {
        write(0)                                    // first-response code = 0 (OK)
        repeat(8) { write(0xDE) }                   // 8B session key (arbitrary)
        write(2)                                    // login result = SUCCESS
        write(lobbyLoginData.size)                  // login data length
        write(lobbyLoginData)                       // login data
        write(postBytes)                            // ISAAC post-login
    }.toByteArray()

    // Build the full world S2C stream:
    //   [9B first-response][result=2][u16 var-block-len][var-block][players][len][login-data][POST]
    val worldLoginData = ByteArray(28) { (0x60 + it).toByte() }
    val worldS2cBytes = ByteArrayOutputStream().apply {
        write(0)
        repeat(8) { write(0xEF) }
        write(2)
        write(0); write(1)                          // server-client-var block len = 1
        write(1)                                    // ack flag
        write(2)                                    // players byte
        write(worldLoginData.size)
        write(worldLoginData)
        write(postBytes)
    }.toByteArray()

    fun c2sBytes(loginOpcode: Int, includeWorldExtra: Boolean = false): ByteArray = ByteArrayOutputStream().apply {
        write(14)                                   // connection type
        write(loginOpcode)
        write(0); write(4)                          // BE block size = 4
        repeat(4) { write(0xBB) }                   // login block (4 bytes)
        if (includeWorldExtra) write(26)
        write(c2sPost.toByteArray())                // ISAAC post-login
    }.toByteArray()

    val lobbyC2sBytes = c2sBytes(19)
    val worldC2sBytes = c2sBytes(16, includeWorldExtra = true)

    // Write a synthetic capture file with both lobby and world connections.
    // Split each S2C stream across TWO IN records to exercise TCP reassembly.
    val tmp = File.createTempFile("recorder-selftest-", ".bin")
    tmp.deleteOnExit()
    SyntheticCapture(pid = 12345).apply {
        connect(fd = 7, ip = intArrayOf(127, 0, 0, 1), port = 43596) // lobby
        out(fd = 7, lobbyC2sBytes)
        val lobbySplit = lobbyS2cBytes.size / 2
        inbound(fd = 7, lobbyS2cBytes.copyOfRange(0, lobbySplit))
        inbound(fd = 7, lobbyS2cBytes.copyOfRange(lobbySplit, lobbyS2cBytes.size))
        close(fd = 7)

        connect(fd = 8, ip = intArrayOf(127, 0, 0, 1), port = 43597) // world
        out(fd = 8, worldC2sBytes)
        val worldSplit = worldS2cBytes.size / 2
        inbound(fd = 8, worldS2cBytes.copyOfRange(0, worldSplit))
        inbound(fd = 8, worldS2cBytes.copyOfRange(0, worldSplit))
        inbound(fd = 8, worldS2cBytes.copyOfRange(worldSplit, worldS2cBytes.size))
        seeds(seeds)
        close(fd = 8)
    }.writeTo(tmp)

    println("[selftest] synthetic capture: ${tmp.absolutePath} (${tmp.length()} bytes)")

    // Run the real deframer to a JSONL string.
    val outFile = File.createTempFile("recorder-selftest-", ".jsonl")
    outFile.deleteOnExit()
    RecorderDeframe.main(
        arrayOf(
            tmp.absolutePath,
            "--out",
            outFile.absolutePath,
            "--strict",
            "--lobby-port",
            "43596",
            "--js5-port",
            "43596",
        )
    )

    val lines = outFile.readLines()
    // Extract decoded S2C and C2S packets in order.
    val decodedS2c = lines
        .filter { it.contains("\"dir\":\"S2C\"") && it.contains("\"seq\":") && it.contains("\"opcode\":") }
        .map { line ->
            val op = Regex("\"opcode\":(\\d+)").find(line)!!.groupValues[1].toInt()
            val name = Regex("\"name\":\"([^\"]*)\"").find(line)!!.groupValues[1]
            val size = Regex("\"size\":(\\d+)").find(line)!!.groupValues[1].toInt()
            Triple(op, name, size)
        }
    val decodedC2s = lines
        .filter { it.contains("\"dir\":\"C2S\"") && it.contains("\"seq\":") && it.contains("\"opcode\":") }
        .map { line ->
            val op = Regex("\"opcode\":(\\d+)").find(line)!!.groupValues[1].toInt()
            val name = Regex("\"name\":\"([^\"]*)\"").find(line)!!.groupValues[1]
            val size = Regex("\"size\":(\\d+)").find(line)!!.groupValues[1].toInt()
            Triple(op, name, size)
        }

    println("[selftest] decoded ${decodedS2c.size} S2C packets:")
    decodedS2c.forEach { println("    op=${it.first} ${it.second} size=${it.third}") }
    println("[selftest] decoded ${decodedC2s.size} C2S packets:")
    decodedC2s.forEach { println("    op=${it.first} ${it.second} size=${it.third}") }

    val expectedS2cAll = expectedS2c + expectedS2c
    val expectedC2sAll = expectedC2s + expectedC2s

    // Assert: decoded == expected (opcode, name, size), in order.
    var failures = 0
    if (decodedS2c.size != expectedS2cAll.size) {
        System.err.println("[selftest] FAIL: decoded ${decodedS2c.size} S2C packets, expected ${expectedS2cAll.size}")
        failures++
    }
    for (i in 0 until minOf(decodedS2c.size, expectedS2cAll.size)) {
        if (decodedS2c[i] != expectedS2cAll[i]) {
            System.err.println("[selftest] FAIL S2C @[$i]: decoded=${decodedS2c[i]} expected=${expectedS2cAll[i]}")
            failures++
        }
    }
    if (decodedC2s.size != expectedC2sAll.size) {
        System.err.println("[selftest] FAIL: decoded ${decodedC2s.size} C2S packets, expected ${expectedC2sAll.size}")
        failures++
    }
    for (i in 0 until minOf(decodedC2s.size, expectedC2sAll.size)) {
        if (decodedC2s[i] != expectedC2sAll[i]) {
            System.err.println("[selftest] FAIL C2S @[$i]: decoded=${decodedC2s[i]} expected=${expectedC2sAll[i]}")
            failures++
        }
    }

    // Also confirm there were no desync lines.
    val desyncs = lines.count { it.contains("\"record\":\"desync\"") }
    if (desyncs > 0) {
        System.err.println("[selftest] FAIL: $desyncs desync line(s) in transcript")
        failures++
    }

    // Confirm the captured seeds round-tripped into the connection header.
    val headerHasSeeds = lines.any { it.contains("\"record\":\"connection\"") && it.contains("0x11111111") }
    if (!headerHasSeeds) {
        System.err.println("[selftest] FAIL: seeds not surfaced in connection header")
        failures++
    }

    if (failures == 0) {
        println("[selftest] PASS — pipeline reconstructed ${expectedS2cAll.size} S2C and ${expectedC2sAll.size} C2S packets across lobby+world, no desync, seeds OK.")
        exitProcess(0)
    } else {
        System.err.println("[selftest] FAILED with $failures problem(s).")
        exitProcess(1)
    }
}

/**
 * Minimal writer for the recorder binary capture format (mirrors
 * `capture.rs`), used only by the self-test to synthesize known captures.
 */
private class SyntheticCapture(private val pid: Int) {
    private val body = ByteArrayOutputStream()

    private fun u8(v: Int) = body.write(v and 0xFF)
    private fun u16(v: Int) { u8(v); u8(v shr 8) }
    private fun u32(v: Int) { u8(v); u8(v shr 8); u8(v shr 16); u8(v shr 24) }
    private fun u64(v: Long) { for (s in 0 until 64 step 8) u8((v shr s).toInt()) }
    private var ts = 1_000_000L

    fun connect(fd: Int, ip: IntArray, port: Int) {
        u8(0x02); u64(ts++); u32(fd); u8(2 /*AF_INET*/); u16(port); u8(ip.size)
        ip.forEach { u8(it) }
    }
    fun out(fd: Int, bytes: ByteArray) = io(fd, dir = 1, syscall = 3 /*send*/, bytes)
    fun inbound(fd: Int, bytes: ByteArray) = io(fd, dir = 0, syscall = 0 /*recv*/, bytes)
    private fun io(fd: Int, dir: Int, syscall: Int, bytes: ByteArray) {
        u8(0x01); u64(ts++); u32(fd); u8(dir); u8(syscall); u32(bytes.size)
        body.write(bytes)
    }
    fun seeds(s: IntArray) {
        u8(0x04); u64(ts++); u32(-1); s.forEach { u32(it) }
    }
    fun close(fd: Int) { u8(0x03); u64(ts++); u32(fd) }

    fun writeTo(file: File) {
        val out = ByteArrayOutputStream()
        // file header: magic "DKRC" u32 LE + version u16 + pid u32
        out.write(0x43); out.write(0x52); out.write(0x4B); out.write(0x44) // 0x444B5243 LE
        out.write(1); out.write(0)                                          // version=1
        out.write(pid and 0xFF); out.write((pid shr 8) and 0xFF); out.write((pid shr 16) and 0xFF); out.write((pid shr 24) and 0xFF)
        out.write(body.toByteArray())
        file.writeBytes(out.toByteArray())
    }
}

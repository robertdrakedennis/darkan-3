package org.darkan.tools.recorder

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.File

/**
 * Test/proof support: synthesize a `prot-table.json` that honors the recorder dylib's CONTRACT, and
 * realize handler-sigs.json wildcard patterns into the CONCRETE 32-byte `handler_sig`s a live capture
 * would carry.
 *
 * "Realizing" a pattern = taking its `blob`+`mask` and filling every wildcard (`mask == 0`) byte with
 * an arbitrary-but-deterministic value (a fake relocated immediate). The significant bytes are left
 * verbatim. The result is exactly what the dylib reads off a function entry: concrete bytes with NO
 * wildcards. A correct [HandlerNamer] re-masks against the authority and recovers the name regardless
 * of what the wildcard slots hold — which is the whole cross-rev guarantee.
 */
object SyntheticProtTable {

    private val json = Json { ignoreUnknownKeys = true }

    /** A handler-sig pattern from handler-sigs.json (name + blob + mask), able to realize a concrete sig. */
    class PatternSig(val name: String, val blob: ByteArray, val mask: ByteArray) {
        /**
         * Concretize: significant bytes verbatim, wildcard bytes filled with a deterministic pseudo
         * value so distinct calls produce stable bytes. The filler differs per index so a relocated
         * 4-byte displacement looks plausibly non-uniform.
         */
        fun concreteSig(filler: (Int) -> Byte = { i -> (0xA0 + i).toByte() }): ByteArray {
            val out = blob.copyOf(HandlerNamer.SIG_BYTES)
            val m = mask.copyOf(HandlerNamer.SIG_BYTES)
            for (i in out.indices) if (m[i].toInt() and 0xFF == 0) out[i] = filler(i)
            return out
        }
    }

    data class Entry(val opcode: Int, val sizeClass: Int, val handlerSig: ByteArray)

    /** Load ONLY the packet-handler entries (jag::packethandlers / jag::ServerProt that are real packets). */
    fun loadPacketHandlerSigs(sigsFile: File): List<PatternSig> = loadSigs(sigsFile) { name ->
        name.contains("packethandlers")
    }

    /** Load ALL handler-sig entries as realizable patterns. */
    fun loadAllSigs(sigsFile: File): List<PatternSig> = loadSigs(sigsFile) { true }

    private fun loadSigs(sigsFile: File, accept: (String) -> Boolean): List<PatternSig> {
        val root = json.parseToJsonElement(sigsFile.readText()).jsonObject
        val arr = root["handlers"]?.jsonArray ?: error("handler-sigs.json has no `handlers`")
        val out = ArrayList<PatternSig>()
        for (el in arr) {
            val obj = el.jsonObject
            val name = obj["name"]?.jsonPrimitive?.contentOrNull ?: continue
            if (!accept(name)) continue
            val blob = HandlerNamer.parseHex(obj["blob_hex"]?.jsonPrimitive?.contentOrNull ?: continue) ?: continue
            val mask = HandlerNamer.parseHex(obj["mask_hex"]?.jsonPrimitive?.contentOrNull ?: continue) ?: continue
            out += PatternSig(name, blob, mask)
        }
        return out
    }

    /** Find a single named pattern (by full name) from handler-sigs.json. */
    fun findByName(sigsFile: File, fullName: String): PatternSig? =
        loadAllSigs(sigsFile).firstOrNull { it.name == fullName }

    /** Hex (lowercase, no separators, no `0x`) of [bytes] — the `handler_sig` wire form. */
    fun toHex(bytes: ByteArray): String = buildString(bytes.size * 2) {
        for (b in bytes) append("%02x".format(b.toInt() and 0xFF))
    }

    /**
     * Render a prot-table.json honoring the contract:
     * `{ "build", "server_prot":[{op,size_class,handler,handler_sig}], "client_prot":[...] }`.
     * `handler` is a synthetic `0x<addr>` (provenance only; the namer matches on `handler_sig`).
     */
    fun render(build: String, server: List<Entry>, client: List<Entry>): String {
        fun dir(entries: List<Entry>): String = entries.joinToString(",\n") { e ->
            """    {"op":${e.opcode},"size_class":${e.sizeClass},"handler":"0x${(0x100000000L + e.opcode * 0x40L).toString(16)}","handler_sig":"${toHex(e.handlerSig)}"}"""
        }
        return buildString {
            append("{\n")
            append("  \"build\": \"").append(build).append("\",\n")
            append("  \"server_prot\": [\n").append(dir(server)).append("\n  ],\n")
            append("  \"client_prot\": [\n").append(dir(client)).append("\n  ]\n")
            append("}\n")
        }
    }
}

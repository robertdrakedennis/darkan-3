package org.darkan.core.net.prot

/**
 * Marker interface for all client-to-server packets.
 *
 * Each packet is a data class (or value class for zero-payload packets) that
 * carries decoded fields. The Codec registry maps opcodes to their size and
 * decoder lambda for a given revision.
 */
interface ClientProt

@JvmInline
value class Ping(val dummy: Int = 0) : ClientProt

data class RequestWorldList(val worldlistVersion: Int) : ClientProt

// --- Social ---

data class FriendListAdd(val displayName: String) : ClientProt
data class FriendListDel(val displayName: String) : ClientProt
data class IgnoreListAdd(val displayName: String) : ClientProt
// IgnoreListDel not needed yet — client sends opcode we haven't confirmed

/** Catch-all for opcodes we haven't implemented handlers for yet. Carries opcode for logging. */
data class UnhandledClientProt(val opcode: Int, val name: String, val size: Int) : ClientProt

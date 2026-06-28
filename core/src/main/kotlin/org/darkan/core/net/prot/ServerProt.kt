package org.darkan.core.net.prot

/**
 * Marker interface for all server-to-client packets.
 *
 * Each packet is a data class (or value class for zero-payload packets) that
 * carries its fields. The Codec registry maps each class to its opcode, size,
 * and encoder lambda for a given revision.
 *
 * The concrete packet classes live in per-domain sibling files in this same
 * package (`ServerProtVariables.kt`, `ServerProtInterfaces.kt`, `ServerProtMisc.kt`,
 * `ServerProtSocial.kt`, `ServerProtClan.kt`, `ServerProtWorldInit.kt`,
 * `ServerProtRebuild.kt`, `ServerProtZone.kt`, `ServerProtEntitySync.kt`).
 */
interface ServerProt

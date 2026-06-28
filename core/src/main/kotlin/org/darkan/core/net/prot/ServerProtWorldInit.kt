package org.darkan.core.net.prot

import org.darkan.core.worldlist.WorldList

// --- Client Scripts ---

/**
 * RUNCLIENTSCRIPT — rev948 op 110, varShort. Invokes a CS2 script on the client.
 *
 * Wire format: type descriptor (RS string) + args (in REVERSED type order) + script ID (4B BE).
 * The type descriptor is a string of chars: 'i' = int, 's' = string, 'l' = long.
 * Args are written in REVERSED order of the type chars because the client reads them reversed.
 */
data class RunClientScript(val scriptId: Int, val types: String, val args: Array<Any>) : ServerProt {
    companion object {
        /** Build with named args -- ints and strings. */
        fun of(scriptId: Int, vararg args: Any): RunClientScript {
            val types = StringBuilder()
            for (arg in args) {
                when (arg) {
                    is Int -> types.append('i')
                    is String -> types.append('s')
                    is Long -> types.append('l')
                    else -> error("Unsupported arg type: ${arg::class}")
                }
            }
            return RunClientScript(scriptId, types.toString(), arrayOf(*args))
        }

        /** Component hash from interface ID and component ID. */
        fun componentHash(interfaceId: Int, componentId: Int) = (interfaceId shl 16) or componentId
    }
}

// --- World Login ---

/**
 * WorldLoginDetails — sent by the world server immediately after login success (byte 2).
 * Sent with noIsaac=true because ISAAC is not yet active at this point in the handshake.
 * Opcode 2, VarByte — matches the client's world-login response parser.
 */
data class WorldLoginDetails(
    val rights: Int,
    val modLevel: Int,
    val quickChat: Boolean,
    val verifiedEmail: Boolean,
    val aBool7322: Boolean,
    val quickChatOnly: Boolean,
    val playerIndex: Int,
    val members: Boolean,
    val dob: Int,
    val memberWorld: Boolean,
    val worldName: String,
) : ServerProt

// --- World init ---

/**
 * HASHED_WORLD_TOKEN — revision-dependent world session nonce packet.
 *
 * Rev948 binary expects op54 body as flag + cipher-subtracted string(s). The current encoder still
 * carries the captured single-token payload until ServerProt encoders can access outbound ISAAC for
 * payload-level ciphered bytes.
 */
data class HashedWorldToken(val token: String) : ServerProt

/**
 * SET_WORLD_TARGET — rev948 op 212, varByte. Tells the client the hostname/port of the next lobby
 * target. Only populates the LOBBY login slot in WorldSwitcher — does NOT trigger a world transfer.
 * Cold-lobby Play Now uses the world-target tail in the lobby login response, not this packet.
 *
 * Wire format:
 *   string hostname (CP1252 + null) + ushort worldId + ushort port1 + ushort port2 (all BE).
 */
data class SetWorldTarget(
    val hostname: String,
    val worldId: Int,
    val port1: Int,
    val port2: Int = port1,
) : ServerProt

/**
 * SWITCH_WORLD — rev948 op 213, varByte. Explicit world-switch path, not cold-lobby Play Now.
 *
 * The client handler stores the world target in `WorldSwitcher`, sets MainState to 0x25, which fires
 * the login state machine — the client then opens a TCP connection to hostname:port1 for world login.
 *
 * Rev948 wire format is worldId BE u16, hostname (CP1252 + null), portA BE u16,
 * portB BE u16, reconnectFlag u8. worldId is first, unlike [SetWorldTarget].
 */
data class SwitchWorld(
    val hostname: String,
    val worldId: Int,
    val port1: Int,
    val port2: Int = port1,
    val pendingFlag: Int = 0,
) : ServerProt

/** JCOINS_UPDATE — rev948 op 191, 4B. RuneCoins balance display. Value is BE int. */
data class JcoinsUpdate(val balance: Int) : ServerProt

/** Scene timing base — rev948 op 74, 4B. Writes SceneTargetContext+0x64 client-side. */
data class SceneTimingBase(val value: Int) : ServerProt
// --- World list ---

data class WorldListPacket(
    val worldList: WorldList,
    val fullRefresh: Boolean,         // true = send full world defs, false = delta (counts only)
) : ServerProt

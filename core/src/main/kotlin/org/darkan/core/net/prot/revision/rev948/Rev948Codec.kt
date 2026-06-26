package org.darkan.core.net.prot.revision.rev948

import org.darkan.core.net.prot.Codec
import org.darkan.core.net.prot.update.ActiveMaskKeys

/**
 * Top-level entry point for the 948 codec.
 *
 * Real encoders/decoders are registered first; the stub functions then fill in metadata for any
 * remaining opcodes using `putIfAbsent` semantics so they correctly defer to the real encoder
 * registrations rather than overwriting them.
 *
 * `register948()` registers into [Codec.companion.codecs] under revision 948. Switching active
 * revision is done via `Codec.get(EnvVars.majorVersion)`.
 *
 * **APPEARANCE (resolved):** an exhaustive walk of `ProcessExtendedInfo @ 0x0015e110`
 * (rs2client.948-2-2) confirmed APPEARANCE MOVED from 947-3 bit 2 to **948 bit 3** (dispatch
 * order 4): the block reads a scrambled length byte + a mode-buffer payload and queues it via
 * `PathingEntity::QueueExtendedInfoPacket`. The earlier "Phase 1 walk" missed it because the
 * mask test is the bare `if ((mask & 8) != 0)` (no `0x` prefix). The 948 APPEARANCE key and
 * encoder are now live, so `PlayerInfoBuilder`'s synth-on-first-tick path works under 948.
 */
fun register948() = Codec.register(948) {
    // Register real encoders/decoders first (these populate protInfo via the registration methods)
    registerRev948ClientProts()
    registerRev948ServerCodecsVariable()
    registerRev948ServerCodecsInterface()
    registerRev948ServerCodecsMisc()
    registerRev948ServerCodecsSocial()
    registerRev948ServerCodecsInventory()
    registerRev948ServerCodecsRebuild()
    registerRev948ServerCodecsZone()
    registerRev948ServerCodecsPlayerInfo()
    registerRev948ServerCodecsNpcInfo()
    // Then fill in metadata for all remaining opcodes (putIfAbsent — won't overwrite real registrations)
    registerRev948ServerProtStubs()
    registerRev948ClientProtStubs()

    // Update-mask encoders live in process-global singletons (PlayerUpdateMaskEncoder /
    // NpcUpdateMaskEncoder); call once per codec init. Note: encoders from 947 and 948
    // co-exist in the singletons because they are keyed by the PlayerUpdateMaskKey /
    // NpcUpdateMaskKey INTERFACE — per-revision enum instances are distinct objects.
    registerRev948ServerCodecsUpdateMasks()

    // Publish revision-specific well-known mask keys. APPEARANCE is confirmed at 948 bit 3
    // (dispatch order 4) — see file-level doc and Rev948PlayerUpdateMaskKey.
    ActiveMaskKeys.playerAppearance = Rev948PlayerUpdateMaskKey.APPEARANCE

    // Publish 948 ext-info flag-bitset expansion ("continue") bits so the world builders drive
    // their header expansion from the active revision rather than hardcoded 947 literals.
    // 948 player = {0,13,22}; 948 NPC = {6,8,19,25} (CHANGED from 947-3 {0,14,18} / {6,13,22,24}).
    ActiveMaskKeys.playerExpansionBits = Rev948PlayerUpdateMaskKey.EXPANSION_BITS
    ActiveMaskKeys.npcExpansionBits = Rev948NpcUpdateMaskKey.EXPANSION_BITS
}

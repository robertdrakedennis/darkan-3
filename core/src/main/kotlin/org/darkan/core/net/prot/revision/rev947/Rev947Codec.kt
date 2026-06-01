package org.darkan.core.net.prot.revision.rev947

import org.darkan.core.net.prot.Codec
import org.darkan.core.net.prot.update.ActiveMaskKeys

fun register947() = Codec.register(947) {
    // Register real encoders/decoders first (these populate protInfo via the registration methods)
    registerRev947ClientProts()
    registerRev947ServerCodecsVariable()
    registerRev947ServerCodecsInterface()
    registerRev947ServerCodecsMisc()
    registerRev947ServerCodecsSocial()
    registerRev947ServerCodecsRebuild()
    registerRev947ServerCodecsZone()
    registerRev947ServerCodecsPlayerInfo()
    registerRev947ServerCodecsNpcInfo()
    // Then fill in metadata for all remaining opcodes (putIfAbsent — won't overwrite real registrations)
    registerRev947ServerProtStubs()
    registerRev947ClientProtStubs()

    // Update-mask encoders live in process-global singletons (PlayerUpdateMaskEncoder /
    // NpcUpdateMaskEncoder); call once per codec init.
    registerRev947ServerCodecsUpdateMasks()

    // Publish revision-specific well-known mask keys for builder fallbacks (PlayerInfoBuilder
    // synthesizes APPEARANCE for first-tick render).
    ActiveMaskKeys.playerAppearance = Rev947PlayerUpdateMaskKey.APPEARANCE

    // Publish 947-3 ext-info flag-bitset expansion bits so the world builders drive their header
    // expansion from the active revision rather than hardcoded literals.
    ActiveMaskKeys.playerExpansionBits = Rev947PlayerUpdateMaskKey.EXPANSION_BITS
    ActiveMaskKeys.npcExpansionBits = Rev947NpcUpdateMaskKey.EXPANSION_BITS
}

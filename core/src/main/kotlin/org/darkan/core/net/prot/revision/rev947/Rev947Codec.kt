package org.darkan.core.net.prot.revision.rev947

import org.darkan.core.net.prot.Codec

fun register947() = Codec.register(947) {
    // Register real encoders/decoders first (these populate protInfo via the registration methods)
    registerRev947ClientProts()
    registerRev947ServerCodecsVariable()
    registerRev947ServerCodecsInterface()
    registerRev947ServerCodecsMisc()
    registerRev947ServerCodecsSocial()
    // Then fill in metadata for all remaining opcodes (putIfAbsent — won't overwrite real registrations)
    registerRev947ServerProtStubs()
    registerRev947ClientProtStubs()
}

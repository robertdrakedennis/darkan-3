package org.darkan.core.net.prot.revision.rev946

import org.darkan.core.net.prot.Codec

fun register946() = Codec.register(946) {
    registerRev946ClientProts()
    registerRev946ServerCodecsVariable()
    registerRev946ServerCodecsInterface()
    registerRev946ServerCodecsMisc()
}

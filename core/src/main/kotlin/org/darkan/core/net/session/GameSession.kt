package org.darkan.core.net.session

import io.ktor.utils.io.*
import org.darkan.core.net.Isaac
import org.darkan.core.net.Session
import org.darkan.core.net.prot.Codec

class GameSession(
    write: ByteWriteChannel,
    isaacIn: Isaac,
    isaacOut: Isaac,
    ip: String,
    codec: Codec,
    /** Protocol username of the logged-in player. Set after login. */
    var username: String = "",
) : Session(write, isaacIn, isaacOut, ip, codec)

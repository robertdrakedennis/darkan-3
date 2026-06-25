package com.undercut.game.nxt

import com.undercut.game.memory.NativeAccess
import com.undercut.game.memory.NativeAccess.pointerAtOffset
import com.undercut.game.memory.NativeAccess.readByte
import com.undercut.game.memory.NativeAccess.readInt
import com.undercut.game.memory.NativeAccess.readShort
import com.undercut.game.memory.eastl.EastlString
import com.undercut.game.nxt.entity.player.Player
import java.lang.foreign.MemorySegment

class LoggedInPlayer(val ptr: MemorySegment, val client: Client = Client.getClient(NativeAccess.BASE_ADDR)) {

    val playerRights: Int
        get() = if (ptr.address() == 0L) 0 else ptr.readInt(OLoggedInPlayer.PLAYER_RIGHTS)

    val serverIndex: Int
        get() = if (ptr.address() == 0L) 0 else ptr.readInt(OLoggedInPlayer.PLAYER_INDEX)

    val targetIndex: Int
        get() = if (ptr.address() == 0L) 0 else ptr.readShort(OLoggedInPlayer.TARGET_INDEX).toInt()

    val targetType: EntityType
        get() = EntityType.fromType(ptr.readByte(OLoggedInPlayer.TARGET_TYPE).toInt())

    val self: Player
        get() = Player(client.playerManager[serverIndex])

    fun getPlayerName(): String? {
        if (ptr.address() == 0L)
            return null
        return EastlString(ptr.pointerAtOffset(OLoggedInPlayer.PLAYER_NAME, 0x24L)).toString()
    }

}
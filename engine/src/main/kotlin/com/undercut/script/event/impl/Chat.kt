package com.undercut.script.event.impl

import com.undercut.game.chat.CrownType
import com.undercut.game.chat.MessageType
import com.undercut.script.event.Event

class Chat(
    val messageType: MessageType,
    val crown: CrownType,
    val cleanSenderName: String?,
    val crownedSenderName: String?,
    val formattedSenderName: String?,
    val message: String
) : Event
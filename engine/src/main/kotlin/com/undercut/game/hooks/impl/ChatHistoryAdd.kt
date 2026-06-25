package com.undercut.game.hooks.impl

import com.undercut.game.bootstrap.Bootstrap
import com.undercut.game.chat.CrownType
import com.undercut.game.chat.MessageType
import com.undercut.game.hooks.Hook
import com.undercut.game.hooks.HookManager
import com.undercut.game.memory.NativeAccess.getOrNull
import com.undercut.game.memory.NativeAccess.readInt
import com.undercut.game.memory.eastl.EastlString
import com.undercut.game.nxt.OFunctions
import com.undercut.quest.runtime.RecentChat
import com.undercut.script.ScriptExecutor
import com.undercut.script.event.impl.Chat
import java.lang.foreign.MemorySegment

object ChatHistoryAdd {
    @JvmStatic
    @Hook(OFunctions.CHATHISTORY_ADDCHAT_HOOKABLE)
    fun chatHistoryAddHook(chatHistoryPtr: MemorySegment, messageTypeId: Int, unkByte1: Byte, unkInt1: Int, formattedSenderNamePtr: MemorySegment, crownedSenderNamePtr: MemorySegment, cleanSenderNamePtr: MemorySegment, messagePtr: MemorySegment, crownPtr: MemorySegment, unkStringPtr: MemorySegment, unkInt2: Int): MemorySegment {
        var reassignedType = messageTypeId
        synchronized (Bootstrap.lock) {
            try {
                val messageType = MessageType.forId(messageTypeId)
                val formattedSenderName = formattedSenderNamePtr.getOrNull?.let { EastlString(it.reinterpret(24)).toString() }
                val crownedSenderName = crownedSenderNamePtr.getOrNull?.let { EastlString(it.reinterpret(24)).toString() }
                val cleanSenderName = cleanSenderNamePtr.getOrNull?.let { EastlString(it.reinterpret(24)).toString() }
                val message = messagePtr.getOrNull?.let { EastlString(it.reinterpret(24)).toString() }
                unkStringPtr.getOrNull?.let { EastlString(it.reinterpret(24)).toString() }
                val crown = CrownType.forId(crownPtr.getOrNull?.reinterpret(4)?.readInt() ?: 0)

                if (message == "Inventory full. To make more room, sell, drop or bank something.")
                    reassignedType = MessageType.FILTERABLE.id

                //            println("[Chat] chatHistoryPtr: 0x${chatHistoryPtr.address().toString(16)}")
                //            println("\tmessageType: ${messageType ?: "UNK_$messageType($messageType)"}")
                //            println("\tformattedSenderName: $formattedSenderName")
                //            println("\tcrownedSenderName: $crownedSenderName")
                //            println("\tcleanSenderName: $cleanSenderName")
                //            println("\tmessage: $message")
                //            println("\tunkString: $unkString")
                //            println("\tcrown: $crown")
                //            println("\tunkInt1: $unkInt1")
                //            println("\tunkInt2: $unkInt2")
//                if (message == "Ability not ready yet." || message == "Inventory full. To make more room, sell, drop or bank something.")
//                    return MemorySegment.NULL
                if (messageType != null)
                    ScriptExecutor.pushEvent(Chat(messageType, crown, cleanSenderName, crownedSenderName, formattedSenderName, message ?: ""))
                message?.let { RecentChat.record(it) }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
        return HookManager.trampoline(::chatHistoryAddHook.name).invokeExact(chatHistoryPtr, reassignedType, unkByte1, unkInt1, formattedSenderNamePtr, crownedSenderNamePtr, cleanSenderNamePtr, messagePtr, crownPtr, unkStringPtr, unkInt2) as MemorySegment
    }
}
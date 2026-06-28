package org.darkan.core.net.prot.revision.rev948

import io.ktor.utils.io.*
import org.darkan.core.net.prot.Codec
import org.darkan.core.net.prot.InventoryItemParam
import org.darkan.core.net.prot.ProtSize
import org.darkan.core.net.prot.UpdateInvFull
import org.darkan.core.net.prot.UpdateInvPartial
import world.gregs.voidps.buffer.*

internal fun Codec.registerRev948ServerCodecsInventory() {
    serverProt<UpdateInvFull>(opcode = 85, size = ProtSize.VarShort) { out ->
        require(inventoryId in 0..0xFFFF) { "inventoryId out of range: $inventoryId" }
        require(flags in 0..0xFF) { "flags out of range: $flags" }
        require(entries.size <= 0xFFFF) { "Inventory entry count out of range: ${entries.size}" }
        val writeParams = flags and 0x2 != 0

        out.writeShort(inventoryId)
        out.writeByte(flags)
        out.writeShort(entries.size)
        for (entry in entries) {
            require(entry.itemId in -1..0xFFFE) { "itemId out of range: ${entry.itemId}" }
            require(entry.quantity >= 0) { "quantity out of range: ${entry.quantity}" }
            require(entry.params.size <= 0xFF) { "inventory param count out of range: ${entry.params.size}" }
            require(writeParams || entry.params.isEmpty()) { "inventory params require flags bit 1 (0x2)" }
            out.writeShort(entry.itemId + 1)
            if (entry.quantity < 0xFF) {
                out.writeByte(entry.quantity)
            } else {
                out.writeByte(0xFF)
                out.writeInt(entry.quantity)
            }
            if (writeParams) {
                out.writeInventoryParams(entry.params)
            }
        }
    }

    serverProt<UpdateInvPartial>(opcode = 121, size = ProtSize.VarShort) { out ->
        require(inventoryId in 0..0xFFFF) { "inventoryId out of range: $inventoryId" }
        require(flags in 0..0xFF) { "flags out of range: $flags" }
        val writeParams = flags and 0x2 != 0

        out.writeShort(inventoryId)
        out.writeByte(flags)
        for (entry in entries) {
            require(entry.slot in 0..0x7FFF) { "inventory slot out of smart range: ${entry.slot}" }
            require(entry.itemId in -1..0xFFFE) { "itemId out of range: ${entry.itemId}" }
            require(entry.quantity >= 0) { "quantity out of range: ${entry.quantity}" }
            require(entry.params.size <= 0xFF) { "inventory param count out of range: ${entry.params.size}" }
            require(writeParams || entry.params.isEmpty()) { "inventory params require flags bit 1 (0x2)" }
            out.writeSmart(entry.slot)
            out.writeShort(entry.itemId + 1)
            if (entry.itemId == -1) {
                require(entry.quantity == 0) { "empty inventory partial entry must have zero quantity" }
                require(entry.params.isEmpty()) { "empty inventory partial entry cannot carry params" }
                continue
            }
            if (entry.quantity < 0xFF) {
                out.writeByte(entry.quantity)
            } else {
                out.writeByte(0xFF)
                out.writeInt(entry.quantity)
            }
            if (writeParams) {
                out.writeInventoryParams(entry.params)
            }
        }
    }
}

private suspend fun ByteWriteChannel.writeInventoryParams(params: List<InventoryItemParam>) {
    writeByte(params.size)
    for (param in params) {
        require(param.key in 0..0xFFFF) { "inventory param key out of range: ${param.key}" }
        writeShort(param.key)
        writeInt(param.value)
    }
}

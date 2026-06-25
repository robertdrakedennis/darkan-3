package org.darkan.core.net.prot.revision.rev948

import io.ktor.utils.io.*
import org.darkan.core.net.prot.Codec
import org.darkan.core.net.prot.ProtSize
import org.darkan.core.net.prot.UpdateInvFull
import world.gregs.voidps.buffer.*

internal fun Codec.registerRev948ServerCodecsInventory() {
    serverProt<UpdateInvFull>(opcode = 85, size = ProtSize.VarShort) { out ->
        require(inventoryId in 0..0xFFFF) { "inventoryId out of range: $inventoryId" }
        require(flags in 0..0xFF) { "flags out of range: $flags" }
        require(entries.size <= 0xFFFF) { "Inventory entry count out of range: ${entries.size}" }

        out.writeShort(inventoryId)
        out.writeByte(flags)
        out.writeShort(entries.size)
        for (entry in entries) {
            require(entry.itemId in -1..0xFFFE) { "itemId out of range: ${entry.itemId}" }
            require(entry.quantity >= 0) { "quantity out of range: ${entry.quantity}" }
            require(entry.metadata in 0..0xFF) { "inventory metadata out of range: ${entry.metadata}" }
            out.writeShort(entry.itemId + 1)
            if (entry.quantity < 0xFF) {
                out.writeByte(entry.quantity)
            } else {
                out.writeByte(0xFF)
                out.writeInt(entry.quantity)
            }
            if (flags and 0x2 != 0) {
                out.writeByte(entry.metadata)
            }
        }
    }
}

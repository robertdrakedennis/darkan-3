package com.undercut.game.nxt.entity.location

import com.undercut.game.memory.NativeAccess.deref
import com.undercut.game.memory.NativeAccess.pointerAtOffset
import com.undercut.game.memory.NativeAccess.toShared
import com.undercut.game.nxt.OCombinedLocation
import com.undercut.game.nxt.entity.Entity
import com.undercut.game.nxt.types.Vector
import com.undercut.pathfinder.WorldCollision
import java.lang.foreign.MemorySegment

class CombinedLocation(ptr: MemorySegment): Entity(ptr) {
    val locationData: Vector
        get() = Vector(ptr.pointerAtOffset(OCombinedLocation.LOCATION_DATA_VECTOR, 0x20L), 0xC0L)

    val combinedLocationSections: List<CombinedLocationSection>
        get() = locationData.mapNotNull { basePtr ->
            val sectionPtr = basePtr.pointerAtOffset(OCombinedLocation.VECTOR_ELEMENT_LOC_SHAREDPTR, 0x20L)
            if (sectionPtr.deref(size = 0x24L).address() == 0L) return@mapNotNull null
            val section = CombinedLocationSection(sectionPtr.toShared().value(0x380L))
            if (section.tile.x <= 0 || section.tile.y <= 0 || section.tile.plane < 0)
                return@mapNotNull null
            if (section.hidden) {
                WorldCollision.unclip(section)
                return@mapNotNull null
            } else
                WorldCollision.clip(section)
            return@mapNotNull section
        }

//    val combinedLocationSectionsAlt: List<CombinedLocationSection>
//        get() {
//            val sections = mutableListOf<CombinedLocationSection>()
//
//            locationData.forEach { baseLocPtr ->
//                var currLocSection = baseLocPtr.pointerAtOffset(OCombinedLocation.VECTOR_ELEMENT_LOC_SHAREDPTR, 0x100L)
//                val endLocSectionArr = currLocSection.pointerAtOffset(0x8L, 0x8L)
//
//                if (currLocSection.address() == endLocSectionArr.address())
//                    return@forEach
//
//                val bVar3 = currLocSection.deref(size = 0xC0L).readByte(0x58L).toInt()
//                println("bVar3: $bVar3")
//                when {
//                    bVar3 > 0x15 -> {
//                        var sectionPtr = currLocSection
//                        while (sectionPtr.address() < endLocSectionArr.address()) {
//                            sections.add(CombinedLocationSection(sectionPtr.toShared().value(0xC0L)))
//                            sections.add(CombinedLocationSection(sectionPtr.pointerAtOffset(0xC0L, 0x10L).toShared().value(0xC0L)))
//                            sections.add(CombinedLocationSection(sectionPtr.pointerAtOffset(0x180L, 0x10L).toShared().value(0xC0L)))
//                            sections.add(CombinedLocationSection(sectionPtr.pointerAtOffset(0x240L, 0x10L).toShared().value(0xC0L)))
//                            sectionPtr = sectionPtr.pointerAtOffset(0x300L, 0x5000L)
//                        }
//                    }
//                    bVar3 >= 9 && bVar3 != 0x18 -> {
//                        var sectionPtr = currLocSection
//                        while (sectionPtr.address() < endLocSectionArr.address()) {
//                            for (i in 0..7)
//                                sections.add(CombinedLocationSection(sectionPtr.pointerAtOffset(i * 0xC0L, 0x10L).toShared().value(0xC0L)))
//                            sectionPtr = sectionPtr.pointerAtOffset(0x600L, 0x5000L)
//                        }
//                    }
//                    else -> {
//                        var sectionPtr = currLocSection
//                        while (sectionPtr.address() < endLocSectionArr.address()) {
//                            sectionPtr.toShared().value(0xC0L).getOrNull?.let {
//                                sections.add(CombinedLocationSection(it))
//                            }
//                            sectionPtr = sectionPtr.pointerAtOffset(0xC0L, 0x5000L)
//                        }
//                    }
//                }
//            }
//
//            return sections
//        }

//    val sections = locationData.iterateWithStride(0x300L).map { basePtr ->
//    }
}
package com.undercut.pathfinder

import com.undercut.cache.Cache
import com.undercut.cache.Index
import com.undercut.cache.getSmartSizeVar
import com.undercut.cache.getUnsignedSmart
import com.undercut.cache.skip
import com.undercut.cache.type.maps.ObjectShape
import com.undercut.cache.type.maps.RenderFlag
import com.undercut.game.Tile
import com.undercut.game.bootstrap.Bootstrap
import com.undercut.game.memory.NativeAccess.getOrNull
import com.undercut.game.memory.NativeAccess.pointerAtOffset
import com.undercut.game.memory.NativeAccess.readInt
import com.undercut.game.memory.NativeAccess.readLong
import com.undercut.game.memory.NativeAccess.toShared
import com.undercut.game.nxt.OClient
import com.undercut.game.nxt.OMapSquare
import com.undercut.game.nxt.OWorld
import com.undercut.game.nxt.types.Vector
import com.undercut.game.scene.CachedSceneObject
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import java.lang.foreign.ValueLayout.JAVA_BYTE
import java.lang.foreign.ValueLayout.JAVA_LONG
import java.nio.ByteBuffer

/**
 * Manages collision data for instanced (dynamic) regions.
 *
 * Instanced regions (tile X >= 6400) have no collision in the client's WorldCollision
 * system because the NXT client doesn't store pathfinding data — it's server-side.
 *
 * Iterates the World's mapsquare grid to find loaded MapSquare objects. For composite
 * (instanced) MapSquares, reads the ClientCompositeData to get per-zone (8x8) mappings
 * including source region, source zone coordinates, and rotation. Loads collision from
 * the MAPSV2 cache with proper rotation applied per zone.
 */
object DynamicRegionCollision {
    private val LOCK = Any()
    private val LONG_UNALIGNED: ValueLayout.OfLong = JAVA_LONG.withByteAlignment(1)
    private val INT_UNALIGNED: ValueLayout.OfInt = ValueLayout.JAVA_INT.withByteAlignment(1)

    /** Track which virtual regions we've already loaded collision for */
    private val loadedVirtualRegions = mutableSetOf<Int>()

    /** Cache decoded region data to avoid re-reading from cache */
    private val regionDataCache = mutableMapOf<Int, RegionData>()

    /** Ticks spent in dynamic region (for CCD retry timing) */
    private var ticksInDynamic = 0

    /** Whether we've dumped CCD diagnostic data this session */
    private var ccdDumped = false

    /** Set by BuildAreaHook when any build area init occurs */
    @Volatile
    private var isDirty = false

    /** Called by BuildAreaHook when BUILD_AREA_INIT fires */
    fun markDirty() {
        isDirty = true
        // Clear loaded regions so they get re-scanned with new zone data
        synchronized(LOCK) {
            loadedVirtualRegions.clear()
            baDumped = false
            ccdDumped = false
            println("[DRC] Build area changed — clearing loaded regions for rescan")
        }
    }

    // ── Pointer Safety ────────────────────────────────────────────────

    /** Valid userspace pointer on Linux x86_64 with ASLR (above 1TB, below user limit) */
    private fun isValidAddr(addr: Long): Boolean = addr in 0x10000000000L..0x7FFFFFFFFFFFL

    /** Safely read a long from addr. Returns null if address is invalid. */
    private fun readLongSafe(addr: Long): Long? {
        if (!isValidAddr(addr)) return null
        return MemorySegment.ofAddress(addr).reinterpret(8L).get(LONG_UNALIGNED, 0L)
    }

    /** Dump count bytes at addr as hex string. */
    private fun dumpHex(addr: Long, count: Int): String {
        if (!isValidAddr(addr)) return "INVALID(0x${addr.toString(16)})"
        val seg = MemorySegment.ofAddress(addr).reinterpret(count.toLong())
        return (0 until count).joinToString(" ") { "%02x".format(seg.get(JAVA_BYTE, it.toLong())) }
    }

    /** Safely read an int from addr. Returns null if address is invalid. */
    private fun readIntSafe(addr: Long): Int? {
        if (!isValidAddr(addr)) return null
        return MemorySegment.ofAddress(addr).reinterpret(4L).get(INT_UNALIGNED, 0L)
    }

    // ── Build Area Entries ─────────────────────────────────────────────
    //
    // REBUILD_NORMAL stores zone packed data in build area entries at
    // Client + BUILD_AREA_VECTOR (0x193d8). Each entry is 0x68 bytes:
    //   +0x00: mapSquareId (int)
    //   +0x08: mapSquareIds vector (begin/end/cap = 24B)
    //   +0x20: compositeIds(?) vector (begin/end/cap = 24B)
    //   +0x38: indexMap ptr
    //   +0x40: unknown (16B)
    //   +0x50: zone data vector-of-vectors (begin/end/cap = 24B)
    //     Each outer element is 24B (vector<int>: begin/end/cap)
    //     Inner ints are zone packed data:
    //       0x80000000 = empty zone
    //       Otherwise: rotation=(packed>>1)&3, absZoneX=(packed>>3)&0x7FF,
    //                  absZoneY=(packed>>14)&0x3FF, sourceLevel=(packed>>24)&3
    //
    // DecodeFromRegionData reads this as [4 planes][N rows][M cols].
    // Outer vector has 4 elements (planes). Each plane vector has row vectors.
    // Each row vector has column packed ints.

    /** Whether we've dumped build area diagnostic data */
    private var baDumped = false

    /**
     * Read zone data from build area entries.
     * Returns a map of mapSquareId → list of decoded zone mappings.
     */
    private fun readBuildAreaZones(vrx: Int, vry: Int): Int {
        val client = Bootstrap.client
        val vecAddr = client.ptr.address() + OClient.BUILD_AREA_VECTOR
        val mpBegin = readLongSafe(vecAddr) ?: return 0
        val mpEnd = readLongSafe(vecAddr + 8) ?: return 0
        if (mpBegin == 0L || mpEnd <= mpBegin) return 0

        val entrySize = 0x68L
        val numEntries = ((mpEnd - mpBegin) / entrySize).toInt()
        if (numEntries !in 1..100) return 0

        // Diagnostic dump (once)
        if (!baDumped) {
            baDumped = true
            println("[DRC-BA] Build area: $numEntries entries, vecAddr=0x${vecAddr.toString(16)}")
            println("[DRC-BA]   mpBegin=0x${mpBegin.toString(16)} mpEnd=0x${mpEnd.toString(16)}")
            System.out.flush()

            for (i in 0 until numEntries.coerceAtMost(8)) {
                val entryAddr = mpBegin + i * entrySize
                val mapSqId = readIntSafe(entryAddr) ?: continue
                val zoneBegin = readLongSafe(entryAddr + 0x50) ?: 0
                val zoneEnd = readLongSafe(entryAddr + 0x58) ?: 0

                println("[DRC-BA] Entry[$i]: mapSquareId=$mapSqId (0x${mapSqId.toString(16)})")

                if (isValidAddr(zoneBegin) && isValidAddr(zoneEnd) && zoneEnd > zoneBegin) {
                    val outerElemSize = 24L
                    val numOuter = ((zoneEnd - zoneBegin) / outerElemSize).toInt()
                    println("[DRC-BA]   zoneVec: $numOuter outer elements")

                    for (j in 0 until numOuter.coerceAtMost(5)) {
                        val innerBegin = readLongSafe(zoneBegin + j * outerElemSize) ?: continue
                        val innerEnd = readLongSafe(zoneBegin + j * outerElemSize + 8) ?: continue

                        if (isValidAddr(innerBegin) && isValidAddr(innerEnd) && innerEnd > innerBegin) {
                            val numInts = ((innerEnd - innerBegin) / 4).toInt()
                            val seg = MemorySegment.ofAddress(innerBegin).reinterpret(innerEnd - innerBegin)
                            var nonEmpty = 0
                            val samples = mutableListOf<String>()
                            for (k in 0 until numInts) {
                                val packed = seg.get(ValueLayout.JAVA_INT, k * 4L).toLong() and 0xFFFFFFFFL
                                if (packed != 0x80000000L) {
                                    nonEmpty++
                                    if (samples.size < 5) {
                                        val rot = ((packed shr 1) and 3).toInt()
                                        val zx = ((packed shr 3) and 0x7FF).toInt()
                                        val zy = ((packed shr 14) and 0x3FF).toInt()
                                        val sl = ((packed shr 24) and 3).toInt()
                                        samples.add("k=$k:src($zx,$zy,p$sl)r$rot")
                                    }
                                }
                            }
                            println("[DRC-BA]   outer[$j]: $numInts ints, $nonEmpty non-empty. ${samples.joinToString()}")
                        } else {
                            println("[DRC-BA]   outer[$j]: empty")
                        }
                    }
                } else {
                    println("[DRC-BA]   zoneVec: NULL or empty (begin=0x${zoneBegin.toString(16)})")
                }
                println("[DRC-BA]   entry hex(104B): ${dumpHex(entryAddr, 0x68)}")
                System.out.flush()
            }
        }

        // Now try to actually use the zone data
        val virtualBaseX = vrx * 64
        val virtualBaseY = vry * 64
        var zonesLoaded = 0

        for (i in 0 until numEntries) {
            val entryAddr = mpBegin + i * entrySize
            val zoneBegin = readLongSafe(entryAddr + 0x50) ?: continue
            val zoneEnd = readLongSafe(entryAddr + 0x58) ?: continue
            if (!isValidAddr(zoneBegin) || !isValidAddr(zoneEnd) || zoneEnd <= zoneBegin) continue

            val outerElemSize = 24L
            val numPlanes = ((zoneEnd - zoneBegin) / outerElemSize).toInt()
            if (numPlanes !in 1..4) continue

            // Each outer element is a plane vector. Within each plane:
            // The inner vector contains packed ints laid out as [row * numCols + col]
            // We need to determine numCols from the total int count and expected rows (8)
            for (plane in 0 until numPlanes) {
                val innerBegin = readLongSafe(zoneBegin + plane * outerElemSize) ?: continue
                val innerEnd = readLongSafe(zoneBegin + plane * outerElemSize + 8) ?: continue
                if (!isValidAddr(innerBegin) || !isValidAddr(innerEnd) || innerEnd <= innerBegin) continue

                val totalInts = ((innerEnd - innerBegin) / 4).toInt()
                if (totalInts <= 0 || totalInts > 200) continue

                // The inner ints might be a flat array [rows * cols] or a vector of vectors
                // Try flat: total / 8 rows = cols, or total / 13 cols = rows
                // Or the inner might itself be vector<vector<int>>...
                // For now, just try to read each packed int and use it
                val seg = MemorySegment.ofAddress(innerBegin).reinterpret(innerEnd - innerBegin)
                for (k in 0 until totalInts) {
                    val packed = seg.get(ValueLayout.JAVA_INT, k * 4L).toLong() and 0xFFFFFFFFL
                    if (packed == 0x80000000L) continue

                    val rotation = ((packed shr 1) and 3).toInt()
                    val absZoneX = ((packed shr 3) and 0x7FF).toInt()
                    val absZoneY = ((packed shr 14) and 0x3FF).toInt()
                    val sourceLevel = ((packed shr 24) and 3).toInt()

                    if (absZoneX !in 0..2047 || absZoneY !in 0..1023 || sourceLevel !in 0..3) continue

                    val sourceRegionX = absZoneX shr 3
                    val sourceRegionY = absZoneY shr 3
                    val localZoneX = absZoneX and 7
                    val localZoneY = absZoneY and 7
                    val archiveId = sourceRegionX or (sourceRegionY shl 7)

                    val regionData = getOrLoadRegion(archiveId) ?: continue

                    // We know which zone this packed int maps TO based on its position:
                    // k = row * numCols + col (within the plane)
                    // But we don't know numCols yet. For the diagnostic dump, we'll
                    // just load every non-empty zone and see what happens.
                    // The target zone is derived from the entry's virtual region +
                    // the zone's position within the grid.
                    // For now, try assuming 13 cols (modern binary):
                    val numCols = if (totalInts % 13 == 0) 13 else if (totalInts % 8 == 0) 8 else continue
                    val row = k / numCols
                    val col = k % numCols

                    val targetBaseX = virtualBaseX + col * 8
                    val targetBaseY = virtualBaseY + row * 8

                    applyZoneTileCollision(
                        regionData.tileFlags,
                        localZoneX, localZoneY, sourceLevel,
                        targetBaseX, targetBaseY, plane,
                        rotation
                    )
                    applyZoneObjectCollision(
                        regionData.objects + regionData.underwaterObjects,
                        regionData.tileFlags,
                        localZoneX, localZoneY, sourceLevel,
                        targetBaseX, targetBaseY, plane,
                        rotation
                    )
                    zonesLoaded++
                }
            }
        }

        if (zonesLoaded > 0) {
            println("[DRC-BA] Loaded $zonesLoaded zones for ($vrx,$vry) from build area entries")
        }
        return zonesLoaded
    }

    // ── Data Classes ──────────────────────────────────────────────────

    private data class RegionData(
        val tileFlags: Array<Array<ByteArray>>,  // [plane][x][y] — 4 × 64 × 64
        val objects: List<DecodedObject>,
        val underwaterObjects: List<DecodedObject>
    )

    private data class DecodedObject(
        val objectId: Int,
        val localX: Int,     // 0-63 within region
        val localY: Int,     // 0-63 within region
        val plane: Int,
        val shape: ObjectShape,
        val rotation: Int,   // 0-3
    )

    // ── Zone Rotation ─────────────────────────────────────────────────

    /**
     * Rotate a local position within an 8×8 zone.
     * Returns (rotatedX, rotatedY) in 0-7 range.
     */
    private fun rotateInZone(lx: Int, ly: Int, rotation: Int): Pair<Int, Int> {
        return when (rotation) {
            1 -> Pair(ly, 7 - lx)
            2 -> Pair(7 - lx, 7 - ly)
            3 -> Pair(7 - ly, lx)
            else -> Pair(lx, ly)
        }
    }

    // ── Main Entry Point ──────────────────────────────────────────────

    /**
     * Load collision for all instance regions.
     * Called from WorldCollision.checkLoad() when in a dynamic region.
     *
     * For non-composite MapSquares with real source coords (0-127), loads the
     * whole source region's collision directly.
     *
     * For composite MapSquares (sourceX/Y are virtual coords > 127), reads
     * zone mapping data from the ClientCompositeData structure. The CCD data
     * may not be populated immediately on instance entry, so we retry each tick
     * until data appears or we timeout (10 seconds).
     */
    fun loadInstanceCollision(playerTile: Tile) {
        synchronized(LOCK) {
            try {
                ticksInDynamic++

                val world = Bootstrap.client.sceneManager.currentWorld ?: return
                val xOffset = world.mapsquareXOffset
                val yOffset = world.mapsquareYOffset
                val outerVec = Vector(world.ptr.pointerAtOffset(
                    OWorld.MAPSQUARES_VECTOR, 0x20L
                ), 0x18L)

                val outerSize = outerVec.size
                if (outerSize <= 0 || outerSize > 200) return

                val firstScan = loadedVirtualRegions.isEmpty() && ticksInDynamic == 1
                if (firstScan) {
                    println("[DRC] Grid: outerSize=$outerSize, offset=($xOffset,$yOffset)")
                }

                var newRegions = 0
                var pendingCCD = 0
                for (gridX in 0 until outerSize.toInt()) {
                    val innerSeg = outerVec[gridX]
                    val innerVec = Vector(innerSeg, 0x18L)
                    val innerSize = innerVec.size
                    if (innerSize <= 0 || innerSize > 200) continue

                    for (gridY in 0 until innerSize.toInt()) {
                        val virtualRegionX = gridX + xOffset
                        val virtualRegionY = gridY + yOffset
                        val virtualKey = (virtualRegionX shl 16) or (virtualRegionY and 0xFFFF)
                        if (virtualKey in loadedVirtualRegions) continue

                        val mapSquarePtr = innerVec[gridY].toShared().value(0x300L).getOrNull
                            ?: continue

                        val sourceX = mapSquarePtr.readInt(OMapSquare.MAPSQUARE_X)
                        val sourceY = mapSquarePtr.readInt(OMapSquare.MAPSQUARE_Y)
                        val compositeDataAddr = mapSquarePtr.readLong(OMapSquare.COMPOSITE_DATA)

                        if (firstScan) {
                            println("[DRC] ($virtualRegionX,$virtualRegionY) source=($sourceX,$sourceY) composite=${compositeDataAddr != 0L}")
                        }

                        if (compositeDataAddr != 0L) {
                            // Composite MapSquare — try CCD for per-zone mappings
                            val loaded = probeCCD(compositeDataAddr, virtualRegionX, virtualRegionY)
                            if (loaded > 0) {
                                loadedVirtualRegions.add(virtualKey)
                                newRegions++
                            } else {
                                // CCD had no usable zones — try build area entries
                                val baLoaded = readBuildAreaZones(virtualRegionX, virtualRegionY)
                                if (baLoaded > 0) {
                                    loadedVirtualRegions.add(virtualKey)
                                    newRegions++
                                } else if (ticksInDynamic > 250) {
                                    loadedVirtualRegions.add(virtualKey)
                                    println("[DRC] Timeout: no zone data for ($virtualRegionX,$virtualRegionY)")
                                } else {
                                    pendingCCD++
                                }
                            }
                        } else if (sourceX in 0..127 && sourceY in 0..127) {
                            // Non-composite with real source coords → load whole region
                            val archiveId = sourceX or (sourceY shl 7)
                            loadWholeRegion(archiveId, virtualRegionX * 64, virtualRegionY * 64)
                            loadedVirtualRegions.add(virtualKey)
                            newRegions++
                        } else {
                            // No data source — skip permanently
                            loadedVirtualRegions.add(virtualKey)
                        }
                    }
                }

                if (newRegions > 0) {
                    println("[DRC] Loaded $newRegions regions (total: ${loadedVirtualRegions.size})")
                }
                if (pendingCCD > 0 && ticksInDynamic % 25 == 0) {
                    println("[DRC] Waiting for CCD data: $pendingCCD regions pending (tick $ticksInDynamic)")
                }
            } catch (e: Throwable) {
                println("[DRC] Error: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    // ── CCD Layout Constants ─────────────────────────────────────────
    //
    // ClientCompositeData is a nested EASTL fixed_vector hierarchy with
    // inline storage. Each fixed_vector has a 0x28 (40-byte) header:
    //   +0x00: mpBegin    (ptr → inline buffer at +0x28)
    //   +0x08: mpEnd      (ptr → end of active elements)
    //   +0x10: mpCapacity (ptr → end of inline buffer)
    //   +0x18: padding/flags (8 bytes)
    //   +0x20: fixed_pool_allocator ptr (→ +0x28)
    //   +0x28: inline buffer starts here
    //
    // Hierarchy:
    //   Outer: fixed_vector<PlaneGroup, 4>  (0x2648 total)
    //     header(0x28) + 4 × PlaneGroup(0x988)
    //
    //   PlaneGroup (0x988): fixed_vector<ZoneRow, 8>
    //     header(0x28) + 8 × ZoneRow(0x12C)
    //
    //   ZoneRow (0x12C): fixed_vector<Zone, 13>
    //     header(0x28) + 13 × Zone(0x14)
    //
    //   Zone (0x14 = 20 bytes):
    //     +0x00: int sourceLevel (-1 = empty)
    //     +0x04: int absoluteZoneY
    //     +0x08: int absoluteZoneX
    //     +0x0C: int rotation (0-3)
    //     +0x10: int mapIndex

    private const val PLANE_GROUP_SIZE = 0x988L
    private const val ZONE_ROW_SIZE = 0x12CL
    private const val ZONE_SIZE = 0x14L

    /**
     * Read zone mappings from the ClientCompositeData structure.
     * Returns:
     *   >0 = number of zones loaded via CCD remapping
     *   -1 = CCD structure valid but all zones empty (no remapping, caller should fall back)
     *    0 = data not ready yet (retry next tick)
     *
     * The CCD is a nested fixed_vector with inline storage. We compute direct
     * byte offsets into the structure rather than following pointer chains.
     */
    private fun probeCCD(ccdAddr: Long, vrx: Int, vry: Int): Int {
        if (!isValidAddr(ccdAddr)) return 0

        // Map the CCD header (we only need the first 0x28 bytes for vector pointers)
        val ccd = MemorySegment.ofAddress(ccdAddr).reinterpret(0x28L)

        // The CCD may start with the vector directly (mpBegin at +0x00)
        // or it may have a vtable at +0x00 (mpBegin at +0x08).
        // Heuristic: if field0 points near ccdAddr (inline buffer), it's mpBegin.
        // If field0 points to .text/.rodata (< process base), it's a vtable.
        val field0 = ccd.get(JAVA_LONG, 0x00L)
        val field1 = ccd.get(JAVA_LONG, 0x08L)
        val field2 = ccd.get(JAVA_LONG, 0x10L)

        // Determine vector offset: check if field0 looks like it points into CCD's inline buffer
        val inlineRange = ccdAddr..(ccdAddr + 0x40L)
        val vecOffset: Long
        val mpBegin: Long
        val mpEnd: Long
        if (field0 in inlineRange) {
            // No vtable: vector at +0x00
            vecOffset = 0L
            mpBegin = field0
            mpEnd = field1
        } else if (field1 in inlineRange) {
            // Has vtable at +0x00: vector at +0x08
            vecOffset = 0x08L
            mpBegin = field1
            mpEnd = field2
        } else {
            // Neither looks right — dump for analysis
            if (!ccdDumped) {
                ccdDumped = true
                println("[DRC] CCD layout unknown at 0x${ccdAddr.toString(16)} for ($vrx,$vry)")
                println("[DRC]   field0=0x${field0.toString(16)} field1=0x${field1.toString(16)} field2=0x${field2.toString(16)}")
                println("[DRC]   header(64B): ${dumpHex(ccdAddr, 64)}")
                System.out.flush()
            }
            return 0
        }

        if (mpBegin == 0L || mpEnd == 0L) return 0
        if (mpBegin == mpEnd) return 0 // empty vector, data not populated yet

        val vecSize = mpEnd - mpBegin
        val numPlanes = (vecSize / PLANE_GROUP_SIZE).toInt()
        if (numPlanes !in 1..4 || vecSize % PLANE_GROUP_SIZE != 0L) {
            if (!ccdDumped) {
                ccdDumped = true
                println("[DRC] Bad CCD vector: size=0x${vecSize.toString(16)} planes=$numPlanes remainder=${vecSize % PLANE_GROUP_SIZE}")
                println("[DRC]   mpBegin=0x${mpBegin.toString(16)} mpEnd=0x${mpEnd.toString(16)} vecOffset=$vecOffset")
                println("[DRC]   header(64B): ${dumpHex(ccdAddr, 64)}")
                System.out.flush()
            }
            return 0
        }

        // Diagnostic dump (once)
        if (!ccdDumped) {
            ccdDumped = true
            println("[DRC] CCD at 0x${ccdAddr.toString(16)} for ($vrx,$vry) vecOffset=$vecOffset")
            println("[DRC]   mpBegin=0x${mpBegin.toString(16)} mpEnd=0x${mpEnd.toString(16)} planes=$numPlanes")
            System.out.flush()
        }

        // Read all zone mappings
        var zonesLoaded = 0
        val virtualBaseX = vrx * 64
        val virtualBaseY = vry * 64
        val debugFirst = !ccdDumped || zonesLoaded == 0 // dump details for first CCD

        for (plane in 0 until numPlanes) {
            val planeBase = mpBegin + plane * PLANE_GROUP_SIZE

            // Read the plane group's inner vector mpBegin/mpEnd
            val planeBegin = readLongSafe(planeBase) ?: continue
            val planeEnd = readLongSafe(planeBase + 8L) ?: continue

            if (debugFirst && plane == 0) {
                println("[DRC]   plane0 base=0x${planeBase.toString(16)} mpBegin=0x${planeBegin.toString(16)} mpEnd=0x${planeEnd.toString(16)}")
                println("[DRC]   plane0 header(48B): ${dumpHex(planeBase, 48)}")
                val rowSize = if (planeEnd > planeBegin) (planeEnd - planeBegin) else 0
                println("[DRC]   plane0 vecSize=0x${rowSize.toString(16)} / ZONE_ROW_SIZE(0x${ZONE_ROW_SIZE.toString(16)}) = ${rowSize / ZONE_ROW_SIZE}")
                System.out.flush()
            }

            if (planeBegin == 0L || planeBegin == planeEnd) continue

            val rowVecSize = planeEnd - planeBegin
            val numRows = (rowVecSize / ZONE_ROW_SIZE).toInt()
            if (numRows !in 1..8 || rowVecSize % ZONE_ROW_SIZE != 0L) {
                if (debugFirst && plane == 0) {
                    println("[DRC]   plane0 BAD numRows=$numRows remainder=${rowVecSize % ZONE_ROW_SIZE}")
                    System.out.flush()
                }
                continue
            }

            for (row in 0 until numRows) {
                val rowBase = planeBegin + row * ZONE_ROW_SIZE

                // Read the zone row's inner vector mpBegin/mpEnd
                val rowBegin = readLongSafe(rowBase) ?: continue
                val rowEnd = readLongSafe(rowBase + 8L) ?: continue

                if (debugFirst && plane == 0 && row == 0) {
                    println("[DRC]   row0 base=0x${rowBase.toString(16)} mpBegin=0x${rowBegin.toString(16)} mpEnd=0x${rowEnd.toString(16)}")
                    println("[DRC]   row0 header(48B): ${dumpHex(rowBase, 48)}")
                    val colSize = if (rowEnd > rowBegin) (rowEnd - rowBegin) else 0
                    println("[DRC]   row0 vecSize=0x${colSize.toString(16)} / ZONE_SIZE(0x${ZONE_SIZE.toString(16)}) = ${colSize / ZONE_SIZE}")
                    if (colSize > 0 && colSize <= 0x200) {
                        println("[DRC]   row0 data(${colSize}B): ${dumpHex(rowBegin, colSize.toInt().coerceAtMost(160))}")
                    }
                    System.out.flush()
                }

                if (rowBegin == 0L || rowBegin == rowEnd) continue

                val colVecSize = rowEnd - rowBegin
                val numCols = (colVecSize / ZONE_SIZE).toInt()
                if (numCols !in 1..13) continue

                for (col in 0 until numCols) {
                    val zoneAddr = rowBegin + col * ZONE_SIZE

                    if (!isValidAddr(zoneAddr)) continue
                    val zoneSeg = MemorySegment.ofAddress(zoneAddr).reinterpret(ZONE_SIZE)

                    val sourceLevel = zoneSeg.get(ValueLayout.JAVA_INT, 0x00L)
                    if (sourceLevel == -1) continue // empty zone

                    val absZoneY = zoneSeg.get(ValueLayout.JAVA_INT, 0x04L)
                    val absZoneX = zoneSeg.get(ValueLayout.JAVA_INT, 0x08L)
                    val rotation = zoneSeg.get(ValueLayout.JAVA_INT, 0x0CL) and 0x3

                    // Validate zone coordinates
                    if (absZoneX !in 0..2047 || absZoneY !in 0..1023 || sourceLevel !in 0..3) continue

                    val sourceRegionX = absZoneX shr 3
                    val sourceRegionY = absZoneY shr 3
                    val localZoneX = absZoneX and 7
                    val localZoneY = absZoneY and 7
                    val archiveId = sourceRegionX or (sourceRegionY shl 7)

                    // Load the source region's collision data
                    val regionData = getOrLoadRegion(archiveId) ?: continue

                    // Target zone position in virtual tile coordinates
                    // row = middle loop (Y zones), col = inner loop (X zones)
                    val targetBaseX = virtualBaseX + col * 8
                    val targetBaseY = virtualBaseY + row * 8

                    // Apply tile collision for this 8×8 zone
                    applyZoneTileCollision(
                        regionData.tileFlags,
                        localZoneX, localZoneY, sourceLevel,
                        targetBaseX, targetBaseY, plane,
                        rotation
                    )

                    // Apply object collision for this 8×8 zone
                    applyZoneObjectCollision(
                        regionData.objects + regionData.underwaterObjects,
                        regionData.tileFlags,
                        localZoneX, localZoneY, sourceLevel,
                        targetBaseX, targetBaseY, plane,
                        rotation
                    )

                    zonesLoaded++
                }
            }
        }

        if (zonesLoaded > 0) {
            println("[DRC] Loaded $zonesLoaded zones for ($vrx,$vry) from CCD")
        }
        return zonesLoaded
    }

    /**
     * Apply tile collision for a single 8×8 zone with rotation.
     */
    private fun applyZoneTileCollision(
        tileFlags: Array<Array<ByteArray>>,
        localZoneX: Int, localZoneY: Int, sourceLevel: Int,
        virtualBaseX: Int, virtualBaseY: Int, virtualPlane: Int,
        rotation: Int
    ) {
        val srcBaseX = localZoneX * 8
        val srcBaseY = localZoneY * 8

        for (lx in 0 until 8) {
            for (ly in 0 until 8) {
                val srcX = srcBaseX + lx
                val srcY = srcBaseY + ly

                if (RenderFlag.flagged(tileFlags[sourceLevel][srcX][srcY].toInt(), RenderFlag.CLIPPED)) {
                    var finalPlane = virtualPlane
                    if (RenderFlag.flagged(tileFlags[1][srcX][srcY].toInt(), RenderFlag.LOWER_OBJECTS_TO_OVERRIDE_CLIPPING)) {
                        finalPlane--
                    }
                    if (finalPlane >= 0) {
                        val (rx, ry) = rotateInZone(lx, ly, rotation)
                        WorldCollision.addBlockedTile(
                            Tile.of(virtualBaseX + rx, virtualBaseY + ry, finalPlane)
                        )
                    }
                }
            }
        }
    }

    /**
     * Apply object collision for a single 8×8 zone with rotation.
     * Filters objects by source zone position and applies rotation.
     */
    private fun applyZoneObjectCollision(
        objects: List<DecodedObject>,
        tileFlags: Array<Array<ByteArray>>,
        localZoneX: Int, localZoneY: Int, sourceLevel: Int,
        virtualBaseX: Int, virtualBaseY: Int, virtualPlane: Int,
        rotation: Int
    ) {
        val srcBaseX = localZoneX * 8
        val srcBaseY = localZoneY * 8

        for (obj in objects) {
            // Filter: object must be in the target source zone
            if (obj.plane != sourceLevel) continue
            val objZoneX = obj.localX / 8
            val objZoneY = obj.localY / 8
            if (objZoneX != localZoneX || objZoneY != localZoneY) continue

            // Local position within the 8×8 zone
            val lx = obj.localX - srcBaseX
            val ly = obj.localY - srcBaseY

            // Plane adjustment from tile flags
            var objPlane = virtualPlane
            if (obj.localX in 0..63 && obj.localY in 0..63 &&
                tileFlags[1][obj.localX][obj.localY].toInt() and 0x2 != 0) {
                objPlane--
            }
            if (objPlane < 0) continue

            // Apply rotation to position and object rotation
            val (rx, ry) = rotateInZone(lx, ly, rotation)
            val rotatedObjRotation = (obj.rotation + rotation) and 0x3

            val sceneObj = CachedSceneObject(
                MemorySegment.NULL,
                obj.objectId, obj.objectId,
                Tile.of(virtualBaseX + rx, virtualBaseY + ry, objPlane),
                obj.shape, rotatedObjRotation.toByte()
            )
            WorldCollision.clip(sceneObj)
        }
    }

    // ── Whole Region Loading (non-composite fallback) ─────────────────

    /**
     * Load a whole 64×64 region's collision without zone-level rotation.
     * Used for non-composite (simple) MapSquares.
     */
    private fun loadWholeRegion(archiveId: Int, virtualBaseX: Int, virtualBaseY: Int) {
        val regionData = getOrLoadRegion(archiveId) ?: return

        // Apply tile collision
        for (plane in 0 until 4) {
            for (localX in 0 until 64) {
                for (localY in 0 until 64) {
                    if (RenderFlag.flagged(regionData.tileFlags[plane][localX][localY].toInt(), RenderFlag.CLIPPED)) {
                        var finalPlane = plane
                        if (RenderFlag.flagged(regionData.tileFlags[1][localX][localY].toInt(), RenderFlag.LOWER_OBJECTS_TO_OVERRIDE_CLIPPING)) {
                            finalPlane--
                        }
                        if (finalPlane >= 0) {
                            WorldCollision.addBlockedTile(
                                Tile.of(virtualBaseX + localX, virtualBaseY + localY, finalPlane)
                            )
                        }
                    }
                }
            }
        }

        // Apply object collision
        for (obj in regionData.objects + regionData.underwaterObjects) {
            var objPlane = obj.plane
            if (regionData.tileFlags[1][obj.localX][obj.localY].toInt() and 0x2 != 0) {
                objPlane--
            }
            if (objPlane < 0) continue

            val sceneObj = CachedSceneObject(
                MemorySegment.NULL,
                obj.objectId, obj.objectId,
                Tile.of(virtualBaseX + obj.localX, virtualBaseY + obj.localY, objPlane),
                obj.shape, obj.rotation.toByte()
            )
            WorldCollision.clip(sceneObj)
        }
    }

    // ── Region Data Cache ─────────────────────────────────────────────

    /**
     * Get cached region data or load from MAPSV2 cache.
     * Returns null if the archive doesn't exist.
     */
    private fun getOrLoadRegion(archiveId: Int): RegionData? {
        regionDataCache[archiveId]?.let { return it }

        if (!Cache.get().exists(Index.MAPSV2.id, archiveId)) return null
        val archive = Cache.get().getArchive(Index.MAPSV2, archiveId)

        // Decode tile flags
        val tileFlags = archive.files[TILES_FILE]?.let { decodeTileFlags(it.data) }
            ?: Array(4) { Array(64) { ByteArray(64) } }

        // Decode objects
        val objects = archive.files[OBJECTS_FILE]?.let { decodeObjects(it.data) } ?: emptyList()
        val underwaterObjects = archive.files[UNDERWATER_FILE]?.let { decodeObjects(it.data) } ?: emptyList()

        val data = RegionData(tileFlags, objects, underwaterObjects)
        regionDataCache[archiveId] = data
        return data
    }

    // ── Cache Decoding ────────────────────────────────────────────────

    /**
     * Decode tile flags from MAPSV2 tile file.
     * Returns [plane][x][y] tile flag array.
     */
    private fun decodeTileFlags(data: ByteArray): Array<Array<ByteArray>> {
        val stream = ByteBuffer.wrap(data)
        val tileFlags = Array(4) { Array(64) { ByteArray(64) } }

        stream.skip(5) // header

        for (plane in 0 until 4) {
            for (x in 0 until 64) {
                for (y in 0 until 64) {
                    val flags = stream.get().toInt() and 0xff
                    if (flags and 0x1 != 0) {
                        stream.skip(1) // shapeHash
                        stream.getUnsignedSmart() // overlayId
                    }
                    if (flags and 0x2 != 0) tileFlags[plane][x][y] = stream.get()
                    if (flags and 0x4 != 0) stream.getUnsignedSmart() // underlayId
                    if (flags and 0x8 != 0) stream.skip(2) // underlayId (short)
                }
            }
        }

        return tileFlags
    }

    /**
     * Decode all objects from MAPSV2 objects file into a list.
     */
    private fun decodeObjects(data: ByteArray): List<DecodedObject> {
        val stream = ByteBuffer.wrap(data)
        val objects = mutableListOf<DecodedObject>()
        var objectId = -1

        while (true) {
            val incr = stream.getSmartSizeVar()
            if (incr == 0) break
            objectId += incr

            var location = 0
            while (true) {
                val incr2 = stream.getUnsignedSmart()
                if (incr2 == 0) break
                location += incr2 - 1

                val localX = (location shr 6) and 0x3f
                val localY = location and 0x3f
                val plane = location shr 12
                val objectData = stream.get().toInt() and 0xff

                if (objectData and 0x80 != 0) readFlag0x80Data(stream)

                val shape = ObjectShape.forId(objectData shr 2 and 0x1f)
                val rotation = objectData and 0x3

                objects.add(DecodedObject(objectId, localX, localY, plane, shape, rotation))
            }
        }

        return objects
    }

    /** Read the 0x80 flag extended data from object stream */
    private fun readFlag0x80Data(stream: ByteBuffer) {
        val i = stream.get().toInt()
        if (i and 0x1 != 0) stream.skip(8)
        if (i and 0x2 != 0) stream.skip(2)
        if (i and 0x4 != 0) stream.skip(2)
        if (i and 0x8 != 0) stream.skip(2)
        if (i and 0x10 != 0) {
            stream.skip(2)
        } else {
            if (i and 0x20 != 0) stream.skip(2)
            if (i and 0x40 != 0) stream.skip(2)
            if (i and 0x80 != 0) stream.skip(2)
        }
    }

    // ── Clear ─────────────────────────────────────────────────────────

    /**
     * Clear all instance collision data.
     */
    fun clear() {
        synchronized(LOCK) {
            loadedVirtualRegions.clear()
            regionDataCache.clear()
            ticksInDynamic = 0
            ccdDumped = false
            baDumped = false
            isDirty = false
            println("[DRC] Cleared instance collision data")
        }
    }

    private const val OBJECTS_FILE = 0
    private const val UNDERWATER_FILE = 1
    private const val TILES_FILE = 3
}

package com.undercut.game.nxt.entity

import world.gregs.voidps.type.Tile
import com.undercut.game.bootstrap.Bootstrap
import com.undercut.game.memory.NativeAccess.readByte
import com.undercut.game.memory.NativeAccess.readFloat
import com.undercut.game.memory.NativeAccess.toMemorySegment
import com.undercut.game.memory.NativeAccess.toShared
import com.undercut.game.nxt.EntityType
import com.undercut.game.nxt.MapSquare
import com.undercut.game.nxt.OEntity
import com.undercut.game.nxt.OHintArrow
import com.undercut.game.nxt.entity.location.CombinedLocation
import com.undercut.game.nxt.entity.location.CombinedLocationSection
import com.undercut.game.nxt.entity.location.Location
import com.undercut.game.nxt.entity.npc.NPC
import com.undercut.game.nxt.entity.player.Player
import com.undercut.script.api.localPlayer
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout.ADDRESS
import kotlin.math.roundToInt

class HintArrow(ptr: MemorySegment) : Entity(ptr) {
    private fun targetTileFromTargetPos(): Tile? {
        val x = runCatching { ptr.readFloat(OHintArrow.TARGET_POS_VEC3 + 0x0L) }.getOrNull() ?: return null
        val y = runCatching { ptr.readFloat(OHintArrow.TARGET_POS_VEC3 + 0x4L) }.getOrNull() ?: return null

        val tileX = ((x - 256f) / 512f).roundToInt()
        val tileY = ((y - 256f) / 512f).roundToInt()

        if (tileX == 0 || tileY == 0) return null

        val z = runCatching { plane }.getOrDefault(0)
        return Tile.of(tileX, tileY, z)
    }

    private fun wrapEntity(rawPtr: MemorySegment): Entity? {
        val type = runCatching { EntityType.fromType(rawPtr.readByte(OEntity.ENTITY_TYPE).toInt()) }.getOrNull() ?: return null
        return when (type) {
            EntityType.LOCATION -> Location(rawPtr)
            EntityType.NPC_ENTITY -> NPC(rawPtr)
            EntityType.PLAYER_ENTITY -> Player(rawPtr)
            EntityType.OBJ_STACK -> ItemStack(rawPtr)
            EntityType.SPOT_ANIMATION -> SpotAnim(rawPtr)
            EntityType.COMBINED_LOCATION -> CombinedLocation(rawPtr)
            EntityType.MAP_SQUARE -> MapSquare(rawPtr)
            EntityType.COMBINED_LOCATION_SECTION -> CombinedLocationSection(rawPtr)
            EntityType.HINT_ARROW -> HintArrow(rawPtr)
            EntityType.HINT_ARROW_POINTER -> HintArrow(rawPtr)
            else -> null
        }
    }

    val targetEntityRaw: MemorySegment?
        get() = runCatching {
            ptr.asSlice(OHintArrow.TARGET_ENTITY_SHARED_PTR, 0x10L).toShared().valueOrNull(0x2000)
        }.getOrNull()

    val targetType: EntityType?
        get() = runCatching {
            targetEntityRaw?.let { runCatching { EntityType.fromType(it.readByte(OEntity.ENTITY_TYPE).toInt()) }.getOrNull() }
        }.getOrNull()

    val targetEntity: Entity?
        get() = runCatching { targetEntityRaw?.let { wrapEntity(it) } }.getOrNull()

    val targetTile: Tile
        get() {
            val direct = runCatching { targetTileFromTargetPos() }.getOrNull()
            if (direct != null) return direct

            val entityTile = runCatching { targetEntity?.tile }.getOrNull()
            if (entityTile != null) return entityTile

            return runCatching { tile }.getOrElse { runCatching { localPlayer.tile }.getOrElse { Tile.of(0, 0, 0) } }
        }

    val target: Tile
        get() = runCatching { targetTile }.getOrElse { runCatching { localPlayer.tile }.getOrElse { Tile.of(0, 0, 0) } }

    fun isValid(): Boolean {
        return runCatching {
            val type = EntityType.fromType(ptr.readByte(OEntity.ENTITY_TYPE).toInt())
            type == EntityType.HINT_ARROW || type == EntityType.HINT_ARROW_POINTER
        }.getOrDefault(false)
    }

    companion object {
        private data class CachedArrowData(val tile: Tile, val cycle: Int)

        private var lastScanCycle: Int = -1
        private var cachedTargetTile: CachedArrowData? = null

        private fun scanSceneGraphForHintArrows(): List<HintArrow> {
            fun isLikelyValidNativePtr(addr: Long): Boolean {
                if (addr <= 0x10000L) return false
                if ((addr and 0x7L) != 0L) return false
                return true
            }

            val world = runCatching { Bootstrap.client.sceneManager.currentWorld }.getOrNull()
                ?: return emptyList()
            val root = runCatching { world.rootGraphNode }.getOrNull() ?: return emptyList()

            val visited = HashSet<Long>(8192)
            val queue = ArrayDeque<MemorySegment>()

            val rootAddr = root.ptr.address()
            if (!isLikelyValidNativePtr(rootAddr)) return emptyList()
            visited.add(rootAddr)
            queue.add(root.ptr)

            val found = ArrayList<HintArrow>()
            while (queue.isNotEmpty() && visited.size < 20000) {
                val nodePtr = queue.removeFirst()

                val node = runCatching { GraphNode(nodePtr) }.getOrNull() ?: continue
                val entity = runCatching { node.entity }.getOrNull()
                if (entity is HintArrow) found.add(entity)

                val children = runCatching { node.children }.getOrNull() ?: continue
                for (cell in children.asSequence().take(512)) {
                    val childAddr = runCatching { cell.get(ADDRESS, 0).address() }.getOrDefault(0L)
                    if (!isLikelyValidNativePtr(childAddr)) continue
                    if (!visited.add(childAddr)) continue
                    queue.add(childAddr.toMemorySegment(0x200L))
                }
            }

            return found
        }

        fun all(): List<HintArrow> {
            return runCatching { scanSceneGraphForHintArrows() }.getOrDefault(emptyList())
        }

        fun get(): HintArrow? {
            return runCatching {
                val arrows = all()
                if (arrows.isEmpty()) return@runCatching null

                val me = runCatching { localPlayer.tile }.getOrNull() ?: return@runCatching arrows.firstOrNull()

                arrows.minByOrNull { arrow ->
                    runCatching { arrow.targetTile.getDistance(me) }.getOrDefault(Int.MAX_VALUE)
                }
            }.getOrNull()
        }

        fun getTargetTile(): Tile? {
            return runCatching {
                val cycle = Bootstrap.client.clientCycle

                val cached = cachedTargetTile
                if (cached != null && cycle - cached.cycle in 0..5) {
                    return@runCatching cached.tile
                }

                val arrow = get()
                if (arrow == null) {
                    cachedTargetTile = null
                    lastScanCycle = cycle
                    return@runCatching null
                }

                val tile = arrow.target
                cachedTargetTile = CachedArrowData(tile, cycle)
                lastScanCycle = cycle
                tile
            }.getOrNull()
        }

        fun clearCache() {
            cachedTargetTile = null
            lastScanCycle = -1
        }
    }

    override val size get() = 0
}

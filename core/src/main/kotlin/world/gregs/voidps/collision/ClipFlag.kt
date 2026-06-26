package world.gregs.voidps.collision

/**
 * BW = Actually blocks a tile when step is processed
 * BP = Blocks projectiles
 * PF = Pathfinder only takes these flags into consideration when generating a path
 * PFBW = Blocks tiles for both walk steps and the pathfinder
 */
enum class ClipFlag(val flag: Int) {
    EMPTY(-1),                // -1

    BW_NW(1 shl 0),          // 1
    BW_N(1 shl 1),           // 2
    BW_NE(1 shl 2),          // 4
    BW_E(1 shl 3),           // 8
    BW_SE(1 shl 4),          // 16
    BW_S(1 shl 5),           // 32
    BW_SW(1 shl 6),          // 64
    BW_W(1 shl 7),           // 128
    BW_FULL(1 shl 8),        // 256

    BP_NW(1 shl 9),          // 512
    BP_N(1 shl 10),          // 1024
    BP_NE(1 shl 11),         // 2048
    BP_E(1 shl 12),          // 4096
    BP_SE(1 shl 13),         // 8192
    BP_S(1 shl 14),          // 16384
    BP_SW(1 shl 15),         // 32768
    BP_W(1 shl 16),          // 65536
    BP_FULL(1 shl 17),       // 131072

    PFBW_GROUND_DECO(1 shl 18), // 262144

    BW_NPC(1 shl 19),        // 524288
    BW_PLAYER(1 shl 20),     // 1048576

    PFBW_FLOOR(1 shl 21),    // 2097152

    PF_NW(1 shl 22),         // 4194304
    PF_N(1 shl 23),          // 8388608
    PF_NE(1 shl 24),         // 16777216
    PF_E(1 shl 25),          // 33554432
    PF_SE(1 shl 26),         // 67108864
    PF_S(1 shl 27),          // 134217728
    PF_SW(1 shl 28),         // 268435456
    PF_W(1 shl 29),          // 536870912
    PF_FULL(1 shl 30),       // 1073741824
    UNDER_ROOF(1 shl 31);    // -2147483648

    companion object {
        fun getFlags(value: Int): List<ClipFlag> {
            return entries.filter { (value and it.flag) != 0 && it != EMPTY }
        }

        fun flagged(value: Int, vararg flags: ClipFlag): Boolean {
            var flag = 0
            for (f in flags) {
                flag = flag or f.flag
            }
            return value and flag != 0
        }

        fun or(vararg flags: ClipFlag): Int {
            var flag = 0
            for (f in flags) {
                flag = flag or f.flag
            }
            return flag
        }

        fun blockNorth(walk: Boolean, projectiles: Boolean, pathfinder: Boolean): Int {
            var flags = 0
            if (walk) flags = flags or BW_N.flag
            if (projectiles) flags = flags or BP_N.flag
            if (pathfinder) flags = flags or PF_N.flag
            return flags
        }

        fun blockNorthEast(walk: Boolean, projectiles: Boolean, pathfinder: Boolean): Int {
            var flags = 0
            if (walk) flags = flags or BW_NE.flag
            if (projectiles) flags = flags or BP_NE.flag
            if (pathfinder) flags = flags or PF_NE.flag
            return flags
        }

        fun blockNorthWest(walk: Boolean, projectiles: Boolean, pathfinder: Boolean): Int {
            var flags = 0
            if (walk) flags = flags or BW_NW.flag
            if (projectiles) flags = flags or BP_NW.flag
            if (pathfinder) flags = flags or PF_NW.flag
            return flags
        }

        fun blockSouth(walk: Boolean, projectiles: Boolean, pathfinder: Boolean): Int {
            var flags = 0
            if (walk) flags = flags or BW_S.flag
            if (projectiles) flags = flags or BP_S.flag
            if (pathfinder) flags = flags or PF_S.flag
            return flags
        }

        fun blockSouthEast(walk: Boolean, projectiles: Boolean, pathfinder: Boolean): Int {
            var flags = 0
            if (walk) flags = flags or BW_SE.flag
            if (projectiles) flags = flags or BP_SE.flag
            if (pathfinder) flags = flags or PF_SE.flag
            return flags
        }

        fun blockSouthWest(walk: Boolean, projectiles: Boolean, pathfinder: Boolean): Int {
            var flags = 0
            if (walk) flags = flags or BW_SW.flag
            if (projectiles) flags = flags or BP_SW.flag
            if (pathfinder) flags = flags or PF_SW.flag
            return flags
        }

        fun blockEast(walk: Boolean, projectiles: Boolean, pathfinder: Boolean): Int {
            var flags = 0
            if (walk) flags = flags or BW_E.flag
            if (projectiles) flags = flags or BP_E.flag
            if (pathfinder) flags = flags or PF_E.flag
            return flags
        }

        fun blockWest(walk: Boolean, projectiles: Boolean, pathfinder: Boolean): Int {
            var flags = 0
            if (walk) flags = flags or BW_W.flag
            if (projectiles) flags = flags or BP_W.flag
            if (pathfinder) flags = flags or PF_W.flag
            return flags
        }
    }
}

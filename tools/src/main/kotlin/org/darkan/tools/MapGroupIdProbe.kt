package org.darkan.tools

import world.gregs.voidps.cache.Cache
import world.gregs.voidps.cache.Index
import world.gregs.voidps.cache.sqlite.SQLiteCache

/**
 * OFFLINE probe to determine how index-5 map groups are addressed (the index-5 ref
 * table has flags=0x0c, i.e. NO name hashes, so the client does NOT resolve m{X}_{Y}
 * by name-hash — it derives the group id arithmetically from mapsquare coords).
 *
 * Tries candidate coordinate->groupId formulas and reports how many of the present
 * group ids each explains, then back-solves a few present ids to mapsquare coords so
 * we can see whether the Lumbridge window (mapsquare ~50,50) is actually populated.
 *
 * Run: ./gradlew :tools:run -PmainClass=org.darkan.tools.MapGroupIdProbeKt
 */
fun main() {
    val cache: Cache = SQLiteCache.load()
    val ids = cache.archives(Index.MAPS).toSortedSet()
    println("index 5: ${ids.size} group ids present; min=${ids.first()} max=${ids.last()}")
    println()

    // Candidate encodings (terrain 'm' and loc 'l' typically interleave or split the id space).
    // Reverse-decode each present id under each formula and check the coord ranges are sane
    // (mapsquare x,y in 0..127). The formula that yields the tightest, fully-in-range coverage wins.
    data class Formula(val name: String, val encode: (Int, Int) -> Int, val decode: (Int) -> Pair<Int, Int>)
    val formulas = listOf(
        Formula("(x<<7)|y", { x, y -> (x shl 7) or y }, { g -> (g ushr 7) to (g and 0x7F) }),
        Formula("(x<<8)|y", { x, y -> (x shl 8) or y }, { g -> (g ushr 8) to (g and 0xFF) }),
        Formula("(y<<7)|x", { x, y -> (y shl 7) or x }, { g -> (g and 0x7F) to (g ushr 7) }),
        Formula("(y<<8)|x", { x, y -> (y shl 8) or x }, { g -> (g and 0xFF) to (g ushr 8) }),
    )

    for (f in formulas) {
        var inRange = 0
        var xMax = 0; var yMax = 0
        for (g in ids) {
            val (x, y) = f.decode(g)
            if (x in 0..127 && y in 0..127) { inRange++; if (x > xMax) xMax = x; if (y > yMax) yMax = y }
        }
        println("formula ${f.name}: ${inRange}/${ids.size} ids decode to x,y in 0..127 (xMax=$xMax yMax=$yMax)")
    }
    println()

    // For each formula, does the Lumbridge spawn window (mapsquare 49..51 x 49..51) hit present ids?
    for (f in formulas) {
        val hits = mutableListOf<String>()
        for (x in 49..51) for (y in 49..51) {
            val g = f.encode(x, y)
            if (ids.contains(g)) hits.add("($x,$y)->$g")
        }
        println("formula ${f.name}: Lumbridge-window present ids: ${if (hits.isEmpty()) "NONE" else hits.joinToString(", ")}")
    }
    println()

    // Show the actual ids closest to where Lumbridge should be under (x<<8)|y, the RS3 NXT convention.
    // Lumbridge mapsquare (50,50): (50<<8)|50 = 12850 (terrain). Locs often share id-space.
    println("Sample of present ids in [12000..13200] (Lumbridge neighborhood under (x<<8)|y):")
    println("  " + ids.filter { it in 12000..13200 }.joinToString(" ") { it.toString() })
    println()
    println("Sample of present ids in [6300..6600] (Lumbridge neighborhood under (x<<7)|y):")
    println("  " + ids.filter { it in 6300..6600 }.joinToString(" ") { it.toString() })
    println()

    // The (x<<8)|y terrain band tops out at (99<<8)|99 = 25443, but the present max is 25516.
    // So there is a SECOND band. Show the ids above 25443 and decode them a few ways.
    println("Present ids > 25443 (above the (x<<8)|y terrain max):")
    val high = ids.filter { it > 25443 }
    println("  count=${high.size}; ${high.take(40).joinToString(" ")}")
    println()

    // Hypothesis: a single formula (x<<8)|y where x can exceed 99 (expanded RS3 map, up to ~127).
    // Re-test full coverage allowing x,y in 0..255 to see the true shape.
    run {
        var xMax = 0; var yMax = 0; var xMin = 999; var yMin = 999
        val xs = sortedSetOf<Int>(); val ys = sortedSetOf<Int>()
        for (g in ids) { val x = g ushr 8; val y = g and 0xFF; if (x>xMax)xMax=x; if (y>yMax)yMax=y; if (x<xMin)xMin=x; if (y<yMin)yMin=y; xs.add(x); ys.add(y) }
        println("(x<<8)|y over ALL ids: x in $xMin..$xMax (${xs.size} distinct), y in $yMin..$yMax (${ys.size} distinct)")
        println("  distinct x values: ${xs.joinToString(",")}")
    }
    println()

    // Confirm terrain vs loc do not collide: count how many ids share each (x<<8)|y slot is 1 by construction
    // (it's a bijection). The real question is whether m AND l for the same square both exist as DISTINCT ids.
    // Print whether 12850 (m50_50 under x<<8|y) and candidate loc encodings exist.
    val g = 12850
    println("Slot checks around mapsquare (50,50):")
    println("  (50<<8)|50            = $g present=${ids.contains(g)}")
    println("  (50<<8)|50 + 0x4000   = ${g + 0x4000} present=${ids.contains(g + 0x4000)}")  // type-bit hi
    println("  ((50+128)<<8)|50      = ${((50+128) shl 8) or 50} present=${ids.contains(((50+128) shl 8) or 50)}")
}

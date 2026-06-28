package org.darkan.tools

import world.gregs.voidps.cache.Index
import world.gregs.voidps.cache.config.decoder.IDKDecoder
import world.gregs.voidps.cache.definition.data.BodyDefinition
import world.gregs.voidps.cache.definition.decoder.BodyDecoder
import world.gregs.voidps.cache.definition.decoder.ItemDecoder
import world.gregs.voidps.cache.sqlite.SQLiteCache
import java.nio.file.Paths

/**
 * Inspects OUR 948 cache for the data the PLAYER_INFO APPEARANCE encoder depends on, to diagnose the
 * client `DecodeAppearance` over-read crash:
 *  - the human Body/Wearpos def (DEFAULTS/archive-6/file-0): `disabledSlots` (→ emitted slot count)
 *    and the colour/style array sizes (→ K kitColours/kitStyles count the client reads).
 *  - whether the alerion default identitykit ids (male/female body styles) EXIST in our cache.
 *  - whether the bronze dagger (item 1205) exists.
 *
 * Run: `./gradlew :tools:appearanceInspect -Pargs="/Users/robert/darkan-3/948-sqlite-cache"`
 */
fun main(args: Array<String>) {
    val cachePath = if (args.isNotEmpty()) Paths.get(args[0]) else Paths.get("./data/cache")
    println("Inspecting cache at ${cachePath.toAbsolutePath().normalize()}")
    val cache = SQLiteCache.load(cachePath)

    // --- 1. Body/Wearpos def (DEFAULTS index, archive 6, file 0) ---
    val bodies: Array<BodyDefinition> = BodyDecoder().load(cache)
    println("\n=== Body defs (DEFAULTS/6) count=${bodies.size} ===")
    for (b in bodies) {
        println(
            "  body ${b.id}: disabledSlots(${b.disabledSlots.size})=${b.disabledSlots.toList()} " +
                "anInt4506=${b.anInt4506} anInt4504=${b.anInt4504} " +
                "arr4501=${b.anIntArray4501?.toList()} arr4507=${b.anIntArray4507?.toList()}"
        )
    }
    val body0 = bodies.getOrNull(0)
    if (body0 != null) {
        val emitted = body0.disabledSlots.indices.filter { body0.disabledSlots[it] == 0 }
        val disabled = body0.disabledSlots.indices.filter { body0.disabledSlots[it] == 1 }
        println("\n  body 0 emitted slots (disabledSlots[i]==0): $emitted  (count=${emitted.size})")
        println("  body 0 DISABLED slots (disabledSlots[i]==1): $disabled")
    }

    // --- 2. IdentityKit ids (CONFIG/3) ---
    val idks = IDKDecoder().load(cache)
    println("\n=== IDK defs (CONFIG/3) count=${idks.size} ===")
    val maleStyles = intArrayOf(3, 14, 18, 26, 34, 38, 42)
    val femaleStyles = intArrayOf(48, -1, 57, 65, 68, 77, 80)
    fun checkKit(label: String, id: Int) {
        if (id < 0) { println("  $label kit $id: (none — emits empty)"); return }
        val d = idks.getOrNull(id)
        if (d == null || d.id != id) {
            println("  $label kit $id: *** MISSING / null (id=${d?.id}) ***")
        } else {
            println("  $label kit $id: OK bodyPart=${d.bodyPart} models=${d.modelIds?.toList()} nonSelectable=${d.nonSelectable}")
        }
    }
    println("  -- male default body styles --")
    maleStyles.forEach { checkKit("male", it) }
    println("  -- female default body styles --")
    femaleStyles.forEach { checkKit("female", it) }
    println("  IDK max id = ${idks.size - 1}")

    // --- 3. Item id (bronze dagger 1205) ---
    val items = ItemDecoder().load(cache)
    val dagger = items.getOrNull(1205)
    println("\n=== Item 1205 (bronze_dagger) ===")
    println(
        "  item 1205: name=${dagger?.name} wearPos=${dagger?.wearPos} wearPos2=${dagger?.wearPos2} " +
            "wearPos3=${dagger?.wearPos3} primaryMaleModel=${dagger?.primaryMaleModel} " +
            "primaryFemaleModel=${dagger?.primaryFemaleModel}"
    )

    println("\n(Index.DEFAULTS=${Index.DEFAULTS})")
}

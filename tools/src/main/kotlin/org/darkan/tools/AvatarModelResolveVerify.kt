package org.darkan.tools

import world.gregs.voidps.cache.Cache
import world.gregs.voidps.cache.Index
import world.gregs.voidps.cache.config.data.IDKDefinition
import world.gregs.voidps.cache.config.decoder.IDKDecoder
import world.gregs.voidps.cache.definition.data.BodyDefinition
import world.gregs.voidps.cache.definition.decoder.BodyDecoder
import world.gregs.voidps.cache.sqlite.SQLiteCache
import java.nio.file.Paths

/**
 * THROWAWAY verification (do NOT wire into prod): proves — or disproves — that the KIT-ONLY default
 * avatar the server sends actually resolves to real, non-empty models in OUR served 948 cache.
 *
 * The standing mandate is "client is king / do not trust docs". A binary-verified doc claims every
 * default-kit id and item 1205 resolve to non-empty model lists. This tool ignores that claim and
 * decodes the actual cache:
 *  1. For each default identitykit id (male + female sets), decode the IDK config (CONFIG idx 2 / arch 3)
 *     and resolve every model id it references against the MODELS index (7). A kit fails if it is
 *     missing, decodes to an empty model list, or references any model id with no non-empty MODELS entry.
 *  2. Decode the human Body/Wearpos def (DEFAULTS idx 28 / arch 6 / file 0): slot count + disabled set.
 *  3. Print a decisive VERDICT.
 *
 * The avatar compose in the client (DecodeAppearanceEquipment -> per-slot kit lookup -> model load)
 * yields a null composed model (render_model = 0x0, the observed bug) iff any default kit slot fails
 * to load a model. So a single failing kit here == the avatar-invisible root cause.
 *
 * Run: ./gradlew :tools:avatarModelVerify -Pargs="/Users/robert/darkan-3/948-sqlite-cache"
 */

// Default kit ids the server's kit-only appearance emits (ALERION copies — flagged as possibly wrong
// for 948-5 in the task). Index in the array == the wear/body slot the kit fills.
private val MALE_KITS = intArrayOf(3, 14, 18, 26, 34, 38, 42)
private val FEMALE_KITS = intArrayOf(48, 57, 65, 68, 77, 80)

private const val ITEM_1205_MODEL = 73526 // doc-claimed model for worn item 1205

private class ModelResolver(private val cache: Cache) {
    /** Indices the cache library actually exposes (only indices whose ref table parsed). */
    val indices: IntArray = cache.indices()

    val modelsIndexPresent: Boolean = Index.MODELS in indices
    val modelArchiveCount: Int = cache.archiveCount(Index.MODELS)

    // The modern RT7 model index (47) IS present in our cache (~1.6GB). Probe it too so the verdict
    // holds whichever index the client's model loader actually reads kit models from.
    val rt7IndexPresent: Boolean = Index.MODELS_RT7 in indices
    val rt7ArchiveCount: Int = cache.archiveCount(Index.MODELS_RT7)

    /**
     * A model id "resolves" iff the MODELS index holds it as a present archive whose decompressed
     * file-0 blob is non-empty. This is exactly the client's model-load precondition: an absent or
     * empty group yields no model and the slot contributes nothing to the composed avatar.
     */
    fun resolve(modelId: Int): ModelStatus = resolveIn(Index.MODELS, modelsIndexPresent, modelId)

    /** Same resolution, against the RT7 model index (47). */
    fun resolveRt7(modelId: Int): ModelStatus = resolveIn(Index.MODELS_RT7, rt7IndexPresent, modelId)

    private fun resolveIn(index: Int, indexPresent: Boolean, modelId: Int): ModelStatus {
        if (modelId < 0) return ModelStatus.UNSET
        if (!indexPresent) return ModelStatus.NO_INDEX
        if (!cache.exists(index, modelId)) return ModelStatus.MISSING_ARCHIVE
        val rawSize = cache.sectorSize(index, modelId)
        if (rawSize <= 0) return ModelStatus.EMPTY_ARCHIVE
        val data = try {
            cache.data(index, modelId, 0)
        } catch (e: Exception) {
            return ModelStatus.DECODE_ERROR
        }
        return when {
            data == null -> ModelStatus.NULL_DATA
            data.isEmpty() -> ModelStatus.EMPTY_DATA
            else -> ModelStatus.ok(data.size)
        }
    }
}

private sealed class ModelStatus(val ok: Boolean, val label: String) {
    object UNSET : ModelStatus(true, "unset(-1)")            // not a failure: kit simply has no model in that slot
    object NO_INDEX : ModelStatus(false, "*** MODELS INDEX (7) ABSENT ***")
    object MISSING_ARCHIVE : ModelStatus(false, "*** MISSING archive in MODELS ***")
    object EMPTY_ARCHIVE : ModelStatus(false, "*** EMPTY archive (raw size <= 0) ***")
    object NULL_DATA : ModelStatus(false, "*** data() == null ***")
    object EMPTY_DATA : ModelStatus(false, "*** decompressed data EMPTY ***")
    object DECODE_ERROR : ModelStatus(false, "*** decode threw ***")
    class OK(val size: Int) : ModelStatus(true, "OK ($size bytes)")
    companion object { fun ok(size: Int): ModelStatus = OK(size) }
}

private class KitResult(
    val id: Int,
    val present: Boolean,
    val bodyPart: Int,
    val modelIds: IntArray?,
    val modelStatuses: List<Pair<Int, ModelStatus>>,
    val rt7Statuses: List<Pair<Int, ModelStatus>>,
) {
    /** Resolves in the legacy MODELS index (7). */
    val resolves: Boolean
        get() {
            if (!present) return false
            if (modelIds == null || modelIds.isEmpty()) return false
            // every referenced model must resolve (UNSET/-1 entries don't appear in modelIds opcode 2)
            return modelStatuses.all { it.second.ok }
        }

    /** Resolves in the RT7 MODELS index (47). */
    val resolvesRt7: Boolean
        get() {
            if (!present) return false
            if (modelIds == null || modelIds.isEmpty()) return false
            return rt7Statuses.all { it.second.ok }
        }
}

private fun verifyKit(idks: Array<IDKDefinition>, resolver: ModelResolver, id: Int): KitResult {
    val d = idks.getOrNull(id)
    if (d == null || d.id != id) {
        return KitResult(id, present = false, bodyPart = -1, modelIds = null, modelStatuses = emptyList(), rt7Statuses = emptyList())
    }
    val ids = d.modelIds ?: IntArray(0)
    val statuses = ids.map { it to resolver.resolve(it) }
    val rt7 = ids.map { it to resolver.resolveRt7(it) }
    return KitResult(id, present = true, bodyPart = d.bodyPart, modelIds = d.modelIds, modelStatuses = statuses, rt7Statuses = rt7)
}

private fun printKit(label: String, r: KitResult) {
    if (!r.present) {
        println("  [$label] kit ${r.id}: *** MISSING / not present in cache ***")
        return
    }
    val verdict = if (r.resolves) "RESOLVES" else "FAILS"
    println("  [$label] kit ${r.id}: $verdict  bodyPart=${r.bodyPart}  models=${r.modelIds?.toList()}")
    for ((mid, st) in r.modelStatuses) {
        val rt7 = r.rt7Statuses.firstOrNull { it.first == mid }?.second
        println("        model $mid -> idx7: ${st.label}   |   idx47(RT7): ${rt7?.label ?: "?"}")
    }
    if ((r.modelIds?.size ?: 0) == 0) {
        println("        *** opcode-2 model list EMPTY -> nothing composes for this slot ***")
    }
}

fun main(args: Array<String>) {
    val cachePath = if (args.isNotEmpty()) Paths.get(args[0]) else Paths.get("./data/cache")
    println("=".repeat(78))
    println("AVATAR KIT/MODEL RESOLUTION VERIFICATION (verify-against-cache, doc-independent)")
    println("Cache: ${cachePath.toAbsolutePath().normalize()}")
    println("=".repeat(78))

    val cache = SQLiteCache.load(cachePath)
    val resolver = ModelResolver(cache)

    // --- 0. Index inventory (does MODELS even exist as a parsed index?) ---
    println("\n--- 0. INDEX INVENTORY ---")
    println("  indices present (ref table parsed): ${resolver.indices.toList()}")
    println("  MODELS index (7) present: ${resolver.modelsIndexPresent}  (archive count=${resolver.modelArchiveCount})")
    println("  MODELS_RT7 index (47) present: ${resolver.rt7IndexPresent}  (archive count=${resolver.rt7ArchiveCount})")
    println("  CONFIG  index (2) present: ${Index.CONFIGS in resolver.indices}")
    println("  DEFAULTS index (28) present: ${Index.DEFAULTS in resolver.indices}")

    // --- 1. Decode all IDK configs, then resolve each default kit's models ---
    val idks: Array<IDKDefinition> = IDKDecoder().load(cache)
    println("\n--- 1. DEFAULT KIT -> MODEL RESOLUTION ---")
    println("  IDK defs decoded (CONFIG/3): count=${idks.size}, max id=${idks.size - 1}")

    println("\n  MALE default kits {3,14,18,26,34,38,42}:")
    val maleResults = MALE_KITS.map { verifyKit(idks, resolver, it) }
    maleResults.forEach { printKit("male", it) }

    println("\n  FEMALE default kits {48,57,65,68,77,80}:")
    val femaleResults = FEMALE_KITS.map { verifyKit(idks, resolver, it) }
    femaleResults.forEach { printKit("female", it) }

    // --- 1b. Worn item 1205 model (doc claims model 73526) ---
    println("\n  Worn item 1205 claimed model $ITEM_1205_MODEL:")
    println("        model $ITEM_1205_MODEL -> ${resolver.resolve(ITEM_1205_MODEL).label}")

    // --- 2. Human Body / Wearpos def (DEFAULTS/6/0) ---
    val bodies: Array<BodyDefinition> = BodyDecoder().load(cache)
    println("\n--- 2. HUMAN BODY / WEARPOS DEF (DEFAULTS/6/0) ---")
    val body0 = bodies.getOrNull(0)
    if (body0 == null) {
        println("  *** body 0 MISSING ***")
    } else {
        val disabled = body0.disabledSlots.indices.filter { body0.disabledSlots[it] == 1 }
        println("  body 0: slotCount=${body0.slotCount} (doc expects 19)")
        println("  body 0: disabledSlots raw=${body0.disabledSlots.toList()}")
        println("  body 0: DISABLED slot set=$disabled (doc expects {12,13,17})")
        println("  body 0: anInt4506=${body0.anInt4506} anInt4504=${body0.anInt4504} " +
            "arr4501=${body0.anIntArray4501?.toList()} arr4507=${body0.anIntArray4507?.toList()} " +
            "(the +0x240 colour-clamp is a separate config, see BodyDefinition doc)")
    }

    // --- 3. VERDICT ---
    println("\n" + "=".repeat(78))
    println("VERDICT")
    println("=".repeat(78))
    val maleResolveAll = maleResults.all { it.resolves }
    val femaleResolveAll = femaleResults.all { it.resolves }
    val maleFailing = maleResults.filterNot { it.resolves }.map { it.id }
    val femaleFailing = femaleResults.filterNot { it.resolves }.map { it.id }

    val maleRt7All = maleResults.all { it.resolvesRt7 }
    val femaleRt7All = femaleResults.all { it.resolvesRt7 }

    println("  MALE   kits resolve in MODELS idx7:  $maleResolveAll" +
        if (maleFailing.isNotEmpty()) "  (failing: $maleFailing)" else "")
    println("  MALE   kits resolve in MODELS idx47: $maleRt7All" +
        if (!maleRt7All) "  (failing: ${maleResults.filterNot { it.resolvesRt7 }.map { it.id }})" else "")
    println("  FEMALE kits resolve in MODELS idx7:  $femaleResolveAll" +
        if (femaleFailing.isNotEmpty()) "  (failing: $femaleFailing)" else "")
    println("  FEMALE kits resolve in MODELS idx47: $femaleRt7All" +
        if (!femaleRt7All) "  (failing: ${femaleResults.filterNot { it.resolvesRt7 }.map { it.id }})" else "")

    if (!resolver.modelsIndexPresent && !resolver.rt7IndexPresent) {
        println("\n  >>> ROOT CAUSE: NEITHER model index (7 legacy NOR 47 RT7) is present in OUR served cache.")
        println("      No kit model can possibly resolve -> the composed avatar model is null")
        println("      -> render_model = 0x0 -> invisible avatar. This matches the observed bug exactly.")
    } else if (!maleResolveAll && !maleRt7All) {
        println("\n  >>> ROOT CAUSE: the MALE default kits fail to resolve a model in EITHER model index")
        println("      (7 legacy AND 47 RT7) in OUR cache. The kit-only avatar compose loads no model")
        println("      for those slots -> null composed model -> render_model = 0x0 -> invisible avatar.")
        if (!resolver.modelsIndexPresent && resolver.rt7IndexPresent) {
            println("      NOTE: idx7 is empty but idx47 (RT7) is present — yet these kit model ids are NOT")
            println("      in idx47 either, so the served cache is missing the body-kit models outright.")
        }
    } else if (maleResolveAll || maleRt7All) {
        val where = if (maleResolveAll && maleRt7All) "BOTH idx7 and idx47"
            else if (maleResolveAll) "idx7 (legacy MODELS)" else "idx47 (RT7 MODELS)"
        println("\n  >>> The MALE kit-only default avatar resolves CLEANLY to real, non-empty models in")
        println("      OUR cache (via $where). Cache data is NOT the cause; the failure is the")
        println("      client-side compose TRIGGER (being RE'd separately).")
    }
    println("=".repeat(78))
}

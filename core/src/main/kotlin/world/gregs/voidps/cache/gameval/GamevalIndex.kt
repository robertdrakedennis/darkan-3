package world.gregs.voidps.cache.gameval

/**
 * Constants and the archive-id -> gameval-type mapping for cache **index 67** — the RS3
 * gameval / RSCM (RuneScape Content Mapping) id <-> dev-name index.
 *
 * Index 67 stores, per gameval type, a table mapping a numeric content id to its developer name
 * (e.g. `seq 0 -> "swarm_walk"`, `npc 0 -> "hans"`). It is **beta-only**: the live RS3 cache ships
 * index 67 empty, so this is purely additive and is reverse-engineered from real beta bytes
 * (`data/betacache/js5-67.jcache`, rev 947). See `re-resources/docs/cache/gameval_index67.md`.
 *
 * Each gameval **type** is a separate archive (single file, id 0). Archive ids are SPARSE and are
 * **not** name-hashed (the index ref-table sets nameHash = 0). The archive id is the client's own
 * RSCM **type enum** ordinal — it is NOT the [world.gregs.voidps.cache.Config] id of the same name
 * (e.g. archive 69 is `midi`, not `varbit`; archive 60 is `var_object`, not `var_player`).
 *
 * The [TYPE_BY_ARCHIVE] mapping below was derived by decoding every archive and cross-validating
 * its id -> name table against the bundled `re-resources/gamevals/<type>.json` dictionaries: the
 * matching file names the type. 34 of 35 archives matched the bundled json at 100% precision; the
 * `component` archive differs only for the stale `escape_menu` interface (1433) in the bundled json
 * (a data drift, not a decode error).
 *
 * Names verified against content + Jagex's cs2 type system (`re-resources/cs2-dumps/cs2/opcodes.d.ts`):
 * archive 49 `graphic` is the 2D-SPRITE type (`type graphic = number`, e.g. `hitsplat`, stat/emote
 * icons) — that IS Jagex's name, NOT "sprite". Index 67 contains NO `spotanim` archive (cs2
 * `type spotanim` is real and distinct, but spotanims have no gameval mapping) and NO `varbit`
 * archive (varbit/clientscript names live in the live cache instead).
 */
object GamevalIndex {

    /** Cache index that holds the gameval / RSCM id<->name tables. */
    const val INDEX = 67

    /** The one type whose entry keys are composite `(interfaceId << 16) | componentId` values. */
    const val COMPONENT = "component"

    /**
     * Index-67 archive id -> gameval type name. The type name is the stem of the matching
     * `re-resources/gamevals/<name>.json` dictionary (so it lines up with [world.gregs.voidps] code
     * that already keys gamevals by those names). Order matches ascending archive id.
     */
    val TYPE_BY_ARCHIVE: Map<Int, String> = linkedMapOf(
        0 to "component",
        5 to "bas",
        9 to "category",
        12 to "cursor",
        14 to "dbrow",
        15 to "dbtable",
        16 to "enum",
        20 to "headbar",
        21 to "hitmark",
        24 to "interface",
        25 to "inv",
        28 to "loc",
        32 to "material",
        34 to "model",
        35 to "npc",
        36 to "obj",
        37 to "param",
        41 to "quest",
        44 to "seq",
        49 to "graphic",
        50 to "struct",
        55 to "var_clan",
        56 to "var_clan_setting",
        57 to "var_client",
        59 to "var_npc",
        60 to "var_object",
        61 to "var_player",
        64 to "sound",
        69 to "midi",
        80 to "var_player_group",
        89 to "achievement",
        90 to "fontmetrics",
        92 to "stylesheet",
        96 to "ui_anim_curve",
        97 to "ui_anim",
    )

    /** Reverse of [TYPE_BY_ARCHIVE]: gameval type name -> index-67 archive id. */
    val ARCHIVE_BY_TYPE: Map<String, Int> = TYPE_BY_ARCHIVE.entries.associate { (id, name) -> name to id }

    /** The type name for [archive], or a synthetic `"type_<id>"` for an archive not in the map. */
    fun typeName(archive: Int): String = TYPE_BY_ARCHIVE[archive] ?: "type_$archive"

    /** The index-67 archive id that holds [type], or `null` if the type is not stored in index 67. */
    fun archiveId(type: String): Int? = ARCHIVE_BY_TYPE[type]
}

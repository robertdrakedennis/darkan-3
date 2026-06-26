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
 * `type spotanim` is real and distinct, but spotanims have no gameval mapping).
 *
 * ### Combined var/varbit namespace
 * Each `var_*` archive is a **combined** namespace: within it, entries whose dev-name starts with
 * `_` are that domain's **varbits**, and the rest are its **vars**. So index 67 does carry varbit
 * names after all — folded into the `var_*` archives rather than a standalone `varbit` archive.
 * [GamevalIndexDecoder] splits every such archive into a `var_<domain>` table (vars, original ids
 * kept — those ids are the real game var ids) and a `varbit_<domain>` table (varbits, each rebased
 * to `id - offset` with its leading `_` stripped, where `offset` is the smallest `_`-entry id).
 * The per-domain metadata for that split lives in [VAR_DOMAINS]. Verified from the real beta bytes:
 * `var_player` (archive 61) splits to 10056 vars + 50171 varbits at offset 12865, reproducing the
 * legacy bundled `varbit.json` (`varbit_player[0] == "zaros_spellbook"`) exactly.
 */
object GamevalIndex {

    /** Cache index that holds the gameval / RSCM id<->name tables. */
    const val INDEX = 67

    /** The one type whose entry keys are composite `(interfaceId << 16) | componentId` values. */
    const val COMPONENT = "component"

    /** Prefix marking a combined-archive entry as a varbit (vs a var) — see [VAR_DOMAINS]. */
    const val VARBIT_PREFIX = "_"

    /**
     * Metadata for one combined var-domain archive (e.g. archive 61 == `var_player`). Each `var_*`
     * archive is a combined var+varbit namespace (see the class KDoc); this names both halves that
     * [GamevalIndexDecoder] splits out of it.
     *
     * @property archive the index-67 archive id (also the [TYPE_BY_ARCHIVE] key for [varType])
     * @property domain the suffix after `var_` (e.g. `player`, `clan_setting`, `player_group`)
     */
    data class VarDomain(val archive: Int, val domain: String) {
        /** The var-part type name — equal to `TYPE_BY_ARCHIVE[archive]`, e.g. `var_player`. */
        val varType: String get() = "var_$domain"

        /** The varbit-part type name, e.g. `varbit_player`. Empty file is omitted on export. */
        val varbitType: String get() = "varbit_$domain"
    }

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

    /**
     * Every combined var-domain archive (each `var_*` entry of [TYPE_BY_ARCHIVE]), in ascending
     * archive-id order. Used by [GamevalIndexDecoder] and the export tool to split each archive into
     * its `var_<domain>` + `varbit_<domain>` halves. Domains whose data has no `_`-prefixed entries
     * (currently `client` and `player_group`) still appear here — they simply yield an empty varbit
     * table (and so no `varbit_<domain>.json` is written).
     */
    val VAR_DOMAINS: List<VarDomain> = TYPE_BY_ARCHIVE.entries
        .filter { it.value.startsWith("var_") }
        .map { VarDomain(it.key, it.value.removePrefix("var_")) }

    /** [VAR_DOMAINS] keyed by archive id. */
    val VAR_DOMAIN_BY_ARCHIVE: Map<Int, VarDomain> = VAR_DOMAINS.associateBy { it.archive }

    /** [VAR_DOMAINS] keyed by domain suffix (`player`, `npc`, `clan`, …). */
    val VAR_DOMAIN_BY_NAME: Map<String, VarDomain> = VAR_DOMAINS.associateBy { it.domain }

    /** [VAR_DOMAINS] keyed by var-part type name (`var_player`, …). */
    val VAR_DOMAIN_BY_VAR_TYPE: Map<String, VarDomain> = VAR_DOMAINS.associateBy { it.varType }

    /** [VAR_DOMAINS] keyed by varbit-part type name (`varbit_player`, …). */
    val VAR_DOMAIN_BY_VARBIT_TYPE: Map<String, VarDomain> = VAR_DOMAINS.associateBy { it.varbitType }

    /**
     * Reverse of [TYPE_BY_ARCHIVE]: gameval type name -> index-67 archive id. Includes every
     * `varbit_<domain>` name too — it resolves to the SAME archive as its `var_<domain>` (both halves
     * are split from one combined archive), so e.g. `archiveId("varbit_player") == 61 == archiveId("var_player")`.
     */
    val ARCHIVE_BY_TYPE: Map<String, Int> = buildMap {
        for ((id, name) in TYPE_BY_ARCHIVE) put(name, id)
        for (domain in VAR_DOMAINS) put(domain.varbitType, domain.archive)
    }

    /** The type name for [archive], or a synthetic `"type_<id>"` for an archive not in the map. */
    fun typeName(archive: Int): String = TYPE_BY_ARCHIVE[archive] ?: "type_$archive"

    /**
     * The index-67 archive id that holds [type], or `null` if the type is not stored in index 67.
     * Both `var_<domain>` and `varbit_<domain>` resolve to the same combined archive.
     */
    fun archiveId(type: String): Int? = ARCHIVE_BY_TYPE[type]
}

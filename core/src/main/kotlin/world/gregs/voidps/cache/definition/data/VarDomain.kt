package world.gregs.voidps.cache.definition.data

/**
 * Variable domain a [VarBitDefinition] (or var type) belongs to, mapped to the
 * config archive that holds the backing var types. Mirrors the NXT client's
 * domain table (verified for 948-5).
 */
enum class VarDomain(val id: Int, val configArchive: Int) {
    PLAYER(0, 60),
    NPC(1, 61),
    CLIENT(2, 62),
    WORLD(3, 63),
    REGION(4, 64),
    OBJECT(5, 65),
    CLAN(6, 66),
    CLAN_SETTING(7, 67),
    UNK(8, 68);

    companion object {
        private val map = entries.associateBy(VarDomain::id)

        fun forId(id: Int): VarDomain? = map[id]
    }
}

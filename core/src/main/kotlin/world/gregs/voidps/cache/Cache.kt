package world.gregs.voidps.cache

import org.darkan.core.EnvVars
import world.gregs.voidps.cache.config.data.CursorDefinition
import world.gregs.voidps.cache.config.data.HeadbarDefinition
import world.gregs.voidps.cache.config.data.HitmarkDefinition
import world.gregs.voidps.cache.config.data.IDKDefinition
import world.gregs.voidps.cache.config.data.InventoryDefinition
import world.gregs.voidps.cache.config.data.ParamDefinition
import world.gregs.voidps.cache.config.data.QuestDefinition
import world.gregs.voidps.cache.config.decoder.CursorDecoder
import world.gregs.voidps.cache.config.decoder.HeadbarDecoder
import world.gregs.voidps.cache.config.decoder.HitmarkDecoder
import world.gregs.voidps.cache.config.decoder.IDKDecoder
import world.gregs.voidps.cache.config.decoder.InventoryDecoder
import world.gregs.voidps.cache.config.decoder.ParamDecoder
import world.gregs.voidps.cache.config.decoder.QuestDecoder
import world.gregs.voidps.cache.definition.data.*
import world.gregs.voidps.cache.definition.decoder.*
import world.gregs.voidps.cache.secure.Huffman
import world.gregs.voidps.cache.sqlite.SQLiteCache
import java.nio.file.Path

interface Cache {

    val versionTable: ByteArray

    fun indexCount(): Int

    fun indices(): IntArray

    fun indexCrcs(): IntArray

    fun sector(index: Int, archive: Int): ByteArray?

    /**
     * Size in bytes of the raw container for [archive] in [index], or -1 if absent.
     * Implementations should answer this without loading the blob where possible.
     */
    fun sectorSize(index: Int, archive: Int): Int = sector(index, archive)?.size ?: -1

    /**
     * Whether [archive] is present in [index], answered without loading the
     * container where the implementation can (mirrors the client's archive
     * presence check before requesting map groups).
     */
    fun exists(index: Int, archive: Int): Boolean = sectorSize(index, archive) != -1

    fun sectorVersion(index: Int, archive: Int): Int = 0

    fun archives(index: Int): IntArray

    fun archiveCount(index: Int): Int

    fun lastArchiveId(indexId: Int): Int

    fun archiveId(index: Int, hash: Int): Int

    fun archiveId(index: Int, name: String): Int = archiveId(index, name.hashCode())

    fun files(index: Int, archive: Int): IntArray

    fun fileCount(indexId: Int, archiveId: Int): Int

    fun lastFileId(indexId: Int, archive: Int): Int

    fun data(index: Int, archive: Int, file: Int = 0, xtea: IntArray? = null): ByteArray?

    fun data(index: Int, name: String, xtea: IntArray? = null) = data(index, archiveId(index, name), xtea = xtea)

    fun write(index: Int, archive: Int, file: Int, data: ByteArray, xteas: IntArray? = null)

    fun write(index: Int, archive: String, data: ByteArray, xteas: IntArray? = null)

    fun update(): Boolean

    fun close()

    companion object {
        private val lock = Any()

        /**
         * The shared cache instance. Injected (engine) callers should [init] this
         * with their own path before first access; the server leaves it null and
         * the [get] fallback lazily loads from [EnvVars.cachePath].
         */
        @Volatile
        private var instance: Cache? = null

        /** Idempotently load the shared cache from [path] if none is set yet. */
        @JvmStatic
        fun init(path: Path) = init(path, readOnly = false)

        /**
         * Idempotently load the shared cache from [path]. Pass [readOnly] = true when [path] points at
         * a cache owned by another process (the injected engine reading the live NXT client cache), so
         * the SQLite files are opened `mode=ro` and never written — writing them corrupts the client's
         * cache and triggers a full re-download. See [world.gregs.voidps.cache.sqlite.IndexFile].
         */
        @JvmStatic
        fun init(path: Path, readOnly: Boolean) {
            if (instance != null) {
                return
            }
            synchronized(lock) {
                if (instance == null) {
                    instance = SQLiteCache.load(path, readOnly = readOnly)
                }
            }
        }

        /** Idempotently install a pre-built [cache] as the shared instance. */
        @JvmStatic
        fun init(cache: Cache) {
            if (instance != null) {
                return
            }
            synchronized(lock) {
                if (instance == null) {
                    instance = cache
                }
            }
        }

        @JvmStatic
        fun get(): Cache {
            instance?.let { return it }
            return synchronized(lock) {
                instance ?: SQLiteCache.load().also { instance = it }
            }
        }

        private val _huffman: Huffman by lazy {
            Huffman().load(get().data(Index.HUFFMAN, 1)!!)
        }

        @JvmStatic val huffman: Huffman get() = _huffman

        // --- Eager whole-array decoders (server use) ---

        @JvmStatic val varbits: Array<VarBitDefinition> by lazy {
            VarBitDecoder().load(get())
        }

        @JvmStatic val objects: Array<ObjectDefinition> by lazy {
            ObjectDecoder(EnvVars.members).load(get())
        }

        @JvmStatic val items: Array<ItemDefinition> by lazy {
            ItemDecoder().load(get())
        }

        @JvmStatic val npcs: Array<NPCDefinition> by lazy {
            NPCDecoder(EnvVars.members).load(get())
        }

        @JvmStatic val animations: Array<AnimationDefinition> by lazy {
            AnimationDecoder().load(get())
        }

        @JvmStatic val enums: Array<EnumDefinition> by lazy {
            EnumDecoder().load(get())
        }

        @JvmStatic val structs: Array<StructDefinition> by lazy {
            StructDecoder().load(get())
        }

        @JvmStatic val spotAnims: Array<SpotAnimDefinition> by lazy {
            SpotAnimDecoder().load(get())
        }

        @JvmStatic val bas: Array<BASDefinition> by lazy {
            BASDecoder().load(get())
        }

        @JvmStatic val interfaces: Array<InterfaceDefinition> by lazy {
            InterfaceDecoder().load(get())
        }

        @JvmStatic val fonts: Array<FontDefinition> by lazy {
            FontDecoder().load(get())
        }

        // Config-style eager arrays (single CONFIG archive, file-per-id). These
        // back type enumeration (e.g. the MCP list_content_type tool); the per-id
        // accessors below decode lazily for the hot path.

        @JvmStatic val params: Array<ParamDefinition> by lazy {
            ParamDecoder().load(get())
        }

        @JvmStatic val invs: Array<InventoryDefinition> by lazy {
            InventoryDecoder().load(get())
        }

        @JvmStatic val quests: Array<QuestDefinition> by lazy {
            QuestDecoder().load(get())
        }

        // --- Lazy memoized per-id accessors (engine hot path) ---
        //
        // These decode a single id on demand instead of forcing the eager arrays
        // above, so the injected process never decodes tens of thousands of defs.

        private val itemDefinitions by lazy {
            Definitions(ItemDecoder(), get()) { def ->
                intArrayOf(
                    def.notedTemplateId, def.noteId,
                    def.lendTemplateId, def.lendId,
                    def.boundTemplateId, def.bindId,
                )
            }
        }
        private val npcDefinitions by lazy { Definitions(NPCDecoder(EnvVars.members), get()) }
        private val objectDefinitions by lazy { Definitions(ObjectDecoder(EnvVars.members), get()) }
        private val enumDefinitions by lazy { Definitions(EnumDecoder(), get()) }
        private val structDefinitions by lazy { Definitions(StructDecoder(), get()) }
        private val varbitDefinitions by lazy { Definitions(VarBitDecoder(), get()) }
        private val animationDefinitions by lazy { Definitions(AnimationDecoder(), get()) }
        private val basDefinitions by lazy { Definitions(BASDecoder(), get()) }
        private val spotAnimDefinitions by lazy { Definitions(SpotAnimDecoder(), get()) }
        private val interfaceDefinitions by lazy { Definitions(InterfaceDecoder(), get()) }
        private val fontDefinitions by lazy { Definitions(FontDecoder(), get()) }

        @JvmStatic fun item(id: Int): ItemDefinition? = itemDefinitions.getOrNull(id)
        @JvmStatic fun npc(id: Int): NPCDefinition? = npcDefinitions.getOrNull(id)
        @JvmStatic fun obj(id: Int): ObjectDefinition? = objectDefinitions.getOrNull(id)
        @JvmStatic fun enum(id: Int): EnumDefinition? = enumDefinitions.getOrNull(id)
        @JvmStatic fun struct(id: Int): StructDefinition? = structDefinitions.getOrNull(id)
        @JvmStatic fun varbit(id: Int): VarBitDefinition? = varbitDefinitions.getOrNull(id)
        @JvmStatic fun animation(id: Int): AnimationDefinition? = animationDefinitions.getOrNull(id)
        @JvmStatic fun bas(id: Int): BASDefinition? = basDefinitions.getOrNull(id)
        @JvmStatic fun spotAnim(id: Int): SpotAnimDefinition? = spotAnimDefinitions.getOrNull(id)
        @JvmStatic fun interfaceDef(id: Int): InterfaceDefinition? = interfaceDefinitions.getOrNull(id)
        @JvmStatic fun font(id: Int): FontDefinition? = fontDefinitions.getOrNull(id)

        // --- Config + newly-merged per-id accessors (engine hot path) ---

        private val paramDefinitions by lazy { Definitions(ParamDecoder(), get()) }
        private val cursorDefinitions by lazy { Definitions(CursorDecoder(), get()) }
        private val headbarDefinitions by lazy { Definitions(HeadbarDecoder(), get()) }
        private val hitmarkDefinitions by lazy { Definitions(HitmarkDecoder(), get()) }
        private val idkDefinitions by lazy { Definitions(IDKDecoder(), get()) }
        private val inventoryDefinitions by lazy { Definitions(InventoryDecoder(), get()) }
        private val questDefinitions by lazy { Definitions(QuestDecoder(), get()) }
        private val spriteDefinitions by lazy { Definitions(SpriteDecoder(), get()) }
        private val bodyDefinitions by lazy { Definitions(BodyDecoder(), get()) }

        @JvmStatic fun param(id: Int): ParamDefinition? = paramDefinitions.getOrNull(id)
        @JvmStatic fun cursor(id: Int): CursorDefinition? = cursorDefinitions.getOrNull(id)
        @JvmStatic fun headbar(id: Int): HeadbarDefinition? = headbarDefinitions.getOrNull(id)
        @JvmStatic fun hitmark(id: Int): HitmarkDefinition? = hitmarkDefinitions.getOrNull(id)
        @JvmStatic fun idk(id: Int): IDKDefinition? = idkDefinitions.getOrNull(id)
        @JvmStatic fun inv(id: Int): InventoryDefinition? = inventoryDefinitions.getOrNull(id)
        @JvmStatic fun quest(id: Int): QuestDefinition? = questDefinitions.getOrNull(id)
        @JvmStatic fun sprite(id: Int): SpriteDefinition? = spriteDefinitions.getOrNull(id)
        @JvmStatic fun seq(id: Int): AnimationDefinition? = animation(id)

        /**
         * Body / wear-pos ("WearposDefaults") def (DEFAULTS/archive-6/file-0 per id). The appearance
         * encoder resolves this for the avatar's body type to drive the emitted slot order from
         * [BodyDefinition.disabledSlots]. The appearance kit-colour/kit-style count is NOT taken from
         * here — it is a hard-coded 10 in the client (see [BodyDefinition]).
         */
        @JvmStatic fun body(id: Int): BodyDefinition? = bodyDefinitions.getOrNull(id)

        // --- Region (MAPSV2) accessor ---
        //
        // Regions are keyed by packed regionId, not a 256-split definition id, so they
        // fall outside the Definitions per-id holder. Decoding is a direct call; callers
        // that need caching (the engine's Region adapter) memoize on their side.

        private val mapDecoder by lazy { MapDecoder() }

        @JvmStatic fun region(regionId: Int): RegionDefinition? = mapDecoder.decode(get(), regionId)
    }
}

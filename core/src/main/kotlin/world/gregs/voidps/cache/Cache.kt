package world.gregs.voidps.cache

import org.darkan.core.EnvVars
import world.gregs.voidps.cache.definition.data.*
import world.gregs.voidps.cache.definition.decoder.*
import world.gregs.voidps.cache.secure.Huffman
import world.gregs.voidps.cache.sqlite.SQLiteCache

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
        private val singleton: Cache by lazy {
            SQLiteCache.load()
        }

        private val _huffman: Huffman by lazy {
            Huffman().load(singleton.data(Index.HUFFMAN, 1)!!)
        }

        @JvmStatic val varbits: Array<VarBitDefinition> by lazy {
            VarBitDecoder().load(singleton)
        }

        @JvmStatic val objects: Array<ObjectDefinition> by lazy {
            ObjectDecoder(EnvVars.members).load(singleton)
        }

        @JvmStatic val items: Array<ItemDefinition> by lazy {
            ItemDecoder().load(singleton)
        }

        @JvmStatic val npcs: Array<NPCDefinition> by lazy {
            NPCDecoder(EnvVars.members).load(singleton)
        }

        @JvmStatic val animations: Array<AnimationDefinition> by lazy {
            AnimationDecoder().load(singleton)
        }

        @JvmStatic val enums: Array<EnumDefinition> by lazy {
            EnumDecoder().load(singleton)
        }

        @JvmStatic val structs: Array<StructDefinition> by lazy {
            StructDecoder().load(singleton)
        }

        @JvmStatic val graphics: Array<GraphicDefinition> by lazy {
            GraphicDecoder().load(singleton)
        }

        @JvmStatic val bas: Array<BASDefinition> by lazy {
            BASDecoder().load(singleton)
        }

        @JvmStatic val interfaces: Array<InterfaceDefinition> by lazy {
            InterfaceDecoder().load(singleton)
        }

        @JvmStatic val fonts: Array<FontDefinition> by lazy {
            FontDecoder().load(singleton)
        }

        @JvmStatic fun get() = singleton

        @JvmStatic val huffman: Huffman get() = _huffman
    }
}
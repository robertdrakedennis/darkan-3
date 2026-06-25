package world.gregs.voidps.cache.config.data

import world.gregs.voidps.cache.Definition

/**
 * Identity-kit (IDK) type definition (CONFIG index, archive 3 for 948-5).
 *
 * A leaner, engine-shaped view of the same archive served by the legacy
 * IdentityKitDefinition; kept separate so engine consumers get a 1:1 surface.
 */
data class IDKDefinition(
    override var id: Int = -1,
    var bodyPart: Int = -1,
    var modelIds: IntArray? = null,
    var nonSelectable: Boolean = false,
    var recolorSrc: IntArray? = null,
    var recolorDst: IntArray? = null,
    var retextureSrc: IntArray? = null,
    var retextureDst: IntArray? = null,
    var headModelIds: IntArray = IntArray(5) { -1 },
) : Definition {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as IDKDefinition
        return id == other.id
    }

    override fun hashCode(): Int = id

    companion object {
        val EMPTY = IDKDefinition()
    }
}

package world.gregs.voidps.cache.definition.data

import world.gregs.voidps.cache.Definition
import world.gregs.voidps.cache.definition.Extra

data class EnumDefinition(
    override var id: Int = -1,
    var keyType: Char = 0.toChar(),
    var valueType: Char = 0.toChar(),
    var defaultString: String = "null",
    var defaultInt: Int = 0,
    var length: Int = 0,
    var map: Map<Int, Any>? = null,
    override var stringId: String = "",
    override var extras: Map<String, Any>? = null
) : Definition, Extra {

    /** Engine alias for [map] as a mutable view (the backing decode map is mutable). */
    @Suppress("UNCHECKED_CAST")
    val values: MutableMap<Int, Any>
        get() = (map as? MutableMap<Int, Any>) ?: mutableMapOf()

    fun getKey(value: Any) = map?.filterValues { it == value }?.keys?.lastOrNull() ?: -1

    fun getInt(id: Int) = map?.get(id) as? Int ?: defaultInt

    fun randomInt() = map?.values?.random() as? Int ?: defaultInt

    fun getString(id: Int) = map?.get(id) as? String ?: defaultString

    /** Legacy alias for [getString]. */
    fun getStringValue(id: Int) = getString(id)

    /** Legacy alias for [getInt]. */
    fun getIntValue(id: Int) = getInt(id)

    /** Legacy alias for [getInt] - indexed by position. */
    fun getIntValueAtIndex(id: Int) = getInt(id)

    /** Gets the value at the given key. */
    fun getValue(id: Any): Any? = map?.get(id as? Int ?: return null)

    /** Legacy alias for [defaultInt]. */
    fun getDefaultIntValue(): Int = defaultInt

    /** Gets the number of entries in this enum. */
    fun getSize(): Int = map?.size ?: 0

    /** Gets the key for a given value, or -1 if not found. */
    fun getKeyForValue(value: Any): Int {
        val m = map ?: return -1
        for ((k, v) in m) {
            if (v == value) return k
        }
        return -1
    }

    companion object {
        val EMPTY = EnumDefinition()
    }
}
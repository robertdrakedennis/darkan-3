package world.gregs.voidps.cache.config.data

import world.gregs.voidps.cache.Definition

/**
 * Param type definition (CONFIG index, archive 11).
 *
 * [type] is the cp1252 script-var-type character read from opcode 1; [typeId] is
 * the numeric type id from opcode 101. The NXT client maps these onto its
 * `jag::ScriptVarType` table — kept as char/id here to avoid coupling core to the
 * engine's CS2VarType enum.
 */
data class ParamDefinition(
    override var id: Int = -1,
    var type: Char = 0.toChar(),
    var typeId: Int = 0,
    var defaultInt: Int = 0,
    var defaultString: String? = null,
    var autoDisable: Boolean = true,
) : Definition {
    companion object {
        val EMPTY = ParamDefinition()
    }
}

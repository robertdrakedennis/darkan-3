package org.darkan.core.net.prot

import kotlinx.serialization.Serializable

// --- Variables ---

data class VarpSmall(val id: Int, val value: Int) : ServerProt
data class VarpLarge(val id: Int, val value: Int) : ServerProt
data class VarpLong(val id: Int, val value: Long) : ServerProt
data class ClientSetVarcSmall(val id: Int, val value: Int) : ServerProt
data class ClientSetVarcLarge(val id: Int, val value: Int) : ServerProt
data class ClientSetVarcStr(val id: Int, val value: String) : ServerProt
data class UpdateStat(val skillId: Int, val xp: Int, val level: Int) : ServerProt

/** VARP_BIT_SMALL — sets a player varbit with a 1-byte value. 948 op 10. */
data class VarpBitSmall(val id: Int, val value: Int) : ServerProt

/** VARP_BIT_LARGE — sets a player varbit with a 4-byte value. 948 op 51. */
data class VarpBitLarge(val id: Int, val value: Int) : ServerProt

/** CLIENT_SETVARCBIT_SMALL — sets a client varbit with a 1-byte value. 948 op 48. */
data class ClientSetVarcBitSmall(val id: Int, val value: Int) : ServerProt

/** CLIENT_SETVARCBIT_LARGE — sets a client varbit with a 4-byte value. 948 op 69. */
data class ClientSetVarcBitLarge(val id: Int, val value: Int) : ServerProt

/**
 * CLIENT_SETVARC_STR_LARGE — sets a client string var. 948 op 116. STRING-FIRST variant,
 * distinct from [ClientSetVarcStr] (op 92, id-first).
 */
data class ClientSetVarcStrLarge(val id: Int, val value: String) : ServerProt

@JvmInline
value class ResetClientVarcache(val dummy: Int = 0) : ServerProt

@Serializable @JvmInline value class VarclanEnable(val dummy: Int = 0) : ServerProt
@Serializable @JvmInline value class VarclanDisable(val dummy: Int = 0) : ServerProt

package com.undercut.game.interfaces

import com.undercut.script.Script
import com.undercut.script.api.interfaces
import com.undercut.script.api.varcs
import com.undercut.script.api.varps

private const val INSTANCE_SYSTEM_INTERFACE_ID = 1591

private const val INSTANCE_EXPIRY_TIME_ID = 9925
private const val CLIENT_TIMER_TICK_ID = 6930
private const val TICK_SMOOTHING_OFFSET_ID = 6932
private const val PREVIOUS_TICK_POSITION_ID = 6931
private const val TICKS_PER_MINUTE = 3000
private const val TICKS_PER_SECOND = 50
private const val MILLISECONDS_PER_TICK = 20

data class InstanceDetails(
    val name: String,
    val cost: String,
    val maxPlayers: Int,
    val minCombat: Int,
    val spawnSpeed: String,
    val protection: String
)

class InstanceSystem {
    companion object {
        private val instanceCostSlot = IFSlot(INSTANCE_SYSTEM_INTERFACE_ID, 9)
        private val instanceNameSlot = IFSlot(INSTANCE_SYSTEM_INTERFACE_ID, 12)

        private val subtractMaxPlayersSlot = IFSlot(INSTANCE_SYSTEM_INTERFACE_ID, 24)
        private val addMaxPlayersSlot = IFSlot(INSTANCE_SYSTEM_INTERFACE_ID, 25)
        private val valueMaxPlayersSlot = IFSlot(INSTANCE_SYSTEM_INTERFACE_ID, 75)

        private val subtractMinCombatSlot = IFSlot(INSTANCE_SYSTEM_INTERFACE_ID, 28)
        private val addMinCombatSlot = IFSlot(INSTANCE_SYSTEM_INTERFACE_ID, 29)
        private val valueMinCombatSlot = IFSlot(INSTANCE_SYSTEM_INTERFACE_ID, 84)

        private val cycleLeftSpawnSpeedSlot = IFSlot(INSTANCE_SYSTEM_INTERFACE_ID, 32)
        private val cycleRightSpawnSpeedSlot = IFSlot(INSTANCE_SYSTEM_INTERFACE_ID, 33)
        private val valueSpawnSpeedSlot = IFSlot(INSTANCE_SYSTEM_INTERFACE_ID, 95)

        private val cycleLeftProtectionSlot = IFSlot(INSTANCE_SYSTEM_INTERFACE_ID, 36)
        private val cycleRightProtectionSlot = IFSlot(INSTANCE_SYSTEM_INTERFACE_ID, 37)
        private val valueProtectionSlot = IFSlot(INSTANCE_SYSTEM_INTERFACE_ID, 103)

        private val startInstanceSlot = IFSlot(INSTANCE_SYSTEM_INTERFACE_ID, 60)
        private val joinInstanceSlot = IFSlot(INSTANCE_SYSTEM_INTERFACE_ID, 108)
        private val rejoinInstanceSlot = IFSlot(INSTANCE_SYSTEM_INTERFACE_ID, 122)

        fun isOpen() = interfaces.isOpen(INSTANCE_SYSTEM_INTERFACE_ID)

        private fun getComponentText(slot: IFSlot): String =
            interfaces[slot.interfaceId]?.get(slot.componentId)?.text ?: ""

        private fun getComponentInt(slot: IFSlot, default: Int = 0): Int =
            getComponentText(slot).toIntOrNull() ?: default

        val instanceDetails: InstanceDetails?
            get() = if (!isOpen()) null else InstanceDetails(
                name = getComponentText(instanceNameSlot),
                cost = getComponentText(instanceCostSlot),
                maxPlayers = getComponentInt(valueMaxPlayersSlot, 1),
                minCombat = getComponentInt(valueMinCombatSlot, 3),
                spawnSpeed = getComponentText(valueSpawnSpeedSlot),
                protection = getComponentText(valueProtectionSlot)
            )

        fun setMaxPlayers(increase: Boolean) {
            val slot = if (increase) addMaxPlayersSlot else subtractMaxPlayersSlot
            slot.click(1)
        }

        fun setMinCombat(increase: Boolean) {
            val slot = if (increase) addMinCombatSlot else subtractMinCombatSlot
            slot.click(1)
        }

        fun cycleSpawnSpeed(cycleRight: Boolean) {
            val slot = if (cycleRight) cycleRightSpawnSpeedSlot else cycleLeftSpawnSpeedSlot
            slot.click(1)
        }

        fun cycleProtection(cycleRight: Boolean) {
            val slot = if (cycleRight) cycleRightProtectionSlot else cycleLeftProtectionSlot
            slot.click(1)
        }

        fun startInstance() = startInstanceSlot.click(1)

        fun rejoinInstance() = rejoinInstanceSlot.click(1)


        suspend fun Script.joinInstance() {
            joinInstanceSlot.click(1)
            delay(600, 100)
            IFSlot(1469, 1, 0).click() //last entered player name click?
        }

        fun debugComponents() {
            println("=== Instance Interface Debug ===")
            println("Is interface open: ${isOpen()}")

            if (!isOpen()) {
                println("Instance interface is not open")
                return
            }

            println("\n=== Parsed Instance Details ===")
            instanceDetails?.let { details ->
                println("Instance Name: ${details.name}")
                println("Instance Cost: ${details.cost}")
                println("Max Players: ${details.maxPlayers}")
                println("Min Combat: ${details.minCombat}")
                println("Spawn Speed: ${details.spawnSpeed}")
                println("Protection: ${details.protection}")
            } ?: println("No instance details available")

            println("===========================")
        }

        fun getTimeRemainingMs(): Long {
            val instanceExpiryTimeMinutes = varps.getVar(INSTANCE_EXPIRY_TIME_ID)
            val minutesRemaining = (instanceExpiryTimeMinutes - currentTimeMins - 1).toLong()

            if (minutesRemaining < 0) return 0L

            val clientTimerTick = varcs.getVar(CLIENT_TIMER_TICK_ID) % TICKS_PER_MINUTE
            val tickSmoothingOffset = varcs.getVar(TICK_SMOOTHING_OFFSET_ID)
            val previousTickPosition = varcs.getVar(PREVIOUS_TICK_POSITION_ID)

            val adjustedOffset = if (previousTickPosition > 50) {
                val currentTickPosition = (clientTimerTick + tickSmoothingOffset % TICKS_PER_SECOND)
                val positionDiff = (previousTickPosition % TICKS_PER_SECOND) - (currentTickPosition % TICKS_PER_SECOND)
                if (positionDiff != 0) tickSmoothingOffset + positionDiff else tickSmoothingOffset
            } else
                tickSmoothingOffset

            val finalTickPosition = (clientTimerTick + adjustedOffset % TICKS_PER_SECOND)
            val ticksRemaining = TICKS_PER_MINUTE - finalTickPosition
            val totalMilliseconds = minutesRemaining * 60 * 1000 + ticksRemaining * MILLISECONDS_PER_TICK

            return maxOf(0L, totalMilliseconds)
        }

        fun getFormattedTimeRemaining(): String {
            val millisRemaining = getTimeRemainingMs()
            if (millisRemaining == 0L) return "00:00"

            val totalSeconds = millisRemaining / 1000
            val hours = totalSeconds / 3600
            val minutes = (totalSeconds % 3600) / 60
            val seconds = totalSeconds % 60

            return if (hours > 0)
                String.format("%02d:%02d:%02d", hours, minutes, seconds)
            else
                String.format("%02d:%02d", minutes, seconds)
        }

        fun isExpired() = getTimeRemainingMs() == 0L

        private val currentTimeMins get() = (System.currentTimeMillis() / 60000).toInt()
    }
}

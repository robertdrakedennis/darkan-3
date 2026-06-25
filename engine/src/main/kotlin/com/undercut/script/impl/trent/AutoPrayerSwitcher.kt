package com.undercut.script.impl.trent

import com.undercut.game.nxt.entity.HitType
import com.undercut.game.nxt.entity.npc.NPC
import com.undercut.script.BooleanConfigItem
import com.undercut.script.ConfigurableScript
import com.undercut.script.Script
import com.undercut.script.ScriptDescription
import com.undercut.script.api.*
import com.undercut.script.event.impl.Hitsplat
import com.undercut.util.gaussian

enum class PrayerConstraint(val prayer: Prayer, val soulSplitAfterHit: Boolean, val condition: AutoPrayerSwitcher.() -> Boolean) {
    RAX_MAGIC(Prayer.PROTECT_MAGIC, false, {
        projectiles.any {  it.id == 4979 && it.lockedOnto(localPlayer) && it.tile.getDistance(localPlayer.tile) <= 2 }
    }),

    RAX_RANGE(Prayer.PROTECT_RANGED, false, {
        projectiles.any { it.id == 4997 && it.lockedOnto(localPlayer) && it.tile.getDistance(localPlayer.tile) <= 2 }
    }),

    SANCTUM_MAGE(Prayer.PROTECT_MAGIC, true, {
        projectiles.any { it.id == 8182 && it.lockedOnto(localPlayer) && it.tile.getDistance(localPlayer.tile) <= 5 }
    }),

    SANCTUM_RANGE(Prayer.PROTECT_RANGED, true, {
        projectiles.any { it.id == 8185 && it.lockedOnto(localPlayer) && it.tile.getDistance(localPlayer.tile) <= 5 }
    }),

    ARCH_GLACOR_MELEE(Prayer.PROTECT_MELEE, true, melee@{
        val anim = boss?.animationId ?: return@melee false
        return@melee anim == 34276 || anim == 34277
    }),

    ARCH_GLACOR_RANGE(Prayer.PROTECT_RANGED, true, range@{
        val anim = boss?.animationId ?: return@range false
        return@range anim == 34274 || anim == 34275
    }),

    ARCH_GLACOR_MAGIC(Prayer.PROTECT_MAGIC, true, mage@{
        val anim = boss?.animationId ?: return@mage false
        return@mage anim == 34272 || anim == 34273 || anim == 34278 || anim == 34282
    }),

    KK_MELEE(Prayer.PROTECT_MELEE, true, melee@{
        val id = boss?.id ?: return@melee false
        return@melee id == 16697 && boss?.interactingWith(localPlayer) == true
    }),

    KK_RANGE(Prayer.PROTECT_RANGED, true, range@{
        val id = boss?.id ?: return@range false
        return@range id == 16699
    }),

    KK_MAGIC(Prayer.PROTECT_MAGIC, true, mage@{
        val id = boss?.id ?: return@mage false
        return@mage id == 16698 || (id == 16697 && boss?.interactingWith(localPlayer) == false)
    }),

    RAKSHA_MELEE(Prayer.PROTECT_MELEE, true, melee@{
        val anim = boss?.animation ?: return@melee false
        return@melee anim.id == 33702 && anim.currentFrame < 30
    }),

    RAKSHA_RANGE(Prayer.PROTECT_RANGED, true, range@{
        projectiles.any {  it.id == 7392 && it.lockedOnto(localPlayer) && it.tile.getDistance(localPlayer.tile) <= 2 }
    }),

    RAKSHA_MAGIC(Prayer.PROTECT_MAGIC, true, mage@{
        projectiles.any {  it.id == 7390 && it.lockedOnto(localPlayer) && it.tile.getDistance(localPlayer.tile) <= 2 }
    }),

    ;

    suspend fun check(context: AutoPrayerSwitcher): Boolean {
        var actualPrayer = prayer
        if (onCursesPrayers) {
            when(prayer) {
                Prayer.PROTECT_MELEE -> actualPrayer = Prayer.DEFLECT_MELEE
                Prayer.PROTECT_MAGIC -> actualPrayer = Prayer.DEFLECT_MAGIC
                Prayer.PROTECT_RANGED -> actualPrayer = Prayer.DEFLECT_RANGE
                Prayer.PROTECT_NECROMANCY -> actualPrayer = Prayer.DEFLECT_NECROMANCY
                Prayer.ECLIPSED_SOUL -> actualPrayer = Prayer.SOUL_SPLIT
                else -> prayer
            }
        }
        if (!actualPrayer.active && context.condition() && actualPrayer.click()) {
            if (soulSplitAfterHit && context.ssFlick.value) {
                val hitType = when(prayer) {
                    Prayer.PROTECT_MELEE -> HitType.MELEE
                    Prayer.PROTECT_MAGIC -> HitType.MAGIC
                    Prayer.PROTECT_RANGED -> HitType.RANGED
                    else -> HitType.NECROMANCY
                }
                context.waitForEvent(gaussian(2500L, 1210L)) { it is Hitsplat && (it.type == hitType || it.type == HitType.MISS) }
                context.delay(gaussian(210, 110))
                val ss = if (onCursesPrayers) Prayer.SOUL_SPLIT else Prayer.ECLIPSED_SOUL
                if (ss.click())
                    context.delayUntil(gaussian(1059L, 200L)) { ss.active }
            } else
                context.delayUntil(gaussian(1059L, 200L)) { actualPrayer.active }
            return true
        }
        return false
    }
}

@ScriptDescription(
    name = "Auto Prayer Switcher",
    version = "1.0.0",
    author = "Trent",
    description = "Auto switches prayers at various bosses"
)
class AutoPrayerSwitcher : Script(), ConfigurableScript {
    val ssFlick = BooleanConfigItem(name = "Soul split flick", description = "Should we soul split flick?", initialValue = false)

    var boss: NPC? = null

    override suspend fun loop() {
        if (prayerPoints <= 0) return
        if (boss == null || boss?.exists() == false)
            boss = findClosestNPC("Arch-Glacor")
        if (boss == null || boss?.exists() == false)
            boss = findClosestNPC("Raksha, the Shadow Colossus")
        if (boss == null || boss?.exists() == false)
            boss = findClosestNPC("Kalphite King")
        PrayerConstraint.entries.firstOrNull { it.check(this) }
        delay(10)
    }
}
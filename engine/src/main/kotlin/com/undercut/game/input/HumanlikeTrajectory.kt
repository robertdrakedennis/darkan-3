package com.undercut.game.input

import java.util.concurrent.ThreadLocalRandom
import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.pow

/**
 * Minimum-jerk reaching trajectory with perpendicular tremor. Matches the
 * humanizer in `training/tools/visualize_model.py` so server-bound trails
 * generated in-engine look the same as the offline-validated ones.
 *
 * Caller supplies a (start, end, duration). Sample with [sampleAt] using a
 * normalized progress τ in [0, 1] — the curve passes through start at τ=0 and
 * end at τ=1, with bell-shaped velocity in between (Flash & Hogan 1985).
 * Tremor is added perpendicular to the motion direction, peaking mid-stroke.
 */
class HumanlikeTrajectory(
    val startX: Float,
    val startY: Float,
    val endX: Float,
    val endY: Float,
    val startTimeNanos: Long,
    val durationNanos: Long,
    val tremorPx: Float,
) {
    val distance: Float = hypot((endX - startX).toDouble(), (endY - startY).toDouble()).toFloat()
    val endTimeNanos: Long = startTimeNanos + durationNanos

    private val tx: Float
    private val ty: Float

    init {
        if (distance > 1f) {
            tx = (endX - startX) / distance
            ty = (endY - startY) / distance
        } else {
            tx = 0f
            ty = 0f
        }
    }

    fun progressAt(nowNanos: Long): Float {
        if (durationNanos <= 0L) return 1f
        val elapsed = nowNanos - startTimeNanos
        return when {
            elapsed <= 0L -> 0f
            elapsed >= durationNanos -> 1f
            else -> elapsed.toFloat() / durationNanos.toFloat()
        }
    }

    fun sampleAt(nowNanos: Long): Pair<Float, Float> = sampleAtTau(progressAt(nowNanos).toDouble())

    fun sampleAtTau(tau: Double): Pair<Float, Float> {
        val tauClamped = tau.coerceIn(0.0, 1.0)
        val f = 10.0 * tauClamped.pow(3) - 15.0 * tauClamped.pow(4) + 6.0 * tauClamped.pow(5)
        val baseX = (startX + (endX - startX) * f).toFloat()
        val baseY = (startY + (endY - startY) * f).toFloat()
        if (tremorPx <= 0f || distance < 1f) return baseX to baseY

        val perpX = -ty
        val perpY = tx
        val envelope = (1.0 - abs(2 * tauClamped - 1.0)).toFloat()
        val offset = (ThreadLocalRandom.current().nextGaussian() * tremorPx * envelope).toFloat()
        return (baseX + perpX * offset) to (baseY + perpY * offset)
    }

    fun isExpired(nowNanos: Long): Boolean = nowNanos >= endTimeNanos

    companion object {
        /** Distance-scaled movement duration. ~80 ms reaction floor + dist / peak speed. */
        fun durationForDistanceNanos(distance: Float, peakPxPerSecond: Float = 1400f): Long {
            val seconds = 0.08 + max(0f, distance) / max(1f, peakPxPerSecond)
            return (seconds * 1_000_000_000.0).toLong()
        }
    }
}

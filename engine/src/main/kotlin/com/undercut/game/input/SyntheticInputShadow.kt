package com.undercut.game.input

import world.gregs.voidps.type.Tile
import com.undercut.game.bootstrap.Bootstrap
import com.undercut.game.math.WorldToScreen
import com.undercut.game.nxt.Client
import com.undercut.game.nxt.DoActionOpcode
import com.undercut.game.nxt.MainState
import com.undercut.profiling.PlayerProfile
import com.undercut.profiling.PlayerProfiles
import com.undercut.script.api.localPlayer
import com.undercut.ui.backend.native.NativeBridge
import java.io.File
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.hypot
import kotlin.math.max

/**
 * Generates a humanlike server-bound mouse trail by sampling the MDN position
 * head ([InputModel]). Mirrors the offline-validated visualizer logic:
 *
 *  - σ scale (0.3) and component temperature (0.5) tighten each step to the
 *    learned dense modes — no fat-tail teleports.
 *  - Single-step delta clamped to 0.06 normalized (~115 px X).
 *  - Cursor position anchored to the training-data centroid with a mild
 *    target-bias so it doesn't drift to a screen corner over time. DoAction
 *    intents bump the bias temporarily.
 *
 * Critical invariants — DO NOT VIOLATE:
 *  - No call to [InputInjector]. Real cursor never touched.
 *  - No push to [MouseEventBuffer] (game's local processor consumes it →
 *    triggers real clicks). The buffer's [MouseEventBuffer.push] is also
 *    hard-disabled, but we don't even call it.
 *  - Real player input never feeds into the model's window
 *    ([observeEvent] is a no-op while synth is enabled).
 *
 * Output is purely the trail buffer that [SyntheticInputOverlayRenderer] paints —
 * the "what we would send" preview. Server-bound injection awaits a safe write
 * path ([feedback_mouse_buffer_cascade]).
 */
object SyntheticInputShadow {
    private const val SLIDING_WINDOW_SIZE = 64
    private const val TRAIL_CAPACITY = 256
    private const val DEFAULT_SCREEN_X = 960
    private const val DEFAULT_SCREEN_Y = 540

    // All trajectory / pause parameters come from PlayerProfile (randomized per
    // account) so each user has unique synth fingerprints rather than a shared
    // constant signature. Viewport size is queried from the live window.

    private val recentEvents = ConcurrentLinkedDeque<InputEvent>()
    private val trail = ConcurrentLinkedDeque<TrailPoint>()

    @Volatile private var model: InputModel? = null
    @Volatile var currentModelKey: String = ""
        private set

    @Volatile var entriesGenerated: Long = 0
        private set
    @Volatile var lastConfidence: Float = 0f
        private set
    @Volatile var lastInferenceMillis: Double = 0.0
        private set

    @Volatile var lastSynthX: Float = DEFAULT_SCREEN_X.toFloat()
        private set
    @Volatile var lastSynthY: Float = DEFAULT_SCREEN_Y.toFloat()
        private set

    @Volatile private var currentTrajectory: HumanlikeTrajectory? = null
    @Volatile private var currentTrajectoryIsActionTarget: Boolean = false
    @Volatile private var pendingActionTarget: Pair<Float, Float>? = null
    @Volatile private var prevMotionDirX: Float = 0f
    @Volatile private var prevMotionDirY: Float = 0f
    @Volatile private var hasPrevMotion: Boolean = false
    @Volatile private var pauseUntilNanos: Long = 0L
    @Volatile private var lastTickNanos: Long = 0L

    // Tick-internal sub-sampling: the engine ticks at ~50 ms but real OS mouse
    // events arrive at ~1 ms. To make the trail look realistic we emit several
    // events per tick along the trajectory, spaced ~12 ms apart.
    private const val SUB_EVENT_INTERVAL_MS = 12L

    enum class TrailKind { MOVE, CLICK }
    data class TrailPoint(val x: Float, val y: Float, val timestampNanos: Long, val kind: TrailKind)

    fun trailSnapshot(): List<TrailPoint> = trail.toList()

    private fun recordTrail(x: Float, y: Float, kind: TrailKind) {
        trail.add(TrailPoint(x, y, System.nanoTime(), kind))
        while (trail.size > TRAIL_CAPACITY) trail.poll()
    }

    fun loadModel(path: String) {
        try {
            model?.close()
            model = InputModel(path)
            currentModelKey = File(path).parentFile?.name ?: ""
        } catch (e: Throwable) {
            println("[SyntheticInputShadow] Failed to load model: ${e.message}")
            model = null
            currentModelKey = ""
        }
    }

    fun loadModelForPlayer(playerKey: String) {
        if (playerKey.isBlank()) return
        if (playerKey == currentModelKey && model != null) return
        val modelFile = File(System.getProperty("user.home"), ".undercut/models/$playerKey/input_predictor.onnx")
        if (!modelFile.exists()) {
            println("[SyntheticInputShadow] No model for '$playerKey' at ${modelFile.absolutePath}")
            return
        }
        loadModel(modelFile.absolutePath)
    }

    fun unload() {
        try { model?.close() } catch (_: Throwable) {}
        model = null
        currentModelKey = ""
    }

    fun isModelLoaded(): Boolean = model != null

    /**
     * Public observe: only accepts events when synth is OFF. Real player input
     * must not pollute the autoregressive window while we are generating.
     */
    fun observeEvent(event: InputEvent) {
        try {
            if (PlayerProfiles.get().synthInputEnabled) return
        } catch (_: Throwable) { /* fall through */ }
        observeSynthEvent(event)
    }

    private fun observeSynthEvent(event: InputEvent) {
        recentEvents.add(event)
        while (recentEvents.size > SLIDING_WINDOW_SIZE * 2) {
            recentEvents.poll()
        }
    }

    fun tick() {
        val profile = try { PlayerProfiles.get() } catch (_: Throwable) { return }
        if (!profile.synthInputEnabled) return
        val client = Bootstrap.client
        if (client.mainState != MainState.LOGGED_IN) return

        ensureModelLoaded(profile, client)
        val mdl = model ?: return

        consumeIntents()

        val now = System.nanoTime()
        if (lastTickNanos == 0L) lastTickNanos = now

        // Trajectory finishing: emit a click marker if it was script-targeted,
        // then enter an idle pause (unless an action is already queued).
        var traj = currentTrajectory
        if (traj != null && traj.isExpired(now)) {
            if (currentTrajectoryIsActionTarget) {
                recordTrail(lastSynthX, lastSynthY, TrailKind.CLICK)
                if (profile.synthSendToServer) {
                    MouseClickPacketSender.sendMouseEvent(lastSynthX.toInt(), lastSynthY.toInt(), isClick = true)
                }
            }
            currentTrajectory = null
            currentTrajectoryIsActionTarget = false
            traj = null
            if (pendingActionTarget == null) {
                pauseUntilNanos = now + idlePauseNanos()
            }
        }
        if (traj == null) {
            if (pendingActionTarget == null && now < pauseUntilNanos) {
                lastTickNanos = now
                return  // still paused — silent
            }
            val isActionTarget = pendingActionTarget != null
            traj = startNewTrajectory(mdl, client, now) ?: run {
                pauseUntilNanos = now + (200L * 1_000_000L)
                lastTickNanos = now
                return
            }
            currentTrajectory = traj
            currentTrajectoryIsActionTarget = isActionTarget
        }

        emitSubSamples(traj, client, now)
        // One server-bound motion packet per game tick (matches natural send rate).
        if (profile.synthSendToServer) {
            MouseClickPacketSender.sendMouseEvent(lastSynthX.toInt(), lastSynthY.toInt(), isClick = false)
        }
        lastTickNanos = now
    }

    /**
     * Sample the active trajectory at multiple points covering this tick's wall-clock
     * window so the overlay/server stream shows OS-event-density motion, not one
     * sparse dot per game tick.
     */
    private fun emitSubSamples(traj: HumanlikeTrajectory, client: Client, nowNs: Long) {
        val (vpWidth, vpHeight) = viewportSize()
        val tickStartNs = lastTickNanos
        val intervalNs = SUB_EVENT_INTERVAL_MS * 1_000_000L
        var sampleAt = ((tickStartNs / intervalNs) + 1) * intervalNs
        if (sampleAt < tickStartNs) sampleAt = tickStartNs + intervalNs
        var emitted = false
        while (sampleAt <= nowNs) {
            val (sx, sy) = traj.sampleAt(sampleAt)
            val newX = sx.coerceIn(0f, vpWidth - 1f)
            val newY = sy.coerceIn(0f, vpHeight - 1f)
            recordTrail(newX, newY, TrailKind.MOVE)
            observeSynthEvent(
                MouseMotionEvent(
                    timestampNanos = sampleAt,
                    gameTick = client.clientCycle,
                    x = newX.toInt(),
                    y = newY.toInt(),
                )
            )
            lastSynthX = newX
            lastSynthY = newY
            entriesGenerated++
            emitted = true
            sampleAt += intervalNs
        }
        if (!emitted) {
            // First tick of a trajectory may not cross a sub-event boundary — emit one anyway.
            val (sx, sy) = traj.sampleAt(nowNs)
            val newX = sx.coerceIn(0f, vpWidth - 1f)
            val newY = sy.coerceIn(0f, vpHeight - 1f)
            recordTrail(newX, newY, TrailKind.MOVE)
            lastSynthX = newX
            lastSynthY = newY
            entriesGenerated++
        }
    }

    private fun startNewTrajectory(mdl: InputModel, client: Client, now: Long): HumanlikeTrajectory? {
        val profile = PlayerProfiles.get()
        val (targetX, targetY) = nextTarget(mdl, client, profile) ?: return null
        val startX = lastSynthX
        val startY = lastSynthY
        val dist = hypot((targetX - startX).toDouble(), (targetY - startY).toDouble()).toFloat()
        if (dist < profile.synthMinTravelPx) {
            return null
        }
        // Per-trajectory parameter jitter so the same profile doesn't emit
        // identical-looking motion every time. The randomization range is
        // profile-derived (different per account); only the variance noise
        // here is global.
        val rng = ThreadLocalRandom.current()
        val speedJitter = 1.0f + (rng.nextGaussian().toFloat() * 0.18f).coerceIn(-0.45f, 0.6f)
        val tremorJitter = 1.0f + (rng.nextGaussian().toFloat() * 0.35f).coerceIn(-0.6f, 1.0f)
        val effectiveSpeed = (profile.synthPeakPxPerSecond * speedJitter).coerceIn(450f, 3000f)
        val effectiveTremor = (profile.synthTremorPx * tremorJitter).coerceAtLeast(0f)

        val durationNs = HumanlikeTrajectory.durationForDistanceNanos(dist, effectiveSpeed)
        val minNs = profile.synthMinTrajectoryMs * 1_000_000L
        val effectiveDuration = max(durationNs, minNs)
        val traj = HumanlikeTrajectory(
            startX = startX,
            startY = startY,
            endX = targetX,
            endY = targetY,
            startTimeNanos = now,
            durationNanos = effectiveDuration,
            tremorPx = effectiveTremor,
        )
        val dx = targetX - startX
        val dy = targetY - startY
        val mag = hypot(dx.toDouble(), dy.toDouble()).toFloat()
        if (mag > 1e-3f) {
            prevMotionDirX = dx / mag
            prevMotionDirY = dy / mag
            hasPrevMotion = true
        }
        return traj
    }

    /**
     * Determine the next motion target. Priority:
     *   1. A pending script-action target (script just did a DoAction; we want
     *      the cursor to head to wherever that action was directed).
     *   2. A sample from the model's MDN — turned into an offset from the
     *      current cursor with the profile's amplification so a single sample
     *      reaches a meaningful distance.
     */
    private fun nextTarget(mdl: InputModel, client: Client, profile: PlayerProfile): Pair<Float, Float>? {
        val (vpWidth, vpHeight) = viewportSize()
        val pending = pendingActionTarget
        if (pending != null) {
            pendingActionTarget = null
            return pending.first.coerceIn(0f, vpWidth - 1f) to pending.second.coerceIn(0f, vpHeight - 1f)
        }

        val window = recentEvents.toList().takeLast(SLIDING_WINDOW_SIZE)
        val context = buildGameContext(client)
        val startNs = System.nanoTime()
        val prediction = try {
            mdl.predict(window, context)
        } catch (e: Throwable) {
            e.printStackTrace()
            return null
        }
        lastInferenceMillis = (System.nanoTime() - startNs) / 1_000_000.0
        lastConfidence = prediction.confidence

        val (dxNorm, dyNorm) = mdl.sampleDelta(
            prediction = prediction,
            sigmaScale = 1.0f,
            componentTemperature = 1.0f,
            deltaClipNorm = InputModel.DEFAULT_MAX_DELTA_NORM,
        )
        // Model was trained with NORM_X / NORM_Y normalization; treat dxNorm * NORM_X
        // as a normalized-pixel offset and scale by per-profile amplification before
        // mapping into the live viewport.
        var offsetX = dxNorm * InputModel.NORM_X * profile.synthOffsetAmplification
        var offsetY = dyNorm * InputModel.NORM_Y * profile.synthOffsetAmplification
        // Map model-pixel-space offset into live-viewport-pixel-space.
        offsetX *= (vpWidth / InputModel.NORM_X)
        offsetY *= (vpHeight / InputModel.NORM_Y)
        val offsetMag = hypot(offsetX.toDouble(), offsetY.toDouble()).toFloat()
        val momentum = profile.synthMomentumBlend
        val sampleBlend = 1f - momentum
        if (hasPrevMotion && offsetMag > 1e-3f) {
            val sxUnit = offsetX / offsetMag
            val syUnit = offsetY / offsetMag
            val blendedX = momentum * prevMotionDirX + sampleBlend * sxUnit
            val blendedY = momentum * prevMotionDirY + sampleBlend * syUnit
            val blendedMag = hypot(blendedX.toDouble(), blendedY.toDouble()).toFloat().coerceAtLeast(1e-6f)
            offsetX = (blendedX / blendedMag) * offsetMag
            offsetY = (blendedY / blendedMag) * offsetMag
        }
        val targetX = (lastSynthX + offsetX).coerceIn(0f, vpWidth - 1f)
        val targetY = (lastSynthY + offsetY).coerceIn(0f, vpHeight - 1f)
        return targetX to targetY
    }

    private fun viewportSize(): Pair<Float, Float> {
        return try {
            val (w, h) = NativeBridge.getDisplaySize()
            val wf = w.coerceAtLeast(640f)
            val hf = h.coerceAtLeast(480f)
            wf to hf
        } catch (_: Throwable) {
            InputModel.NORM_X to InputModel.NORM_Y
        }
    }

    private fun consumeIntents() {
        var intent: ShadowIntent? = ShadowInputBus.poll()
        while (intent != null) {
            if (intent is DoActionShadow) {
                val tx = intent.resolvedTargetX
                val ty = intent.resolvedTargetY
                if (tx != null && ty != null) {
                    pendingActionTarget = tx to ty
                    currentTrajectory = null
                    pauseUntilNanos = 0L
                }
                // If the intent didn't carry a resolved target (opcode unsupported
                // or resolution failed at publish time) we just ignore it — the
                // model keeps doing whatever it was doing.
            }
            intent = ShadowInputBus.poll()
        }
    }

    private fun idlePauseNanos(): Long {
        val profile = PlayerProfiles.get()
        val lo = profile.synthIdlePauseMinMs.coerceAtLeast(50L)
        val hi = profile.synthIdlePauseMaxMs.coerceAtLeast(lo + 1L)
        val ms = ThreadLocalRandom.current().nextLong(lo, hi)
        return ms * 1_000_000L
    }

    private fun resolveActionTarget(intent: DoActionShadow): Pair<Float, Float>? {
        // Only resolve from action params via pure-math projection. NPC / loc /
        // component pointers can be freed between the script's fire() and our
        // tick processing the intent, and a native deref of freed memory takes
        // the whole JVM down — Kotlin try/catch can't recover from SIGSEGV.
        // For NPC and other entity-based opcodes, fall through to null so the
        // model picks its own target naturally instead of crashing.
        return try {
            when (intent.opcode) {
                DoActionOpcode.WALK,
                DoActionOpcode.SELECT_TILE -> projectTile(intent.param2, intent.param3)
                else -> null
            }
        } catch (_: Throwable) { null }
    }

    private fun projectTile(tileX: Int, tileY: Int): Pair<Float, Float>? {
        val plane = try { localPlayer.tile.plane.toInt() } catch (_: Throwable) { 0 }
        val v = WorldToScreen.getEstimatedTileCenter(Tile.of(tileX, tileY, plane)) ?: return null
        return v.x to v.y
    }

    private fun ensureModelLoaded(profile: PlayerProfile, client: Client) {
        val desired = profile.synthModelPlayer.ifBlank {
            try { client.loggedInPlayer.getPlayerName() ?: "" } catch (_: Throwable) { "" }
        }
        if (desired.isBlank()) return
        if (desired != currentModelKey || model == null) {
            loadModelForPlayer(desired)
        }
    }

    private fun buildGameContext(client: Client): GameContext {
        return try {
            val tile = localPlayer.tile
            GameContext(
                gameTick = client.clientCycle,
                mainState = client.mainState,
                playerX = tile.x.toInt(),
                playerY = tile.y.toInt(),
                playerPlane = tile.plane.toInt(),
                cameraYaw = 0f,
                cameraPitch = 0f,
                timestampNanos = System.nanoTime()
            )
        } catch (_: Throwable) {
            GameContext(
                gameTick = client.clientCycle,
                mainState = client.mainState,
                playerX = 0, playerY = 0, playerPlane = 0,
                cameraYaw = 0f, cameraPitch = 0f,
                timestampNanos = System.nanoTime()
            )
        }
    }
}

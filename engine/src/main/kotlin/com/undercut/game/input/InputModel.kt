package com.undercut.game.input

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import java.nio.FloatBuffer
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.exp
import kotlin.math.hypot
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min

/**
 * ONNX wrapper for the trained input-prediction model.
 *
 * Outputs (from `training/model.py`):
 *  - action_logits         (B, 7)      — categorical over event type, only 0..3 trained
 *  - timing_params         (B, 1)      — predicted Δt to next event (seconds)
 *  - position_log_weights  (B, K)      — log-mixture-weights of Δ position MDN (K = 5)
 *  - position_means        (B, K, 2)   — mixture component means in normalized Δ space
 *  - position_log_stds     (B, K, 2)   — mixture component log-stddevs
 *  - key_id                (B, 1)      — normalized low-byte of next key code
 *
 * The position head is a Gaussian Mixture over normalized (Δx, Δy). To get a
 * concrete pixel-delta for one inference step, call [sampleDelta] with the
 * mirrored knobs from the visualizer (sigmaScale, componentTemperature, clamp).
 * Caller owns the absolute cursor position and advances it by the sampled
 * delta; the model never sees / cares about absolute pixel coords.
 */
class InputModel(private val modelPath: String) {

    companion object {
        const val WINDOW_SIZE = 64
        const val FEATURES = 12
        const val CONTEXT_DIM = 8
        const val NUM_MIXTURES = 5
        const val NUM_ACTIONS = 7

        const val NORM_X = 1920f
        const val NORM_Y = 1080f
        private const val NORM_SCROLL = 120f
        private const val NORM_KEY = 256f
        private const val NORM_TICK = 1_000_000f
        private const val NORM_TILE = 16384f
        private const val NORM_MAIN_STATE = 40f

        const val DEFAULT_MAX_DELTA_NORM = 0.06f
        const val DEFAULT_SIGMA_SCALE = 0.3f
        const val DEFAULT_COMPONENT_TEMPERATURE = 0.5f
    }

    private val env: OrtEnvironment = OrtEnvironment.getEnvironment()
    private val session: OrtSession

    init {
        println("[InputModel] Loading model from: $modelPath")
        val options = OrtSession.SessionOptions()
        session = env.createSession(modelPath, options)
        println("[InputModel] Model loaded. Inputs=${session.inputNames}, outputs=${session.outputNames}")
    }

    @Synchronized
    fun predict(recentEvents: List<InputEvent>, context: GameContext): ModelPrediction {
        val flat = FloatArray(WINDOW_SIZE * FEATURES)
        encodeWindow(recentEvents, context, flat)

        OnnxTensor.createTensor(
            env,
            FloatBuffer.wrap(flat),
            longArrayOf(1, WINDOW_SIZE.toLong(), FEATURES.toLong())
        ).use { eventTensor ->
            val contextFeatures = FloatArray(CONTEXT_DIM)
            extractContext(flat, contextFeatures)

            OnnxTensor.createTensor(
                env,
                FloatBuffer.wrap(contextFeatures),
                longArrayOf(1, CONTEXT_DIM.toLong())
            ).use { contextTensor ->
                val inputs = mapOf(
                    "event_sequence" to eventTensor,
                    "context" to contextTensor
                )
                session.run(inputs).use { result ->
                    @Suppress("UNCHECKED_CAST")
                    val actionLogits = (result.get("action_logits").get().value as Array<FloatArray>)[0]
                    @Suppress("UNCHECKED_CAST")
                    val timingParams = (result.get("timing_params").get().value as Array<FloatArray>)[0]
                    @Suppress("UNCHECKED_CAST")
                    val logWeights = (result.get("position_log_weights").get().value as Array<FloatArray>)[0]
                    @Suppress("UNCHECKED_CAST")
                    val meansArr = (result.get("position_means").get().value as Array<Array<FloatArray>>)[0]
                    @Suppress("UNCHECKED_CAST")
                    val logStdsArr = (result.get("position_log_stds").get().value as Array<Array<FloatArray>>)[0]
                    @Suppress("UNCHECKED_CAST")
                    val keyParams = (result.get("key_id").get().value as Array<FloatArray>)[0]
                    return decode(actionLogits, timingParams, logWeights, meansArr, logStdsArr, keyParams)
                }
            }
        }
    }

    /**
     * Draw a single (Δx, Δy) in NORMALIZED screen coordinates from the MDN.
     *
     * @param targetDirNorm  optional unit vector toward a desired target in normalized
     *                       delta space; if provided AND targetBias>0, candidate deltas
     *                       are scored by alignment with this direction.
     */
    fun sampleDelta(
        prediction: ModelPrediction,
        sigmaScale: Float = DEFAULT_SIGMA_SCALE,
        componentTemperature: Float = DEFAULT_COMPONENT_TEMPERATURE,
        deltaClipNorm: Float = DEFAULT_MAX_DELTA_NORM,
        targetDirNorm: Pair<Float, Float>? = null,
        targetBias: Float = 0f,
        candidateCount: Int = 16,
    ): Pair<Float, Float> {
        val weights = FloatArray(NUM_MIXTURES) { exp(prediction.positionLogWeights[it]) }
        val weightsSum = weights.sum().coerceAtLeast(1e-12f)
        for (i in weights.indices) weights[i] = weights[i] / weightsSum

        if (targetDirNorm == null || targetBias <= 1e-3f) {
            val k = pickComponent(weights, componentTemperature)
            return sampleAndClamp(prediction, k, sigmaScale, deltaClipNorm)
        }

        val (tdx, tdy) = targetDirNorm
        var bestDx = 0f
        var bestDy = 0f
        var bestScore = -Float.MAX_VALUE
        val scores = FloatArray(candidateCount)
        val cands = Array(candidateCount) { sampleAndClamp(prediction, pickComponent(weights, componentTemperature), sigmaScale, deltaClipNorm) }
        var maxScore = -Float.MAX_VALUE
        for (i in 0 until candidateCount) {
            val (dx, dy) = cands[i]
            val mag = hypot(dx.toDouble(), dy.toDouble()).toFloat()
            val score = if (mag < 1e-6f) -1e6f else ((dx / mag) * tdx + (dy / mag) * tdy) * mag
            scores[i] = score
            if (score > maxScore) maxScore = score
        }
        val temperature = max(0.01f, 1f - targetBias)
        var sumExp = 0f
        for (i in 0 until candidateCount) {
            val e = exp(((scores[i] - maxScore) / temperature).toDouble()).toFloat()
            scores[i] = e
            sumExp += e
        }
        val target = ThreadLocalRandom.current().nextFloat() * sumExp
        var acc = 0f
        var chosen = 0
        for (i in 0 until candidateCount) {
            acc += scores[i]
            if (acc >= target) {
                chosen = i
                break
            }
        }
        bestDx = cands[chosen].first
        bestDy = cands[chosen].second
        return bestDx to bestDy
    }

    private fun pickComponent(weights: FloatArray, componentTemperature: Float): Int {
        if (componentTemperature <= 1e-3f) {
            var best = 0
            var bestW = weights[0]
            for (i in 1 until weights.size) if (weights[i] > bestW) { bestW = weights[i]; best = i }
            return best
        }
        val temped = FloatArray(weights.size)
        var sum = 0f
        for (i in weights.indices) {
            temped[i] = exp(ln(weights[i].coerceAtLeast(1e-12f)) / componentTemperature)
            sum += temped[i]
        }
        val target = ThreadLocalRandom.current().nextFloat() * sum
        var acc = 0f
        for (i in temped.indices) {
            acc += temped[i]
            if (acc >= target) return i
        }
        return temped.size - 1
    }

    private fun sampleAndClamp(
        prediction: ModelPrediction,
        k: Int,
        sigmaScale: Float,
        clip: Float,
    ): Pair<Float, Float> {
        val muX = prediction.positionMeans[k * 2]
        val muY = prediction.positionMeans[k * 2 + 1]
        val sigX = exp(prediction.positionLogStds[k * 2]) * sigmaScale
        val sigY = exp(prediction.positionLogStds[k * 2 + 1]) * sigmaScale
        val rng = ThreadLocalRandom.current()
        var dx = muX + sigX * rng.nextGaussian().toFloat()
        var dy = muY + sigY * rng.nextGaussian().toFloat()
        val mag = hypot(dx.toDouble(), dy.toDouble()).toFloat()
        if (mag > clip) {
            val s = clip / mag
            dx *= s
            dy *= s
        }
        return dx to dy
    }

    fun close() {
        try {
            session.close()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        println("[InputModel] Model closed")
    }

    private fun encodeWindow(recentEvents: List<InputEvent>, context: GameContext, out: FloatArray) {
        val window = recentEvents.takeLast(WINDOW_SIZE)
        val pad = WINDOW_SIZE - window.size
        var previousTimestampNanos = if (window.isNotEmpty()) window.first().timestampNanos else context.timestampNanos

        for (i in window.indices) {
            val event = window[i]
            val base = (pad + i) * FEATURES
            val deltaUs = ((event.timestampNanos - previousTimestampNanos) / 1_000L).coerceAtLeast(0)
            previousTimestampNanos = event.timestampNanos
            encodeEvent(event, deltaUs, context, out, base)
        }
    }

    private fun encodeEvent(
        event: InputEvent,
        deltaUs: Long,
        context: GameContext,
        out: FloatArray,
        base: Int
    ) {
        out[base + 1] = deltaUs / 1e6f
        out[base + 8] = event.gameTick / NORM_TICK
        out[base + 9] = context.playerX / NORM_TILE
        out[base + 10] = context.playerY / NORM_TILE
        out[base + 11] = context.mainState / NORM_MAIN_STATE

        when (event) {
            is MouseMotionEvent -> {
                out[base + 0] = 0f
                out[base + 2] = event.x / NORM_X
                out[base + 3] = event.y / NORM_Y
            }
            is MouseButtonEvent -> {
                out[base + 0] = 1f
                out[base + 2] = event.x / NORM_X
                out[base + 3] = event.y / NORM_Y
                out[base + 4] = event.button.id.toFloat()
                out[base + 5] = if (event.pressed) 1f else 0f
            }
            is MouseScrollEvent -> {
                out[base + 0] = 2f
                out[base + 2] = event.x / NORM_X
                out[base + 3] = event.y / NORM_Y
                out[base + 6] = event.scrollDelta / NORM_SCROLL
            }
            is KeyboardEvent -> {
                out[base + 0] = 3f
                out[base + 5] = if (event.pressed) 1f else 0f
                out[base + 7] = (event.keyCode and 0xFF) / NORM_KEY
            }
        }
    }

    private fun extractContext(flat: FloatArray, out: FloatArray) {
        val lastEventBase = (WINDOW_SIZE - 1) * FEATURES
        for (i in 0 until CONTEXT_DIM) {
            out[i] = flat[lastEventBase + 4 + i]
        }
    }

    private fun decode(
        actionLogits: FloatArray,
        timingParams: FloatArray,
        logWeights: FloatArray,
        meansArr: Array<FloatArray>,
        logStdsArr: Array<FloatArray>,
        keyParams: FloatArray
    ): ModelPrediction {
        var bestIdx = 0
        var bestVal = actionLogits[0]
        var sumExp = 0f
        val maxLogit = actionLogits.max()
        for (i in actionLogits.indices) {
            val e = exp((actionLogits[i] - maxLogit).toDouble()).toFloat()
            sumExp += e
            if (actionLogits[i] > bestVal) {
                bestVal = actionLogits[i]
                bestIdx = i
            }
        }
        val confidence = (exp((bestVal - maxLogit).toDouble()).toFloat() / sumExp).coerceIn(0f, 1f)

        val timingSeconds = timingParams[0].coerceAtLeast(0f)
        val deltaTimeMs = (timingSeconds * 1000f).toLong().coerceIn(0L, 5_000L)

        val flatMeans = FloatArray(NUM_MIXTURES * 2)
        val flatLogStds = FloatArray(NUM_MIXTURES * 2)
        for (k in 0 until NUM_MIXTURES) {
            flatMeans[k * 2] = meansArr[k][0]
            flatMeans[k * 2 + 1] = meansArr[k][1]
            flatLogStds[k * 2] = logStdsArr[k][0]
            flatLogStds[k * 2 + 1] = logStdsArr[k][1]
        }

        val keyId = (keyParams[0] * NORM_KEY).toInt()

        return ModelPrediction(
            actionType = ActionType.entries[bestIdx.coerceIn(0, ActionType.entries.size - 1)],
            deltaTimeMs = deltaTimeMs,
            keyId = keyId,
            confidence = confidence,
            positionLogWeights = logWeights,
            positionMeans = flatMeans,
            positionLogStds = flatLogStds,
        )
    }
}

data class ModelPrediction(
    val actionType: ActionType,
    val deltaTimeMs: Long,
    val keyId: Int,
    val confidence: Float,
    val positionLogWeights: FloatArray,
    val positionMeans: FloatArray,
    val positionLogStds: FloatArray,
)

enum class ActionType {
    MOUSE_MOVE,
    LEFT_CLICK,
    RIGHT_CLICK,
    SCROLL,
    KEY_DOWN,
    KEY_UP,
    IDLE
}

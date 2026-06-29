package org.darkan.core.net.recorder

/**
 * Provenance tier for recorder-derived opcode knowledge. Higher tiers dominate lower tiers.
 */
enum class Confidence(val rank: Int) {
    CLIENT_VERIFIED(3),
    BINARY_PROVEN(2),
    CAPTURE_OBSERVED(1),
    HYPOTHESIS(0),
}

object ConfidenceAssigner {
    fun assign(
        clientVerified: Boolean,
        binaryProven: Boolean,
        captureObserved: Boolean,
    ): Confidence = when {
        clientVerified -> Confidence.CLIENT_VERIFIED
        binaryProven -> Confidence.BINARY_PROVEN
        captureObserved -> Confidence.CAPTURE_OBSERVED
        else -> Confidence.HYPOTHESIS
    }
}

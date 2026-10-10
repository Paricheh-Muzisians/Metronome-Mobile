package com.paricheh.metronome.core.audio

/**
 * Represents a single frame of audio data.
 *
 * @property samples The normalized audio samples (ranging from -1.0 to 1.0).
 * @property sampleRate The sample rate of the audio data in Hz.
 * @property timestamp The timestamp when the frame was captured in milliseconds.
 */
data class AudioFrame(
    val samples: FloatArray,
    val sampleRate: Int,
    val timestamp: Long
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AudioFrame) return false

        if (!samples.contentEquals(other.samples)) return false
        if (sampleRate != other.sampleRate) return false
        if (timestamp != other.timestamp) return false

        return true
    }

    override fun hashCode(): Int {
        var result = samples.contentHashCode()
        result = 31 * result + sampleRate
        result = 31 * result + timestamp.hashCode()
        return result
    }
}

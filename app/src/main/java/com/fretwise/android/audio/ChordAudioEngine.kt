package com.fretwise.android.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.fretwise.android.data.model.ChordFrequencies
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.floor
import kotlin.math.pow
import kotlin.random.Random
import javax.inject.Inject
import javax.inject.Singleton

private const val SAMPLE_RATE = 44_100
private const val SUSTAIN_SEC = 2.2
private const val STRUM_STEP_SEC = 0.014
private const val ATTACK_SEC = 0.008
private const val DECAY_END_SEC = 0.18
private const val DECAY_TARGET_RATIO = 0.35
private const val FILTER_CUTOFF_HZ = 2800.0
private const val MASTER_START_GAIN = 0.22
private const val MASTER_END_GAIN = 0.001

private data class OscLayer(val waveform: Waveform, val gain: Double)
private enum class Waveform { SAWTOOTH, TRIANGLE }

/**
 * Synthesizes and plays chord strums on-device (no audio assets), replicating the
 * Web Audio graph used in the reference web app: per-string saw+triangle oscillators
 * with a strum offset, a one-pole lowpass "body" filter, a short attack/decay envelope
 * per note, and an overall exponentially-decaying master envelope.
 */
@Singleton
class ChordAudioEngine @Inject constructor() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val layers = listOf(OscLayer(Waveform.SAWTOOTH, 0.6), OscLayer(Waveform.TRIANGLE, 0.4))

    fun playChord(chordId: String) {
        val freqs = ChordFrequencies.forChord(chordId) ?: return
        scope.launch {
            val samples = synthesize(freqs)
            playPcm(samples)
        }
    }

    private fun synthesize(freqs: List<Double>): ShortArray {
        val lastOffset = (freqs.size - 1) * STRUM_STEP_SEC
        val totalSeconds = SUSTAIN_SEC + lastOffset
        val totalSamples = (totalSeconds * SAMPLE_RATE).toInt()
        val out = FloatArray(totalSamples)

        // One lowpass filter state + a small random detune per (string, oscillator layer).
        val filters = Array(freqs.size) { Array(layers.size) { OnePoleLowpass(FILTER_CUTOFF_HZ, SAMPLE_RATE) } }
        val detuneRatios = Array(freqs.size) {
            DoubleArray(layers.size) { centsToRatio((Random.nextDouble() - 0.5) * 6.0) }
        }

        for (n in 0 until totalSamples) {
            val t = n.toDouble() / SAMPLE_RATE
            var mix = 0.0

            for (stringIdx in freqs.indices) {
                val strumOffset = stringIdx * STRUM_STEP_SEC
                val tRel = t - strumOffset
                if (tRel < 0.0 || tRel > SUSTAIN_SEC) continue

                val baseFreq = freqs[stringIdx]
                for (layerIdx in layers.indices) {
                    val layer = layers[layerIdx]
                    val freq = baseFreq * detuneRatios[stringIdx][layerIdx]
                    val raw = waveformSample(layer.waveform, freq, tRel)
                    val filtered = filters[stringIdx][layerIdx].process(raw)
                    val perStringGain = layer.gain / freqs.size
                    val env = noteEnvelope(tRel, perStringGain)
                    mix += filtered * env
                }
            }

            val masterEnv = masterEnvelope(t)
            out[n] = (mix * masterEnv).toFloat()
        }

        return floatsToPcm16(out)
    }

    private fun waveformSample(waveform: Waveform, freq: Double, tRel: Double): Double {
        val phase = freq * tRel
        val frac = phase - floor(phase)
        return when (waveform) {
            Waveform.SAWTOOTH -> 2.0 * frac - 1.0
            Waveform.TRIANGLE -> if (frac < 0.5) 4.0 * frac - 1.0 else 3.0 - 4.0 * frac
        }
    }

    /** Linear attack to peak, exponential decay to a sustain ratio, then holds (matches Web Audio automation). */
    private fun noteEnvelope(tRel: Double, peak: Double): Double {
        val sustainLevel = peak * DECAY_TARGET_RATIO
        return when {
            tRel < ATTACK_SEC -> peak * (tRel / ATTACK_SEC)
            tRel < DECAY_END_SEC -> {
                val progress = (tRel - ATTACK_SEC) / (DECAY_END_SEC - ATTACK_SEC)
                peak * (sustainLevel / peak).pow(progress)
            }
            else -> sustainLevel
        }
    }

    /** Exponential decay from [MASTER_START_GAIN] to [MASTER_END_GAIN] over [SUSTAIN_SEC], then holds. */
    private fun masterEnvelope(t: Double): Double {
        if (t >= SUSTAIN_SEC) return MASTER_END_GAIN
        val progress = t / SUSTAIN_SEC
        return MASTER_START_GAIN * (MASTER_END_GAIN / MASTER_START_GAIN).pow(progress)
    }

    private fun centsToRatio(cents: Double): Double = 2.0.pow(cents / 1200.0)

    private fun floatsToPcm16(samples: FloatArray): ShortArray =
        ShortArray(samples.size) { i ->
            (samples[i].coerceIn(-1f, 1f) * Short.MAX_VALUE).toInt().toShort()
        }

    private fun playPcm(samples: ShortArray) {
        val track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build(),
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(SAMPLE_RATE)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build(),
            )
            .setBufferSizeInBytes(samples.size * 2)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        track.write(samples, 0, samples.size)
        track.play()

        scope.launch {
            val durationMs = (samples.size.toDouble() / SAMPLE_RATE * 1000).toLong()
            delay(durationMs + 150)
            track.stop()
            track.release()
        }
    }
}

/** Simple one-pole RC lowpass filter approximating the Web Audio BiquadFilterNode body tone. */
private class OnePoleLowpass(cutoffHz: Double, sampleRate: Int) {
    private val alpha: Double = run {
        val dt = 1.0 / sampleRate
        val rc = 1.0 / (2.0 * PI * cutoffHz)
        dt / (rc + dt)
    }
    private var previous = 0.0

    fun process(input: Double): Double {
        previous += alpha * (input - previous)
        return previous
    }
}

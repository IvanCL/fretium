package com.fretwise.android.audio

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

private const val SAMPLE_RATE = 44_100
private const val FFT_SIZE = 4096

/**
 * Captures microphone audio and emits detected fundamental frequencies (Hz), or -1
 * when no clear pitch is present. Mirrors the analyser + autocorrelation loop in tuner.astro.
 * Caller must hold RECORD_AUDIO permission before calling [detectPitch].
 */
@Singleton
class TunerEngine @Inject constructor() {

    @SuppressLint("MissingPermission")
    fun detectPitch(): Flow<Double> = callbackFlow {
        val minBuffer = AudioRecord.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_FLOAT,
        )
        val bufferSize = maxOf(minBuffer, FFT_SIZE * 4)

        val audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_FLOAT,
            bufferSize,
        )

        if (audioRecord.state != AudioRecord.STATE_INITIALIZED) {
            audioRecord.release()
            close(IllegalStateException("No se pudo inicializar el micrófono"))
            return@callbackFlow
        }

        audioRecord.startRecording()

        val readingJob = launch {
            val readBuffer = FloatArray(FFT_SIZE)
            while (isActive) {
                val read = audioRecord.read(readBuffer, 0, FFT_SIZE, AudioRecord.READ_BLOCKING)
                if (read > 0) {
                    trySend(PitchDetector.autocorrelate(readBuffer, SAMPLE_RATE))
                }
            }
        }

        awaitClose {
            readingJob.cancel()
            audioRecord.stop()
            audioRecord.release()
        }
    }.flowOn(Dispatchers.IO)
}

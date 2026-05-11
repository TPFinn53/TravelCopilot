package com.example.travelcopilot.infra.speech

import android.media.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat


@Singleton
class AudioAmplitudeManager @Inject constructor() {

    private val _amplitude = MutableStateFlow(0f)
    val amplitude: StateFlow<Float> = _amplitude

    private var job: Job? = null

    fun hasAudioPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun start(context: Context) {
        if (!hasAudioPermission(context)) return

        if (job != null) return

        job = CoroutineScope(Dispatchers.IO).launch {
            try {

                val bufferSize = AudioRecord.getMinBufferSize(
                    44100,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )

                val audioRecord = AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    44100,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    bufferSize
                )

                val buffer = ShortArray(bufferSize)

                audioRecord.startRecording()

                while (isActive) {
                    val read = audioRecord.read(buffer, 0, buffer.size)

                    if (read > 0) {
                        val avg = buffer.take(read).map { kotlin.math.abs(it.toFloat()) }.average()
                        _amplitude.value = (avg / 32767f).toFloat()
                    }
                }

                audioRecord.stop()
                audioRecord.release()

            } catch (e: SecurityException) {
                // 🔥 REQUIRED fallback
                _amplitude.value = 0f
            }
        }
    }

    fun stop() {
        job?.cancel()
        job = null
    }
}
package com.example.travelcopilot.infra.speech

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import android.os.Handler
import android.os.Looper


class VoiceInputManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private var recognizer: SpeechRecognizer? = null
    private var isListening = false

    init {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            recognizer = SpeechRecognizer.createSpeechRecognizer(context)
        }
    }

    fun stopListening() {
        isListening = false
        recognizer?.stopListening()
    }

    fun destroy() {
        recognizer?.destroy()
        recognizer = null
    }

    // 🔁 INTERNAL LOOP
    fun startContinuousListening(
        onSpeechStart: () -> Unit,
        onResult: (String) -> Unit
    ) {
        if (isListening) return
        isListening = true

        startListeningLoop(onSpeechStart, onResult) // ✅ FIX
    }

    private fun startListeningLoop(
        onSpeechStart: () -> Unit,
        onResult: (String) -> Unit
    ) {

        val r = recognizer ?: return

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
        }

        r.setRecognitionListener(object : RecognitionListener {

            override fun onBeginningOfSpeech() {
                onSpeechStart() // ✅ FIXED
            }

            override fun onResults(results: Bundle) {
                val text = results
                    .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull()

                text?.let { onResult(it) }

                restart()
            }

            override fun onError(error: Int) {
                restart()
            }

            override fun onEndOfSpeech() {
                restart()
            }

            private fun restart() {
                if (!isListening) return

                try {
                    r.stopListening()
                    r.cancel()
                } catch (_: Exception) {}

                Handler(Looper.getMainLooper()).postDelayed({
                    if (isListening) {
                        try {
                            r.startListening(intent)
                        } catch (_: Exception) {}
                    }
                }, 300)
            }

            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        r.startListening(intent)
    }
}
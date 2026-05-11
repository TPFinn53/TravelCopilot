package com.example.travelcopilot.infra.speech

import android.os.Bundle
import android.speech.RecognitionListener
import android.util.Log

class SimpleRecognitionListener(
    private val onResult: (String) -> Unit
) : RecognitionListener {

    override fun onResults(results: Bundle?) {
        val matches = results
            ?.getStringArrayList(android.speech.SpeechRecognizer.RESULTS_RECOGNITION)

        val text = matches?.firstOrNull()
        if (text != null) {
            onResult(text)
        }
    }

    override fun onError(error: Int) {
        Log.e("Speech", "Error: $error")
    }

    // Required overrides (can be empty)
    override fun onReadyForSpeech(params: Bundle?) {}
    override fun onBeginningOfSpeech() {}
    override fun onRmsChanged(rmsdB: Float) {}
    override fun onBufferReceived(buffer: ByteArray?) {}
    override fun onEndOfSpeech() {}
    override fun onPartialResults(partialResults: Bundle?) {}
    override fun onEvent(eventType: Int, params: Bundle?) {}
}
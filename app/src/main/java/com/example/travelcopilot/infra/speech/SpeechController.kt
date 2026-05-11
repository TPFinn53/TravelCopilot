package com.example.travelcopilot.infra.speech

import android.speech.tts.TextToSpeech

class SpeechController(
    private val tts: TextToSpeech
) {

    fun speak(text: String) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    fun stop() {
        tts.stop()
    }
}
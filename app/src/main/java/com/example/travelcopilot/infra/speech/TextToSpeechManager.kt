package com.example.travelcopilot.infra.speech

import android.content.Context
import android.speech.tts.TextToSpeech
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TextToSpeechManager @Inject constructor(
    @ApplicationContext private val context: Context
) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isReady = false

    // Buffer for smarter streaming (optional use)
    private var lastSpokenText: String = ""

    init {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.US
            isReady = true
        }
    }

    // 🔊 Speak full message
    fun speak(text: String) {
        if (!isReady) return

        tts?.speak(
            text,
            TextToSpeech.QUEUE_FLUSH,
            null,
            "FULL_SPEECH"
        )

        lastSpokenText = text
    }

    // 🔊 Streaming support (basic version)
    fun speakPartial(text: String) {
        if (!isReady) return

        // ❗ naive approach: speak every update (can be noisy)
        if (text.length > lastSpokenText.length) {
            val newPart = text.substring(lastSpokenText.length)

            tts?.speak(
                newPart,
                TextToSpeech.QUEUE_ADD,
                null,
                "PARTIAL_SPEECH"
            )

            lastSpokenText = text
        }
    }

    // ⛔ Stop immediately (used for barge-in)
    fun stop() {
        tts?.stop()
        lastSpokenText = ""
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
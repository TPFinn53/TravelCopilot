package com.example.travelcopilot.infra.speech

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import com.example.travelcopilot.core.voice.VoiceState
import com.example.travelcopilot.core.voice.VoiceStateHolder
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject

class VoiceOutputManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private var tts: TextToSpeech? = null
    private var isSpeaking = false
    @Inject lateinit var voiceStateHolder: VoiceStateHolder

    init {
        tts = TextToSpeech(context) {
            // init
        }
    }

    fun speak(text: String) {
        voiceStateHolder.setState(VoiceState.SPEAKING)

        tts?.speak(
            text,
            TextToSpeech.QUEUE_FLUSH,
            null,
            "COPILOT_TTS"
        )
    }

    fun stop() {
        tts?.stop()
        voiceStateHolder.setState(VoiceState.LISTENING)
    }

    fun isSpeaking(): Boolean = isSpeaking

    fun shutdown() {
        tts?.shutdown()
        tts = null
    }
}
package com.example.travelcopilot.core.voice

interface VoiceOutput {
    fun speak(text: String)
    fun stop()
}
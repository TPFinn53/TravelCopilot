package com.example.travelcopilot.core.voice

interface VoiceListener {
    fun onResult(text: String)
    fun onError(error: String)
}
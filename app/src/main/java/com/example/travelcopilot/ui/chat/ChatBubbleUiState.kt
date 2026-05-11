package com.example.travelcopilot.ui.chat

data class ChatBubbleUiState(

    val id: Long,

    val text: String,

    val isUser: Boolean,

    // UI-only
    val isStreaming: Boolean = false,
    val isSpeaking: Boolean = false,

    // future waveform support
    val amplitude: Float = 0f
)
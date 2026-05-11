package com.example.travelcopilot.domain.model

sealed class ChatStreamEvent {
    data class PartialText(val text: String) : ChatStreamEvent()
    data class FinalText(val text: String) : ChatStreamEvent()
}
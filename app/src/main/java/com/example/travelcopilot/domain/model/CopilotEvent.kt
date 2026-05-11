package com.example.travelcopilot.domain.model

sealed class CopilotEvent {

    // 🔊 Voice
    data class Speak(val text: String) : CopilotEvent()
    data class SpeakPartial(val text: String) : CopilotEvent()
    object StopSpeaking : CopilotEvent()

    // existing…
    data class UserMessage(val text: String) : CopilotEvent()
    data class AssistantPartial(val text: String) : CopilotEvent()
    data class AssistantMessage(val text: String) : CopilotEvent()
    data class NavigateTo(val destination: String) : CopilotEvent()
    data class TripLog(val tripId: Long, val type: String, val data: String?) : CopilotEvent()
    object Cancel : CopilotEvent()
}
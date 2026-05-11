package com.example.travelcopilot.core.engine

import com.example.travelcopilot.core.model.VoiceCommand

object VoiceCommandParser {

    fun parse(input: String): VoiceCommand {
        val normalized = input.lowercase()

        return when {
            normalized.startsWith("log") -> VoiceCommand.LOG_NOTE
            normalized.contains("start") -> VoiceCommand.START_TRIP
            normalized.contains("pause") -> VoiceCommand.PAUSE_TRIP
            normalized.contains("resume") -> VoiceCommand.RESUME_TRIP
            normalized.contains("stop") || normalized.contains("end") -> VoiceCommand.STOP_TRIP
            else -> VoiceCommand.UNKNOWN
        }
    }

    fun extractNote(input: String): String {
        return input
            .substringAfter("log", "")
            .substringAfter("note", "")
            .substringAfter("this", "")
            .trim()
    }
}
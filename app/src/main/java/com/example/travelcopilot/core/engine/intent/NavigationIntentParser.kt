package com.example.travelcopilot.core.engine.intent

import com.example.travelcopilot.core.engine.model.NavigationCommand
import com.example.travelcopilot.domain.model.CopilotEvent

object NavigationIntentParser {

    fun parse(text: String): CopilotEvent? {
        // example logic
        if (text.contains("gas station")) {
            return CopilotEvent.NavigateTo("nearest gas station")
        }

        return null
    }
}
package com.example.travelcopilot.core.engine.model

sealed class CopilotCommand {
    data class Navigation(val command: NavigationCommand) : CopilotCommand()
    data class Chat(val message: String) : CopilotCommand()
}
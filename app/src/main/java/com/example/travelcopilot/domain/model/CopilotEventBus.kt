package com.example.travelcopilot.domain.model

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class CopilotEventBus {

    private val _events = MutableSharedFlow<CopilotEvent>(
        extraBufferCapacity = 64
    )

    val events = _events.asSharedFlow()

    fun send(event: CopilotEvent) {
        _events.tryEmit(event)
    }
}
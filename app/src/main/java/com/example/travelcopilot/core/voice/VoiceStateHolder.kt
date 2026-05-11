package com.example.travelcopilot.core.voice

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VoiceStateHolder @Inject constructor() {

    private val _state = MutableStateFlow(VoiceState.IDLE)
    val state: StateFlow<VoiceState> = _state

    fun setState(newState: VoiceState) {
        _state.value = newState
    }
}
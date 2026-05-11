package com.example.travelcopilot.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelcopilot.core.voice.VoiceState
import com.example.travelcopilot.core.voice.VoiceStateHolder
import com.example.travelcopilot.infra.speech.AudioAmplitudeManager
import com.example.travelcopilot.storage.StorageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val voiceStateHolder: VoiceStateHolder,
    private val amplitudeManager: AudioAmplitudeManager,
    private val storageManager: StorageManager
) : ViewModel() {

    init {
        viewModelScope.launch {
            val uri = storageManager.getRootUri()
            if (uri != null && !storageManager.isExternalStorageAvailable()) {
                // Optional: clear invalid URI or trigger re-selection
            }
        }
    }

    val voiceState: StateFlow<VoiceState> = voiceStateHolder.state

    // ✅ ADD THIS
    val amplitude: StateFlow<Float> = amplitudeManager.amplitude
}
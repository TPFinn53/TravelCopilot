package com.example.travelcopilot.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelcopilot.core.conversation.ConversationManager
import com.example.travelcopilot.data.repository.ChatRepository
import com.example.travelcopilot.data.repository.TripRepository
import com.example.travelcopilot.domain.model.ChatMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val conversationManager: ConversationManager,
    private val chatRepository: ChatRepository,
    private val tripRepository: TripRepository
) : ViewModel() {

    val messages: StateFlow<List<ChatMessage>> =
        flow {
            val trip = tripRepository.getActiveTrip()
            if (trip != null) {
                emitAll(chatRepository.getMessages(trip.id))
            } else {
                emit(emptyList())
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun sendMessage(text: String) {
        conversationManager.processUserInput(text)
    }
}
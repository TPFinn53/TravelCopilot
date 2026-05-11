package com.example.travelcopilot.core.conversation

import com.example.travelcopilot.core.context.ContextProvider
import com.example.travelcopilot.core.engine.CopilotEngine
import com.example.travelcopilot.core.engine.intent.NavigationIntentParser
import com.example.travelcopilot.data.local.Role
import com.example.travelcopilot.data.repository.ChatRepository
import com.example.travelcopilot.data.repository.TripRepository
import com.example.travelcopilot.domain.model.CopilotEvent
import com.example.travelcopilot.domain.model.ChatMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID
import javax.inject.Inject

class ConversationManager @Inject constructor(
    private val chatRepository: ChatRepository,
    private val tripRepository: TripRepository,
    private val contextProvider: ContextProvider,
    private val copilotEngine: CopilotEngine
) {

    fun processUserInput(text: String): Flow<CopilotEvent> = flow {

        val trip = tripRepository.getActiveTrip() ?: return@flow
        val now = System.currentTimeMillis()

        // 1️⃣ Save USER message
        chatRepository.insert(
            ChatMessage(
                id = now,
                tripId = trip.id,
                content = text,
                timestamp = now,
                role = Role.USER
            )
        )

        emit(CopilotEvent.UserMessage(text))

        // 2️⃣ Fast-path navigation
        NavigationIntentParser.parse(text)?.let {
            emit(it)
            return@flow
        }

        // 3️⃣ Insert placeholder AI message
        val aiId = System.currentTimeMillis()

        chatRepository.insert(
            ChatMessage(
                id = aiId,
                tripId = trip.id,
                content = "",
                timestamp = aiId,
                role = Role.AI
            )
        )

        // 4️⃣ Build context
        val context = contextProvider.getSnapshot(trip.id)

        // 5️⃣ Stream AI response
        var finalText = ""

        copilotEngine.streamResponse(text, context)
            .collect { partial ->

                finalText = partial

                // update DB so UI reacts
                chatRepository.updateMessageContent(aiId, partial)

                emit(CopilotEvent.AssistantPartial(partial))
                emit(CopilotEvent.SpeakPartial(partial))
            }

        // 6️⃣ Final message event
        emit(CopilotEvent.AssistantMessage(finalText))
        emit(CopilotEvent.Speak(finalText))
    }
}
package com.example.travelcopilot.infra.copilot

import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import android.util.Log
import androidx.core.net.toUri
import com.example.travelcopilot.core.context.ContextProvider
import com.example.travelcopilot.core.context.ContextSnapshot
import com.example.travelcopilot.core.conversation.ConversationManager
import com.example.travelcopilot.core.engine.CopilotEngine
import com.example.travelcopilot.core.voice.VoiceState
import com.example.travelcopilot.core.voice.VoiceStateHolder
import com.example.travelcopilot.data.repository.TripEventRepository
import com.example.travelcopilot.data.repository.TripRepository
import com.example.travelcopilot.domain.model.CopilotEvent
import com.example.travelcopilot.domain.model.TripEventType
import com.example.travelcopilot.infra.speech.VoiceInputManager
import com.example.travelcopilot.infra.speech.VoiceOutputManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.launchIn
import javax.inject.Inject

@AndroidEntryPoint
class CopilotService : Service() {

    companion object {
        private const val TAG = "CopilotService"
        const val ACTION_START = "ACTION_START_COPILOT"
        const val ACTION_STOP = "ACTION_STOP_COPILOT"
    }

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private var mainJob: Job? = null
    private var isRunning = false

    @Inject lateinit var tripRepository: TripRepository
    @Inject lateinit var eventRepository: TripEventRepository
    @Inject lateinit var contextProvider: ContextProvider
    @Inject lateinit var copilotEngine: CopilotEngine
    @Inject lateinit var voiceInputManager: VoiceInputManager
    @Inject lateinit var voiceOutputManager: VoiceOutputManager
    @Inject lateinit var voiceStateHolder: VoiceStateHolder
    @Inject lateinit var conversationManager: ConversationManager

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startCopilot()
            ACTION_STOP -> stopCopilot()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        stopCopilot()
        super.onDestroy()
    }

    // 🚀 START
    private fun startCopilot() {
        if (isRunning) return

        Log.d(TAG, "Starting Copilot Service")
        isRunning = true

        startVoiceLoop()
        startMainLoop()
    }

    // 🛑 STOP
    private fun stopCopilot() {
        Log.d(TAG, "Stopping Copilot Service")

        isRunning = false
        mainJob?.cancel()

        voiceInputManager.stopListening()
        voiceInputManager.destroy()

        voiceOutputManager.stop()
        voiceOutputManager.shutdown()

        stopSelf()
    }

    // 🎤 VOICE INPUT → EVENT PIPELINE
    private fun startVoiceLoop() {

        voiceStateHolder.setState(VoiceState.LISTENING)

        voiceInputManager.startContinuousListening(

            onSpeechStart = {
                // 🔥 BARGE-IN
                if (voiceOutputManager.isSpeaking()) {
                    voiceOutputManager.stop()
                }
                voiceStateHolder.setState(VoiceState.LISTENING)
            },

            onResult = { text ->
                handleUserInput(text)
            }
        )
    }

    private fun handleUserInput(text: String) {

        conversationManager.processUserInput(text)
            .onEach { event -> handleEvent(event) }
            .launchIn(serviceScope)
    }

    // 🔁 BACKGROUND LOOP (context + proactive AI + auto events)
    private fun startMainLoop() {
        mainJob = serviceScope.launch {

            while (isRunning && isActive) {
                try {
                    val trip = tripRepository.getActiveTrip()

                    if (trip == null) {
                        delay(5000)
                        continue
                    }

                    val context = contextProvider.getSnapshot(trip.id)

                    detectAutoEvents(context)
                    maybeGenerateProactiveResponse(context)

                    delay(2000)

                } catch (e: Exception) {
                    Log.e(TAG, "Main loop error", e)
                    delay(3000)
                }
            }
        }
    }

    // 🚗 AUTO EVENT DETECTION
    private suspend fun detectAutoEvents(context: ContextSnapshot) {

        if (context.time.idleDurationMs > 60_000) {
            eventRepository.logEvent(
                tripId = context.trip.tripId,
                type = TripEventType.TRIP_STOPPED,
                data = "Stopped for over 1 minute"
            )
        }
    }

    // 🤖 PROACTIVE AI → EVENTS
    private suspend fun maybeGenerateProactiveResponse(context: com.example.travelcopilot.core.context.ContextSnapshot) {

        if (!copilotEngine.shouldGenerateProactiveResponse(context)) return

        val response = copilotEngine.generateProactiveResponse(context)

        handleEvent(CopilotEvent.Speak(response.message))
    }

    // 🧠 CENTRAL EVENT HANDLER (THE CORE)
    private fun handleEvent(event: CopilotEvent) {

        when (event) {

            // 🔊 STREAMING VOICE
            is CopilotEvent.SpeakPartial -> {
                //voiceOutputManager.speakPartial(event.text)
            }

            // 🔊 FINAL SPEECH
            is CopilotEvent.Speak -> {
                voiceOutputManager.speak(event.text)
            }

            // 🤖 AI TEXT (optional auto-speak mapping)
            is CopilotEvent.AssistantPartial -> {
                //voiceOutputManager.speakPartial(event.text)
            }

            is CopilotEvent.AssistantMessage -> {
                voiceOutputManager.speak(event.text)
            }

            // 🚗 NAVIGATION
            is CopilotEvent.NavigateTo -> {
                launchGoogleMaps(event.destination)

                serviceScope.launch {
                    val tripId = tripRepository.getActiveTrip()?.id ?: return@launch
                    eventRepository.logEvent(
                        tripId = tripId,
                        type = TripEventType.NAVIGATION_STARTED,
                        data = event.destination
                    )
                }
            }

            // 🧾 TRIP LOGGING
            is CopilotEvent.TripLog -> {
                serviceScope.launch {
                    eventRepository.logEvent(
                        tripId = event.tripId,
                        type = TripEventType.valueOf(event.type),
                        data = event.data
                    )
                }
            }

            // 🛑 CANCEL / BARGE-IN
            CopilotEvent.Cancel,
            CopilotEvent.StopSpeaking -> {
                voiceOutputManager.stop()
            }

            else -> Unit
        }
    }

    // 🗺️ NAVIGATION EXECUTION
    private fun launchGoogleMaps(destination: String) {

        val uri = "google.navigation:q=${Uri.encode(destination)}".toUri()

        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage("com.google.android.apps.maps")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        try {
            startActivity(intent)
        } catch (e: Exception) {
            val fallback = Intent(
                Intent.ACTION_VIEW,
                "https://www.google.com/maps/dir/?api=1&destination=${Uri.encode(destination)}".toUri()
            ).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(fallback)
        }
    }
}
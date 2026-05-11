package com.example.travelcopilot.core.engine

import com.example.travelcopilot.core.context.ContextSnapshot
import com.example.travelcopilot.core.engine.VoiceCommandParser.extractNote
import com.example.travelcopilot.core.model.CopilotResponse
import com.example.travelcopilot.core.model.VoiceCommand

import com.example.travelcopilot.domain.model.TripEvent
import com.example.travelcopilot.domain.model.TripState

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject

class CopilotEngine @Inject constructor() {

    // =========================================================
    // Voice command handling
    // =========================================================

    fun handleCommand(
        currentState: TripState,
        command: VoiceCommand,
        rawInput: String,
        tripId: Long
    ): CopilotResponse {

        return when (command) {

            VoiceCommand.LOG_NOTE -> {

                val text =
                    extractNote(rawInput)

                if (text.isBlank()) {

                    CopilotResponse(
                        message =
                            "What would you like me to note?"
                    )

                } else {

                    CopilotResponse(
                        message =
                            "Got it, saved your note",

                        tripEvent =
                            TripEvent.UserNote(
                                tripId = tripId,

                                timestamp =
                                    System.currentTimeMillis(),

                                text = text
                            )
                    )
                }
            }

            else -> {

                CopilotResponse(
                    message =
                        "I didn't understand that command."
                )
            }
        }
    }

    // =========================================================
    // Natural language processing
    // =========================================================

    fun processUserInput(
        input: String,
        context: ContextSnapshot
    ): CopilotResponse {

        val normalized =
            input.lowercase()

        val tripId =
            context.trip.tripId

        val timestamp =
            System.currentTimeMillis()

        val location =
            context.location.location

        return when {

            // =================================================
            // Trip lifecycle
            // =================================================

            normalized.contains("start trip") -> {

                CopilotResponse(

                    message =
                        "Starting your trip",

                    tripEvent =
                        TripEvent.TripStarted(
                            tripId = tripId,
                            timestamp = timestamp
                        )
                )
            }

            normalized.contains("stop trip") -> {

                CopilotResponse(

                    message =
                        "Stopping your trip",

                    tripEvent =
                        TripEvent.TripStopped(
                            tripId = tripId,
                            timestamp = timestamp
                        )
                )
            }

            normalized.contains("complete trip") ||
                    normalized.contains("end trip") -> {

                CopilotResponse(

                    message =
                        "Trip completed",

                    tripEvent =
                        TripEvent.TripCompleted(
                            tripId = tripId,
                            timestamp = timestamp
                        )
                )
            }

            // =================================================
            // Journey events
            // =================================================

            normalized.contains("fuel") ||
                    normalized.contains("gas station") -> {

                CopilotResponse(

                    message =
                        "Logged fuel stop",

                    tripEvent =
                        TripEvent.FuelStop(
                            tripId = tripId,

                            timestamp = timestamp,

                            locationName =
                                context.location.placeName
                        )
                )
            }

            normalized.contains("rest stop") ||
                    normalized.contains("taking a break") -> {

                CopilotResponse(

                    message =
                        "Logged rest stop",

                    tripEvent =
                        TripEvent.RestStop(
                            tripId = tripId,

                            timestamp = timestamp,

                            locationName =
                                context.location.placeName
                        )
                )
            }

            // =================================================
            // Notes
            // =================================================

            normalized.startsWith("log") ||
                    normalized.startsWith("note") -> {

                val noteText =
                    extractNote(input)

                if (noteText.isBlank()) {

                    CopilotResponse(
                        message =
                            "What would you like me to note?"
                    )

                } else {

                    CopilotResponse(

                        message =
                            "Got it, saved your note",

                        tripEvent =
                            TripEvent.UserNote(
                                tripId = tripId,

                                timestamp = timestamp,

                                text = noteText
                            )
                    )
                }
            }

            // =================================================
            // Default fallback
            // =================================================

            else -> {

                CopilotResponse(
                    message =
                        generateResponse(
                            input = input,
                            context = context
                        )
                )
            }
        }
    }

    // =========================================================
    // Proactive AI
    // =========================================================

    fun shouldGenerateProactiveResponse(
        context: ContextSnapshot
    ): Boolean {

        val now =
            context.time.currentTime

        val hour =
            Instant.ofEpochMilli(now)
                .atZone(ZoneId.systemDefault())
                .hour

        val lastAiResponse =
            context.memory.lastAiResponseTime
                ?: 0L

        val cooldownMs =
            2 * 60 * 1000L

        // Cooldown protection
        if (now - lastAiResponse < cooldownMs) {
            return false
        }

        // Long driving
        if (
            !context.location.isStopped &&
            context.time.driveDurationMs >
            2 * 60 * 60 * 1000
        ) {
            return true
        }

        // Idle
        if (
            context.location.isStopped &&
            context.time.idleDurationMs >
            5 * 60 * 1000
        ) {
            return true
        }

        // Low fuel
        if (
            context.signals.fuelLevelPercent != null &&
            context.signals.fuelLevelPercent < 15
        ) {
            return true
        }

        // Late driving
        if (hour >= 22 || hour <= 5) {
            return true
        }

        // Near destination
        if (
            context.location.distanceToDestinationMeters != null &&
            context.location.distanceToDestinationMeters < 5_000
        ) {
            return true
        }

        return false
    }

    fun generateProactiveResponse(
        context: ContextSnapshot
    ): CopilotResponse {

        val now =
            context.time.currentTime

        val hour =
            Instant.ofEpochMilli(now)
                .atZone(ZoneId.systemDefault())
                .hour

        return when {

            // Long driving
            !context.location.isStopped &&
                    context.time.driveDurationMs >
                    2 * 60 * 60 * 1000 -> {

                CopilotResponse(
                    message =
                        "You've been driving for a while. Want to take a break?"
                )
            }

            // Long idle
            context.location.isStopped &&
                    context.time.idleDurationMs >
                    5 * 60 * 1000 -> {

                CopilotResponse(
                    message =
                        "You've been stopped for a bit. Want me to log a stop?"
                )
            }

            // Low fuel
            context.signals.fuelLevelPercent != null &&
                    context.signals.fuelLevelPercent < 15 -> {

                CopilotResponse(
                    message =
                        "Fuel is getting low. You may want to find a gas station soon."
                )
            }

            // Late driving
            hour !in 6..21 -> {

                CopilotResponse(
                    message =
                        "It's getting late. Consider stopping for the night."
                )
            }

            // Near destination
            context.location.distanceToDestinationMeters != null &&
                    context.location.distanceToDestinationMeters < 5_000 -> {

                CopilotResponse(
                    message =
                        "You're getting close to your destination."
                )
            }

            else -> {

                CopilotResponse(
                    message =
                        "Everything looks good so far."
                )
            }
        }
    }

    // =========================================================
    // Streaming responses
    // =========================================================

    fun streamResponse(
        input: String,
        context: ContextSnapshot
    ): Flow<String> = flow {

        val full =
            generateResponse(
                input,
                context
            )

        val words =
            full.split(" ")

        var current = ""

        for (word in words) {

            current += "$word "

            emit(current.trim())

            delay(50)
        }
    }

    // =========================================================
    // AI response generation
    // =========================================================

    private fun generateResponse(
        input: String,
        context: ContextSnapshot
    ): String {

        val lower =
            input.lowercase()

        val tripName =
            context.trip.tripName

        val city =
            context.location.city
                ?: "your current area"

        val driveTimeMin =
            context.time.driveDurationMs / 60000

        val idleTimeMin =
            context.time.idleDurationMs / 60000

        return when {

            // =================================================
            // Stops
            // =================================================

            lower.contains("stop") ||
                    lower.contains("rest") -> {

                "You're near $city. " +
                        "You've been driving for about " +
                        "$driveTimeMin minutes. " +
                        "A break soon might be a good idea."
            }

            // =================================================
            // Fuel
            // =================================================

            lower.contains("fuel") ||
                    lower.contains("gas") -> {

                "You're near $city. " +
                        "If fuel is low, it may be a good time " +
                        "to stop at a gas station soon."
            }

            // =================================================
            // Status
            // =================================================

            lower.contains("status") ||
                    lower.contains("how am i doing") -> {

                "You're on \"$tripName\" near $city. " +
                        "Drive time is about $driveTimeMin minutes " +
                        "with approximately $idleTimeMin minutes stopped."
            }

            // =================================================
            // Location
            // =================================================

            lower.contains("where am i") -> {

                "You're currently near $city."
            }

            // =================================================
            // Default
            // =================================================

            else -> {

                "You're on \"$tripName\" near $city. " +
                        "How can I help with your trip?"
            }
        }
    }
}
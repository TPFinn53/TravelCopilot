package com.example.travelcopilot.core.context

import com.example.travelcopilot.core.analytics.TripAnalytics
import com.example.travelcopilot.data.repository.ChatRepository
import com.example.travelcopilot.data.repository.TripEventRepository
import com.example.travelcopilot.data.repository.TripRepository
import com.example.travelcopilot.infra.location.LocationProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ContextProvider @Inject constructor(
    private val tripRepository: TripRepository,
    private val eventRepository: TripEventRepository,
    private val chatRepository: ChatRepository,
    private val locationProvider: LocationProvider
) {

    suspend fun getSnapshot(tripId: Long): ContextSnapshot =
        withContext(Dispatchers.Default) {

        // ✅ Trip
        val trip = tripRepository.getTripById(tripId)
            ?: throw IllegalStateException("Trip not found: $tripId")

        // ✅ Location
        val location = locationProvider.getCurrentLocation()

        // ✅ Messages (LIMIT to recent)
        val messages = chatRepository.getRecentMessages(tripId)

        // ✅ Time calculations (basic version)
        val currentTime = System.currentTimeMillis()

        // ✅ Events (LIMIT to recent later if needed)
        val events = eventRepository.getRecentEvents(tripId)

        val driveDurationMs = TripAnalytics.calculateDriveTime(events)
        val idleDurationMs = TripAnalytics.calculateIdleTime(events)
        val lastAiResponseTime = TripAnalytics.findLastAiResponseTime(events)
        val isStopped = location?.speed!! < 2.0f

        val tripContext = TripContext(
            tripId = trip.id,
            tripName = trip.name,
            tripState = trip.state
        )

        val timeContext = TimeContext(
            currentTime = currentTime,
            driveDurationMs = driveDurationMs,
            idleDurationMs = idleDurationMs,
            lastAiResponseTime = lastAiResponseTime
        )

        val memoryContext = MemoryContext(
            recentEvents = events,
            recentMessages = messages,
            lastAiResponseTime = null
        )

        val signalContext = SignalContext(
            fuelLevelPercent = null
        )

        val locationContext = LocationContext(
            location = location,
            placeName = location?.placeName,
            city = location?.city,
            regionState = location?.regionState,
            distanceToDestinationMeters = 0.0F,
            isStopped = isStopped
        )
        ContextSnapshot(
            trip = tripContext,
            location = locationContext,
            time = timeContext,
            memory = memoryContext,
            signals = signalContext
        )
    }
}


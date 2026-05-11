package com.example.travelcopilot.core.state

import com.example.travelcopilot.domain.model.TripEvent
import com.example.travelcopilot.domain.model.TripEventType
import com.example.travelcopilot.domain.model.TripState

class TripStateMachine {

    private var currentState = TripState.CREATED

    private var lastMovementTime = 0L

    fun update(
        isMoving: Boolean,
        currentTime: Long
    ): TripState {

        if (isMoving) {

            lastMovementTime = currentTime

            currentState = TripState.DRIVING

        } else {

            val idleMs = currentTime - lastMovementTime

            currentState = when {

                idleMs > 10 * 60 * 1000 ->
                    TripState.PARKED

                idleMs > 60 * 1000 ->
                    TripState.IDLE

                else ->
                    TripState.STOPPED
            }
        }

        return currentState
    }
}
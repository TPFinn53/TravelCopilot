package com.example.travelcopilot.core.analytics

import com.example.travelcopilot.domain.model.TripEvent

object TripAnalytics {

    fun List<TripEvent>.latestEvent(): TripEvent? {
        return maxByOrNull { it.timestamp }
    }

    fun countStops(
        events: List<TripEvent>
    ): Int {

        return events.count {

            it is TripEvent.FuelStop ||
                    it is TripEvent.RestStop ||
                    it is TripEvent.ScenicStop
        }
    }

    fun countNotes(
        events: List<TripEvent>
    ): Int {

        return events.count {
            it is TripEvent.UserNote
        }
    }

    fun findLastAiResponseTime(
        events: List<TripEvent>
    ): Long? {

        return events
            .filterIsInstance<TripEvent.AiMessage>()
            .maxOfOrNull { it.timestamp }
    }

    fun calculateDriveTime(
        events: List<TripEvent>
    ): Long {

        var totalDriveTime = 0L

        var driveStartTime: Long? = null

        val sorted =
            events.sortedBy { it.timestamp }

        for (event in sorted) {

            when (event) {

                is TripEvent.DrivingDetected -> {

                    if (driveStartTime == null) {
                        driveStartTime = event.timestamp
                    }
                }

                is TripEvent.IdleDetected,
                is TripEvent.Parked -> {

                    if (driveStartTime != null) {

                        totalDriveTime +=
                            event.timestamp - driveStartTime

                        driveStartTime = null
                    }
                }

                else -> {}
            }
        }

        // Still driving
        if (driveStartTime != null) {

            totalDriveTime +=
                System.currentTimeMillis() - driveStartTime
        }

        return totalDriveTime
    }

    fun calculateIdleTime(
        events: List<TripEvent>
    ): Long {

        var totalIdleTime = 0L

        var idleStartTime: Long? = null

        val sorted =
            events.sortedBy { it.timestamp }

        for (event in sorted) {

            when (event) {

                is TripEvent.IdleDetected -> {

                    if (idleStartTime == null) {
                        idleStartTime = event.timestamp
                    }
                }

                is TripEvent.DrivingDetected -> {

                    if (idleStartTime != null) {

                        totalIdleTime +=
                            event.timestamp - idleStartTime

                        idleStartTime = null
                    }
                }

                else -> {}
            }
        }

        // Still idle
        if (idleStartTime != null) {

            totalIdleTime +=
                System.currentTimeMillis() - idleStartTime
        }

        return totalIdleTime
    }
}
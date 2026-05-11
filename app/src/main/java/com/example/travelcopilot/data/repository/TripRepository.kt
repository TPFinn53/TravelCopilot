package com.example.travelcopilot.data.repository

import com.example.travelcopilot.data.local.dao.TripDao

import com.example.travelcopilot.data.local.mapper.toTrip
import com.example.travelcopilot.data.local.mapper.toEntity

import com.example.travelcopilot.domain.model.Trip
import com.example.travelcopilot.domain.model.TripState

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

import javax.inject.Inject

class TripRepository @Inject constructor(
    private val tripDao: TripDao
) {

    // =========================================================
    // One-shot fetches
    // =========================================================

    suspend fun getAllTripsOnce(): List<Trip> {

        return tripDao
            .getAllTrips()
            .map { it.toTrip() }
    }

    suspend fun getTripById(
        tripId: Long
    ): Trip? {

        return tripDao
            .getTripById(tripId)
            ?.toTrip()
    }

    suspend fun getActiveTrip(): Trip? {

        return tripDao
            .getActiveTrip()
            ?.toTrip()
    }

    // =========================================================
    // Observers
    // =========================================================

    fun observeTrips(): Flow<List<Trip>> {

        return tripDao
            .observeTrips()
            .map { entities ->

                entities.map {
                    it.toTrip()
                }
            }
    }

    fun observeTrip(
        tripId: Long
    ): Flow<Trip?> {

        return tripDao
            .observeTrip(tripId)
            .map { entity ->

                entity?.toTrip()
            }
    }

    fun getActiveTripFlow(): Flow<Trip?> {

        return tripDao
            .getActiveTripFlow()
            .map { entity ->

                entity?.toTrip()
            }
    }

    // =========================================================
    // CRUD
    // =========================================================

    suspend fun insert(
        trip: Trip
    ): Long {

        return tripDao
            .insertTrip(trip.toEntity())
    }

    suspend fun update(
        trip: Trip
    ) {

        tripDao.updateTrip(
            trip.toEntity()
        )
    }

    suspend fun delete(
        trip: Trip
    ) {

        tripDao.deleteTrip(
            trip.toEntity()
        )
    }

    // =========================================================
    // Trip creation
    // =========================================================

    suspend fun createTrip(
        name: String,
        destination: String?
    ): Long {

        val trip = Trip(

            name = name,

            destinationName = destination,

            state = TripState.CREATED,

            createdAt =
                System.currentTimeMillis()
        )

        return tripDao.insertTrip(
            trip.toEntity()
        )
    }

    // =========================================================
    // Lifecycle
    // =========================================================

    suspend fun startTrip(
        tripId: Long
    ) {

        tripDao.startTrip(
            tripId = tripId,

            state = TripState.DRIVING,

            startedAt =
                System.currentTimeMillis()
        )
    }

    suspend fun stopTrip(
        tripId: Long
    ) {

        tripDao.updateTripState(
            tripId = tripId,

            state = TripState.STOPPED
        )
    }

    suspend fun parkTrip(
        tripId: Long
    ) {

        tripDao.updateTripState(
            tripId = tripId,

            state = TripState.PARKED
        )
    }

    suspend fun idleTrip(
        tripId: Long
    ) {

        tripDao.updateTripState(
            tripId = tripId,

            state = TripState.IDLE
        )
    }

    suspend fun resumeDriving(
        tripId: Long
    ) {

        tripDao.updateTripState(
            tripId = tripId,

            state = TripState.DRIVING
        )
    }

    suspend fun completeTrip(
        tripId: Long
    ) {

        tripDao.completeTrip(
            tripId = tripId,

            completedAt =
                System.currentTimeMillis()
        )
    }

    // =========================================================
    // Metrics
    // =========================================================

    suspend fun updateTripMetrics(
        tripId: Long,
        distanceMeters: Float,
        driveTimeMs: Long,
        idleTimeMs: Long
    ) {

        tripDao.updateTripMetrics(
            tripId = tripId,

            distanceMeters = distanceMeters,

            driveTimeMs = driveTimeMs,

            idleTimeMs = idleTimeMs
        )
    }

    // =========================================================
    // Location snapshot
    // =========================================================

    suspend fun updateCurrentLocation(
        tripId: Long,
        lat: Double?,
        lng: Double?,
        locationName: String?
    ) {

        tripDao.updateCurrentLocation(
            tripId = tripId,

            lat = lat,

            lng = lng,

            locationName = locationName
        )
    }

    // =========================================================
    // Maintenance
    // =========================================================

    suspend fun deleteAllTrips() {

        tripDao.deleteAllTrips()
    }
}
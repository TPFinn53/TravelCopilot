package com.example.travelcopilot.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

import com.example.travelcopilot.data.local.entity.TripEntity
import com.example.travelcopilot.domain.model.TripState

import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {

    // =========================================================
    // Create
    // =========================================================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(
        trip: TripEntity
    ): Long

    // =========================================================
    // Update
    // =========================================================

    @Update
    suspend fun updateTrip(
        trip: TripEntity
    )

    // =========================================================
    // Delete
    // =========================================================

    @Delete
    suspend fun deleteTrip(
        trip: TripEntity
    )

    // =========================================================
    // Single trip
    // =========================================================

    @Query("""
        SELECT * FROM trips
        WHERE id = :tripId
        LIMIT 1
    """)
    suspend fun getTripById(
        tripId: Long
    ): TripEntity?

    @Query("""
        SELECT * FROM trips
        WHERE id = :tripId
        LIMIT 1
    """)
    fun observeTrip(
        tripId: Long
    ): Flow<TripEntity?>

    // =========================================================
    // All trips
    // =========================================================

    @Query("""
        SELECT * FROM trips
        ORDER BY createdAt DESC
    """)
    suspend fun getAllTrips(): List<TripEntity>

    @Query("""
        SELECT * FROM trips
        ORDER BY createdAt DESC
    """)
    fun observeTrips(): Flow<List<TripEntity>>

    // =========================================================
    // Active trip
    // =========================================================

    @Query("""
        SELECT * FROM trips
        WHERE state != 'COMPLETED'
        ORDER BY createdAt DESC
        LIMIT 1
    """)
    suspend fun getActiveTrip(): TripEntity?

    @Query("""
        SELECT * FROM trips
        WHERE state != 'COMPLETED'
        ORDER BY createdAt DESC
        LIMIT 1
    """)
    fun getActiveTripFlow(): Flow<TripEntity?>

    // =========================================================
    // State queries
    // =========================================================

    @Query("""
        SELECT * FROM trips
        WHERE state = :state
        ORDER BY createdAt DESC
    """)
    suspend fun getTripsByState(
        state: TripState
    ): List<TripEntity>

    @Query("""
        SELECT * FROM trips
        WHERE state = :state
        ORDER BY createdAt DESC
    """)
    fun observeTripsByState(
        state: TripState
    ): Flow<List<TripEntity>>

    // =========================================================
    // Lifecycle
    // =========================================================

    @Query("""
        UPDATE trips
        SET
            state = :state,
            startedAt = :startedAt
        WHERE id = :tripId
    """)
    suspend fun startTrip(
        tripId: Long,
        state: TripState = TripState.DRIVING,
        startedAt: Long = System.currentTimeMillis()
    )

    @Query("""
        UPDATE trips
        SET
            state = :state
        WHERE id = :tripId
    """)
    suspend fun updateTripState(
        tripId: Long,
        state: TripState
    )

    @Query("""
        UPDATE trips
        SET
            state = 'COMPLETED',
            completedAt = :completedAt
        WHERE id = :tripId
    """)
    suspend fun completeTrip(
        tripId: Long,
        completedAt: Long = System.currentTimeMillis()
    )

    // =========================================================
    // Metrics
    // =========================================================

    @Query("""
        UPDATE trips
        SET
            totalDistanceMeters = :distanceMeters,
            driveTimeMs = :driveTimeMs,
            idleTimeMs = :idleTimeMs
        WHERE id = :tripId
    """)
    suspend fun updateTripMetrics(
        tripId: Long,
        distanceMeters: Float,
        driveTimeMs: Long,
        idleTimeMs: Long
    )

    // =========================================================
    // Location snapshot
    // =========================================================

    @Query("""
        UPDATE trips
        SET
            currentLat = :lat,
            currentLng = :lng,
            currentLocationName = :locationName
        WHERE id = :tripId
    """)
    suspend fun updateCurrentLocation(
        tripId: Long,
        lat: Double?,
        lng: Double?,
        locationName: String?
    )

    // =========================================================
    // Maintenance
    // =========================================================

    @Query("""
        DELETE FROM trips
    """)
    suspend fun deleteAllTrips()
}
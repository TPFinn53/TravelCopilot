package com.example.travelcopilot.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelcopilot.data.repository.TripRepository
import com.example.travelcopilot.domain.model.Trip
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TripViewModel @Inject constructor(
    private val tripRepository: TripRepository
) : ViewModel() {

    // 🔄 STREAM: all trips (UI observes this)
    val trips: Flow<List<Trip>> = tripRepository.observeTrips()

    // 🔄 STREAM: single trip
    fun observeTrip(tripId: Long): Flow<Trip?> {
        return tripRepository.observeTrip(tripId)
    }

    // ➕ CREATE
    fun createTrip(name: String, destination: String) {
        viewModelScope.launch {
            tripRepository.createTrip(name, destination)
        }
    }

    // ▶️ START
    fun startTrip(tripId: Long) {
        viewModelScope.launch {
            tripRepository.startTrip(tripId)
        }
    }

    // ⏹️ STOP
    fun stopTrip(tripId: Long) {
        viewModelScope.launch {
            tripRepository.stopTrip(tripId)
        }
    }

    // ⏹️ COMPLETED
    fun completeTrip(tripId: Long) {
        viewModelScope.launch {
            tripRepository.completeTrip(tripId)
        }
    }
}
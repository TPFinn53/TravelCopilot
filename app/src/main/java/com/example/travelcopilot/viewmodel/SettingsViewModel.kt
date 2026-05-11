package com.example.travelcopilot.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelcopilot.storage.StorageManager
import com.example.travelcopilot.data.repository.ChatRepository
import com.example.travelcopilot.data.repository.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val storageManager: StorageManager,
    private val tripRepository: TripRepository,
    private val chatRepository: ChatRepository
) : ViewModel() {

    val rootUriFlow = storageManager.rootUriFlow

    private val _uiState = MutableStateFlow(StorageUiState())
    val uiState: StateFlow<StorageUiState> = _uiState

    fun onFolderSelected(uri: Uri) {
        storageManager.persistUriPermission(uri)
        viewModelScope.launch {
            storageManager.saveRootUri(uri)
        }
    }

    fun exportCurrentTrip() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, message = null) }

            val trip = tripRepository.getActiveTrip()

            if (trip == null) {
                _uiState.update { it.copy(isLoading = false, message = "No active trip") }
                return@launch
            }

            val messages = chatRepository.getMessagesOnce(trip.id)

            val success = storageManager.exportTrip(trip, messages)

            _uiState.update {
                it.copy(
                    isLoading = false,
                    message = if (success) "Trip exported ✅" else "Export failed ❌"
                )
            }
        }
    }

    fun backupAllTrips() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, message = null) }

            val trips = tripRepository.getAllTripsOnce()

            val json = buildString {
                append("[")
                trips.forEachIndexed { index, trip ->
                    append("""{"id":${trip.id},"name":"${trip.name}"}""")
                    if (index != trips.lastIndex) append(",")
                }
                append("]")
            }

            val success = storageManager.saveTextFile("backup_all_trips.json", json)

            _uiState.update {
                it.copy(
                    isLoading = false,
                    message = if (success) "Backup complete ✅" else "Backup failed ❌"
                )
            }
        }
    }
}

data class StorageUiState(
    val isLoading: Boolean = false,
    val message: String? = null
)
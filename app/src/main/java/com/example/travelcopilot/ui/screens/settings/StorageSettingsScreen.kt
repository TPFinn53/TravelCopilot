package com.example.travelcopilot.ui.screens.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.travelcopilot.viewmodel.SettingsViewModel

@Composable
fun StorageSettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val selectedUri by viewModel.rootUriFlow
        .collectAsStateWithLifecycle(initialValue = null)

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri: Uri? ->
        uri?.let { viewModel.onFolderSelected(it) }
    }

    // 🔥 Show snackbar messages
    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(padding)
        ) {

            Text("Storage Settings", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(24.dp))

            Text("Selected Folder", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = selectedUri?.toString() ?: "No folder selected",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { launcher.launch(null) }) {
                Text("Choose Folder")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Divider()

            Spacer(modifier = Modifier.height(16.dp))

            val enabled = selectedUri != null && !uiState.isLoading

            // 📤 Export current trip
            Button(
                onClick = { viewModel.exportCurrentTrip() },
                enabled = enabled,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Export Current Trip")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 💾 Backup all trips
            Button(
                onClick = { viewModel.backupAllTrips() },
                enabled = enabled,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Backup All Trips (JSON)")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}
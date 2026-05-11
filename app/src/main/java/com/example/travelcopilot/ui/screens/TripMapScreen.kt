package com.example.travelcopilot.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

import com.example.travelcopilot.domain.model.Trip
import com.example.travelcopilot.viewmodel.TripViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripMapScreen(
    navController: NavController,
    tripId: Long,
    modifier: Modifier = Modifier,
    viewModel: TripViewModel = hiltViewModel()
) {

    val trip by viewModel
        .observeTrip(tripId)
        .collectAsState(initial = null)

    Scaffold(
        modifier = modifier,

        topBar = {

            TopAppBar(
                title = {
                    Text("Trip Map")
                }
            )
        }
    ) { padding ->

        val currentTrip = trip

        if (currentTrip == null) {

            LoadingMapContent(
                padding = padding
            )

            return@Scaffold
        }

        TripMapContent(
            trip = currentTrip,

            modifier = Modifier
                .padding(padding)
        )
    }
}

@Composable
private fun LoadingMapContent(
    padding: PaddingValues
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),

        contentAlignment = Alignment.Center
    ) {

        CircularProgressIndicator()
    }
}

@Composable
fun TripMapContent(
    trip: Trip,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = trip.name,

            style =
                MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text =
                "Destination: ${
                    trip.destinationName
                        ?: "Unknown"
                }"
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "State: ${trip.state}"
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {

            Box(
                modifier = Modifier
                    .fillMaxSize(),

                contentAlignment = Alignment.Center
            ) {

                Text(
                    text =
                        "Map rendering coming soon"
                )
            }
        }
    }
}
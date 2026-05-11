package com.example.travelcopilot.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.hilt.navigation.compose.hiltViewModel

import com.example.travelcopilot.domain.model.Trip
import com.example.travelcopilot.domain.model.TripState

import com.example.travelcopilot.viewmodel.TripViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripScreen(
    onOpenChat: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TripViewModel = hiltViewModel()
) {

    val trips by viewModel.trips
        .collectAsState(initial = emptyList())

    Scaffold(
        modifier = modifier,

        topBar = {
            TopAppBar(
                title = {
                    Text("Trips")
                }
            )
        }
    ) { padding ->

        if (trips.isEmpty()) {

            EmptyTripContent(
                padding = padding
            )

        } else {

            TripListContent(
                trips = trips,

                padding = padding,

                onStart = { tripId ->
                    viewModel.startTrip(tripId)
                },

                onStop = { tripId ->
                    viewModel.stopTrip(tripId)
                },

                onComplete = { tripId ->
                    viewModel.completeTrip(tripId)
                },

                onOpenChat = onOpenChat
            )
        }
    }
}

@Composable
private fun EmptyTripContent(
    padding: PaddingValues
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(24.dp),

        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "No trips yet",

            style =
                MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text =
                "Create a trip to begin your journey."
        )
    }
}

@Composable
private fun TripListContent(
    trips: List<Trip>,

    padding: PaddingValues,

    onStart: (Long) -> Unit,

    onStop: (Long) -> Unit,

    onComplete: (Long) -> Unit,

    onOpenChat: (Long) -> Unit
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {

        items(
            items = trips,
            key = { trip -> trip.id }
        ) { trip ->

            TripItem(

                trip = trip,

                onStart = {
                    onStart(trip.id)
                },

                onStop = {
                    onStop(trip.id)
                },

                onComplete = {
                    onComplete(trip.id)
                },

                onOpenChat = {
                    onOpenChat(trip.id)
                }
            )
        }
    }
}

@Composable
fun TripItem(
    trip: Trip,

    onStart: () -> Unit,

    onStop: () -> Unit,

    onComplete: () -> Unit,

    onOpenChat: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = trip.name,

                style =
                    MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text =
                    "Destination: ${
                        trip.destinationName
                            ?: "Unknown"
                    }"
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "State: ${trip.state}"
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text =
                    "Distance: ${
                        (trip.totalDistanceMeters / 1000.0)
                    } km"
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (trip.state) {

                TripState.CREATED -> {

                    Button(onClick = onStart) {
                        Text("Start Trip")
                    }
                }

                TripState.DRIVING,
                TripState.IDLE -> {

                    Column(
                        verticalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {

                        Button(onClick = onStop) {
                            Text("Stop")
                        }

                        Button(onClick = onOpenChat) {
                            Text("Open Chat")
                        }
                    }
                }

                TripState.STOPPED,
                TripState.PARKED -> {

                    Column(
                        verticalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {

                        Button(onClick = onStart) {
                            Text("Resume")
                        }

                        Button(onClick = onComplete) {
                            Text("Complete Trip")
                        }

                        Button(onClick = onOpenChat) {
                            Text("Open Chat")
                        }
                    }
                }

                TripState.COMPLETED -> {

                    Column(
                        verticalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {

                        Text("Trip completed")

                        Button(onClick = onOpenChat) {
                            Text("View Chat")
                        }
                    }
                }
            }
        }
    }
}
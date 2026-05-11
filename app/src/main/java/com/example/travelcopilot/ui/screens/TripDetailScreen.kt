package com.example.travelcopilot.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding

import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

import com.example.travelcopilot.domain.model.Trip
import com.example.travelcopilot.domain.model.TripState

import com.example.travelcopilot.viewmodel.TripViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDetailScreen(
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

                    Text(
                        text = trip?.name ?: "Trip"
                    )
                }
            )
        }
    ) { padding ->

        val currentTrip = trip

        if (currentTrip == null) {

            LoadingTripContent(
                padding = padding
            )

            return@Scaffold
        }

        TripDetailContent(

            trip = currentTrip,

            padding = padding,

            onStart = {
                viewModel.startTrip(tripId)
            },

            onStop = {
                viewModel.stopTrip(tripId)
            },

            onComplete = {
                viewModel.completeTrip(tripId)
            },

            onOpenChat = {

                navController.navigate(
                    "chat/$tripId"
                )
            }
        )
    }
}

@Composable
private fun LoadingTripContent(
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
private fun TripDetailContent(
    trip: Trip,

    padding: PaddingValues,

    onStart: () -> Unit,

    onStop: () -> Unit,

    onComplete: () -> Unit,

    onOpenChat: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(16.dp),

        verticalArrangement =
            Arrangement.spacedBy(12.dp)
    ) {

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    text = trip.name,

                    style =
                        MaterialTheme.typography.headlineSmall
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text =
                        "Destination: ${
                            trip.destinationName
                                ?: "Unknown"
                        }"
                )

                Text(
                    text =
                        "State: ${trip.state}"
                )

                Text(
                    text =
                        "Distance: ${
                            trip.totalDistanceMeters / 1000.0
                        } km"
                )

                Text(
                    text =
                        "Drive Time: ${
                            trip.driveTimeMs / 60000
                        } min"
                )

                Text(
                    text =
                        "Idle Time: ${
                            trip.idleTimeMs / 60000
                        } min"
                )
            }
        }

        when (trip.state) {

            TripState.CREATED -> {

                Button(
                    onClick = onStart,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text("Start Trip")
                }
            }

            TripState.DRIVING,
            TripState.IDLE -> {

                Column(
                    verticalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {

                    Button(
                        onClick = onStop,
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Text("Stop")
                    }

                    Button(
                        onClick = onOpenChat,
                        modifier = Modifier.fillMaxWidth()
                    ) {

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

                    Button(
                        onClick = onStart,
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Text("Resume")
                    }

                    Button(
                        onClick = onComplete,
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Text("Complete Trip")
                    }

                    Button(
                        onClick = onOpenChat,
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Text("Open Chat")
                    }
                }
            }

            TripState.COMPLETED -> {

                Column(
                    verticalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {

                    Text(
                        text = "Trip completed",

                        style =
                            MaterialTheme.typography.bodyLarge
                    )

                    Button(
                        onClick = onOpenChat,
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Text("View Chat")
                    }
                }
            }
        }
    }
}
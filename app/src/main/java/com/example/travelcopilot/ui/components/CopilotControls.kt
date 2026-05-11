package com.example.travelcopilot.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.example.travelcopilot.core.state.CopilotState

@Composable
fun CopilotControls(
    state: CopilotState,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onStop: () -> Unit
) {
    when (state) {

        CopilotState.ACTIVE -> {
            Button(onClick = onPause) {
                Text("Pause")
            }
            Button(onClick = onStop) {
                Text("Stop")
            }
        }

        CopilotState.PAUSED -> {
            Button(onClick = onResume) {
                Text("Resume")
            }
            Button(onClick = onStop) {
                Text("Stop")
            }
        }

        CopilotState.IDLE -> {
            Text("Trip not started")
        }

        CopilotState.STOPPED -> {
            Text("Trip completed")
        }
    }
}
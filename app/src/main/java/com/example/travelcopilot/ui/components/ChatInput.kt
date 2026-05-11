package com.example.travelcopilot.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ChatInput(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {

        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("Ask your copilot...") }
        )

        Button(onClick = onSend) {
            Text("Send")
        }
    }
}
package com.example.travelcopilot.ui.components

import androidx.compose.foundation.lazy.*
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier

@Composable
fun MessageList(
    messages: List<String>,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(messages) { message ->
            Text(text = message)
        }
    }
}
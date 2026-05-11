package com.example.travelcopilot.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.travelcopilot.data.local.Role
import com.example.travelcopilot.domain.model.ChatMessage
import com.example.travelcopilot.viewmodel.ChatViewModel

@Composable
fun ChatScreen(
    onBack: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val messages by viewModel.messages.collectAsState()

    // ✅ Persist scroll across tab switches
    val listState = rememberSaveable(saver = LazyListState.Saver) {
        LazyListState()
    }

    Column(modifier = Modifier.fillMaxSize()) {

        // 🔙 Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Chat", style = MaterialTheme.typography.titleLarge)

            Button(onClick = onBack) {
                Text("Back")
            }
        }

        // 💬 Message list
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            state = listState,
            contentPadding = PaddingValues(8.dp)
        ) {
            items(messages, key = { it.id }) { message ->
                ChatBubble(message)
            }

            // 🧠 Typing indicator at bottom
            if (isTyping(messages)) {
                item {
                    TypingIndicator()
                }
            }
        }

        // 🔄 Auto-scroll during streaming
        LaunchedEffect(messages.lastOrNull()?.content) {
            if (messages.isNotEmpty()) {
                listState.animateScrollToItem(messages.lastIndex)
            }
        }

        // ✏️ Input
        ChatInputBar(
            onSend = { text ->
                viewModel.sendMessage(text)
            }
        )
    }
}
fun isTyping(messages: List<ChatMessage>): Boolean {
    val last = messages.lastOrNull() ?: return false

    return last.role == Role.AI && last.content.isBlank()
}
@Composable
fun TypingIndicator() {

    var dots by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        while (true) {
            dots = when (dots.length) {
                0 -> "."
                1 -> ".."
                2 -> "..."
                else -> ""
            }
            kotlinx.coroutines.delay(400)
        }
    }

    Text(
        text = "AI is typing$dots",
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(8.dp)
    )
}
@Composable
fun ChatBubble(message: ChatMessage) {

    val isUser = message.role == Role.USER

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            color = if (isUser)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.secondary,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .padding(4.dp)
                .widthIn(max = 280.dp)
        ) {
            Text(
                text = message.content,
                modifier = Modifier.padding(12.dp),
                color = if (isUser)
                    MaterialTheme.colorScheme.onPrimary
                else
                    MaterialTheme.colorScheme.onSecondary
            )
        }
    }
}
@Composable
fun ChatInputBar(
    onSend: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {

        TextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.weight(1f),
            placeholder = { Text("Type a message...") }
        )

        Spacer(modifier = Modifier.width(8.dp))

        Button(
            onClick = {
                if (text.isNotBlank()) {
                    onSend(text)
                    text = ""
                }
            }
        ) {
            Text("Send")
        }
    }
}

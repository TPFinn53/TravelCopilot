package com.example.travelcopilot.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.travelcopilot.core.voice.VoiceState
import kotlin.random.Random

@Composable
fun VoiceIndicator(
    state: VoiceState,
    amplitude: Float,
    modifier: Modifier = Modifier
) {

    val infiniteTransition = rememberInfiniteTransition()

    // 🎧 Waveform animation (only used when SPEAKING)
    val waveHeights = List(5) {
        infiniteTransition.animateFloat(
            initialValue = 10f,
            targetValue = Random.nextInt(20, 60).toFloat(),
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 300 + Random.nextInt(0, 200),
                    easing = FastOutSlowInEasing
                ),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    // 🔵 Pulse for LISTENING
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (state == VoiceState.LISTENING) 1.3f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        )
    )

    val color = when (state) {
        VoiceState.IDLE -> Color.Gray
        VoiceState.LISTENING -> Color.Green
        VoiceState.PROCESSING -> Color.Yellow
        VoiceState.SPEAKING -> Color.Cyan
    }

    val label = when (state) {
        VoiceState.IDLE -> "Idle"
        VoiceState.LISTENING -> "Listening"
        VoiceState.PROCESSING -> "Thinking"
        VoiceState.SPEAKING -> "Speaking"
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        when (state) {

            VoiceState.SPEAKING -> {
                WaveformBars(waveHeights)
            }

            else -> {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .scale(pulseScale)
                        .background(color, shape = CircleShape)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = label)
    }
}
@Composable
fun WaveformBars(
    waves: List<State<Float>>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(48.dp)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {

        waves.forEach { heightState ->

            val height = heightState.value

            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height(height.dp)
                    .background(Color.Cyan, shape = CircleShape)
            )
        }
    }
}
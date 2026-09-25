package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.util.AudioHelper
import kotlinx.coroutines.delay

@Composable
fun VoicePlayWidget(
    audioUri: String,
    durationMs: Long,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(false) }
    var currentProgress by remember { mutableStateOf(0f) }
    var elapsedMs by remember { mutableStateOf(0L) }

    // Audio Playback progress simulator
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            val start = System.currentTimeMillis()
            val initialElapsed = elapsedMs
            while (isPlaying) {
                delay(50)
                val elapsed = System.currentTimeMillis() - start + initialElapsed
                if (elapsed >= durationMs) {
                    elapsedMs = durationMs
                    currentProgress = 1f
                    isPlaying = false
                } else {
                    elapsedMs = elapsed
                    currentProgress = elapsed.toFloat() / durationMs.toFloat()
                }
            }
        }
    }

    // Stop playing on dispose
    DisposableEffect(Unit) {
        onDispose {
            if (isPlaying) {
                AudioHelper.stopPlaying()
            }
        }
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Play/Pause Fab button
            IconButton(
                onClick = {
                    if (isPlaying) {
                        AudioHelper.stopPlaying()
                        isPlaying = false
                    } else {
                        isPlaying = true
                        AudioHelper.startPlaying(
                            context = context,
                            audioPath = audioUri,
                            onComplete = {
                                isPlaying = false
                                elapsedMs = 0L
                                currentProgress = 0f
                            },
                            onError = { _ ->
                                isPlaying = false
                            }
                        )
                    }
                },
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.primary, shape = CircleShape)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Tạm dừng" else "Phát ghi âm",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Volume Indicator
            Icon(
                imageVector = Icons.Default.VolumeUp,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Progress bar and timers
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Voice Memo",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${AudioHelper.formatDuration(elapsedMs)} / ${AudioHelper.formatDuration(durationMs)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(6.dp))
                
                // Beautiful linear indicator progress bar
                LinearProgressIndicator(
                    progress = { currentProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
                )
            }
        }
    }
}

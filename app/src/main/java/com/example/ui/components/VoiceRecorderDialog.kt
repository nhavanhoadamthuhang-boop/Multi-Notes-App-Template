package com.example.ui.components

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardVoice
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.example.ui.util.AudioHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceRecorderDialog(
    onDismiss: () -> Unit,
    onResult: (text: String, audioPath: String?, durationMs: Long) -> Unit,
    titleText: String = "Ghi âm giọng nói"
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        if (!granted) {
            Toast.makeText(context, "Ứng dụng cần quyền ghi âm để sử dụng tính năng này!", Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(Unit) {
        if (!hasPermission) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    if (!hasPermission) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Yêu cầu quyền truy cập") },
            text = { Text("Quyền ghi âm (Microphone) là bắt buộc để có thể ghi âm voice memo và chuyển đổi giọng nói thành văn bản.") },
            confirmButton = {
                Button(onClick = { permissionLauncher.launch(Manifest.permission.RECORD_AUDIO) }) {
                    Text("Cấp quyền")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Đóng")
                }
            }
        )
        return
    }

    Dialog(
        onDismissRequest = {
            AudioHelper.stopRecording()
            onDismiss()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = titleText,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = {
                        AudioHelper.stopRecording()
                        onDismiss()
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "Đóng")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                VoiceRecorderContent(
                    context = context,
                    onResult = { text, file, duration ->
                        onResult(text, file?.absolutePath, duration)
                        onDismiss()
                    }
                )
            }
        }
    }
}

@Composable
fun VoiceRecorderContent(
    context: Context,
    onResult: (text: String, file: File?, durationMs: Long) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var isRecording by remember { mutableStateOf(false) }
    var durationMs by remember { mutableStateOf(0L) }
    var transcribedText by remember { mutableStateOf("") }
    var currentFile by remember { mutableStateOf<File?>(null) }
    var isListeningSpeech by remember { mutableStateOf(false) }
    var isUsingFallback by remember { mutableStateOf(false) }

    // Speech Recognizer
    var speechRecognizer by remember { mutableStateOf<SpeechRecognizer?>(null) }
    
    // Timer Effect
    LaunchedEffect(isRecording) {
        if (isRecording) {
            durationMs = 0L
            while (isRecording) {
                delay(100)
                durationMs += 100
            }
        }
    }

    // Initialize native Speech Recognizer safely
    fun startSpeechRecognizer() {
        try {
            if (SpeechRecognizer.isRecognitionAvailable(context)) {
                val recognizer = SpeechRecognizer.createSpeechRecognizer(context)
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, "vi-VN")
                    putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                }

                recognizer.setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        isListeningSpeech = true
                    }
                    override fun onBeginningOfSpeech() {}
                    override fun onRmsChanged(rmsdB: Float) {}
                    override fun onBufferReceived(buffer: ByteArray?) {}
                    override fun onEndOfSpeech() {
                        isListeningSpeech = false
                    }
                    override fun onError(error: Int) {
                        isListeningSpeech = false
                        isUsingFallback = true
                    }
                    override fun onResults(results: Bundle?) {
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        if (!matches.isNullOrEmpty()) {
                            transcribedText = matches[0]
                        }
                    }
                    override fun onPartialResults(partialResults: Bundle?) {
                        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        if (!matches.isNullOrEmpty()) {
                            transcribedText = matches[0]
                        }
                    }
                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
                
                recognizer.startListening(intent)
                speechRecognizer = recognizer
            } else {
                isUsingFallback = true
            }
        } catch (e: Exception) {
            isUsingFallback = true
        }
    }

    fun stopSpeechRecognizer() {
        try {
            speechRecognizer?.apply {
                stopListening()
                destroy()
            }
        } catch (e: Exception) {
            // Safe ignore
        } finally {
            speechRecognizer = null
            isListeningSpeech = false
        }
    }

    // Clean up
    DisposableEffect(Unit) {
        onDispose {
            try {
                speechRecognizer?.destroy()
            } catch (e: Exception) { /* ignore */ }
        }
    }

    // Handle Start/Stop Recording
    fun toggleRecording() {
        if (!isRecording) {
            val file = AudioHelper.startRecording(context)
            if (file != null) {
                currentFile = file
                isRecording = true
                isUsingFallback = false
                startSpeechRecognizer()
            } else {
                Toast.makeText(context, "Không thể bắt đầu ghi âm!", Toast.LENGTH_SHORT).show()
            }
        } else {
            val (_, duration) = AudioHelper.stopRecording()
            isRecording = false
            stopSpeechRecognizer()
            
            // If native SpeechRecognizer failed/was absent or we are in simulation mode
            if (isUsingFallback || transcribedText.isBlank()) {
                isUsingFallback = true
                // Pre-populate with high quality smart simulation templates for convenient testing and offline usage
                if (transcribedText.isBlank()) {
                    transcribedText = "Ghi chú giọng nói lúc " + java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
                }
            }
        }
    }

    // Infinite transition for pulsing wave animation during recording
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    // Visual wave UI Container
    Box(
        modifier = Modifier
            .size(160.dp)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isRecording) {
            // Pulse wave 1
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .scale(pulseScale)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = pulseAlpha),
                        shape = CircleShape
                    )
            )
            // Pulse wave 2
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .scale(pulseScale * 0.8f)
                    .background(
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = pulseAlpha * 1.2f),
                        shape = CircleShape
                    )
            )
        }

        // Inner Record Button
        val buttonColor by animateColorAsState(
            targetValue = if (isRecording) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
            label = "buttonColor"
        )
        FloatingActionButton(
            onClick = { toggleRecording() },
            modifier = Modifier.size(80.dp),
            shape = CircleShape,
            containerColor = buttonColor,
            contentColor = Color.White
        ) {
            Icon(
                imageVector = if (isRecording) Icons.Default.MicOff else Icons.Default.Mic,
                contentDescription = if (isRecording) "Dừng ghi âm" else "Bắt đầu ghi âm",
                modifier = Modifier.size(36.dp)
            )
        }
    }

    // Status Timer
    Text(
        text = AudioHelper.formatDuration(durationMs),
        style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp
        ),
        color = if (isRecording) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
    )

    Text(
        text = if (isRecording) "Đang ghi âm giọng nói..." else "Chạm vào Micro để bắt đầu ghi âm",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(vertical = 8.dp)
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Real-time speech display & editable transcript
    OutlinedTextField(
        value = transcribedText,
        onValueChange = { transcribedText = it },
        label = { Text("Bản dịch giọng nói (Có thể chỉnh sửa)") },
        placeholder = { Text("Kết quả dịch giọng nói sẽ xuất hiện ở đây...") },
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = RoundedCornerShape(12.dp),
        maxLines = 4
    )

    // Convenient testing templates if using fallback
    if (isUsingFallback && !isRecording) {
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Chọn mẫu dịch nhanh (Hỗ trợ giả lập/test):",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Left
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val templates = listOf(
                "Họp phòng ban sáng thứ hai",
                "Mua sữa, táo và bánh mì",
                "Ý kiến đóng góp ý tưởng ghi chú",
                "Ghi chú nhắc nhở công việc ngày mai"
            )
            templates.forEach { tmpl ->
                SuggestionChip(
                    onClick = { transcribedText = tmpl },
                    label = {
                        Text(
                            text = tmpl,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    )
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Confirm / Reset Actions
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(
            onClick = {
                transcribedText = ""
                currentFile = null
                durationMs = 0L
                isUsingFallback = false
            },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            enabled = !isRecording && (transcribedText.isNotBlank() || currentFile != null)
        ) {
            Icon(Icons.Default.Refresh, contentDescription = "Xóa nháp")
            Spacer(modifier = Modifier.width(4.dp))
            Text("Xóa nháp")
        }

        Button(
            onClick = {
                onResult(transcribedText, currentFile, durationMs)
            },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            enabled = !isRecording && (transcribedText.isNotBlank() || currentFile != null)
        ) {
            Icon(Icons.Default.Check, contentDescription = "Áp dụng")
            Spacer(modifier = Modifier.width(4.dp))
            Text("Áp dụng")
        }
    }
}

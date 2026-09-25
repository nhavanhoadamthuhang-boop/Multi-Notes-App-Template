package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.repository.ThemeMode
import com.example.ui.NotesViewModel
import com.example.ui.screens.NoteDetailScreen
import com.example.ui.screens.NoteListScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: NotesViewModel = viewModel()
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
            val isDark = when (themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
                ThemeMode.NIGHT_READING -> true
            }
            val isNightReading = themeMode == ThemeMode.NIGHT_READING

            MyApplicationTheme(darkTheme = isDark, nightReading = isNightReading) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    NotesApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun NotesApp(viewModel: NotesViewModel = viewModel()) {
    val selectedNoteId by viewModel.selectedNoteId.collectAsStateWithLifecycle()
    val allNotes by viewModel.allNotes.collectAsStateWithLifecycle()
    val context = androidx.compose.ui.platform.LocalContext.current

    androidx.compose.runtime.LaunchedEffect(allNotes) {
        com.example.widget.WidgetUpdater.updateWidget(context)
    }

    if (selectedNoteId != null) {
        BackHandler {
            viewModel.selectNote(null)
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val formFactor = when {
            maxWidth >= 1200.dp -> FormFactor.DESKTOP
            maxWidth >= 840.dp -> FormFactor.LAPTOP
            maxWidth >= 600.dp -> FormFactor.TABLET
            else -> FormFactor.PHONE
        }

        val listPaneWidth = when (formFactor) {
            FormFactor.DESKTOP -> 440.dp
            FormFactor.LAPTOP -> 400.dp
            FormFactor.TABLET -> 360.dp
            FormFactor.PHONE -> maxWidth
        }

        when (formFactor) {
            FormFactor.DESKTOP -> {
                // Desktop 3-Column Productivity Layout (Sidebar Rail + List Pane + Detail Pane)
                Row(modifier = Modifier.fillMaxSize()) {
                    // Desktop Left Navigation Rail
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier
                            .width(80.dp)
                            .fillMaxHeight()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(vertical = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            androidx.compose.material3.IconButton(
                                onClick = { viewModel.startCreateNote() }
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        androidx.compose.material3.Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Tạo ghi chú",
                                            tint = MaterialTheme.colorScheme.onPrimary
                                        )
                                    }
                                }
                            }
                            androidx.compose.material3.HorizontalDivider(modifier = Modifier.padding(horizontal = 12.dp))
                            androidx.compose.material3.IconButton(onClick = { viewModel.selectNote(null) }) {
                                androidx.compose.material3.Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = "Tất cả ghi chú",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    // Vertical Divider
                    Surface(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                        modifier = Modifier
                            .width(1.dp)
                            .fillMaxHeight()
                    ) {}

                    // Middle List Pane
                    Box(
                        modifier = Modifier
                            .width(listPaneWidth)
                            .fillMaxHeight()
                    ) {
                        NoteListScreen(
                            viewModel = viewModel,
                            isEmbeddedInSplitPane = true
                        )
                    }

                    // Vertical Divider
                    Surface(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                        modifier = Modifier
                            .width(1.dp)
                            .fillMaxHeight()
                    ) {}

                    // Right Detail / Editor Pane
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        if (selectedNoteId != null) {
                            NoteDetailScreen(
                                viewModel = viewModel,
                                isEmbeddedInSplitPane = true
                            )
                        } else {
                            TabletEmptyDetailPlaceholder(
                                onCreateNote = { viewModel.startCreateNote() }
                            )
                        }
                    }
                }
            }
            FormFactor.LAPTOP, FormFactor.TABLET -> {
                // Laptop & Tablet 2-Pane Master-Detail Layout
                Row(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .width(listPaneWidth)
                            .fillMaxHeight()
                    ) {
                        NoteListScreen(
                            viewModel = viewModel,
                            isEmbeddedInSplitPane = true
                        )
                    }

                    Surface(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                        modifier = Modifier
                            .width(1.dp)
                            .fillMaxHeight()
                    ) {}

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        if (selectedNoteId != null) {
                            NoteDetailScreen(
                                viewModel = viewModel,
                                isEmbeddedInSplitPane = true
                            )
                        } else {
                            TabletEmptyDetailPlaceholder(
                                onCreateNote = { viewModel.startCreateNote() }
                            )
                        }
                    }
                }
            }
            FormFactor.PHONE -> {
                // Mobile Phone Single-Pane Animated Transition Layout
                AnimatedContent(
                    targetState = selectedNoteId != null,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "ScreenTransition"
                ) { hasSelectedNote ->
                    if (hasSelectedNote) {
                        NoteDetailScreen(
                            viewModel = viewModel,
                            isEmbeddedInSplitPane = false
                        )
                    } else {
                        NoteListScreen(
                            viewModel = viewModel,
                            isEmbeddedInSplitPane = false
                        )
                    }
                }
            }
        }
    }
}

enum class FormFactor {
    PHONE, TABLET, LAPTOP, DESKTOP
}

@Composable
private fun TabletEmptyDetailPlaceholder(
    onCreateNote: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.width(360.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                modifier = Modifier.size(96.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            Text(
                text = "Chọn một ghi chú",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Hãy chọn một ghi chú từ danh sách bên trái để đọc nội dung và thảo luận bình luận, hoặc bấm nút bên dưới để tạo ghi chú mới.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onCreateNote,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Tạo ghi chú mới", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}


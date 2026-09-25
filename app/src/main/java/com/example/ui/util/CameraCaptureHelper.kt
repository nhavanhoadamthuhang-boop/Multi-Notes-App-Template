package com.example.ui.util

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CameraCaptureHelper {

    /**
     * Creates a new unique image file in the app's internal files directory
     * and returns both the File and its content Uri via FileProvider.
     */
    fun createPhotoUri(context: Context): Pair<File, Uri> {
        val photosDir = File(context.filesDir, "photos")
        if (!photosDir.exists()) {
            photosDir.mkdirs()
        }
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val photoFile = File(photosDir, "IMG_${timestamp}_${System.currentTimeMillis()}.jpg")
        
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            photoFile
        )
        return Pair(photoFile, uri)
    }
}

/**
 * Jetpack Compose helper hook to launch camera photo capture and return the content Uri.
 */
@Composable
fun rememberCameraLauncher(
    onPhotoCaptured: (Uri) -> Unit
): () -> Unit {
    val context = LocalContext.current
    var currentUri by remember { mutableStateOf<Uri?>(null) }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            currentUri?.let { uri ->
                onPhotoCaptured(uri)
            }
        }
    }

    return {
        try {
            val (_, uri) = CameraCaptureHelper.createPhotoUri(context)
            currentUri = uri
            takePictureLauncher.launch(uri)
        } catch (e: Exception) {
            Toast.makeText(context, "Không thể mở máy ảnh: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}

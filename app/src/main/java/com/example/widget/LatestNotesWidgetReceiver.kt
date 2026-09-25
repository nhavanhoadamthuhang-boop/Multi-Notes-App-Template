package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import com.example.data.local.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LatestNotesWidgetReceiver : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.widget_latest_notes)

        // Intent to launch app when widget is clicked
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

        // Query database on IO thread
        CoroutineScope(Dispatchers.IO).launch {
            val database = AppDatabase.getDatabase(context)
            val latestNote = database.noteDao().getWidgetNote()
            val latestComment = database.commentDao().getWidgetComment()

            // Update UI on main thread is not needed for RemoteViews, but we need to notify AppWidgetManager
            if (latestNote != null) {
                views.setTextViewText(R.id.widget_note_title, latestNote.title.takeIf { it.isNotBlank() } ?: "Ghi chú không tên")
                views.setTextViewText(R.id.widget_note_content, latestNote.description.takeIf { it.isNotBlank() } ?: "Trống")
            } else {
                views.setTextViewText(R.id.widget_note_title, "Không có ghi chú")
                views.setTextViewText(R.id.widget_note_content, "")
            }

            if (latestComment != null) {
                views.setTextViewText(R.id.widget_comment_content, latestComment.content.takeIf { it.isNotBlank() } ?: "Trống")
            } else {
                views.setTextViewText(R.id.widget_comment_content, "Không có bình luận")
            }

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}

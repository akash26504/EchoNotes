package com.example.videosummarise.ui.selection.model

import android.net.Uri

data class Video(
    val id: Long,
    val name: String,
    val uri: Uri,
    val duration: Long, // in milliseconds
    val size: Long, // in bytes
    val path: String
) {
    fun getFormattedDuration(): String {
        val seconds = duration / 1000
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%d:%02d", minutes, remainingSeconds)
    }
    
    fun getFormattedSize(): String {
        return when {
            size < 1024 -> "${size}B"
            size < 1024 * 1024 -> "${size / 1024}KB"
            size < 1024 * 1024 * 1024 -> "${size / (1024 * 1024)}MB"
            else -> "${size / (1024 * 1024 * 1024)}GB"
        }
    }
}

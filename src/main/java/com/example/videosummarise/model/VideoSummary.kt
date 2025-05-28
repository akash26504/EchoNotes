package com.example.videosummarise.model

data class VideoSummary(
    val id: String,
    val title: String,
    val description: String,
    val date: String,
    val thumbnailUrl: String? = null,
    val summaryText: String? = null,
    val captions: List<Caption> = emptyList()
)

data class Caption(
    val timestamp: String,
    val text: String
)
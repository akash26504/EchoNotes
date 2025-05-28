package com.example.videosummarise.ai.models

import android.graphics.Bitmap

// Video metadata structure
data class VideoMetadata(
    val duration: Long,
    val width: Int,
    val height: Int,
    val title: String,
    val frameRate: Double
)

// Audio analysis results
data class AudioAnalysis(
    val transcript: String,
    val segments: List<TranscriptSegment>,
    val confidence: Double,
    val language: String,
    val speakerCount: Int,
    val audioQuality: AudioQuality
)

data class TranscriptSegment(
    val startTime: Long,
    val endTime: Long,
    val text: String,
    val confidence: Double,
    val speakerId: Int? = null
)

data class TranscriptionResult(
    val fullText: String,
    val segments: List<TranscriptSegment>,
    val confidence: Double,
    val detectedLanguage: String
)

enum class AudioQuality {
    EXCELLENT, GOOD, FAIR, POOR
}

data class AudioFeatures(
    val estimatedSpeakers: Int,
    val quality: AudioQuality
)

// Visual analysis results
data class VisualAnalysis(
    val keyFrames: List<Bitmap>,
    val detectedObjects: List<String>,
    val detectedText: List<String>,
    val sceneTypes: List<String>,
    val visualQuality: VisualQuality,
    val hasSlides: Boolean,
    val hasFaces: Boolean
)

data class FrameAnalysis(
    val timestamp: Long,
    val objects: List<String>,
    val textElements: List<String>,
    val faces: List<FaceDetection>,
    val sceneType: String,
    val sharpness: Double,
    val brightness: Double,
    val motionLevel: Double
)

data class FaceDetection(
    val confidence: Double,
    val boundingBox: BoundingBox,
    val emotions: Map<String, Double>
)

data class BoundingBox(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
)

enum class VisualQuality {
    EXCELLENT, GOOD, FAIR, POOR
}

// Content classification
data class ContentClassification(
    val primaryType: ContentType,
    val confidence: Double,
    val detectedTopics: List<String>,
    val estimatedDuration: Long,
    val complexity: ContentComplexity
)

enum class ContentType {
    TUTORIAL,
    EDUCATIONAL_LECTURE,
    PRESENTATION,
    ENTERTAINMENT,
    MUSIC_VIDEO,
    SPORTS,
    NEWS,
    DOCUMENTARY,
    INTERVIEW,
    WEBINAR,
    PRODUCT_DEMO,
    GAMING,
    VLOG,
    UNKNOWN
}

enum class ContentComplexity {
    BEGINNER, INTERMEDIATE, ADVANCED, EXPERT
}

// AI Model interfaces
interface AIModel {
    suspend fun initialize(): Boolean
    fun isInitialized(): Boolean
    suspend fun cleanup()
}

// Summarization parameters
data class SummarizationConfig(
    val maxSummaryLength: Int = 500,
    val includeTimestamps: Boolean = true,
    val focusAreas: List<String> = emptyList(),
    val summaryStyle: SummaryStyle = SummaryStyle.COMPREHENSIVE
)

enum class SummaryStyle {
    BRIEF, COMPREHENSIVE, TECHNICAL, CASUAL
}

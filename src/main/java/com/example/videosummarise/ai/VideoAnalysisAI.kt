package com.example.videosummarise.ai

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMetadataRetriever
import android.net.Uri
import com.example.videosummarise.ai.models.*
import com.example.videosummarise.data.model.Caption
import com.example.videosummarise.data.model.VideoSummary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * AI-powered video analysis and summarization engine
 * Combines multiple AI models for comprehensive video understanding
 */
class VideoAnalysisAI(private val context: Context) {
    
    private val audioTranscriber = AudioTranscriptionAI()
    private val visualAnalyzer = VisualAnalysisAI()
    private val textSummarizer = TextSummarizationAI()
    private val contentClassifier = ContentClassificationAI()
    private val keyPointExtractor = KeyPointExtractionAI()
    
    suspend fun analyzeVideo(videoUri: String): VideoSummary = withContext(Dispatchers.IO) {
        try {
            // Step 1: Extract video metadata
            val metadata = extractVideoMetadata(videoUri)
            
            // Step 2: Extract and analyze audio (transcription)
            val audioAnalysis = analyzeAudio(videoUri)
            
            // Step 3: Extract and analyze visual frames
            val visualAnalysis = analyzeVisualContent(videoUri)
            
            // Step 4: Classify content type and topic
            val contentClassification = classifyContent(metadata, audioAnalysis, visualAnalysis)
            
            // Step 5: Generate comprehensive summary
            val summary = generateSummary(audioAnalysis, visualAnalysis, contentClassification)
            
            // Step 6: Extract key points
            val keyPoints = extractKeyPoints(audioAnalysis.transcript, contentClassification)
            
            // Step 7: Generate timestamped captions
            val captions = generateTimestampedCaptions(audioAnalysis.segments)
            
            VideoSummary(
                id = "ai_summary_${System.currentTimeMillis()}",
                videoUri = videoUri,
                title = metadata.title,
                summary = summary,
                keyPoints = keyPoints,
                captions = captions,
                duration = metadata.duration
            )
            
        } catch (e: Exception) {
            throw VideoAnalysisException("Failed to analyze video: ${e.message}", e)
        }
    }
    
    private suspend fun extractVideoMetadata(videoUri: String): VideoMetadata {
        return withContext(Dispatchers.IO) {
            val retriever = MediaMetadataRetriever()
            try {
                retriever.setDataSource(context, Uri.parse(videoUri))
                
                val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L
                val width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toIntOrNull() ?: 0
                val height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toIntOrNull() ?: 0
                val title = getVideoTitle(videoUri)
                
                VideoMetadata(
                    duration = duration,
                    width = width,
                    height = height,
                    title = title,
                    frameRate = 30.0 // Default, could be extracted more precisely
                )
            } finally {
                retriever.release()
            }
        }
    }
    
    private suspend fun analyzeAudio(videoUri: String): AudioAnalysis {
        return withContext(Dispatchers.IO) {
            // Extract audio from video
            val audioFile = extractAudioFromVideo(videoUri)
            
            // Transcribe audio to text with timestamps
            val transcriptionResult = audioTranscriber.transcribe(audioFile)
            
            // Analyze audio characteristics
            val audioFeatures = analyzeAudioFeatures(audioFile)
            
            AudioAnalysis(
                transcript = transcriptionResult.fullText,
                segments = transcriptionResult.segments,
                confidence = transcriptionResult.confidence,
                language = transcriptionResult.detectedLanguage,
                speakerCount = audioFeatures.estimatedSpeakers,
                audioQuality = audioFeatures.quality
            )
        }
    }
    
    private suspend fun analyzeVisualContent(videoUri: String): VisualAnalysis {
        return withContext(Dispatchers.IO) {
            // Extract key frames from video
            val keyFrames = extractKeyFrames(videoUri)
            
            // Analyze each frame for objects, scenes, text
            val frameAnalyses = keyFrames.map { frame ->
                visualAnalyzer.analyzeFrame(frame)
            }
            
            // Aggregate visual information
            val detectedObjects = frameAnalyses.flatMap { it.objects }.distinct()
            val detectedText = frameAnalyses.flatMap { it.textElements }
            val sceneTypes = frameAnalyses.map { it.sceneType }.distinct()
            val visualQuality = calculateVisualQuality(frameAnalyses)
            
            VisualAnalysis(
                keyFrames = keyFrames,
                detectedObjects = detectedObjects,
                detectedText = detectedText,
                sceneTypes = sceneTypes,
                visualQuality = visualQuality,
                hasSlides = detectSlidePresentation(frameAnalyses),
                hasFaces = frameAnalyses.any { it.faces.isNotEmpty() }
            )
        }
    }
    
    private suspend fun classifyContent(
        metadata: VideoMetadata,
        audioAnalysis: AudioAnalysis,
        visualAnalysis: VisualAnalysis
    ): ContentClassification {
        return contentClassifier.classify(
            title = metadata.title,
            transcript = audioAnalysis.transcript,
            visualElements = visualAnalysis.detectedObjects + visualAnalysis.detectedText,
            duration = metadata.duration,
            hasSlides = visualAnalysis.hasSlides,
            speakerCount = audioAnalysis.speakerCount
        )
    }
    
    private suspend fun generateSummary(
        audioAnalysis: AudioAnalysis,
        visualAnalysis: VisualAnalysis,
        classification: ContentClassification
    ): String {
        return textSummarizer.generateSummary(
            transcript = audioAnalysis.transcript,
            contentType = classification.primaryType,
            topics = classification.detectedTopics,
            keyVisualElements = visualAnalysis.detectedObjects.take(5),
            duration = classification.estimatedDuration
        )
    }
    
    private suspend fun extractKeyPoints(
        transcript: String,
        classification: ContentClassification
    ): List<String> {
        return keyPointExtractor.extractKeyPoints(
            text = transcript,
            contentType = classification.primaryType,
            maxPoints = 8
        )
    }
    
    private suspend fun generateTimestampedCaptions(segments: List<TranscriptSegment>): List<Caption> {
        return segments.map { segment ->
            Caption(
                startTime = segment.startTime,
                endTime = segment.endTime,
                text = segment.text
            )
        }
    }
    
    // Helper methods for video processing
    private suspend fun extractAudioFromVideo(videoUri: String): File {
        // Implementation would extract audio track from video
        // For now, return a placeholder
        return File.createTempFile("audio", ".wav", context.cacheDir)
    }
    
    private suspend fun extractKeyFrames(videoUri: String, maxFrames: Int = 10): List<Bitmap> {
        return withContext(Dispatchers.IO) {
            val frames = mutableListOf<Bitmap>()
            val retriever = MediaMetadataRetriever()
            
            try {
                retriever.setDataSource(context, Uri.parse(videoUri))
                val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L
                
                if (duration > 0) {
                    val interval = duration / maxFrames
                    for (i in 0 until maxFrames) {
                        val timeUs = i * interval * 1000 // Convert to microseconds
                        val frame = retriever.getFrameAtTime(timeUs, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                        frame?.let { frames.add(it) }
                    }
                }
            } finally {
                retriever.release()
            }
            
            frames
        }
    }
    
    private fun getVideoTitle(videoUri: String): String {
        return try {
            val cursor = context.contentResolver.query(
                Uri.parse(videoUri), 
                arrayOf(android.provider.OpenableColumns.DISPLAY_NAME), 
                null, null, null
            )
            cursor?.use {
                if (it.moveToFirst()) {
                    val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    if (nameIndex >= 0) {
                        return it.getString(nameIndex) ?: "Unknown Video"
                    }
                }
            }
            "Unknown Video"
        } catch (e: Exception) {
            "Unknown Video"
        }
    }
    
    private suspend fun analyzeAudioFeatures(audioFile: File): AudioFeatures {
        // Placeholder for audio feature analysis
        return AudioFeatures(
            estimatedSpeakers = 1,
            quality = AudioQuality.GOOD
        )
    }
    
    private fun calculateVisualQuality(frameAnalyses: List<FrameAnalysis>): VisualQuality {
        // Analyze frame quality metrics
        val avgSharpness = frameAnalyses.map { it.sharpness }.average()
        val avgBrightness = frameAnalyses.map { it.brightness }.average()
        
        return when {
            avgSharpness > 0.8 && avgBrightness > 0.6 -> VisualQuality.EXCELLENT
            avgSharpness > 0.6 && avgBrightness > 0.4 -> VisualQuality.GOOD
            avgSharpness > 0.4 -> VisualQuality.FAIR
            else -> VisualQuality.POOR
        }
    }
    
    private fun detectSlidePresentation(frameAnalyses: List<FrameAnalysis>): Boolean {
        // Detect if video contains presentation slides
        val textFrames = frameAnalyses.count { it.textElements.isNotEmpty() }
        val staticFrames = frameAnalyses.count { it.motionLevel < 0.1 }
        
        return textFrames > frameAnalyses.size * 0.6 && staticFrames > frameAnalyses.size * 0.7
    }
}

class VideoAnalysisException(message: String, cause: Throwable? = null) : Exception(message, cause)

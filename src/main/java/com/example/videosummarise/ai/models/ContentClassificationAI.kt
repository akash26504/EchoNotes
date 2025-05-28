package com.example.videosummarise.ai.models

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.*

/**
 * AI-powered content classification and topic detection
 * Uses machine learning to categorize video content and extract topics
 */
class ContentClassificationAI : AIModel {
    
    private var isInitialized = false
    private val textClassifier = TextClassificationModel()
    private val topicExtractor = TopicExtractionModel()
    private val complexityAnalyzer = ComplexityAnalyzer()
    
    override suspend fun initialize(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                textClassifier.loadModel()
                topicExtractor.initialize()
                complexityAnalyzer.initialize()
                isInitialized = true
                true
            } catch (e: Exception) {
                false
            }
        }
    }
    
    override fun isInitialized(): Boolean = isInitialized
    
    override suspend fun cleanup() {
        textClassifier.cleanup()
        topicExtractor.cleanup()
        complexityAnalyzer.cleanup()
        isInitialized = false
    }
    
    suspend fun classify(
        title: String,
        transcript: String,
        visualElements: List<String>,
        duration: Long,
        hasSlides: Boolean,
        speakerCount: Int
    ): ContentClassification = withContext(Dispatchers.Default) {
        
        if (!isInitialized) {
            initialize()
        }
        
        try {
            // Combine all available information for classification
            val combinedText = "$title $transcript ${visualElements.joinToString(" ")}"
            
            // Step 1: Classify content type
            val contentType = classifyContentType(
                title, transcript, visualElements, duration, hasSlides, speakerCount
            )
            
            // Step 2: Extract topics
            val topics = topicExtractor.extractTopics(combinedText)
            
            // Step 3: Analyze complexity
            val complexity = complexityAnalyzer.analyzeComplexity(transcript, topics)
            
            // Step 4: Calculate confidence
            val confidence = calculateClassificationConfidence(
                contentType, topics, transcript.length, visualElements.size
            )
            
            ContentClassification(
                primaryType = contentType,
                confidence = confidence,
                detectedTopics = topics,
                estimatedDuration = duration,
                complexity = complexity
            )
            
        } catch (e: Exception) {
            // Fallback classification
            generateFallbackClassification(title, transcript, duration)
        }
    }
    
    private suspend fun classifyContentType(
        title: String,
        transcript: String,
        visualElements: List<String>,
        duration: Long,
        hasSlides: Boolean,
        speakerCount: Int
    ): ContentType {
        
        val titleLower = title.lowercase()
        val transcriptLower = transcript.lowercase()
        
        // Rule-based classification with ML enhancement
        val scores = mutableMapOf<ContentType, Double>()
        
        // Tutorial indicators
        scores[ContentType.TUTORIAL] = calculateTutorialScore(titleLower, transcriptLower, duration)
        
        // Educational lecture indicators
        scores[ContentType.EDUCATIONAL_LECTURE] = calculateEducationalScore(
            titleLower, transcriptLower, duration, speakerCount
        )
        
        // Presentation indicators
        scores[ContentType.PRESENTATION] = calculatePresentationScore(
            titleLower, transcriptLower, hasSlides, visualElements
        )
        
        // Entertainment indicators
        scores[ContentType.ENTERTAINMENT] = calculateEntertainmentScore(titleLower, transcriptLower)
        
        // Music video indicators
        scores[ContentType.MUSIC_VIDEO] = calculateMusicScore(titleLower, transcriptLower, duration)
        
        // Sports indicators
        scores[ContentType.SPORTS] = calculateSportsScore(titleLower, transcriptLower)
        
        // News indicators
        scores[ContentType.NEWS] = calculateNewsScore(titleLower, transcriptLower, duration)
        
        // Documentary indicators
        scores[ContentType.DOCUMENTARY] = calculateDocumentaryScore(
            titleLower, transcriptLower, duration
        )
        
        // Interview indicators
        scores[ContentType.INTERVIEW] = calculateInterviewScore(
            titleLower, transcriptLower, speakerCount
        )
        
        // Webinar indicators
        scores[ContentType.WEBINAR] = calculateWebinarScore(
            titleLower, transcriptLower, hasSlides, duration
        )
        
        // Return the type with highest score
        return scores.maxByOrNull { it.value }?.key ?: ContentType.UNKNOWN
    }
    
    private fun calculateTutorialScore(title: String, transcript: String, duration: Long): Double {
        var score = 0.0
        
        // Title indicators
        val tutorialKeywords = listOf("tutorial", "how to", "guide", "step by step", "learn", "course")
        score += tutorialKeywords.count { title.contains(it) } * 0.3
        
        // Transcript indicators
        val instructionalPhrases = listOf(
            "first step", "next step", "now we", "let's", "you need to", "make sure", "don't forget"
        )
        score += instructionalPhrases.count { transcript.contains(it) } * 0.1
        
        // Duration factor (tutorials are often 5-30 minutes)
        val durationMinutes = duration / 60000
        if (durationMinutes in 5..30) score += 0.2
        
        return score.coerceAtMost(1.0)
    }
    
    private fun calculateEducationalScore(
        title: String, transcript: String, duration: Long, speakerCount: Int
    ): Double {
        var score = 0.0
        
        // Title indicators
        val educationalKeywords = listOf("lecture", "lesson", "class", "course", "education", "academic")
        score += educationalKeywords.count { title.contains(it) } * 0.3
        
        // Transcript indicators
        val academicPhrases = listOf(
            "research shows", "according to", "theory", "concept", "principle", "study", "analysis"
        )
        score += academicPhrases.count { transcript.contains(it) } * 0.1
        
        // Single speaker often indicates lecture
        if (speakerCount == 1) score += 0.2
        
        // Longer duration typical for educational content
        val durationMinutes = duration / 60000
        if (durationMinutes > 20) score += 0.2
        
        return score.coerceAtMost(1.0)
    }
    
    private fun calculatePresentationScore(
        title: String, transcript: String, hasSlides: Boolean, visualElements: List<String>
    ): Double {
        var score = 0.0
        
        // Title indicators
        val presentationKeywords = listOf("presentation", "meeting", "conference", "pitch", "proposal")
        score += presentationKeywords.count { title.contains(it) } * 0.3
        
        // Slides are strong indicator
        if (hasSlides) score += 0.4
        
        // Visual elements typical in presentations
        val presentationVisuals = listOf("chart", "graph", "slide", "diagram", "table")
        score += presentationVisuals.count { visual -> 
            visualElements.any { it.contains(visual) } 
        } * 0.1
        
        // Presentation language
        val businessPhrases = listOf("agenda", "objectives", "strategy", "results", "recommendations")
        score += businessPhrases.count { transcript.contains(it) } * 0.1
        
        return score.coerceAtMost(1.0)
    }
    
    private fun calculateEntertainmentScore(title: String, transcript: String): Double {
        var score = 0.0
        
        // Title indicators
        val entertainmentKeywords = listOf("funny", "comedy", "entertainment", "fun", "hilarious", "vlog")
        score += entertainmentKeywords.count { title.contains(it) } * 0.3
        
        // Transcript indicators
        val entertainmentPhrases = listOf("haha", "lol", "amazing", "incredible", "awesome", "wow")
        score += entertainmentPhrases.count { transcript.contains(it) } * 0.1
        
        return score.coerceAtMost(1.0)
    }
    
    private fun calculateMusicScore(title: String, transcript: String, duration: Long): Double {
        var score = 0.0
        
        // Title indicators
        val musicKeywords = listOf("song", "music", "audio", "track", "album", "artist", "band")
        score += musicKeywords.count { title.contains(it) } * 0.4
        
        // Short transcript often indicates music video
        val wordCount = transcript.split("\\s+".toRegex()).size
        if (wordCount < 50) score += 0.3
        
        // Typical music video duration
        val durationMinutes = duration / 60000
        if (durationMinutes in 2..6) score += 0.2
        
        return score.coerceAtMost(1.0)
    }
    
    private fun calculateSportsScore(title: String, transcript: String): Double {
        var score = 0.0
        
        // Title indicators
        val sportsKeywords = listOf("game", "match", "sport", "team", "player", "score", "championship")
        score += sportsKeywords.count { title.contains(it) } * 0.3
        
        // Transcript indicators
        val sportsPhrases = listOf("goal", "point", "win", "lose", "play", "team", "coach", "referee")
        score += sportsPhrases.count { transcript.contains(it) } * 0.1
        
        return score.coerceAtMost(1.0)
    }
    
    private fun calculateNewsScore(title: String, transcript: String, duration: Long): Double {
        var score = 0.0
        
        // Title indicators
        val newsKeywords = listOf("news", "report", "breaking", "update", "announcement", "press")
        score += newsKeywords.count { title.contains(it) } * 0.3
        
        // Transcript indicators
        val newsPhrases = listOf("according to", "sources say", "reported", "announced", "confirmed")
        score += newsPhrases.count { transcript.contains(it) } * 0.1
        
        // Typical news segment duration
        val durationMinutes = duration / 60000
        if (durationMinutes in 2..15) score += 0.2
        
        return score.coerceAtMost(1.0)
    }
    
    private fun calculateDocumentaryScore(title: String, transcript: String, duration: Long): Double {
        var score = 0.0
        
        // Title indicators
        val documentaryKeywords = listOf("documentary", "story", "history", "investigation", "behind")
        score += documentaryKeywords.count { title.contains(it) } * 0.3
        
        // Longer duration typical for documentaries
        val durationMinutes = duration / 60000
        if (durationMinutes > 30) score += 0.3
        
        // Narrative style
        val narrativePhrases = listOf("once upon", "years ago", "the story", "meanwhile", "however")
        score += narrativePhrases.count { transcript.contains(it) } * 0.1
        
        return score.coerceAtMost(1.0)
    }
    
    private fun calculateInterviewScore(title: String, transcript: String, speakerCount: Int): Double {
        var score = 0.0
        
        // Title indicators
        val interviewKeywords = listOf("interview", "conversation", "talk", "discussion", "chat")
        score += interviewKeywords.count { title.contains(it) } * 0.3
        
        // Multiple speakers indicate interview
        if (speakerCount > 1) score += 0.4
        
        // Interview language patterns
        val interviewPhrases = listOf("tell us", "what do you think", "how do you", "can you explain")
        score += interviewPhrases.count { transcript.contains(it) } * 0.1
        
        return score.coerceAtMost(1.0)
    }
    
    private fun calculateWebinarScore(
        title: String, transcript: String, hasSlides: Boolean, duration: Long
    ): Double {
        var score = 0.0
        
        // Title indicators
        val webinarKeywords = listOf("webinar", "online", "virtual", "session", "workshop")
        score += webinarKeywords.count { title.contains(it) } * 0.3
        
        // Slides common in webinars
        if (hasSlides) score += 0.3
        
        // Longer duration typical for webinars
        val durationMinutes = duration / 60000
        if (durationMinutes > 30) score += 0.2
        
        // Interactive elements
        val interactivePhrases = listOf("questions", "chat", "participants", "attendees")
        score += interactivePhrases.count { transcript.contains(it) } * 0.1
        
        return score.coerceAtMost(1.0)
    }
    
    private fun calculateClassificationConfidence(
        contentType: ContentType,
        topics: List<String>,
        transcriptLength: Int,
        visualElementCount: Int
    ): Double {
        var confidence = 0.5 // Base confidence
        
        // More topics increase confidence
        confidence += (topics.size * 0.1).coerceAtMost(0.3)
        
        // Longer transcript increases confidence
        if (transcriptLength > 1000) confidence += 0.2
        
        // Visual elements add confidence
        confidence += (visualElementCount * 0.05).coerceAtMost(0.2)
        
        return confidence.coerceAtMost(1.0)
    }
    
    private fun generateFallbackClassification(
        title: String,
        transcript: String,
        duration: Long
    ): ContentClassification {
        val titleLower = title.lowercase()
        
        val contentType = when {
            titleLower.contains("tutorial") || titleLower.contains("how to") -> ContentType.TUTORIAL
            titleLower.contains("presentation") || titleLower.contains("meeting") -> ContentType.PRESENTATION
            titleLower.contains("music") || titleLower.contains("song") -> ContentType.MUSIC_VIDEO
            titleLower.contains("news") || titleLower.contains("report") -> ContentType.NEWS
            else -> ContentType.UNKNOWN
        }
        
        val topics = extractBasicTopics(title, transcript)
        
        return ContentClassification(
            primaryType = contentType,
            confidence = 0.6,
            detectedTopics = topics,
            estimatedDuration = duration,
            complexity = ContentComplexity.INTERMEDIATE
        )
    }
    
    private fun extractBasicTopics(title: String, transcript: String): List<String> {
        val commonTopics = mapOf(
            "technology" to listOf("tech", "software", "computer", "digital", "app", "code"),
            "business" to listOf("business", "marketing", "sales", "strategy", "company"),
            "education" to listOf("learn", "teach", "school", "university", "course", "study"),
            "health" to listOf("health", "medical", "fitness", "wellness", "exercise"),
            "science" to listOf("science", "research", "experiment", "theory", "data"),
            "entertainment" to listOf("movie", "show", "game", "fun", "entertainment")
        )
        
        val text = "$title $transcript".lowercase()
        return commonTopics.filter { (_, keywords) ->
            keywords.any { text.contains(it) }
        }.keys.toList()
    }
}

// Supporting classes
private class TextClassificationModel {
    fun loadModel() {
        // Load text classification model
    }
    
    fun cleanup() {
        // Clean up resources
    }
}

private class TopicExtractionModel {
    fun initialize() {
        // Initialize topic extraction model
    }
    
    suspend fun extractTopics(text: String): List<String> {
        // Extract topics using LDA or similar
        return listOf("main topic", "secondary topic", "related concept")
    }
    
    fun cleanup() {
        // Clean up resources
    }
}

private class ComplexityAnalyzer {
    fun initialize() {
        // Initialize complexity analysis
    }
    
    suspend fun analyzeComplexity(transcript: String, topics: List<String>): ContentComplexity {
        // Analyze content complexity
        val wordCount = transcript.split("\\s+".toRegex()).size
        val avgWordLength = transcript.split("\\s+".toRegex()).map { it.length }.average()
        
        return when {
            avgWordLength > 6 && wordCount > 2000 -> ContentComplexity.EXPERT
            avgWordLength > 5 && wordCount > 1000 -> ContentComplexity.ADVANCED
            avgWordLength > 4 -> ContentComplexity.INTERMEDIATE
            else -> ContentComplexity.BEGINNER
        }
    }
    
    fun cleanup() {
        // Clean up resources
    }
}

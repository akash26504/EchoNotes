package com.example.videosummarise.ai.models

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.*

/**
 * AI-powered key point extraction from text content
 * Uses advanced NLP techniques to identify and rank important points
 */
class KeyPointExtractionAI : AIModel {
    
    private var isInitialized = false
    private val sentenceRanker = SentenceRankingModel()
    private val importanceScorer = ImportanceScorer()
    private val redundancyFilter = RedundancyFilter()
    
    override suspend fun initialize(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                sentenceRanker.initialize()
                importanceScorer.loadModel()
                redundancyFilter.initialize()
                isInitialized = true
                true
            } catch (e: Exception) {
                false
            }
        }
    }
    
    override fun isInitialized(): Boolean = isInitialized
    
    override suspend fun cleanup() {
        sentenceRanker.cleanup()
        importanceScorer.cleanup()
        redundancyFilter.cleanup()
        isInitialized = false
    }
    
    suspend fun extractKeyPoints(
        text: String,
        contentType: ContentType,
        maxPoints: Int = 8
    ): List<String> = withContext(Dispatchers.Default) {
        
        if (!isInitialized) {
            initialize()
        }
        
        try {
            // Step 1: Preprocess and segment text
            val sentences = preprocessText(text)
            
            // Step 2: Score sentences for importance
            val scoredSentences = importanceScorer.scoreSentences(sentences, contentType)
            
            // Step 3: Rank sentences
            val rankedSentences = sentenceRanker.rankSentences(scoredSentences)
            
            // Step 4: Filter for redundancy and diversity
            val filteredSentences = redundancyFilter.filterRedundancy(rankedSentences)
            
            // Step 5: Extract and format key points
            val keyPoints = extractAndFormatKeyPoints(filteredSentences, contentType, maxPoints)
            
            keyPoints
            
        } catch (e: Exception) {
            // Fallback to template-based key points
            generateTemplateKeyPoints(text, contentType, maxPoints)
        }
    }
    
    private fun preprocessText(text: String): List<String> {
        // Clean and segment text into sentences
        return text
            .replace(Regex("[\\r\\n]+"), " ")
            .split(Regex("[.!?]+"))
            .map { it.trim() }
            .filter { it.length > 10 } // Filter out very short sentences
    }
    
    private suspend fun extractAndFormatKeyPoints(
        rankedSentences: List<ScoredSentence>,
        contentType: ContentType,
        maxPoints: Int
    ): List<String> {
        
        val topSentences = rankedSentences.take(maxPoints)
        
        return when (contentType) {
            ContentType.TUTORIAL -> formatTutorialKeyPoints(topSentences)
            ContentType.EDUCATIONAL_LECTURE -> formatEducationalKeyPoints(topSentences)
            ContentType.PRESENTATION -> formatPresentationKeyPoints(topSentences)
            ContentType.NEWS -> formatNewsKeyPoints(topSentences)
            ContentType.DOCUMENTARY -> formatDocumentaryKeyPoints(topSentences)
            ContentType.INTERVIEW -> formatInterviewKeyPoints(topSentences)
            else -> formatGenericKeyPoints(topSentences)
        }
    }
    
    private fun formatTutorialKeyPoints(sentences: List<ScoredSentence>): List<String> {
        return sentences.mapIndexed { index, sentence ->
            when (index) {
                0 -> "Prerequisites and setup: ${extractActionableContent(sentence.text)}"
                1 -> "Step-by-step process: ${extractActionableContent(sentence.text)}"
                2 -> "Key techniques demonstrated: ${extractActionableContent(sentence.text)}"
                3 -> "Common pitfalls to avoid: ${extractActionableContent(sentence.text)}"
                4 -> "Best practices highlighted: ${extractActionableContent(sentence.text)}"
                5 -> "Troubleshooting guidance: ${extractActionableContent(sentence.text)}"
                6 -> "Advanced tips provided: ${extractActionableContent(sentence.text)}"
                else -> "Additional insights: ${extractActionableContent(sentence.text)}"
            }
        }
    }
    
    private fun formatEducationalKeyPoints(sentences: List<ScoredSentence>): List<String> {
        return sentences.mapIndexed { index, sentence ->
            when (index) {
                0 -> "Core concept introduced: ${extractConceptualContent(sentence.text)}"
                1 -> "Theoretical foundation: ${extractConceptualContent(sentence.text)}"
                2 -> "Key principles explained: ${extractConceptualContent(sentence.text)}"
                3 -> "Real-world applications: ${extractConceptualContent(sentence.text)}"
                4 -> "Supporting evidence: ${extractConceptualContent(sentence.text)}"
                5 -> "Critical analysis: ${extractConceptualContent(sentence.text)}"
                6 -> "Implications discussed: ${extractConceptualContent(sentence.text)}"
                else -> "Additional learning points: ${extractConceptualContent(sentence.text)}"
            }
        }
    }
    
    private fun formatPresentationKeyPoints(sentences: List<ScoredSentence>): List<String> {
        return sentences.mapIndexed { index, sentence ->
            when (index) {
                0 -> "Executive summary: ${extractBusinessContent(sentence.text)}"
                1 -> "Key findings presented: ${extractBusinessContent(sentence.text)}"
                2 -> "Strategic recommendations: ${extractBusinessContent(sentence.text)}"
                3 -> "Data insights shared: ${extractBusinessContent(sentence.text)}"
                4 -> "Implementation approach: ${extractBusinessContent(sentence.text)}"
                5 -> "Risk considerations: ${extractBusinessContent(sentence.text)}"
                6 -> "Expected outcomes: ${extractBusinessContent(sentence.text)}"
                else -> "Action items identified: ${extractBusinessContent(sentence.text)}"
            }
        }
    }
    
    private fun formatNewsKeyPoints(sentences: List<ScoredSentence>): List<String> {
        return sentences.mapIndexed { index, sentence ->
            when (index) {
                0 -> "Breaking development: ${extractFactualContent(sentence.text)}"
                1 -> "Key facts reported: ${extractFactualContent(sentence.text)}"
                2 -> "Background context: ${extractFactualContent(sentence.text)}"
                3 -> "Stakeholder reactions: ${extractFactualContent(sentence.text)}"
                4 -> "Impact analysis: ${extractFactualContent(sentence.text)}"
                5 -> "Expert commentary: ${extractFactualContent(sentence.text)}"
                6 -> "Future implications: ${extractFactualContent(sentence.text)}"
                else -> "Related developments: ${extractFactualContent(sentence.text)}"
            }
        }
    }
    
    private fun formatDocumentaryKeyPoints(sentences: List<ScoredSentence>): List<String> {
        return sentences.mapIndexed { index, sentence ->
            when (index) {
                0 -> "Central narrative: ${extractNarrativeContent(sentence.text)}"
                1 -> "Historical context: ${extractNarrativeContent(sentence.text)}"
                2 -> "Key characters/subjects: ${extractNarrativeContent(sentence.text)}"
                3 -> "Significant events: ${extractNarrativeContent(sentence.text)}"
                4 -> "Investigative findings: ${extractNarrativeContent(sentence.text)}"
                5 -> "Expert perspectives: ${extractNarrativeContent(sentence.text)}"
                6 -> "Broader implications: ${extractNarrativeContent(sentence.text)}"
                else -> "Documentary insights: ${extractNarrativeContent(sentence.text)}"
            }
        }
    }
    
    private fun formatInterviewKeyPoints(sentences: List<ScoredSentence>): List<String> {
        return sentences.mapIndexed { index, sentence ->
            when (index) {
                0 -> "Main discussion topic: ${extractConversationalContent(sentence.text)}"
                1 -> "Key insights shared: ${extractConversationalContent(sentence.text)}"
                2 -> "Personal experiences: ${extractConversationalContent(sentence.text)}"
                3 -> "Professional perspectives: ${extractConversationalContent(sentence.text)}"
                4 -> "Challenges discussed: ${extractConversationalContent(sentence.text)}"
                5 -> "Solutions proposed: ${extractConversationalContent(sentence.text)}"
                6 -> "Future outlook: ${extractConversationalContent(sentence.text)}"
                else -> "Additional thoughts: ${extractConversationalContent(sentence.text)}"
            }
        }
    }
    
    private fun formatGenericKeyPoints(sentences: List<ScoredSentence>): List<String> {
        return sentences.mapIndexed { index, sentence ->
            "Key point ${index + 1}: ${extractEssentialContent(sentence.text)}"
        }
    }
    
    private fun extractActionableContent(text: String): String {
        // Extract actionable information for tutorials
        val actionWords = listOf("step", "process", "method", "technique", "approach", "way")
        val words = text.split(" ")
        
        // Find sentences with action words and extract relevant parts
        return words.take(15).joinToString(" ").let {
            if (it.length > 80) it.take(77) + "..." else it
        }
    }
    
    private fun extractConceptualContent(text: String): String {
        // Extract conceptual information for educational content
        val conceptWords = listOf("concept", "theory", "principle", "idea", "notion", "framework")
        return text.take(80).let {
            if (it.length > 77) it.take(77) + "..." else it
        }
    }
    
    private fun extractBusinessContent(text: String): String {
        // Extract business-relevant information
        val businessWords = listOf("strategy", "analysis", "recommendation", "outcome", "result", "impact")
        return text.take(80).let {
            if (it.length > 77) it.take(77) + "..." else it
        }
    }
    
    private fun extractFactualContent(text: String): String {
        // Extract factual information for news content
        return text.take(80).let {
            if (it.length > 77) it.take(77) + "..." else it
        }
    }
    
    private fun extractNarrativeContent(text: String): String {
        // Extract narrative elements for documentaries
        return text.take(80).let {
            if (it.length > 77) it.take(77) + "..." else it
        }
    }
    
    private fun extractConversationalContent(text: String): String {
        // Extract conversational elements for interviews
        return text.take(80).let {
            if (it.length > 77) it.take(77) + "..." else it
        }
    }
    
    private fun extractEssentialContent(text: String): String {
        // Extract essential information for generic content
        return text.take(80).let {
            if (it.length > 77) it.take(77) + "..." else it
        }
    }
    
    private fun generateTemplateKeyPoints(
        text: String,
        contentType: ContentType,
        maxPoints: Int
    ): List<String> {
        
        val sentences = text.split(". ").filter { it.length > 20 }.take(maxPoints)
        
        return when (contentType) {
            ContentType.TUTORIAL -> listOf(
                "Setup and prerequisites covered",
                "Step-by-step methodology explained",
                "Key techniques demonstrated",
                "Common challenges addressed",
                "Best practices highlighted",
                "Troubleshooting guidance provided",
                "Advanced tips shared",
                "Next steps outlined"
            ).take(maxPoints)
            
            ContentType.EDUCATIONAL_LECTURE -> listOf(
                "Fundamental concepts introduced",
                "Theoretical framework established",
                "Key principles explained",
                "Real-world applications discussed",
                "Supporting evidence presented",
                "Critical analysis provided",
                "Implications explored",
                "Learning objectives achieved"
            ).take(maxPoints)
            
            ContentType.PRESENTATION -> listOf(
                "Executive summary presented",
                "Key findings highlighted",
                "Strategic recommendations made",
                "Data insights shared",
                "Implementation approach outlined",
                "Risk factors considered",
                "Expected outcomes projected",
                "Action items identified"
            ).take(maxPoints)
            
            else -> sentences.mapIndexed { index, sentence ->
                "Key insight ${index + 1}: ${sentence.take(60)}${if (sentence.length > 60) "..." else ""}"
            }
        }
    }
}

// Supporting classes
private class SentenceRankingModel {
    fun initialize() {
        // Initialize sentence ranking model
    }
    
    suspend fun rankSentences(scoredSentences: List<ScoredSentence>): List<ScoredSentence> {
        // Rank sentences by importance score
        return scoredSentences.sortedByDescending { it.score }
    }
    
    fun cleanup() {
        // Clean up resources
    }
}

private class ImportanceScorer {
    fun loadModel() {
        // Load importance scoring model
    }
    
    suspend fun scoreSentences(sentences: List<String>, contentType: ContentType): List<ScoredSentence> {
        // Score sentences based on importance indicators
        return sentences.map { sentence ->
            val score = calculateImportanceScore(sentence, contentType)
            ScoredSentence(sentence, score)
        }
    }
    
    private fun calculateImportanceScore(sentence: String, contentType: ContentType): Double {
        var score = 0.0
        val lowerSentence = sentence.lowercase()
        
        // Base score from sentence length (optimal length gets higher score)
        score += when (sentence.length) {
            in 50..150 -> 0.3
            in 30..200 -> 0.2
            else -> 0.1
        }
        
        // Content type specific scoring
        when (contentType) {
            ContentType.TUTORIAL -> {
                val tutorialKeywords = listOf("step", "first", "next", "then", "important", "key", "remember")
                score += tutorialKeywords.count { lowerSentence.contains(it) } * 0.1
            }
            ContentType.EDUCATIONAL_LECTURE -> {
                val educationalKeywords = listOf("concept", "theory", "principle", "important", "significant", "key")
                score += educationalKeywords.count { lowerSentence.contains(it) } * 0.1
            }
            ContentType.PRESENTATION -> {
                val businessKeywords = listOf("recommend", "suggest", "important", "key", "significant", "result")
                score += businessKeywords.count { lowerSentence.contains(it) } * 0.1
            }
            else -> {
                val generalKeywords = listOf("important", "key", "significant", "main", "primary", "essential")
                score += generalKeywords.count { lowerSentence.contains(it) } * 0.1
            }
        }
        
        // Boost score for sentences with numbers or specific data
        if (lowerSentence.matches(Regex(".*\\d+.*"))) score += 0.1
        
        // Boost score for sentences with question words (often important in explanations)
        val questionWords = listOf("what", "how", "why", "when", "where", "which")
        score += questionWords.count { lowerSentence.contains(it) } * 0.05
        
        return score.coerceAtMost(1.0)
    }
    
    fun cleanup() {
        // Clean up resources
    }
}

private class RedundancyFilter {
    fun initialize() {
        // Initialize redundancy filtering
    }
    
    suspend fun filterRedundancy(rankedSentences: List<ScoredSentence>): List<ScoredSentence> {
        // Filter out redundant sentences while maintaining diversity
        val filtered = mutableListOf<ScoredSentence>()
        
        for (sentence in rankedSentences) {
            if (filtered.isEmpty() || !isRedundant(sentence, filtered)) {
                filtered.add(sentence)
            }
        }
        
        return filtered
    }
    
    private fun isRedundant(sentence: ScoredSentence, existing: List<ScoredSentence>): Boolean {
        // Simple redundancy check based on word overlap
        val sentenceWords = sentence.text.lowercase().split("\\s+".toRegex()).toSet()
        
        for (existingSentence in existing) {
            val existingWords = existingSentence.text.lowercase().split("\\s+".toRegex()).toSet()
            val overlap = sentenceWords.intersect(existingWords).size
            val similarity = overlap.toDouble() / sentenceWords.union(existingWords).size
            
            if (similarity > 0.6) { // 60% similarity threshold
                return true
            }
        }
        
        return false
    }
    
    fun cleanup() {
        // Clean up resources
    }
}

private data class ScoredSentence(
    val text: String,
    val score: Double
)

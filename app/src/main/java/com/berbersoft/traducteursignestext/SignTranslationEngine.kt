package com.berbersoft.traducteursignestext

import java.text.Normalizer
import java.util.Locale

/**
 * Type of match used during translation.
 */
enum class MatchType {
    EXACT,           // Matches SignWord.word directly
    SYNONYM,         // Matched via multi-language synonym dictionary (FR, EN, AR)
    COMPOUND_PHRASE, // Matched as a multi-word expression (e.g. "au revoir", "s'il te plait")
    FINGERSPELLED    // Decomposed character-by-character into alphabet signs (dactylologie)
}

/**
 * Represents an individual translated sign token in a sequence.
 */
data class SignToken(
    val sourceText: String,
    val signWord: SignWord,
    val isFingerspelled: Boolean = false,
    val matchType: MatchType = MatchType.EXACT
)

/**
 * Configuration options for the translation engine.
 */
data class TranslationOptions(
    val enableFingerspellingFallback: Boolean = true,
    val maxCompoundPhraseWordLength: Int = 5,
    val filterPunctuation: Boolean = true,
    val preservePunctuationInTokens: Boolean = false
)

/**
 * Structured result of translating a sentence or utterance into sign language.
 */
data class TranslationResult(
    val originalText: String,
    val normalizedText: String,
    val tokens: List<SignToken>,
    val unmatchedWords: List<String>,
    val totalWordsCount: Int,
    val translatedWordsCount: Int
) {
    val isSuccessful: Boolean get() = tokens.isNotEmpty()

    val coveragePercentage: Float
        get() = if (totalWordsCount == 0) 0f else (translatedWordsCount.toFloat() / totalWordsCount.toFloat()) * 100f

    val hasFingerspelling: Boolean
        get() = tokens.any { it.isFingerspelled }

    val signWords: List<SignWord>
        get() = tokens.map { it.signWord }

    val isFullCoverage: Boolean
        get() = unmatchedWords.isEmpty() && totalWordsCount > 0
}

/**
 * Immutable state of the sequence player.
 */
data class PlayerState(
    val currentIndex: Int = 0,
    val isPlaying: Boolean = false,
    val isLooping: Boolean = false,
    val speed: Float = 1.0f,
    val isCompleted: Boolean = false
)

/**
 * State machine managing playback, stepping, speed, and looping for sign sequences.
 */
class SignSequencePlayer(
    val tokens: List<SignToken>,
    initialState: PlayerState = PlayerState()
) {
    var state: PlayerState = initialState
        private set

    val currentToken: SignToken?
        get() = if (tokens.isEmpty() || state.currentIndex !in tokens.indices) null else tokens[state.currentIndex]

    val currentSignWord: SignWord?
        get() = currentToken?.signWord

    val hasNext: Boolean
        get() = state.currentIndex < tokens.size - 1

    val hasPrevious: Boolean
        get() = state.currentIndex > 0

    val progress: Float
        get() = if (tokens.isEmpty()) 0f else (state.currentIndex + 1).toFloat() / tokens.size.toFloat()

    val size: Int get() = tokens.size

    fun play() {
        if (tokens.isEmpty()) return
        if (state.isCompleted) {
            state = state.copy(currentIndex = 0, isPlaying = true, isCompleted = false)
        } else {
            state = state.copy(isPlaying = true)
        }
    }

    fun pause() {
        state = state.copy(isPlaying = false)
    }

    fun next(): Boolean {
        if (tokens.isEmpty()) return false
        return if (hasNext) {
            state = state.copy(currentIndex = state.currentIndex + 1, isCompleted = false)
            true
        } else if (state.isLooping) {
            state = state.copy(currentIndex = 0, isCompleted = false)
            true
        } else {
            state = state.copy(isPlaying = false, isCompleted = true)
            false
        }
    }

    fun previous(): Boolean {
        if (tokens.isEmpty()) return false
        return if (hasPrevious) {
            state = state.copy(currentIndex = state.currentIndex - 1, isCompleted = false)
            true
        } else if (state.isLooping) {
            state = state.copy(currentIndex = tokens.size - 1, isCompleted = false)
            true
        } else {
            false
        }
    }

    fun restart() {
        if (tokens.isEmpty()) return
        state = state.copy(currentIndex = 0, isCompleted = false)
    }

    fun seekTo(index: Int): Boolean {
        if (index in tokens.indices) {
            state = state.copy(currentIndex = index, isCompleted = false)
            return true
        }
        return false
    }

    fun toggleLoop() {
        state = state.copy(isLooping = !state.isLooping)
    }

    fun setSpeed(speed: Float) {
        val sanitizedSpeed = when {
            speed <= 0.5f -> 0.5f
            speed <= 0.75f -> 0.75f
            speed <= 1.0f -> 1.0f
            speed <= 1.25f -> 1.25f
            speed <= 1.5f -> 1.5f
            else -> 2.0f
        }
        state = state.copy(speed = sanitizedSpeed)
    }

    /**
     * Estimates total duration in milliseconds considering static vs video signs and playback speed.
     */
    fun getEstimatedDurationMs(
        baseStaticDurationMs: Long = 2000L,
        baseVideoDurationMs: Long = 3500L
    ): Long {
        if (tokens.isEmpty()) return 0L
        val totalMs = tokens.sumOf { token ->
            if (SignDictionary.hasVideo(token.signWord.word)) {
                baseVideoDurationMs
            } else {
                baseStaticDurationMs
            }
        }
        return (totalMs / state.speed).toLong()
    }
}

/**
 * Domain engine for translating natural language text, multi-word expressions,
 * and spoken sentences into French Sign Language (LSF) sequences.
 */
object SignTranslationEngine {

    /**
     * Regex matching punctuation characters across Latin, Arabic, and general Unicode.
     */
    private val PUNCTUATION_REGEX = Regex("[.,;:!?¿¡\"'«»—_\\(\\)\\[\\]{}…/\\\\*#@+\\-=~`]+")

    /**
     * Normalizes an input string: converts to lower case, trims whitespace,
     * harmonizes typography quotes, and optionally removes punctuation.
     */
    fun normalize(text: String, filterPunctuation: Boolean = true): String {
        var cleaned = text
            .replace('’', '\'')
            .replace('‘', '\'')
            .replace('`', '\'')
            .replace('«', '"')
            .replace('»', '"')
            .trim()

        if (filterPunctuation) {
            // Keep apostrophes within words like "c'est", "d'accord", "s'il", but remove isolated ones
            cleaned = cleaned.replace(Regex("(?<=\\s|^)'|'(?=\\s|$)"), "")
            // Replace commas, question marks, exclamation marks with spaces
            cleaned = cleaned.replace(Regex("[.,;:!?¿¡\"_\\(\\)\\[\\]{}…/\\\\*#@+\\-=~`]"), " ")
        }

        // Collapse multiple whitespaces
        return cleaned.replace(Regex("\\s+"), " ").trim().lowercase(Locale.ROOT)
    }

    /**
     * Decomposes a word or name into its fingerspelled LSF letters (dactylologie).
     */
    fun fingerspellWord(word: String): List<SignToken> {
        val tokens = mutableListOf<SignToken>()
        // Normalize accents (e.g. é -> e, à -> a, ç -> c)
        val normalized = Normalizer.normalize(word.lowercase(Locale.ROOT), Normalizer.Form.NFD)
            .replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")

        for (ch in normalized) {
            if (ch in 'a'..'z') {
                val letterSign = SignDictionary.findSignByWord("lettre $ch")
                if (letterSign != null) {
                    tokens.add(
                        SignToken(
                            sourceText = ch.toString(),
                            signWord = letterSign,
                            isFingerspelled = true,
                            matchType = MatchType.FINGERSPELLED
                        )
                    )
                }
            }
        }
        return tokens
    }

    /**
     * Translates a complete natural language sentence or voice utterance into a structured sequence of signs.
     */
    fun translateSentence(
        text: String,
        options: TranslationOptions = TranslationOptions()
    ): TranslationResult {
        if (text.isBlank()) {
            return TranslationResult(
                originalText = text,
                normalizedText = "",
                tokens = emptyList(),
                unmatchedWords = emptyList(),
                totalWordsCount = 0,
                translatedWordsCount = 0
            )
        }

        val normalized = normalize(text, options.filterPunctuation)
        val words = normalized.split(' ').filter { it.isNotBlank() }

        if (words.isEmpty()) {
            return TranslationResult(
                originalText = text,
                normalizedText = normalized,
                tokens = emptyList(),
                unmatchedWords = emptyList(),
                totalWordsCount = 0,
                translatedWordsCount = 0
            )
        }

        val tokens = mutableListOf<SignToken>()
        val unmatched = mutableListOf<String>()
        var translatedCount = 0
        var i = 0

        while (i < words.size) {
            var matchedPhrase = false
            val maxWindow = minOf(options.maxCompoundPhraseWordLength, words.size - i)

            // Greedy matching: start from longest possible multi-word window down to 2 words
            for (windowSize in maxWindow downTo 2) {
                val candidatePhrase = words.subList(i, i + windowSize).joinToString(" ")
                val signWord = SignDictionary.findSignByWord(candidatePhrase)
                if (signWord != null) {
                    tokens.add(
                        SignToken(
                            sourceText = candidatePhrase,
                            signWord = signWord,
                            isFingerspelled = false,
                            matchType = MatchType.COMPOUND_PHRASE
                        )
                    )
                    translatedCount += windowSize
                    i += windowSize
                    matchedPhrase = true
                    break
                }
            }

            if (matchedPhrase) continue

            // Single word lookup
            val singleWord = words[i]
            val singleSign = SignDictionary.findSignByWord(singleWord)

            if (singleSign != null) {
                val matchType = if (singleWord.equals(singleSign.word, ignoreCase = true)) {
                    MatchType.EXACT
                } else {
                    MatchType.SYNONYM
                }
                tokens.add(
                    SignToken(
                        sourceText = singleWord,
                        signWord = singleSign,
                        isFingerspelled = false,
                        matchType = matchType
                    )
                )
                translatedCount++
            } else {
                // Word not found directly
                if (options.enableFingerspellingFallback) {
                    val fingerspelled = fingerspellWord(singleWord)
                    if (fingerspelled.isNotEmpty()) {
                        tokens.addAll(fingerspelled)
                        translatedCount++
                    } else {
                        unmatched.add(singleWord)
                    }
                } else {
                    unmatched.add(singleWord)
                }
            }

            i++
        }

        return TranslationResult(
            originalText = text,
            normalizedText = normalized,
            tokens = tokens,
            unmatchedWords = unmatched,
            totalWordsCount = words.size,
            translatedWordsCount = translatedCount
        )
    }

    /**
     * Computes the distribution of sign categories represented across a list of tokens.
     */
    fun getCategorySummary(tokens: List<SignToken>): Map<SignCategory, Int> {
        val summary = mutableMapOf<SignCategory, Int>()
        for (token in tokens) {
            val cat = token.signWord.category
            summary[cat] = (summary[cat] ?: 0) + 1
        }
        return summary
    }

    /**
     * Creates an interactive sequence player for a translation result.
     */
    fun createPlayer(
        result: TranslationResult,
        isLooping: Boolean = false,
        speed: Float = 1.0f
    ): SignSequencePlayer {
        val player = SignSequencePlayer(result.tokens)
        if (isLooping) player.toggleLoop()
        player.setSpeed(speed)
        return player
    }
}

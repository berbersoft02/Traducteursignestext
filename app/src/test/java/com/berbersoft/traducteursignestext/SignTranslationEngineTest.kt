package com.berbersoft.traducteursignestext

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SignTranslationEngineTest {

    @Test
    fun testNormalizeTextHandling() {
        val raw = "  Bonjour,  C'EST   Merveilleux !??  "
        val normalized = SignTranslationEngine.normalize(raw)
        assertEquals("bonjour c'est merveilleux", normalized)

        val curlyQuotes = "« Bonjour » et ‘merci’"
        val cleanedQuotes = SignTranslationEngine.normalize(curlyQuotes)
        assertEquals("bonjour et merci", cleanedQuotes)
    }

    @Test
    fun testTranslateSingleWordExactAndSynonym() {
        // Exact French word
        val resultBonjour = SignTranslationEngine.translateSentence("bonjour")
        assertTrue(resultBonjour.isSuccessful)
        assertEquals(1, resultBonjour.tokens.size)
        assertEquals("bonjour", resultBonjour.tokens[0].signWord.word)
        assertEquals(MatchType.EXACT, resultBonjour.tokens[0].matchType)
        assertFalse(resultBonjour.tokens[0].isFingerspelled)
        assertEquals(100f, resultBonjour.coveragePercentage, 0.01f)

        // English synonym
        val resultHello = SignTranslationEngine.translateSentence("hello")
        assertTrue(resultHello.isSuccessful)
        assertEquals(1, resultHello.tokens.size)
        assertEquals("bonjour", resultHello.tokens[0].signWord.word)
        assertEquals(MatchType.SYNONYM, resultHello.tokens[0].matchType)

        // Arabic synonym
        val resultShukran = SignTranslationEngine.translateSentence("شكرا")
        assertTrue(resultShukran.isSuccessful)
        assertEquals(1, resultShukran.tokens.size)
        assertEquals("merci", resultShukran.tokens[0].signWord.word)
        assertEquals(MatchType.SYNONYM, resultShukran.tokens[0].matchType)
    }

    @Test
    fun testTranslateCompoundPhrasesGreedyMatching() {
        // Compound expression "au revoir"
        val resultAuRevoir = SignTranslationEngine.translateSentence("au revoir")
        assertEquals(1, resultAuRevoir.tokens.size)
        assertEquals("au revoir", resultAuRevoir.tokens[0].signWord.word)
        assertEquals(MatchType.COMPOUND_PHRASE, resultAuRevoir.tokens[0].matchType)
        assertEquals(2, resultAuRevoir.totalWordsCount)
        assertEquals(2, resultAuRevoir.translatedWordsCount)
        assertEquals(100f, resultAuRevoir.coveragePercentage, 0.01f)

        // Compound expression "s'il te plait"
        val resultStp = SignTranslationEngine.translateSentence("s'il te plait")
        assertEquals(1, resultStp.tokens.size)
        assertEquals(MatchType.COMPOUND_PHRASE, resultStp.tokens[0].matchType)

        // Compound expression "c'est bon"
        val resultCestBon = SignTranslationEngine.translateSentence("c'est bon")
        assertEquals(1, resultCestBon.tokens.size)
        assertEquals("c'est bon", resultCestBon.tokens[0].signWord.word)
        assertEquals(MatchType.COMPOUND_PHRASE, resultCestBon.tokens[0].matchType)
    }

    @Test
    fun testTranslateFullSentenceWithCompoundPhrasesAndWords() {
        val sentence = "Bonjour, comment ça va ? Merci, au revoir !"
        val result = SignTranslationEngine.translateSentence(sentence)

        assertTrue(result.isSuccessful)
        assertTrue(result.isFullCoverage)
        assertEquals(0, result.unmatchedWords.size)

        // Should produce: bonjour, ça va, merci, au revoir
        val words = result.tokens.map { it.signWord.word }
        assertEquals(listOf("bonjour", "ça va", "merci", "au revoir"), words)
    }

    @Test
    fun testFingerspellingFallbackForUnrecognizedWords() {
        val unknownName = "Karim"
        val result = SignTranslationEngine.translateSentence(unknownName)

        assertTrue(result.isSuccessful)
        assertTrue(result.hasFingerspelling)
        assertEquals(5, result.tokens.size)

        val letters = result.tokens.map { it.sourceText }
        assertEquals(listOf("k", "a", "r", "i", "m"), letters)
        assertTrue(result.tokens.all { it.isFingerspelled && it.matchType == MatchType.FINGERSPELLED })

        // Accent handling in fingerspelling (Saïd -> s, a, i, d)
        val fingerspelledSaid = SignTranslationEngine.fingerspellWord("Saïd")
        assertEquals(4, fingerspelledSaid.size)
        assertEquals(listOf("s", "a", "i", "d"), fingerspelledSaid.map { it.sourceText })
    }

    @Test
    fun testFingerspellingDisabledFallback() {
        val options = TranslationOptions(enableFingerspellingFallback = false)
        val result = SignTranslationEngine.translateSentence("Karim", options)

        assertFalse(result.isSuccessful)
        assertEquals(0, result.tokens.size)
        assertEquals(listOf("karim"), result.unmatchedWords)
        assertEquals(0f, result.coveragePercentage, 0.01f)
    }

    @Test
    fun testBlankAndPunctuationOnlyInputs() {
        val blankResult = SignTranslationEngine.translateSentence("   ")
        assertFalse(blankResult.isSuccessful)
        assertEquals(0, blankResult.tokens.size)
        assertEquals(0, blankResult.totalWordsCount)

        val punctResult = SignTranslationEngine.translateSentence("!??,,...")
        assertFalse(punctResult.isSuccessful)
        assertEquals(0, punctResult.tokens.size)
    }

    @Test
    fun testGetCategorySummary() {
        val sentence = "bonjour merci manger"
        val result = SignTranslationEngine.translateSentence(sentence)
        val summary = SignTranslationEngine.getCategorySummary(result.tokens)

        assertEquals(2, summary[SignCategory.BASIC])
        assertEquals(1, summary[SignCategory.ACTIONS])
    }

    @Test
    fun testSignSequencePlayerNavigationAndStateTransitions() {
        val sentence = "bonjour merci au revoir"
        val result = SignTranslationEngine.translateSentence(sentence)
        val player = SignTranslationEngine.createPlayer(result)

        assertEquals(3, player.size)
        assertEquals(0, player.state.currentIndex)
        assertFalse(player.state.isPlaying)
        assertFalse(player.hasPrevious)
        assertTrue(player.hasNext)
        assertEquals("bonjour", player.currentSignWord?.word)

        // Start playback
        player.play()
        assertTrue(player.state.isPlaying)

        // Step forward
        val advanced = player.next()
        assertTrue(advanced)
        assertEquals(1, player.state.currentIndex)
        assertEquals("merci", player.currentSignWord?.word)
        assertTrue(player.hasPrevious)
        assertTrue(player.hasNext)

        // Step forward to last item
        player.next()
        assertEquals(2, player.state.currentIndex)
        assertEquals("au revoir", player.currentSignWord?.word)
        assertFalse(player.hasNext)

        // Next at end should complete and stop
        val movedPastEnd = player.next()
        assertFalse(movedPastEnd)
        assertTrue(player.state.isCompleted)
        assertFalse(player.state.isPlaying)

        // Play from completed restarts
        player.play()
        assertEquals(0, player.state.currentIndex)
        assertTrue(player.state.isPlaying)
        assertFalse(player.state.isCompleted)
    }

    @Test
    fun testSignSequencePlayerLoopingAndSpeed() {
        val sentence = "bonjour merci"
        val result = SignTranslationEngine.translateSentence(sentence)
        val player = SignTranslationEngine.createPlayer(result)

        player.toggleLoop()
        assertTrue(player.state.isLooping)

        // Next wraps around
        player.next() // index 1
        assertEquals(1, player.state.currentIndex)
        player.next() // index 0 (wrapped)
        assertEquals(0, player.state.currentIndex)

        // Previous wraps around
        player.previous() // index 1 (wrapped)
        assertEquals(1, player.state.currentIndex)

        // Speed setting and duration estimation
        player.setSpeed(1.5f)
        assertEquals(1.5f, player.state.speed, 0.01f)

        val durationMs = player.getEstimatedDurationMs(baseStaticDurationMs = 2000L, baseVideoDurationMs = 3000L)
        assertTrue(durationMs > 0L)
    }

    @Test
    fun testSignSequencePlayerSeekAndRestart() {
        val sentence = "bonjour merci au revoir"
        val result = SignTranslationEngine.translateSentence(sentence)
        val player = SignTranslationEngine.createPlayer(result)

        assertTrue(player.seekTo(2))
        assertEquals(2, player.state.currentIndex)
        assertEquals("au revoir", player.currentSignWord?.word)

        assertFalse(player.seekTo(99)) // Invalid index
        assertEquals(2, player.state.currentIndex)

        player.restart()
        assertEquals(0, player.state.currentIndex)
    }
}

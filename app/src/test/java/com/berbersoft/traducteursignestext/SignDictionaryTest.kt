package com.berbersoft.traducteursignestext

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SignDictionaryTest {

    @Test
    fun testFindSignByWordFrenchSalutations() {
        val bonjour = SignDictionary.findSignByWord("bonjour")
        assertNotNull(bonjour)
        assertEquals("bonjour", bonjour?.word)
        assertEquals(SignCategory.BASIC, bonjour?.category)

        val salut = SignDictionary.findSignByWord("salut")
        assertNotNull(salut)
        assertEquals("bonjour", salut?.word)

        val auRevoir = SignDictionary.findSignByWord("au revoir")
        assertNotNull(auRevoir)
        assertEquals("au revoir", auRevoir?.word)
    }

    @Test
    fun testFindSignByWordEnglishAndArabic() {
        // English synonyms
        val hello = SignDictionary.findSignByWord("hello")
        assertNotNull(hello)
        assertEquals("bonjour", hello?.word)

        val thanks = SignDictionary.findSignByWord("thank you")
        assertNotNull(thanks)
        assertEquals("merci", thanks?.word)

        // Arabic synonyms
        val marhaban = SignDictionary.findSignByWord("مرحبا")
        assertNotNull(marhaban)
        assertEquals("bonjour", marhaban?.word)

        val shukran = SignDictionary.findSignByWord("شكرا")
        assertNotNull(shukran)
        assertEquals("merci", shukran?.word)
    }

    @Test
    fun testFindSignByWordCaseAndWhitespaceInsensitive() {
        val mixedCase = SignDictionary.findSignByWord("  bOnJoUr  ")
        assertNotNull(mixedCase)
        assertEquals("bonjour", mixedCase?.word)

        val upperMerci = SignDictionary.findSignByWord("MERCI")
        assertNotNull(upperMerci)
        assertEquals("merci", upperMerci?.word)
    }

    @Test
    fun testFindSignByWordUnknownWordReturnsNull() {
        val unknown = SignDictionary.findSignByWord("mot_inconnu_xyz_123")
        assertNull(unknown)
    }

    @Test
    fun testGetWordsByCategory() {
        val days = SignDictionary.getWordsByCategory(SignCategory.DAYS)
        assertEquals(7, days.size)
        assertTrue(days.all { it.category == SignCategory.DAYS })

        val alphabet = SignDictionary.getWordsByCategory(SignCategory.ALPHABET)
        assertEquals(26, alphabet.size)
        assertTrue(alphabet.all { it.category == SignCategory.ALPHABET })

        val basic = SignDictionary.getWordsByCategory(SignCategory.BASIC)
        assertTrue(basic.isNotEmpty())
        assertTrue(basic.any { it.word == "bonjour" })
    }

    @Test
    fun testHasVideoAndGetVideoFileName() {
        // Alphabet
        assertTrue(SignDictionary.hasVideo("lettre a"))
        assertEquals("a", SignDictionary.getVideoFileName("lettre a"))
        assertEquals("z", SignDictionary.getVideoFileName("lettre z"))

        // Common actions and expressions
        assertTrue(SignDictionary.hasVideo("apprendre"))
        assertEquals("apprendre", SignDictionary.getVideoFileName("apprendre"))

        assertTrue(SignDictionary.hasVideo("c'est bon"))
        assertEquals("cestbon", SignDictionary.getVideoFileName("c'est bon"))

        // Verify corrected mappings
        assertEquals("tupeuxsignerlentement", SignDictionary.getVideoFileName("tu peux signer lentement"))
        assertEquals("tumaccomapgnes", SignDictionary.getVideoFileName("tu m'accompagnes"))

        // Non-video words
        assertFalse(SignDictionary.hasVideo("bonjour"))
        assertFalse(SignDictionary.hasVideo("lundi"))

        // Safe fallback for unmapped words does not throw exception
        val fallback = SignDictionary.getVideoFileName("nonexistent word")
        assertEquals("nonexistentword", fallback)
    }
}

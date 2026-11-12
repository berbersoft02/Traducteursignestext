package com.berbersoft.traducteursignestext

import com.berbersoft.traducteursignestext.models.GestureType as ModelGestureType
import com.berbersoft.traducteursignestext.GestureType as UiGestureType
import org.junit.Assert.assertEquals
import org.junit.Test

class GestureTypeTest {

    @Test
    fun testModelGestureTypeAlphabetMapping() {
        val alphabet = ('a'..'z')
        for (char in alphabet) {
            val label = char.toString()
            val gesture = ModelGestureType.fromLabel(label)
            assertEquals("Mapping failed for letter: $label", label.uppercase(), gesture.name)

            // Test uppercase input as well
            val upperGesture = ModelGestureType.fromLabel(label.uppercase())
            assertEquals("Uppercase mapping failed for: ${label.uppercase()}", gesture, upperGesture)
        }
    }

    @Test
    fun testModelGestureTypeCustomLabels() {
        assertEquals(ModelGestureType.AMOUR, ModelGestureType.fromLabel("love"))
        assertEquals(ModelGestureType.AMOUR, ModelGestureType.fromLabel("iloveyou"))
        assertEquals(ModelGestureType.AU_REVOIR, ModelGestureType.fromLabel("bye"))
        assertEquals(ModelGestureType.PAS_BIEN, ModelGestureType.fromLabel("notok"))
        assertEquals(ModelGestureType.OUI, ModelGestureType.fromLabel("yes"))
        assertEquals(ModelGestureType.MERCI, ModelGestureType.fromLabel("thankyou"))
        assertEquals(ModelGestureType.MOI, ModelGestureType.fromLabel("me"))
        assertEquals(ModelGestureType.NON, ModelGestureType.fromLabel("pointing_up"))
        assertEquals(ModelGestureType.DIRE, ModelGestureType.fromLabel("tell"))
        assertEquals(ModelGestureType.HELLO_ASL, ModelGestureType.fromLabel("hello"))
        assertEquals(ModelGestureType.APPRENDRE, ModelGestureType.fromLabel("learn"))
        assertEquals(ModelGestureType.NOM, ModelGestureType.fromLabel("name"))
        assertEquals(ModelGestureType.STYLO_ASL, ModelGestureType.fromLabel("pen"))
        assertEquals(ModelGestureType.PLEASE, ModelGestureType.fromLabel("please"))
    }

    @Test
    fun testModelGestureTypeUnknownLabelFallsBackToNone() {
        assertEquals(ModelGestureType.NONE, ModelGestureType.fromLabel("random_unrecognized_sign"))
        assertEquals(ModelGestureType.NONE, ModelGestureType.fromLabel(""))
    }

    @Test
    fun testUiGestureTypeFromLabel() {
        assertEquals(UiGestureType.AMOUR, UiGestureType.fromLabel("ILoveYou"))
        assertEquals(UiGestureType.BONJOUR, UiGestureType.fromLabel("Open_Palm"))
        assertEquals(UiGestureType.OUI, UiGestureType.fromLabel("Closed_Fist"))
        assertEquals(UiGestureType.NON, UiGestureType.fromLabel("Pointing_Up"))
        assertEquals(UiGestureType.MAUVAIS, UiGestureType.fromLabel("Victory"))

        // Case insensitivity
        assertEquals(UiGestureType.BONJOUR, UiGestureType.fromLabel("open_palm"))
        assertEquals(UiGestureType.NONE, UiGestureType.fromLabel("unknown_gesture"))
    }
}

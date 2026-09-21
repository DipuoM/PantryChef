package com.DipuoMokwena.pantrychef

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun passwordMustBeAtLeast6Characters() {
        val password = "123456"
        assertTrue(password.length >= 6)
    }

    @Test
    fun mealSearchQueryIsTrimmed() {
        val query = "  chicken  "
        assertEquals("chicken", query.trim())
    }

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }
}
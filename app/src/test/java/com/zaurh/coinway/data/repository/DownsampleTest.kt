package com.zaurh.coinway.data.repository

import org.junit.Assert.assertEquals
import org.junit.Test

class DownsampleTest {

    @Test
    fun `short lists are untouched`() {
        val values = listOf(1.0, 2.0, 3.0)
        assertEquals(values, downsample(values, 48))
    }

    @Test
    fun `long lists keep first and last point`() {
        val values = List(168) { it.toDouble() }
        val result = downsample(values, 48)
        assertEquals(48, result.size)
        assertEquals(0.0, result.first(), 0.0)
        assertEquals(167.0, result.last(), 0.0)
    }
}

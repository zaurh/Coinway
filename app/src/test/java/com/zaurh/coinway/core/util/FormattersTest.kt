package com.zaurh.coinway.core.util

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Locale

class FormattersTest {

    private val us = Locale.US

    @Test
    fun `prices above one use two decimals`() {
        assertEquals("$64,123.45", Formatters.price(64123.451, "usd", us))
    }

    @Test
    fun `cheap coins get four decimals`() {
        assertEquals("$0.4521", Formatters.price(0.45213, "usd", us))
    }

    @Test
    fun `tiny prices keep four significant digits`() {
        assertEquals("$0.00001234", Formatters.price(0.000012341, "usd", us))
    }

    @Test
    fun `yen has no decimals`() {
        assertEquals("¥9,876,543", Formatters.price(9876543.2, "jpy", us))
    }

    @Test
    fun `compact price uses suffixes`() {
        assertEquals("$1.23T", Formatters.compactPrice(1.2345e12, "usd", us))
        assertEquals("$456.70B", Formatters.compactPrice(4.567e11, "usd", us))
        assertEquals("$12.35M", Formatters.compactPrice(12_345_678.0, "usd", us))
    }

    @Test
    fun `compact price puts the suffix after the number when the symbol trails`() {
        assertEquals("1,23T €", Formatters.compactPrice(1.23e12, "eur", Locale.GERMANY))
    }

    @Test
    fun `small values are not compacted`() {
        assertEquals("$999.50", Formatters.compactPrice(999.5, "usd", us))
    }

    @Test
    fun `compact number`() {
        assertEquals("19.98M", Formatters.compactNumber(19_980_000.0, us))
        assertEquals("950", Formatters.compactNumber(950.0, us))
    }

    @Test
    fun `percent has explicit sign`() {
        assertEquals("+2.31%", Formatters.percent(2.3149, us))
        assertEquals("−0.84%", Formatters.percent(-0.8412, us))
        assertEquals("0.00%", Formatters.percent(0.0, us))
    }
}

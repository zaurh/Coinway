package com.zaurh.coinway.core.util

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.log10

/** Locale-aware money, percent and big-number formatting tuned for crypto prices. */
object Formatters {

    /** "$64,123.45", "$0.4521", "$0.00001234" — more decimals for cheaper coins. */
    fun price(value: Double, currencyCode: String, locale: Locale = Locale.getDefault()): String {
        val format = currencyFormat(currencyCode, locale)
        val digits = priceFractionDigits(value, format.currency?.defaultFractionDigits ?: 2)
        format.minimumFractionDigits = digits
        format.maximumFractionDigits = digits
        return format.format(value)
    }

    /** "$1.23T", "$456.7B", "$12.35M" for market caps and volumes. */
    fun compactPrice(value: Double, currencyCode: String, locale: Locale = Locale.getDefault()): String {
        val (scaled, suffix) = scale(value)
        if (suffix.isEmpty()) return price(value, currencyCode, locale)
        val format = currencyFormat(currencyCode, locale).apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
        }
        return insertAfterLastDigit(format.format(scaled), suffix)
    }

    /** "19.98M", "21.00M" for supplies. */
    fun compactNumber(value: Double, locale: Locale = Locale.getDefault()): String {
        val (scaled, suffix) = scale(value)
        val format = NumberFormat.getNumberInstance(locale).apply {
            minimumFractionDigits = if (suffix.isEmpty()) 0 else 2
            maximumFractionDigits = 2
        }
        return format.format(scaled) + suffix
    }

    /** "+2.31%", "−0.84%" (with a true minus sign). */
    fun percent(value: Double, locale: Locale = Locale.getDefault()): String {
        val format = NumberFormat.getNumberInstance(locale).apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
        }
        val sign = when {
            value > 0.0 -> "+"
            value < 0.0 -> "−"
            else -> ""
        }
        return sign + format.format(abs(value)) + "%"
    }

    fun currencySymbol(currencyCode: String, locale: Locale = Locale.getDefault()): String =
        Currency.getInstance(currencyCode.uppercase(Locale.ROOT)).getSymbol(locale)

    internal fun priceFractionDigits(value: Double, defaultDigits: Int): Int {
        val absolute = abs(value)
        return when {
            absolute == 0.0 -> defaultDigits
            absolute >= 1.0 -> defaultDigits
            absolute >= 0.01 -> 4
            // Keep ~4 significant digits for tiny prices, e.g. 0.00001234.
            else -> (ceil(-log10(absolute)).toInt() + 3).coerceAtMost(12)
        }
    }

    private fun currencyFormat(currencyCode: String, locale: Locale): NumberFormat =
        NumberFormat.getCurrencyInstance(locale).apply {
            currency = Currency.getInstance(currencyCode.uppercase(Locale.ROOT))
        }

    private fun scale(value: Double): Pair<Double, String> {
        val absolute = abs(value)
        return when {
            absolute >= 1e12 -> value / 1e12 to "T"
            absolute >= 1e9 -> value / 1e9 to "B"
            absolute >= 1e6 -> value / 1e6 to "M"
            absolute >= 1e3 -> value / 1e3 to "K"
            else -> value to ""
        }
    }

    private fun insertAfterLastDigit(text: String, suffix: String): String {
        val index = text.indexOfLast { it.isDigit() }
        return if (index < 0) text + suffix
        else text.substring(0, index + 1) + suffix + text.substring(index + 1)
    }
}

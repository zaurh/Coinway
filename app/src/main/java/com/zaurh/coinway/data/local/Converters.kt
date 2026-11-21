package com.zaurh.coinway.data.local

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun fromDoubles(values: List<Double>): String = values.joinToString(",")

    @TypeConverter
    fun toDoubles(value: String): List<Double> =
        if (value.isEmpty()) emptyList() else value.split(',').mapNotNull { it.toDoubleOrNull() }
}

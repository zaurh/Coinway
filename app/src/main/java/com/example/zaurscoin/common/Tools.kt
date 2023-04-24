package com.example.zaurscoin.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier


//Adding commas to Long numbers
fun Long.addCommas(): String {
    val parts = this.toString().split(".")
    val decimal = if (parts.size > 1) ".${parts[1]}" else ""
    val integer = parts[0].toLong()
    return String.format("%,d", integer) + decimal
}

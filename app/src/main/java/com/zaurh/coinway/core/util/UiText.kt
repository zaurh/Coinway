package com.zaurh.coinway.core.util

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import android.content.Context

/** Text that can be produced by a ViewModel without holding a Context. */
sealed interface UiText {
    data class Plain(val value: String) : UiText
    class Res(@StringRes val id: Int, vararg val args: Any) : UiText

    fun asString(context: Context): String = when (this) {
        is Plain -> value
        is Res -> context.getString(id, *args)
    }

    @Composable
    fun asString(): String = when (this) {
        is Plain -> value
        is Res -> stringResource(id, *args)
    }
}

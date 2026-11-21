package com.zaurh.coinway.presentation.components

import androidx.annotation.StringRes
import com.zaurh.coinway.R
import com.zaurh.coinway.core.util.AppError

@StringRes
fun AppError.messageRes(): Int = when (this) {
    AppError.Network -> R.string.error_network
    AppError.RateLimited -> R.string.error_rate_limit
    AppError.Server -> R.string.error_server
    AppError.Unknown -> R.string.error_unknown
}

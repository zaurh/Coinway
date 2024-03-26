package com.zaurh.coinway.presentation.components.chart

import com.zaurh.coinway.domain.model.Chart

data class ChartState(
    val chart: Chart? = null,
    val isLoading: Boolean = false,
    val error: String = ""
)

package com.example.zaurscoin.presentation.components.chart

import com.example.zaurscoin.domain.model.Chart

data class ChartState(
    val chart: Chart? = null,
    val isLoading: Boolean = false,
    val error: String = ""
)

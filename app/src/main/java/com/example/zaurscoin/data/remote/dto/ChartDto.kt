package com.example.zaurscoin.data.remote.dto

import com.example.zaurscoin.domain.model.Chart

data class ChartDto(
    val prices: List<List<Float>>,
)


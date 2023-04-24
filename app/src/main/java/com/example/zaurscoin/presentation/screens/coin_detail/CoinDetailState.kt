package com.example.zaurscoin.presentation.screens.coin_detail

import com.example.zaurscoin.domain.model.Coin

data class CoinDetailState(
    val coin: Coin? = null,
    val isLoading: Boolean = false,
    val error: String = ""
)

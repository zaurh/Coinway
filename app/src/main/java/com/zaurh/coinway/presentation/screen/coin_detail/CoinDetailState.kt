package com.zaurh.coinway.presentation.screen.coin_detail

import com.zaurh.coinway.domain.model.Coin

data class CoinDetailState(
    val coin: Coin? = null,
    val isLoading: Boolean = false,
    val error: String = ""
)

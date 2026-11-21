package com.zaurh.coinway.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable data object MarketsRoute
@Serializable data object WatchlistRoute
@Serializable data object SettingsRoute
@Serializable data class DetailRoute(val coinId: String)

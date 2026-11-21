package com.zaurh.coinway.core.mvi

/** Immutable snapshot of everything a screen renders. */
interface UiState

/** A user action or event the screen sends to its ViewModel. */
interface UiIntent

/** A one-off event (navigation, snackbar, share sheet) the screen consumes exactly once. */
interface UiEffect

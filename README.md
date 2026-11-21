# Coinway

Coinway is a clean, fast crypto tracker for Android. Browse the top 250 coins with live prices and 7-day sparklines, dig into any coin with an interactive chart, and keep your favourites on a watchlist. Prices are cached, so the app keeps working offline.

Market data: [CoinGecko](https://www.coingecko.com).

## Features

- **Markets**: global market cap, 24h volume and BTC dominance; search; Gainers / Losers filters; sort by rank, price, 24h change or name; pull to refresh; refreshes itself every minute while open
- **Coin details**: interactive chart (drag to scrub) for 24H / 7D / 1M / 3M / 1Y, 24h range bar, performance (24h / 7d / 30d / 1y), market statistics, ATH/ATL, supply, description and website, share
- **Watchlist**: star coins from their detail page or long-press them in Markets; swipe to remove with undo; a daily summary
- **Settings**: Light / Dark / System theme; prices in USD, EUR, GBP, TRY, RUB, JPY, INR or AED; clear cache
- **Offline first**: the last market snapshot and the watchlist are stored in Room
- Material 3, edge-to-edge, splash screen, predictive back

## Architecture: MVI + Clean Architecture

```
presentation/          UI (Jetpack Compose) + MVI ViewModels
  markets/ watchlist/ detail/ settings/
    XContract.kt       State, Intent and Effect for the screen
    XViewModel.kt      Reduces intents into a new State; emits one-off Effects
    XScreen.kt         Stateful entry (collects state/effects) + stateless XContent
  components/          Shared composables (PriceChart, Sparkline, CoinRow, ...)
  navigation/          Type-safe routes + NavHost with bottom navigation
  theme/
core/
  mvi/                 MviViewModel base class, CollectEffects
  util/                Formatters, AppError mapping, UiText
domain/                Models + repository interfaces (pure Kotlin)
data/                  Retrofit (CoinGecko), Room cache, DataStore settings, repositories
di/                    Hilt modules
```

Data flows in one direction:

```
View ──Intent──▶ ViewModel.onIntent ──▶ setState { reducer } ──▶ StateFlow<State> ──▶ View
                                    └─▶ sendEffect(...)      ──▶ Flow<Effect> ──▶ View (once)
```

## Tech stack

Kotlin 2.4 · Jetpack Compose (Material 3) · Coroutines & Flow · Hilt · Retrofit + OkHttp + kotlinx.serialization · Room · DataStore · Navigation Compose (type-safe) · Coil · Splash Screen API · JUnit + kotlinx-coroutines-test

## Building

Open the project in a recent Android Studio, or run:

```bash
./gradlew assembleDebug          # debug APK
./gradlew testDebugUnitTest      # unit tests
./gradlew bundleRelease          # Play Store bundle (see signing below)
```

### Release signing

Create a git-ignored `keystore.properties` in the project root:

```properties
storeFile=/absolute/path/to/upload-keystore.jks
storePassword=...
keyAlias=...
keyPassword=...
```

Without it, use Android Studio's **Build → Generate Signed App Bundle**.

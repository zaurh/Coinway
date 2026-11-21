package com.zaurh.coinway.presentation.navigation

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ShowChart
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.zaurh.coinway.R
import com.zaurh.coinway.presentation.detail.DetailScreen
import com.zaurh.coinway.presentation.markets.MarketsScreen
import com.zaurh.coinway.presentation.settings.SettingsScreen
import com.zaurh.coinway.presentation.watchlist.WatchlistScreen
import kotlin.reflect.KClass

private enum class TopLevelDestination(
    val route: Any,
    val routeClass: KClass<*>,
    @StringRes val label: Int,
    val selectedIcon: ImageVector,
    val icon: ImageVector,
) {
    Markets(MarketsRoute, MarketsRoute::class, R.string.nav_markets, Icons.AutoMirrored.Rounded.ShowChart, Icons.AutoMirrored.Rounded.ShowChart),
    Watchlist(WatchlistRoute, WatchlistRoute::class, R.string.nav_watchlist, Icons.Rounded.Star, Icons.Rounded.StarOutline),
    Settings(SettingsRoute, SettingsRoute::class, R.string.nav_settings, Icons.Rounded.Settings, Icons.Outlined.Settings),
}

private const val PUSH_DURATION = 350
private const val FADE_DURATION = 200

@Composable
fun CoinwayRoot() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val destination = backStackEntry?.destination
    val showBottomBar = destination == null ||
        TopLevelDestination.entries.any { top -> destination.hierarchy.any { it.hasRoute(top.routeClass) } }

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = expandVertically(expandFrom = androidx.compose.ui.Alignment.Top),
                exit = shrinkVertically(shrinkTowards = androidx.compose.ui.Alignment.Top),
            ) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceContainer) {
                    TopLevelDestination.entries.forEach { top ->
                        val selected = destination?.hierarchy?.any { it.hasRoute(top.routeClass) } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = { navController.navigateToTopLevel(top.route) },
                            icon = { Icon(if (selected) top.selectedIcon else top.icon, contentDescription = null) },
                            label = { Text(stringResource(top.label)) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                                indicatorColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.onSurface,
                            ),
                        )
                    }
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = MarketsRoute,
            modifier = Modifier
                .padding(padding)
                .consumeWindowInsets(padding),
            enterTransition = { enter() },
            exitTransition = { exit() },
            popEnterTransition = { popEnter() },
            popExitTransition = { popExit() },
        ) {
            composable<MarketsRoute> {
                MarketsScreen(onOpenCoin = { navController.navigate(DetailRoute(it)) })
            }
            composable<WatchlistRoute> {
                WatchlistScreen(
                    onOpenCoin = { navController.navigate(DetailRoute(it)) },
                    onOpenMarkets = { navController.navigateToTopLevel(MarketsRoute) },
                )
            }
            composable<SettingsRoute> { SettingsScreen() }
            composable<DetailRoute> {
                DetailScreen(onBack = { navController.navigateUp() })
            }
        }
    }
}

private fun NavHostController.navigateToTopLevel(route: Any) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

private val NavBackStackEntry.isDetail: Boolean get() = destination.hasRoute(DetailRoute::class)

// Detail screens slide in from the side; switching tabs cross-fades.

private fun AnimatedContentTransitionScope<NavBackStackEntry>.enter(): EnterTransition =
    if (targetState.isDetail) slideInHorizontally(tween(PUSH_DURATION, easing = FastOutSlowInEasing)) { it } + fadeIn(tween(PUSH_DURATION))
    else fadeIn(tween(FADE_DURATION))

private fun AnimatedContentTransitionScope<NavBackStackEntry>.exit(): ExitTransition =
    if (targetState.isDetail) slideOutHorizontally(tween(PUSH_DURATION, easing = FastOutSlowInEasing)) { -it / 4 } + fadeOut(tween(PUSH_DURATION))
    else fadeOut(tween(FADE_DURATION))

private fun AnimatedContentTransitionScope<NavBackStackEntry>.popEnter(): EnterTransition =
    if (initialState.isDetail) slideInHorizontally(tween(PUSH_DURATION, easing = FastOutSlowInEasing)) { -it / 4 } + fadeIn(tween(PUSH_DURATION))
    else fadeIn(tween(FADE_DURATION))

private fun AnimatedContentTransitionScope<NavBackStackEntry>.popExit(): ExitTransition =
    if (initialState.isDetail) slideOutHorizontally(tween(PUSH_DURATION, easing = FastOutSlowInEasing)) { it } + fadeOut(tween(PUSH_DURATION))
    else fadeOut(tween(FADE_DURATION))

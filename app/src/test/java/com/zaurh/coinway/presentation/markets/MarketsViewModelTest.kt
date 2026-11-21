package com.zaurh.coinway.presentation.markets

import com.zaurh.coinway.core.util.AppError
import com.zaurh.coinway.domain.model.FiatCurrency
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MarketsViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val remote = listOf(
        coin("bitcoin", rank = 1, price = 60000.0, change = 1.2),
        coin("ethereum", rank = 2, price = 3000.0, change = -2.5),
        coin("solana", rank = 3, price = 150.0, change = 6.0),
        coin("dogecoin", rank = 4, price = 0.12, change = -0.4),
    )
    private lateinit var coins: FakeCoinRepository
    private lateinit var settings: FakeSettingsRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        coins = FakeCoinRepository(remote)
        settings = FakeSettingsRepository()
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel() = MarketsViewModel(coins, settings)

    @Test
    fun `loads coins on start`() = runTest(dispatcher) {
        val vm = viewModel()
        advanceUntilIdle()

        val state = vm.state.value
        assertFalse(state.isInitialLoading)
        assertEquals(listOf("bitcoin", "ethereum", "solana", "dogecoin"), state.coins.map { it.id })
        assertEquals(4, state.totalCount)
        assertEquals(55.0, state.global!!.btcDominance, 0.0)
        assertNull(state.error)
    }

    @Test
    fun `search matches name and symbol`() = runTest(dispatcher) {
        val vm = viewModel()
        advanceUntilIdle()

        vm.onIntent(MarketsIntent.QueryChanged("sol"))
        assertEquals(listOf("solana"), vm.state.value.coins.map { it.id })

        vm.onIntent(MarketsIntent.QueryChanged("nothing"))
        assertTrue(vm.state.value.showNoResults)

        vm.onIntent(MarketsIntent.ClearQuery)
        assertEquals(4, vm.state.value.coins.size)
    }

    @Test
    fun `gainers filter sorts by biggest change`() = runTest(dispatcher) {
        val vm = viewModel()
        advanceUntilIdle()

        vm.onIntent(MarketsIntent.FilterSelected(MarketFilter.Gainers))
        assertEquals(listOf("solana", "bitcoin"), vm.state.value.coins.map { it.id })

        vm.onIntent(MarketsIntent.FilterSelected(MarketFilter.Losers))
        assertEquals(listOf("ethereum", "dogecoin"), vm.state.value.coins.map { it.id })
    }

    @Test
    fun `selecting the same sort flips direction`() = runTest(dispatcher) {
        val vm = viewModel()
        advanceUntilIdle()

        vm.onIntent(MarketsIntent.SortSelected(MarketSort.Price))
        assertEquals("bitcoin", vm.state.value.coins.first().id)

        vm.onIntent(MarketsIntent.SortSelected(MarketSort.Price))
        assertEquals("dogecoin", vm.state.value.coins.first().id)
    }

    @Test
    fun `failed first load shows full screen error`() = runTest(dispatcher) {
        coins.refreshError = AppError.Network
        val vm = viewModel()
        advanceUntilIdle()

        assertTrue(vm.state.value.showFullScreenError)
        assertFalse(vm.state.value.showStaleBanner)
    }

    @Test
    fun `failed refresh with cached data shows banner and message`() = runTest(dispatcher) {
        val vm = viewModel()
        advanceUntilIdle()

        coins.refreshError = AppError.RateLimited
        vm.onIntent(MarketsIntent.Refresh)
        advanceUntilIdle()

        assertTrue(vm.state.value.showStaleBanner)
        assertFalse(vm.state.value.isRefreshing)
        assertTrue(vm.effects.first() is MarketsEffect.ShowMessage)
    }

    @Test
    fun `long press toggles favorite`() = runTest(dispatcher) {
        val vm = viewModel()
        advanceUntilIdle()

        vm.onIntent(MarketsIntent.ToggleFavorite(remote.first()))
        advanceUntilIdle()

        assertTrue(vm.state.value.coins.first { it.id == "bitcoin" }.isFavorite)
        assertTrue(vm.effects.first() is MarketsEffect.ShowMessage)
    }

    @Test
    fun `changing currency refetches prices`() = runTest(dispatcher) {
        val vm = viewModel()
        advanceUntilIdle()

        settings.setCurrency(FiatCurrency.EUR)
        advanceUntilIdle()

        assertEquals(listOf(FiatCurrency.USD, FiatCurrency.EUR), coins.refreshedCurrencies)
        assertEquals("eur", vm.state.value.coins.first().currency)
    }

    @Test
    fun `clicking a coin opens it`() = runTest(dispatcher) {
        val vm = viewModel()
        vm.onIntent(MarketsIntent.CoinClicked("bitcoin"))
        assertEquals(MarketsEffect.OpenCoin("bitcoin"), vm.effects.first())
    }
}

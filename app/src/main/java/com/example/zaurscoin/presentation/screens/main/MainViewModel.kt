package com.example.zaurscoin.presentation.screens.main

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zaurscoin.common.Resource
import com.example.zaurscoin.domain.model.CoinList
import com.example.zaurscoin.domain.use_case.CoinListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val coinListUseCase: CoinListUseCase,
) : ViewModel() {

    val coinList = mutableStateOf<List<CoinList>>(listOf())
    var isRefreshing = mutableStateOf(false)

    private var initialCoinList = listOf<CoinList>()
    private var isSearchStarting = true

    init {
        getCoinList()
    }

    private fun getCoinList() {
        viewModelScope.launch {
            coinListUseCase().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        coinList.value = result.data ?: emptyList()
                    }
                    is Resource.Loading -> {

                    }
                    is Resource.Error -> {

                    }
                }
            }
        }
    }

    //Search Coins
    fun searchCoins(query: String) {
        val listToSearch = if (isSearchStarting) {
            coinList.value
        } else {
            initialCoinList
        }
        viewModelScope.launch(Dispatchers.Default) {
            if (query.isEmpty()) {
                coinList.value = initialCoinList
                isSearchStarting = true
                return@launch
            }
            val results = listToSearch.filter {
                it.name!!.contains(
                    query.trim(),
                    ignoreCase = true
                ) || it.symbol!!.contains(query.trim(), ignoreCase = true)
            }
            if (isSearchStarting) {
                initialCoinList = coinList.value
                isSearchStarting = false
            }
            coinList.value = results
        }
    }

    fun clearSearch() {
        coinList.value = initialCoinList
        isSearchStarting = true
    }

    //Refresh Coins
    fun refreshCoinList() {
        coinList.value = emptyList()
        getCoinList()
    }

}

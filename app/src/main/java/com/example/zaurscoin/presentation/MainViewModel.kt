package com.example.zaurscoin.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zaurscoin.common.Resource
import com.example.zaurscoin.domain.model.Chart
import com.example.zaurscoin.domain.model.Coin
import com.example.zaurscoin.domain.model.CoinList
import com.example.zaurscoin.domain.use_case.ChartUseCase
import com.example.zaurscoin.domain.use_case.CoinListUseCase
import com.example.zaurscoin.domain.use_case.CoinUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val coinListUseCase: CoinListUseCase,
    private val coinUseCase: CoinUseCase,
    private val chartUseCase: ChartUseCase,
    savedStateHandle: SavedStateHandle
): ViewModel(){

    val coinList = mutableStateOf<List<CoinList>>(listOf())
    val coinListError = mutableStateOf("")

    val coin = mutableStateOf<Coin?>(null)
    val chart = MutableLiveData<Chart>()


    private var initialCoinList = listOf<CoinList>()
    private var isSearchStarting = true

    init {
        getCoinList()
        savedStateHandle.get<String>("id")?.let { id ->
            getCoin(id)
            getChart(id)
        }
    }


    private fun getChart(id: String){
        viewModelScope.launch {
            chartUseCase(id).collect{ result ->
                when(result){
                    is Resource.Success -> {
                        chart.value = result.data
                    }
                    is Resource.Loading -> {

                    }
                    is Resource.Error -> {

                    }
                }
            }
        }

    }

    private fun getCoin(id: String){
        viewModelScope.launch {
            coinUseCase(id).collect{ result ->
                when(result){
                    is Resource.Success -> {
                        coin.value = result.data
                    }
                    is Resource.Loading -> {

                    }
                    is Resource.Error -> {
                        coin.value = null
                    }
                }
            }
        }
    }

    private fun getCoinList(){
        viewModelScope.launch {
            coinListUseCase().collect{ result ->
                when(result){
                    is Resource.Success -> {
                        coinList.value = result.data ?: emptyList()
                    }
                    is Resource.Loading -> {

                    }
                    is Resource.Error -> {
                        coinListError.value = "Error"
                    }
                }
            }
        }
    }



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

}

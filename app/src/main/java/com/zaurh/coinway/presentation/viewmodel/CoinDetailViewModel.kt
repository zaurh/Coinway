package com.zaurh.coinway.presentation.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaurh.coinway.common.Resource
import com.zaurh.coinway.domain.use_case.CoinUseCase
import com.zaurh.coinway.presentation.screen.coin_detail.CoinDetailState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoinDetailViewModel @Inject constructor(
    private val coinUseCase: CoinUseCase,
    savedStateHandle: SavedStateHandle
): ViewModel(){

    private val _coin = mutableStateOf(CoinDetailState())
    val coin: State<CoinDetailState> = _coin


    init {
        savedStateHandle.get<String>("id")?.let { id ->
            getCoin(id)
        }
    }

    private fun getCoin(id: String){
        viewModelScope.launch {
            coinUseCase(id).collect{ result ->
                when(result){
                    is Resource.Success -> {
                        _coin.value = coin.value.copy(
                            coin = result.data,
                            isLoading = false
                        )
                    }
                    is Resource.Loading -> {
                        _coin.value = coin.value.copy(
                            isLoading = true
                        )
                    }
                    is Resource.Error -> {
                        _coin.value = coin.value.copy(
                            error = "There is a network problem. Please try again",
                            isLoading = false
                        )
                    }
                }
            }
        }
    }
}
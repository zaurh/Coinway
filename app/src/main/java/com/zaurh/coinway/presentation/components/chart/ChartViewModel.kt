package com.zaurh.coinway.presentation.components.chart

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaurh.coinway.common.Resource
import com.zaurh.coinway.domain.use_case.ChartUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChartViewModel @Inject constructor(
    private val chartUseCase: ChartUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _chart = mutableStateOf(ChartState())
    val chart: State<ChartState> = _chart


    init {
        savedStateHandle.get<String>("id")?.let { id ->
            getChart(id)
        }
    }


    private fun getChart(id: String) {
        viewModelScope.launch {
            chartUseCase(id).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _chart.value = chart.value.copy(
                            chart = result.data,
                            isLoading = false
                        )
                    }
                    is Resource.Loading -> {
                        _chart.value = chart.value.copy(
                            isLoading = true
                        )
                    }
                    is Resource.Error -> {
                        _chart.value = chart.value.copy(
                            error = "Error",
                            isLoading = false
                        )
                    }
                }
            }
        }
    }
}
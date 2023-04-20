package com.example.zaurscoin.domain.use_case

import com.example.zaurscoin.common.Resource
import com.example.zaurscoin.domain.model.Chart
import com.example.zaurscoin.domain.repository.CoinRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChartUseCase @Inject constructor(
    private val repository: CoinRepository
) {

    operator fun invoke(id: String): Flow<Resource<Chart>> {
        return repository.getChart(id)
    }
}
package com.zaurh.coinway.domain.use_case

import com.zaurh.coinway.common.Resource
import com.zaurh.coinway.domain.model.Chart
import com.zaurh.coinway.domain.repository.CoinRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChartUseCase @Inject constructor(
    private val repository: CoinRepository
) {

    operator fun invoke(id: String): Flow<Resource<Chart>> {
        return repository.getChart(id)
    }
}
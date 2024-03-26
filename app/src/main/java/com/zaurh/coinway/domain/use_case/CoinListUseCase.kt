package com.zaurh.coinway.domain.use_case

import com.zaurh.coinway.common.Resource
import com.zaurh.coinway.domain.model.CoinList
import com.zaurh.coinway.domain.repository.CoinRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CoinListUseCase @Inject constructor(
    private val repository: CoinRepository
) {

    operator fun invoke(): Flow<Resource<List<CoinList>>> {
        return repository.getCoins()
    }
}
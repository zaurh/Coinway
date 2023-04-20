package com.example.zaurscoin.domain.use_case

import com.example.zaurscoin.common.Resource
import com.example.zaurscoin.domain.model.CoinList
import com.example.zaurscoin.domain.repository.CoinRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CoinListUseCase @Inject constructor(
    private val repository: CoinRepository
) {

    operator fun invoke(): Flow<Resource<List<CoinList>>> {
        return repository.getCoins()
    }
}
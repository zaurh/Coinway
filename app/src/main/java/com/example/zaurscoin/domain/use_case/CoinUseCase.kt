package com.example.zaurscoin.domain.use_case

import com.example.zaurscoin.common.Resource
import com.example.zaurscoin.domain.model.Coin
import com.example.zaurscoin.domain.model.CoinList
import com.example.zaurscoin.domain.repository.CoinRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CoinUseCase @Inject constructor(
    private val repository: CoinRepository
) {

    operator fun invoke(id: String): Flow<Resource<Coin>> {
        return repository.getCoin(id)
    }
}
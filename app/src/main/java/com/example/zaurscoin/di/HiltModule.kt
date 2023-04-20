package com.example.zaurscoin.di

import android.app.Application
import androidx.room.Room
import com.example.zaurscoin.common.Constants.BASE_URL
import com.example.zaurscoin.data.remote.CoinApi
import com.example.zaurscoin.data.repository.CoinRepositoryImpl
import com.example.zaurscoin.domain.repository.CoinRepository
import com.example.zaurscoin.domain.use_case.CoinListUseCase
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    //Retrofit
    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    @Singleton
    @Provides
    fun provideRetrofitApi(retrofit: Retrofit): CoinApi{
        return retrofit.create(CoinApi::class.java)
    }

    //Repository
    @Singleton
    @Provides
    fun provideRepository(api: CoinApi): CoinRepository = CoinRepositoryImpl(api)

    @Singleton
    @Provides
    fun provideUseCase(coinRepository: CoinRepository) = CoinListUseCase(coinRepository)

}
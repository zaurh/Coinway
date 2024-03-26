package com.zaurh.coinway.di

import android.app.Application
import androidx.room.Room
import com.zaurh.coinway.common.Constants.BASE_URL
import com.zaurh.coinway.data.local.CoinListDB
import com.zaurh.coinway.data.remote.CoinApi
import com.zaurh.coinway.data.repository.CoinRepositoryImpl
import com.zaurh.coinway.domain.repository.CoinRepository
import com.zaurh.coinway.domain.use_case.CoinListUseCase
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
    fun provideRetrofitApi(retrofit: Retrofit): CoinApi {
        return retrofit.create(CoinApi::class.java)
    }

    //Repository
    @Singleton
    @Provides
    fun provideRepository(api: CoinApi, db: CoinListDB): CoinRepository =
        CoinRepositoryImpl(api, db.coinListDao())

    @Singleton
    @Provides
    fun provideUseCase(coinRepository: CoinRepository) = CoinListUseCase(coinRepository)

    //Room
    @Singleton
    @Provides
    fun provideRoomDatabase(app: Application): CoinListDB {
        return Room.databaseBuilder(
            app,
            CoinListDB::class.java,
            "database_name"
        ).fallbackToDestructiveMigration().build()
    }


}

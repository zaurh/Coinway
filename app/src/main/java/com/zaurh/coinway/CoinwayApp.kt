package com.zaurh.coinway

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CoinwayApp : Application() {

    override fun onCreate() {
        super.onCreate()
        // Versions before 2.0 cached prices in a database we no longer use.
        deleteDatabase(LEGACY_DATABASE_NAME)
    }

    private companion object {
        const val LEGACY_DATABASE_NAME = "database_name"
    }
}

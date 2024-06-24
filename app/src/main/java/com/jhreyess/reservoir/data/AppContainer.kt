package com.jhreyess.reservoir.data

import android.content.Context
import com.jhreyess.reservoir.data.local.AppDatabase
import com.jhreyess.reservoir.data.local.SettingsDataStore
import com.jhreyess.reservoir.data.local.dataStore
import com.jhreyess.reservoir.data.network.WaterApi

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val recordsRepository: RecordsRepository
    val damRepository: DamRepository
    val dataStore: SettingsDataStore
}

/**
 * [AppContainer] implementation that provides instance of repositories
 */
class AppDataContainer(private val context: Context) : AppContainer {
    /**
     * Implementation for repositories
     */
    override val recordsRepository: RecordsRepository by lazy {
        RecordsRepository(
            recordsDao = AppDatabase.getDatabase(context).recordsDao
        )
    }
    override val damRepository: DamRepository by lazy {
        DamRepository(
            apiService = WaterApi.retrofitService,
            damsDao = AppDatabase.getDatabase(context).damsDao
        )
    }

    override val dataStore: SettingsDataStore by lazy {
        SettingsDataStore(context.dataStore)
    }
}
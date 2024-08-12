package com.jhreyess.reservoir.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private const val PREFERENCES_NAME = "prefs"

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = PREFERENCES_NAME
)

class SettingsDataStore(
    private val preferencesDataStore: DataStore<Preferences>
) {
    private val LAST_UPDATE = longPreferencesKey("last_update")
    private val LAST_ID = longPreferencesKey("last_id")
    private val LAST_DATE = stringPreferencesKey("last_date")
    private val FIRST_FETCH = booleanPreferencesKey("first_fetch")

    val preferenceFlow: Flow<Long> = preferencesDataStore.data
        .catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[LAST_UPDATE] ?: 0L
        }

    val lastFetchedIdPreferenceFlow: Flow<Long> = preferencesDataStore.data
        .catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[LAST_ID] ?: 0L
        }

    val lastFetchedDatePreferenceFlow: Flow<String> = preferencesDataStore.data
        .catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[LAST_DATE] ?: ""
        }

    val firstFetchPreferenceFlow: Flow<Boolean> = preferencesDataStore.data
        .catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[FIRST_FETCH] ?: false
        }

    suspend fun saveLastUpdateToPreferences(lastUpdate: Long) {
        preferencesDataStore.edit { preferences ->
            preferences[LAST_UPDATE] = lastUpdate
        }
    }

    suspend fun saveLastFetchedIdToPreferences(id: Long) {
        preferencesDataStore.edit { preferences ->
            preferences[LAST_ID] = id
        }
    }

    suspend fun saveLastFetchedDateToPreferences(date: String) {
        preferencesDataStore.edit { preferences ->
            preferences[LAST_DATE] = date
        }
    }

    suspend fun saveFirstFetchToPreference(newValue: Boolean) {
        preferencesDataStore.edit { preferences ->
            preferences[FIRST_FETCH] = newValue
        }
    }
}
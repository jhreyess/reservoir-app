package com.jhreyess.reservoir.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.jhreyess.reservoir.data.DamRepository
import com.jhreyess.reservoir.data.RecordsRepository
import com.jhreyess.reservoir.data.local.DamEntity
import com.jhreyess.reservoir.data.local.RecordEntity
import com.jhreyess.reservoir.data.local.SettingsDataStore
import com.jhreyess.reservoir.data.model.Result
import com.jhreyess.reservoir.util.getCurrentDate
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import kotlin.math.absoluteValue

class HomeViewModel(
    private val recordRepo: RecordsRepository,
    private val damRepo: DamRepository,
    private val dataStore: SettingsDataStore
): ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state = combine(
        _state,
        recordRepo.getRecords(30),
        damRepo.allDams,
        dataStore.preferenceFlow
    ) { state, records, dams, lastUpdate ->
        val last10days = records.take(10)
        val newPeriodAvg = last10days.take(5).sumOf { it.waterLevels.toDouble() }.div(5)
        val oldPeriodAvg = last10days.takeLast(5).sumOf { it.waterLevels.toDouble() }.div(5)
        state.copy(
            dams = dams.sortedByDescending { it.id },
            totalStorage = dams.sumOf { it.totalStorage.toDouble() }.toFloat(),
            diff = ((newPeriodAvg - oldPeriodAvg) / oldPeriodAvg).toFloat(),
            diffValue = (newPeriodAvg - oldPeriodAvg).toFloat().absoluteValue,
            currentStorage = dams.sumOf { it.currentStorage.toDouble() }.toFloat(),
            records = records,
            lastUpdate = lastUpdate
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        HomeState()
    )

    init {
        viewModelScope.launch {
            val hasData = dataStore.firstFetchPreferenceFlow.first()
            if(!hasData) {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale("es","MX"))
                dateFormat.timeZone = TimeZone.getTimeZone("America/Monterrey")
                val calendar = Calendar.getInstance()
                val last30Days = mutableListOf<String>()

                repeat(30) {
                    val date = dateFormat.format(calendar.time)
                    last30Days.add(date)
                    calendar.add(Calendar.DAY_OF_YEAR, -1)
                }

                last30Days.reversed().mapIndexed { idx, day ->
                    async {
                        Log.d("DEBUG", day)
                        if(idx == last30Days.size - 1) {
                            pollData(day)
                        } else {
                            pollData(day, true)
                        }
                    }
                }.awaitAll()

                Log.d("DEBUG", "Storing first fetch in data store...")
                dataStore.saveFirstFetchToPreference(true)
            }
        }
    }

    private suspend fun pollData(
        from: String,
        fetchDataOnly: Boolean = false
    ) {
        if(fetchDataOnly) {
            fetchData(from, false)
            return
        }

        damRepo.getLastUpdate().collect { result ->
            when(result) {
                is Result.Success -> {
                    val remoteId = result.data.id
                    val lastFetchedId = dataStore.lastFetchedIdPreferenceFlow.first()
                    Log.d("DEBUG", "Remote ID at vm: $remoteId")
                    Log.d("DEBUG", "Local ID at vm: $lastFetchedId")
                    if(remoteId > lastFetchedId) {
                        Log.d("DEBUG", "View Model fetching...")
                        fetchData(result.data.date, true, remoteId)
                    }
                    val now = Calendar.getInstance(Locale.getDefault()).timeInMillis
                    dataStore.saveLastUpdateToPreferences(now)
                }
                is Result.Loading -> { _state.update { it.copy(isLoading = result.isLoading) } }
                is Result.Error -> { result.exception.message }
            }
        }
    }

    private suspend fun fetchData(
        from: String,
        storeInDb: Boolean,
        fetchedId: Long? = null
    ) {
        damRepo.getInformation(from, storeInDb).collect { result ->
            if(result is Result.Success) {
                recordRepo.insert(result.data)
                fetchedId?.let {
                    dataStore.saveLastFetchedIdToPreferences(it)
                }
            }
        }
    }

    fun onEvent(event: ScreenEvent) {
        when(event) {
            is ScreenEvent.Refresh -> {
                viewModelScope.launch {
                    pollData(getCurrentDate())
                }
            }
        }
    }

    companion object {
        fun provideFactory(
            recordsRepo: RecordsRepository,
            damRepo: DamRepository,
            dataStore: SettingsDataStore
        ) : ViewModelProvider.Factory = object: ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return HomeViewModel(recordsRepo, damRepo, dataStore) as T
                }
                throw IllegalArgumentException("Unable to construct viewmodel")
            }
        }
    }
}

sealed interface ScreenEvent {
    data object Refresh: ScreenEvent
}

data class HomeState(
    val dams: List<DamEntity> = emptyList(),
    val totalStorage: Float = 0.0f,
    val currentStorage: Float = 0.0f,
    val diff: Float = 0.0f,
    val diffValue: Float = 0.0f,
    val records: List<RecordEntity> = emptyList(),
    val lastUpdate: Long = 0L,
    val isLoading: Boolean = false,
)
package com.jhreyess.reservoir.workers

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.jhreyess.reservoir.App
import com.jhreyess.reservoir.R
import com.jhreyess.reservoir.data.local.getWaterAdice
import com.jhreyess.reservoir.data.network.WaterApi
import com.jhreyess.reservoir.util.formatDecimals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Locale
import com.jhreyess.reservoir.data.model.Result as ModelResult

class PollDataWorker(
    private val context: Context,
    private val params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    override suspend fun doWork(): Result {

        val appContainer = (applicationContext as App).container
        val recordsRepo = appContainer.recordsRepository
        val damsRepo = appContainer.damRepository

        val firstFetch = appContainer.dataStore.firstFetchPreferenceFlow.first()
        Log.d("DEBUG", "(Worker) First fetch?: $firstFetch ")
        if(!firstFetch) {
            return Result.success()
        }

        return withContext(Dispatchers.IO) {
            return@withContext try{
                val response = WaterApi.retrofitService.getLastUpdate().first()
                val remoteId = response.id
                val localId = appContainer.dataStore.lastFetchedIdPreferenceFlow.first()
                Log.d("DEBUG", "Worker fetching...")
                Log.d("DEBUG", "Local ID at worker: $localId")

                if(remoteId > localId) {
                    damsRepo.getInformation(response.date, true).collect { result ->
                        if(result is ModelResult.Success) {
                            recordsRepo.insert(result.data)
                            appContainer.dataStore.saveLastFetchedIdToPreferences(remoteId)

                            val formattedString = StringBuilder()
                            result.data.forEach {
                                val storage = it.currentPercStorage.times(100).formatDecimals()
                                formattedString.append("\uD83D\uDCA7 ${it.name}: $storage%\n")
                            }
                            showNotification("Niveles de Agua de hoy", formattedString.toString())
                        }
                    }
                } else {
                    showNotification("Recuerda", getWaterAdice())
                }

                val now = Calendar.getInstance(Locale.getDefault()).timeInMillis
                appContainer.dataStore.saveLastUpdateToPreferences(now)
                Result.success()
            } catch (e: Exception) {
                Result.failure()
            }
        }
    }

    private fun showNotification(
        title: String,
        message: String
    ) {
        val notification = NotificationCompat.Builder(context, DAILY_UPDATE_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_icon)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(message))
            .build()
        notificationManager.notify(1, notification)
    }

    companion object {
        const val DAILY_UPDATE_CHANNEL_ID = "daily_channel"
    }

}
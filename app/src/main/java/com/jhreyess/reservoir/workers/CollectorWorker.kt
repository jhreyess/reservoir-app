package com.jhreyess.reservoir.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.jhreyess.reservoir.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CollectorWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {

        val appContainer = (applicationContext as App).container
        val recordsRepo = appContainer.recordsRepository

        return withContext(Dispatchers.IO) {
            try {
                // 31 days * 24 hours * 60 minutes * 60 seconds * 1000 milliseconds
                val thirtyDaysAgoTimestamp = System.currentTimeMillis() - 2678400000L
                Log.d("DEBUG", "Removing all records older than timestamp: $thirtyDaysAgoTimestamp")
                recordsRepo.deleteOldRecords(thirtyDaysAgoTimestamp)
                Result.success()
            }catch (e: Exception) {
                Result.failure()
            }
        }
    }
}
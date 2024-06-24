package com.jhreyess.reservoir.data

import com.jhreyess.reservoir.data.local.DamEntity
import com.jhreyess.reservoir.data.local.RecordDao
import com.jhreyess.reservoir.data.local.RecordEntity
import com.jhreyess.reservoir.util.asTimestamp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.Date

class RecordsRepository(
    private val recordsDao: RecordDao
) {

    fun getRecords(amount: Int): Flow<List<RecordEntity>> = recordsDao.getRecords(amount)

    suspend fun insert(dams: List<DamEntity>) {

        val waterLevels = dams.sumOf { it.currentStorage.toDouble() }.toFloat()
        val timestamp = dams.first().lastUpdated.asTimestamp()

        val newRecord = RecordEntity(
            waterLevels = waterLevels,
            timestamp = Date(timestamp),
        )
        recordsDao.insertRecord(newRecord)
    }

    suspend fun deleteOldRecords(until: Long) {
        withContext(Dispatchers.IO) {
            recordsDao.deleteRecords(until)
        }
    }
}
package com.jhreyess.reservoir.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordDao {
    @Query("SELECT * FROM records ORDER BY timestamp DESC LIMIT :amount")
    fun getRecords(amount: Int): Flow<List<RecordEntity>>

    @Upsert
    suspend fun insertRecord(record: RecordEntity)

    @Query("DELETE FROM records WHERE timestamp < :timestamp")
    suspend fun deleteRecords(timestamp: Long)
}
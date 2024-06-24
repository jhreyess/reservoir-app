package com.jhreyess.reservoir.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface DamDao {
    @Query("SELECT * FROM dams")
    fun getDams(): Flow<List<DamEntity>>

    @Upsert
    suspend fun insertDams(dams: List<DamEntity>)

    @Delete
    suspend fun deleteDam(dam: DamEntity)
}
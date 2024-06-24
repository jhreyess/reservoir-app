package com.jhreyess.reservoir.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dams")
data class DamEntity(
    @PrimaryKey val id: String,
    val name: String,
    val state: String,
    val totalStorage: Float,
    val currentStorage: Float,
    val currentPercStorage: Float,
    val lastUpdated: String
)
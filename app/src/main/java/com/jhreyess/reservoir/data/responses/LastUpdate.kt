package com.jhreyess.reservoir.data.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LastUpdate(
    val id: Long,
    @SerialName(value = "fecha") val date: String
)
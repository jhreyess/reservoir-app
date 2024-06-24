package com.jhreyess.reservoir.data.responses

import com.jhreyess.reservoir.data.local.DamEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DamInfo(
    @SerialName(value = "idmonitoreodiario")
    val id: Long = 0L,
    @SerialName(value = "fechamonitoreo")
    val date: String = "",
    @SerialName(value = "clavesih")
    val keyName: String = "",
    @SerialName(value = "nombrecomun")
    var name: String = "",
    @SerialName(value = "estado")
    val state: String = "",
    @SerialName(value = "uso")
    val type: String = "",
    @SerialName(value = "inicioop")
    val startYear: String = "",
    @SerialName(value = "nameelev")
    val elevationNAME: Float = 0f,
    @SerialName(value = "namealmac")
    val storageNAME: Float = 0f,
    @SerialName(value = "namoelev")
    val elevationNAMO: Float = 0f,
    @SerialName(value = "namoalmac")
    val storageNAMO: Float = 0f,
    @SerialName(value = "almacenaactual")
    val currentStorage: Float = 0f,
    @SerialName(value = "llenano")
    val percentageStorage: Double = 0.0
)

fun DamInfo.asDamEntity(): DamEntity {
    return DamEntity(
        id = keyName,
        name = name.split(",").first(),
        state = state,
        totalStorage = storageNAMO,
        currentStorage = currentStorage,
        currentPercStorage = percentageStorage.toFloat(),
        lastUpdated = date
    )
}
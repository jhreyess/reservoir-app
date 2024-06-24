package com.jhreyess.reservoir.data.network

import com.jhreyess.reservoir.data.responses.DamInfo
import com.jhreyess.reservoir.data.responses.LastUpdate
import retrofit2.Retrofit
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

private const val BASE_URL = "https://sinav30.conagua.gob.mx:8080/"

private val httpClient = OkHttpClient.Builder()
    .connectTimeout(60, TimeUnit.SECONDS)
    .readTimeout(60, TimeUnit.SECONDS)
    .build()

private val json = Json { ignoreUnknownKeys = true; coerceInputValues = true }
private val retrofit = Retrofit.Builder()
    .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
    .client(httpClient)
    .baseUrl(BASE_URL)
    .build()

interface WaterApiService {

    @GET("PresasPG/presas/reporte/{date}")
    suspend fun getDamsInfo(@Path("date") at: String): List<DamInfo>

    @GET("SINA44/fechaMonitoreo/ultimo")
    suspend fun getLastUpdate(): List<LastUpdate>

}

/**
 * A public Api object that exposes the lazy-initialized Retrofit service
 */
object WaterApi {
    val retrofitService: WaterApiService by lazy { retrofit.create(WaterApiService::class.java) }
}
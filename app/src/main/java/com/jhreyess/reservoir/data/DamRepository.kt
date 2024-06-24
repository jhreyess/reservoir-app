package com.jhreyess.reservoir.data

import android.util.Log
import com.jhreyess.reservoir.data.local.DamDao
import com.jhreyess.reservoir.data.local.DamEntity
import com.jhreyess.reservoir.data.model.Result
import com.jhreyess.reservoir.data.network.WaterApiService
import com.jhreyess.reservoir.data.responses.LastUpdate
import com.jhreyess.reservoir.data.responses.asDamEntity
import com.jhreyess.reservoir.util.asUTF8
import com.jhreyess.reservoir.util.unaccented
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class DamRepository(
    private val apiService: WaterApiService,
    private val damsDao: DamDao
) {

    val allDams: Flow<List<DamEntity>> = damsDao.getDams()

    suspend fun getLastUpdate(): Flow<Result<LastUpdate>> {
        return flow {
            emit(Result.Loading(true))

            val response = try {
                apiService.getLastUpdate()
            } catch(e: Exception) {
                e.printStackTrace()
                val message = when(e) {
                    is HttpException -> "No se pudo actualizar. Error ${e.code()}"
                    is IOException -> "Algo salió mal. Intenta de nuevo después."
                    else -> e.message
                }
                emit(Result.Error(Exception(message)))
                null
            }

            response?.let {
                emit(Result.Success(it.first()))
            }

            emit(Result.Loading(false))
        }
    }

    suspend fun getInformation(
        date: String,
        storeInDb: Boolean = false
    ): Flow<Result<List<DamEntity>>> {
        return flow {
            emit(Result.Loading(true))

            Log.d("API", "Polling at: $date")
            val response = try {
                apiService.getDamsInfo(at = date).filter {
                    it.state.asUTF8().unaccented().lowercase() == "nuevo leon"
                    && it.type.contains("AP")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                val message = when(e) {
                    is HttpException -> "No se pudo obtener la información. Error ${e.code()}"
                    is IOException -> "Algo salió mal. Intenta de nuevo después."
                    else -> e.message
                }
                emit(Result.Error(Exception(message)))
                null
            }

            response?.let { dams ->
            Log.d("API", "Response: $response")
                if(dams.isNotEmpty()) {
                    val result = dams.map { it.asDamEntity() }
                    if(storeInDb) {
                        damsDao.insertDams(result)
                    }
                    emit(Result.Success(result))
                }
            }

            emit(Result.Loading(false))
        }
    }

}
package com.example.pancreatic.history

import android.content.Context
import com.example.pancreatic.api.PredictResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class PredictionHistoryRepository private constructor(
    private val dao: PredictionRecordDao,
    private val moshi: Moshi,
) {
    fun observeAll() = dao.observeAll()
    fun observeById(id: Long) = dao.observeById(id)

    suspend fun insertSuccess(
        features: Map<String, Any?>,
        modelKey: String?,
        result: PredictResponse,
    ): Long {
        val featuresAdapter = moshi.adapter(Map::class.java)
        val resultAdapter = moshi.adapter(PredictResponse::class.java)
        val record = PredictionRecord(
            createdAtEpochMs = System.currentTimeMillis(),
            modelKey = modelKey,
            prediction = result.prediction,
            probability = result.probability,
            confidence = result.confidence,
            predictedLabel = result.predicted_label,
            message = result.message,
            modelUsed = result.model_used,
            modelName = result.model_name,
            featuresJson = featuresAdapter.toJson(features),
            resultJson = resultAdapter.toJson(result),
        )
        return dao.insert(record)
    }

    suspend fun clearAll() = dao.clearAll()

    companion object {
        @Volatile private var INSTANCE: PredictionHistoryRepository? = null

        fun get(context: Context): PredictionHistoryRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: run {
                    val db = PredictionHistoryDb.get(context)
                    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                    PredictionHistoryRepository(db.predictionRecordDao(), moshi)
                }.also { INSTANCE = it }
            }
        }
    }
}


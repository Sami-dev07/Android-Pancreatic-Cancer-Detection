package com.example.pancreatic.data

import com.example.pancreatic.api.ApiClient
import com.example.pancreatic.api.PancreaticApi
import com.example.pancreatic.api.ActiveModelResponse
import com.example.pancreatic.api.ModelPerformanceBlocksResponse
import com.example.pancreatic.api.ModelSummaryResponse
import com.example.pancreatic.api.ModelsListResponse
import com.example.pancreatic.api.PredictRequest
import com.example.pancreatic.api.PredictResponse
import com.example.pancreatic.api.PredictionSchemaResponse
import com.example.pancreatic.api.SelectModelRequest
import com.example.pancreatic.api.SelectModelResponse

class ModelRepository(
    private val api: PancreaticApi = ApiClient.pancreaticApi,
) {

    suspend fun getPredictionSchema(): PredictionSchemaResponse = api.predictionSchema()

    suspend fun predict(
        payload: PredictRequest,
        modelKey: String? = null,
    ): PredictResponse = api.predict(payload.copy(model = modelKey))

    suspend fun listModels(): ModelsListResponse = api.listModels()

    suspend fun getActiveModel(): ActiveModelResponse = api.getActiveModel()

    suspend fun selectModel(modelKey: String): SelectModelResponse =
        api.selectModel(SelectModelRequest(model = modelKey))

    suspend fun getPerformanceBlocks(): ModelPerformanceBlocksResponse = api.modelPerformanceBlocks()
    suspend fun getModelSummary(): ModelSummaryResponse = api.modelSummary()
}


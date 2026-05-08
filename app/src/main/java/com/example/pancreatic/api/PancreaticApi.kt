package com.example.pancreatic.api

import retrofit2.http.GET
import retrofit2.http.Body
import retrofit2.http.POST

interface PancreaticApi {
    @GET("/prediction/schema")
    suspend fun predictionSchema(): PredictionSchemaResponse

    @GET("/features")
    suspend fun features(): FeaturesResponse

    @GET("/model/performance")
    suspend fun modelPerformance(): ModelPerformanceResponse

    @GET("/model/performance/blocks")
    suspend fun modelPerformanceBlocks(): ModelPerformanceBlocksResponse

    @GET("/model/summary")
    suspend fun modelSummary(): ModelSummaryResponse

    @GET("/plots")
    suspend fun plots(): PlotsResponse

    @GET("/models")
    suspend fun listModels(): ModelsListResponse

    @GET("/active-model")
    suspend fun getActiveModel(): ActiveModelResponse

    @POST("/select-model")
    suspend fun selectModel(@Body body: SelectModelRequest): SelectModelResponse

    @POST("/predict")
    suspend fun predict(@Body body: PredictRequest): PredictResponse
}


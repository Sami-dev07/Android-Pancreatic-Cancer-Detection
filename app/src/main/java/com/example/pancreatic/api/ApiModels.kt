package com.example.pancreatic.api

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PredictRequest(
    val features: Map<String, Any?>,
    val model: String? = null,
)

@JsonClass(generateAdapter = true)
data class PredictResponse(
    val prediction: Int,
    val predicted_label: String,
    val probability: Double?,
    val confidence: Double?,
    val message: String,
    val model_path: String,
    val model_used: String? = null,
    val model_name: String? = null,
)

@JsonClass(generateAdapter = true)
data class ModelInfo(
    val key: String,
    val name: String,
    val description: String,
    val active: Boolean,
    val loaded: Boolean,
)

@JsonClass(generateAdapter = true)
data class ModelsListResponse(
    val available_models: List<ModelInfo>,
    val active_model: String,
)

@JsonClass(generateAdapter = true)
data class ActiveModelResponse(
    val active_model: String,
    val model_full_name: String,
    val description: String,
    val loaded: Boolean,
)

@JsonClass(generateAdapter = true)
data class SelectModelRequest(
    val model: String,
)

@JsonClass(generateAdapter = true)
data class SelectModelResponse(
    val message: String,
    val active_model: String,
    val model_full_name: String,
    val description: String,
)

@JsonClass(generateAdapter = true)
data class FeaturesResponse(
    val feature_columns: List<String>,
    val count: Int,
    val fields: List<SchemaField> = emptyList()
)

@JsonClass(generateAdapter = true)
data class SchemaField(
    val name: String,
    val label: String,
    val type: String,
    val required: Boolean = true,
    /**
     * Options may be strings or numbers depending on server heuristics.
     * Keep as Any to preserve compatibility with both.
     */
    val options: List<Any>? = null,
    val min: Double? = null,
    val max: Double? = null,
    val units: String? = null,
    val description: String? = null,
    val dataset_source: String? = null,
    val effect_note: String? = null,
)

@JsonClass(generateAdapter = true)
data class PredictionSchemaResponse(
    val fields: List<SchemaField>,
    val required_fields: List<String> = emptyList(),
    val target: Map<String, Any?>? = null,
)

@JsonClass(generateAdapter = true)
data class PlotItem(
    val filename: String,
    val bytes: Long,
    val static_url: String,
    val download_url: String
)

@JsonClass(generateAdapter = true)
data class PlotsResponse(
    val plots: List<PlotItem>,
    val count: Int
)

@JsonClass(generateAdapter = true)
data class ModelPerformanceResponse(
    val target_definition: String?,
    val best_model: String?,
    val summary_metrics: Map<String, Double?> = emptyMap(),
    val confusion_matrix: List<List<Int>>? = null,
    val classification_report: Map<String, Any?>? = null,
    val feature_importance: List<FeatureImportanceItem> = emptyList(),
    val all_models: Map<String, Any?> = emptyMap(),
    val plots: List<PlotItem> = emptyList(),
    val recommended_plots: Map<String, String?> = emptyMap(),
)

@JsonClass(generateAdapter = true)
data class FeatureImportanceItem(
    val feature: String,
    val importance: Double
)

@JsonClass(generateAdapter = true)
data class PerformanceChip(
    val label: String,
    val tone: String? = null
)

@JsonClass(generateAdapter = true)
data class PerformanceBlock(
    val id: String,
    val title: String,
    val style: String? = null,
    val lines: List<String> = emptyList(),
    val chips: List<PerformanceChip>? = null
)

@JsonClass(generateAdapter = true)
data class ModelPerformanceBlocksResponse(
    val title: String,
    val best_model: String? = null,
    val blocks: List<PerformanceBlock> = emptyList(),
    val plots: List<PlotItem> = emptyList()
)

@JsonClass(generateAdapter = true)
data class ModelSummaryResponse(
    val best_model: String? = null,
    val target_definition: String? = null,
    val metrics: Map<String, Double?> = emptyMap()
)


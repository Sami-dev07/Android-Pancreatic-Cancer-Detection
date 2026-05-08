package com.example.pancreatic.history

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Local-only record of a successful prediction.
 * Stores raw JSON for inputs/result so we don't need to change existing API models.
 */
@Entity(tableName = "prediction_records")
data class PredictionRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val createdAtEpochMs: Long,
    val modelKey: String?,
    val prediction: Int,
    val probability: Double?,
    val confidence: Double?,
    val predictedLabel: String,
    val message: String,
    val modelUsed: String?,
    val modelName: String?,
    val featuresJson: String,
    val resultJson: String,
)


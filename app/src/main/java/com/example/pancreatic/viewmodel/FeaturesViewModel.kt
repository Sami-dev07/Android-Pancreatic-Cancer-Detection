package com.example.pancreatic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pancreatic.api.PredictRequest
import com.example.pancreatic.api.userFacingMessage
import com.example.pancreatic.api.PredictResponse
import com.example.pancreatic.api.SchemaField
import com.example.pancreatic.data.ModelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/** Loads prediction schema and runs /predict with optional model override. */
class FeaturesViewModel(private val repository: ModelRepository) : ViewModel() {
    private val _schemaState = MutableStateFlow<UiState<List<SchemaField>>>(UiState.Idle)
    val schemaState: StateFlow<UiState<List<SchemaField>>> = _schemaState

    private val _predictionState = MutableStateFlow<UiState<PredictResponse>>(UiState.Idle)
    val predictionState: StateFlow<UiState<PredictResponse>> = _predictionState

    fun loadSchema() {
        viewModelScope.launch {
            _schemaState.value = UiState.Loading
            try {
                _schemaState.value = UiState.Success(repository.getPredictionSchema().fields)
            } catch (e: Exception) {
                _schemaState.value = UiState.Error(e.userFacingMessage())
            }
        }
    }

    /**
     * Sends a prediction request. [modelKey] is null to use the server default active model,
     * or one of lr / rf / xgb / ann for a per-request override.
     */
    fun predict(payload: Map<String, Any?>, modelKey: String? = null) {
        viewModelScope.launch {
            _predictionState.value = UiState.Loading
            try {
                _predictionState.value = UiState.Success(
                    repository.predict(PredictRequest(features = payload), modelKey),
                )
            } catch (e: Exception) {
                _predictionState.value = UiState.Error(e.userFacingMessage())
            }
        }
    }
}


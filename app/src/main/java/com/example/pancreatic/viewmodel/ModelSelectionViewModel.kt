package com.example.pancreatic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pancreatic.api.ActiveModelResponse
import com.example.pancreatic.api.ModelInfo
import com.example.pancreatic.api.SelectModelResponse
import com.example.pancreatic.api.userFacingMessage
import com.example.pancreatic.data.ModelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Drives the model selection screen: loads the registry from GET /models,
 * current default from GET /active-model, and applies POST /select-model.
 */
class ModelSelectionViewModel(
    private val repository: ModelRepository,
) : ViewModel() {

    private val _modelsState = MutableStateFlow<UiState<List<ModelInfo>>>(UiState.Idle)
    val modelsState: StateFlow<UiState<List<ModelInfo>>> = _modelsState

    private val _activeModelState = MutableStateFlow<UiState<ActiveModelResponse>>(UiState.Idle)
    val activeModelState: StateFlow<UiState<ActiveModelResponse>> = _activeModelState

    private val _selectModelState = MutableStateFlow<UiState<SelectModelResponse>>(UiState.Idle)
    val selectModelState: StateFlow<UiState<SelectModelResponse>> = _selectModelState

    /** Fetches all models and their load/active flags from the backend. */
    fun loadModels() {
        viewModelScope.launch {
            _modelsState.value = UiState.Loading
            try {
                _modelsState.value = UiState.Success(repository.listModels().available_models)
            } catch (e: Exception) {
                _modelsState.value = UiState.Error(e.userFacingMessage())
            }
        }
    }

    /** Fetches which model is currently the server default for /predict. */
    fun loadActiveModel() {
        viewModelScope.launch {
            _activeModelState.value = UiState.Loading
            try {
                _activeModelState.value = UiState.Success(repository.getActiveModel())
            } catch (e: Exception) {
                _activeModelState.value = UiState.Error(e.userFacingMessage())
            }
        }
    }

    /** Sets the global active model on the server and refreshes local state. */
    fun selectModel(modelKey: String) {
        viewModelScope.launch {
            _selectModelState.value = UiState.Loading
            try {
                val result = repository.selectModel(modelKey)
                _selectModelState.value = UiState.Success(result)
                loadActiveModel()
                loadModels()
            } catch (e: Exception) {
                _selectModelState.value = UiState.Error(e.userFacingMessage())
            }
        }
    }
}

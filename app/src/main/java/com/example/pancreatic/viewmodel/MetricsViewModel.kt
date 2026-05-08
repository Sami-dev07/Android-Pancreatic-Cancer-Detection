package com.example.pancreatic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pancreatic.api.ModelSummaryResponse
import com.example.pancreatic.api.userFacingMessage
import com.example.pancreatic.data.ModelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MetricsViewModel(private val repository: ModelRepository) : ViewModel() {
    private val _summaryState = MutableStateFlow<UiState<ModelSummaryResponse>>(UiState.Idle)
    val summaryState: StateFlow<UiState<ModelSummaryResponse>> = _summaryState

    fun loadSummary() {
        viewModelScope.launch {
            _summaryState.value = UiState.Loading
            try {
                _summaryState.value = UiState.Success(repository.getModelSummary())
            } catch (e: Exception) {
                _summaryState.value = UiState.Error(e.userFacingMessage())
            }
        }
    }
}


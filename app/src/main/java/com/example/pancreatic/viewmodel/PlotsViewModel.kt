package com.example.pancreatic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pancreatic.api.ModelPerformanceBlocksResponse
import com.example.pancreatic.api.userFacingMessage
import com.example.pancreatic.data.ModelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlotsViewModel(private val repository: ModelRepository) : ViewModel() {
    private val _dashboardState = MutableStateFlow<UiState<ModelPerformanceBlocksResponse>>(UiState.Idle)
    val dashboardState: StateFlow<UiState<ModelPerformanceBlocksResponse>> = _dashboardState

    fun loadDashboard() {
        viewModelScope.launch {
            _dashboardState.value = UiState.Loading
            try {
                _dashboardState.value = UiState.Success(repository.getPerformanceBlocks())
            } catch (e: Exception) {
                _dashboardState.value = UiState.Error(e.userFacingMessage())
            }
        }
    }
}


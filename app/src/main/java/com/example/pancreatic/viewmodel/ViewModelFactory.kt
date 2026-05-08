package com.example.pancreatic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pancreatic.data.ModelRepository

/** Central factory so Activities can use `by viewModels { ViewModelFactory() }`. */
class ViewModelFactory(
    private val repository: ModelRepository = ModelRepository(),
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return when {
            modelClass.isAssignableFrom(FeaturesViewModel::class.java) -> FeaturesViewModel(repository) as T
            modelClass.isAssignableFrom(PlotsViewModel::class.java) -> PlotsViewModel(repository) as T
            modelClass.isAssignableFrom(MetricsViewModel::class.java) -> MetricsViewModel(repository) as T
            modelClass.isAssignableFrom(ModelSelectionViewModel::class.java) ->
                ModelSelectionViewModel(repository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}


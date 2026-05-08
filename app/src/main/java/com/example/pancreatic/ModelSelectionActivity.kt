package com.example.pancreatic

import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pancreatic.viewmodel.ModelSelectionViewModel
import com.example.pancreatic.viewmodel.UiState
import com.example.pancreatic.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

/**
 * Screen for inspecting all server-side models and changing the global active model
 * via POST /select-model.
 */
class ModelSelectionActivity : AppCompatActivity() {

    private val viewModel: ModelSelectionViewModel by viewModels {
        ViewModelFactory()
    }

    private lateinit var cardAdapter: ModelCardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_model_selection)

        val title = findViewById<TextView>(R.id.modelSelectionTitle)
        val activeSubtitle = findViewById<TextView>(R.id.activeModelSubtitle)
        val activeDescription = findViewById<TextView>(R.id.activeModelDescription)
        val recycler = findViewById<RecyclerView>(R.id.modelsRecycler)
        val statusText = findViewById<TextView>(R.id.statusText)

        title.setText(R.string.model_selection_title)

        cardAdapter = ModelCardAdapter { key -> viewModel.selectModel(key) }
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = cardAdapter

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.modelsState.collect { state ->
                        when (state) {
                            is UiState.Loading -> statusText.setText(R.string.model_selection_loading_models)
                            is UiState.Success -> cardAdapter.submitList(state.data)
                            is UiState.Error -> statusText.text =
                                getString(R.string.model_selection_error_models, state.message)
                            UiState.Idle -> Unit
                        }
                    }
                }
                launch {
                    viewModel.activeModelState.collect { state ->
                        when (state) {
                            is UiState.Loading -> {
                                activeSubtitle.setText(R.string.model_selection_loading_active)
                                activeDescription.text = ""
                            }
                            is UiState.Success -> {
                                val a = state.data
                                activeSubtitle.text = getString(
                                    R.string.model_selection_active_subtitle,
                                    a.model_full_name,
                                )
                                activeDescription.text = a.description
                            }
                            is UiState.Error -> {
                                activeSubtitle.text = getString(R.string.model_selection_active_error)
                                activeDescription.text = state.message
                            }
                            UiState.Idle -> Unit
                        }
                    }
                }
                launch {
                    viewModel.selectModelState.collect { state ->
                        when (state) {
                            is UiState.Loading -> {
                                cardAdapter.setSelectInProgress(true)
                                statusText.setText(R.string.model_selection_switching)
                            }
                            is UiState.Success -> {
                                cardAdapter.setSelectInProgress(false)
                                statusText.text = getString(
                                    R.string.model_selection_switch_ok,
                                    state.data.message,
                                )
                            }
                            is UiState.Error -> {
                                cardAdapter.setSelectInProgress(false)
                                statusText.text = getString(
                                    R.string.model_selection_switch_error,
                                    state.message,
                                )
                            }
                            UiState.Idle -> cardAdapter.setSelectInProgress(false)
                        }
                    }
                }
            }
        }

        viewModel.loadModels()
        viewModel.loadActiveModel()
    }
}

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
import com.example.pancreatic.viewmodel.FeaturesViewModel
import com.example.pancreatic.viewmodel.UiState
import com.example.pancreatic.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

/**
 * Read-only glossary of schema features and their details (units/description/effect note).
 * Uses GET /prediction/schema so it stays aligned with the backend.
 */
class FeatureGlossaryActivity : AppCompatActivity() {

    private val viewModel: FeaturesViewModel by viewModels { ViewModelFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feature_glossary)

        val recycler = findViewById<RecyclerView>(R.id.glossaryRecycler)
        val statusText = findViewById<TextView>(R.id.statusText)

        recycler.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.schemaState.collect { state ->
                    when (state) {
                        is UiState.Loading -> statusText.text = getString(R.string.glossary_loading)
                        is UiState.Success -> {
                            val rows = state.data
                            statusText.text = if (rows.isEmpty()) getString(R.string.glossary_empty) else ""
                            recycler.adapter = FeatureGlossaryAdapter(rows)
                        }
                        is UiState.Error -> statusText.text = getString(R.string.glossary_error, state.message)
                        UiState.Idle -> Unit
                    }
                }
            }
        }

        viewModel.loadSchema()
    }
}


package com.example.pancreatic

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pancreatic.api.SchemaField
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch
import com.example.pancreatic.viewmodel.FeaturesViewModel
import com.example.pancreatic.viewmodel.UiState
import com.example.pancreatic.viewmodel.ViewModelFactory

/**
 * Dynamic patient feature form and /predict with optional per-request model override.
 */
class FeaturesActivity : AppCompatActivity() {

    private val viewModel: FeaturesViewModel by viewModels {
        ViewModelFactory()
    }
    private var adapter: FeatureInputAdapter? = null
    private var fields: List<SchemaField> = emptyList()
    private var lastPayloadForHistory: Map<String, Any?>? = null
    private var lastModelKeyForHistory: String? = null

    /** Keys aligned with [R.array.model_spinner_options] (null = use server active model). */
    private val modelSpinnerKeys: List<String?> = listOf(null, "lr", "rf", "xgb", "ann")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_features)

        val recycler = findViewById<RecyclerView>(R.id.featuresRecycler)
        val predictButton = findViewById<MaterialButton>(R.id.predictButton)
        val resultText = findViewById<TextView>(R.id.resultText)
        val modelSpinner = findViewById<Spinner>(R.id.modelSpinner)
        val changeModelButton = findViewById<MaterialButton>(R.id.changeModelButton)
        val openGlossaryButton = findViewById<MaterialButton>(R.id.openGlossaryButton)

        val spinnerAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.model_spinner_options,
            android.R.layout.simple_spinner_item,
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        modelSpinner.adapter = spinnerAdapter

        changeModelButton.setOnClickListener {
            startActivity(Intent(this, ModelSelectionActivity::class.java))
        }

        openGlossaryButton.setOnClickListener {
            startActivity(Intent(this, FeatureGlossaryActivity::class.java))
        }

        recycler.layoutManager = LinearLayoutManager(this)

        predictButton.setOnClickListener {
            val localAdapter = adapter ?: return@setOnClickListener
            val values = localAdapter.getValues()
            val requiredMissing = fields
                .filter { it.required }
                .map { it.name }
                .filter { values[it] == null || values[it].toString().isBlank() }
            if (requiredMissing.isNotEmpty()) {
                resultText.text = "Missing required fields: ${requiredMissing.joinToString(", ")}"
                return@setOnClickListener
            }

            val payload: MutableMap<String, Any?> = mutableMapOf()
            fields.forEach { field ->
                val raw = values[field.name]?.toString()?.trim().orEmpty()
                if (field.type == "number") {
                    val num = raw.toDoubleOrNull()
                    if (num == null) {
                        resultText.text = "Field `${field.label}` must be numeric."
                        return@setOnClickListener
                    }
                    payload[field.name] = num
                } else {
                    payload[field.name] = raw
                }
            }
            val modelKey = modelSpinnerKeys.getOrNull(modelSpinner.selectedItemPosition)
            lastPayloadForHistory = payload.toMap()
            lastModelKeyForHistory = modelKey
            viewModel.predict(payload, modelKey)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.schemaState.collect { state ->
                        when (state) {
                            is UiState.Loading -> resultText.text = "Loading /features..."
                            is UiState.Success -> {
                                fields = state.data
                                adapter = FeatureInputAdapter(fields) { field ->
                                    showLearnDialog(field)
                                }
                                recycler.adapter = adapter
                                resultText.text =
                                    "Loaded ${fields.size} fields. Select values for all fields, then Predict."
                            }
                            is UiState.Error -> resultText.text = "Error loading features: ${state.message}"
                            UiState.Idle -> Unit
                        }
                    }
                }
                launch {
                    viewModel.predictionState.collect { state ->
                        when (state) {
                            is UiState.Loading -> resultText.text = "Calling /predict..."
                            is UiState.Success -> {
                                val pr = state.data
                                // Save a local record (frontend-only). Does not change existing behavior.
                                val featuresForHistory = lastPayloadForHistory
                                if (featuresForHistory != null) {
                                    val repo = com.example.pancreatic.history.PredictionHistoryRepository.get(this@FeaturesActivity)
                                    launch {
                                        try {
                                            repo.insertSuccess(featuresForHistory, lastModelKeyForHistory, pr)
                                        } catch (e: Exception) {
                                            android.util.Log.w("FeaturesActivity", "Failed to persist prediction history: ${e.message}")
                                        }
                                    }
                                }
                                val unknown = getString(R.string.prediction_model_unknown)
                                val modelNameLine = getString(
                                    R.string.prediction_model_name_line,
                                    pr.model_name ?: unknown,
                                )
                                val modelKeyLine = getString(
                                    R.string.prediction_model_key_line,
                                    pr.model_used ?: unknown,
                                )
                                resultText.text = buildString {
                                    appendLine("Result: ${pr.predicted_label}")
                                    appendLine("Class: ${pr.prediction} (1=cancer, 0=no cancer)")
                                    appendLine("Cancer probability: ${pr.probability ?: "n/a"}")
                                    appendLine("Confidence: ${pr.confidence ?: "n/a"}")
                                    appendLine("Message: ${pr.message}")
                                    appendLine()
                                    appendLine(modelNameLine)
                                    appendLine(modelKeyLine)
                                    appendLine()
                                    appendLine("Note: this is an AI prediction, not a medical diagnosis.")
                                }
                            }
                            is UiState.Error -> resultText.text = "Error: ${state.message}"
                            UiState.Idle -> Unit
                        }
                    }
                }
            }
        }

        viewModel.loadSchema()
    }

    private fun showLearnDialog(field: SchemaField) {
        val lines = mutableListOf<String>()
        lines.add("${field.label} (${field.name})")

        field.units?.takeIf { it.isNotBlank() }?.let { u ->
            lines.add("")
            lines.add("Units: $u")
        }

        field.description?.takeIf { it.isNotBlank() }?.let { d ->
            lines.add("")
            lines.add("What it is:")
            lines.add(d)
        }

        if (field.type == "number" && (field.min != null || field.max != null)) {
            lines.add("")
            lines.add("Dataset range (observed): ${field.min ?: "—"} to ${field.max ?: "—"}")
        }

        field.dataset_source?.takeIf { it.isNotBlank() }?.let { src ->
            lines.add("")
            lines.add("Dataset:")
            lines.add(src)
        }

        field.effect_note?.takeIf { it.isNotBlank() }?.let { note ->
            lines.add("")
            lines.add("How it can affect the prediction:")
            lines.add(note)
        }

        AlertDialog.Builder(this)
            .setTitle("Learn")
            .setMessage(lines.joinToString("\n"))
            .setPositiveButton("OK", null)
            .show()
    }
}

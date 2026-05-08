package com.example.pancreatic

import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.launch
import com.example.pancreatic.viewmodel.MetricsViewModel
import com.example.pancreatic.viewmodel.UiState
import com.example.pancreatic.viewmodel.ViewModelFactory
import java.util.Locale

class MetricsActivity : AppCompatActivity() {
    private val viewModel: MetricsViewModel by viewModels {
        ViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_metrics)

        val subtitle = findViewById<TextView>(R.id.subtitle)
        val chips = findViewById<ChipGroup>(R.id.chips)
        val details = findViewById<TextView>(R.id.details)

        subtitle.text = "Loading..."
        details.text = ""

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.summaryState.collect { state ->
                    when (state) {
                        is UiState.Loading -> {
                            subtitle.text = "Loading..."
                            details.text = ""
                        }
                        is UiState.Success -> {
                            val resp = state.data
                            val m = resp.metrics
                            fun fmt(key: String): String {
                                val v = m[key]
                                return if (v == null) "n/a" else String.format(Locale.US, "%.6f", v * 100.0)
                            }

                            subtitle.text = "Best model: ${resp.best_model ?: "unknown"}"

                            chips.removeAllViews()
                            listOf(
                                "ACC ${fmt("accuracy")}",
                                "F1 ${fmt("f1_score")}",
                                "SEN ${fmt("sensitivity")}",
                                "SPEC ${fmt("specificity")}",
                                "PRE ${fmt("precision")}",
                                "AUC ${fmt("roc_auc")}",
                                "FNR ${fmt("fnr")}",
                            ).forEach { text ->
                                val chip = Chip(this@MetricsActivity).apply {
                                    this.text = text
                                    isClickable = false
                                    isCheckable = false
                                }
                                chips.addView(chip)
                            }

                            details.text = buildString {
                                appendLine("Target: ${resp.target_definition ?: "n/a"}")
                                appendLine("Accuracy: ${fmt("accuracy")}")
                                appendLine("Precision: ${fmt("precision")}")
                                appendLine("Sensitivity: ${fmt("sensitivity")}")
                                appendLine("Specificity: ${fmt("specificity")}")
                                appendLine("F1 score: ${fmt("f1_score")}")
                                appendLine("ROC AUC: ${fmt("roc_auc")}")
                                appendLine("False negative rate (FNR): ${fmt("fnr")}")
                            }
                        }
                        is UiState.Error -> {
                            subtitle.text = "Error: ${state.message}"
                            chips.removeAllViews()
                            details.text = ""
                        }
                        UiState.Idle -> Unit
                    }
                }
            }
        }

        viewModel.loadSummary()
    }
}


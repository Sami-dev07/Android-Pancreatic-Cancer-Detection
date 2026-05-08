package com.example.pancreatic

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.launch
import com.example.pancreatic.viewmodel.PlotsViewModel
import com.example.pancreatic.viewmodel.UiState
import com.example.pancreatic.viewmodel.ViewModelFactory
import java.util.Locale

class PlotsActivity : AppCompatActivity() {
    private val viewModel: PlotsViewModel by viewModels {
        ViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plots)

        val statusText = findViewById<TextView>(R.id.statusText)
        val summarySubtitle = findViewById<TextView>(R.id.summarySubtitle)
        val summaryChips = findViewById<ChipGroup>(R.id.summaryChips)
        val blocksRecycler = findViewById<RecyclerView>(R.id.blocksRecycler)
        val plotsRecycler = findViewById<RecyclerView>(R.id.plotsRecycler)

        blocksRecycler.layoutManager = LinearLayoutManager(this)
        val span = resources.getInteger(R.integer.plots_grid_span_count)
        plotsRecycler.layoutManager = GridLayoutManager(this, span)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.dashboardState.collect { state ->
                    when (state) {
                        is UiState.Loading -> statusText.text = "Loading dashboard..."
                        is UiState.Success -> {
                            val dash = state.data
                            summarySubtitle.text = "Best model: ${dash.best_model ?: "unknown"}"
                            summaryChips.removeAllViews()
                            val summaryBlock = dash.blocks.firstOrNull { it.id == "summary" }
                            val chips = summaryBlock?.chips ?: emptyList()
                            chips.forEach { c ->
                                val chip = Chip(this@PlotsActivity).apply {
                                    text = formatMetricChip(c.label)
                                    isClickable = false
                                    isCheckable = false
                                }
                                summaryChips.addView(chip)
                            }

                            blocksRecycler.adapter = PerformanceBlocksAdapter(dash.blocks.filter { it.id != "summary" })
                            plotsRecycler.adapter = PlotsAdapter(com.example.pancreatic.BuildConfig.API_BASE_URL, dash.plots) { item ->
                                val cleanBase = com.example.pancreatic.BuildConfig.API_BASE_URL.let { if (it.endsWith("/")) it.dropLast(1) else it }
                                val url = cleanBase + item.static_url
                                startActivity(
                                    Intent(this@PlotsActivity, PlotDetailActivity::class.java)
                                        .putExtra("filename", item.filename)
                                        .putExtra("url", url)
                                )
                            }
                            statusText.text = "Loaded dashboard."
                        }
                        is UiState.Error -> {
                            statusText.text = "Error loading dashboard: ${state.message}"
                            summarySubtitle.text = ""
                            summaryChips.removeAllViews()
                        }
                        UiState.Idle -> Unit
                    }
                }
            }
        }

        viewModel.loadDashboard()
    }

    private fun formatMetricChip(label: String): String {
        val parts = label.trim().split(" ")
        if (parts.size != 2) return label
        val prefix = parts[0]
        val raw = parts[1].toDoubleOrNull() ?: return label
        return String.format(Locale.US, "%s %.6f", prefix, raw * 100.0)
    }
}


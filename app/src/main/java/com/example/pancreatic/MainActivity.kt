package com.example.pancreatic

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.pancreatic.history.PredictionHistoryActivity
import com.example.pancreatic.ui.MainComposeScreen

/**
 * Home screen: navigation to prediction, plots, metrics, and model selection.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainComposeScreen(
                onStartPrediction = { startActivity(Intent(this, FeaturesActivity::class.java)) },
                onViewDashboard = { startActivity(Intent(this, PlotsActivity::class.java)) },
                onViewMetrics = { startActivity(Intent(this, MetricsActivity::class.java)) },
                onSelectModel = { startActivity(Intent(this, ModelSelectionActivity::class.java)) },
                onOpenHistory = { startActivity(Intent(this, PredictionHistoryActivity::class.java)) },
            )
        }
    }
}

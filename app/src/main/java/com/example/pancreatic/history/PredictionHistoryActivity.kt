package com.example.pancreatic.history

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.draw.clip
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PredictionHistoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repo = PredictionHistoryRepository.get(this)
        setContent {
            HistoryScreen(
                repo = repo,
                onOpen = { id ->
                    startActivity(
                        Intent(this, PredictionDetailActivity::class.java).putExtra(
                            PredictionDetailActivity.EXTRA_ID,
                            id,
                        ),
                    )
                },
                onBack = { finish() },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun HistoryScreen(
    repo: PredictionHistoryRepository,
    onOpen: (Long) -> Unit,
    onBack: () -> Unit,
) {
    val rows by repo.observeAll().collectAsState(initial = emptyList())
    var confirmClear by remember { mutableStateOf(false) }
    val scope = androidx.compose.runtime.rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Prediction history", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { confirmClear = true }, enabled = rows.isNotEmpty()) {
                        Icon(Icons.Default.Delete, contentDescription = "Clear")
                    }
                },
            )
        },
    ) { pad ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .padding(pad)
                .padding(16.dp),
        ) {
            Text(
                text = "Saved locally on this device (Room database).",
                color = Color(0xFF64748B),
                fontSize = 13.sp,
            )
            Spacer(modifier = Modifier.height(12.dp))

            AnimatedContent(
                targetState = rows.isEmpty(),
                transitionSpec = {
                    (fadeIn(tween(220)) togetherWith fadeOut(tween(160))).using(SizeTransform(clip = false))
                },
                label = "emptyToList",
            ) { isEmpty ->
                if (isEmpty) {
                    EmptyState()
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(
                            items = rows,
                            key = { it.id },
                        ) { r ->
                            HistoryRow(
                                r = r,
                                onClick = { onOpen(r.id) },
                                modifier = Modifier.animateItemPlacement(),
                            )
                        }
                    }
                }
            }
        }
    }

    if (confirmClear) {
        AlertDialog(
            onDismissRequest = { confirmClear = false },
            title = { Text("Clear history?") },
            text = { Text("This deletes all saved predictions from this device only.") },
            confirmButton = {
                Button(onClick = {
                    confirmClear = false
                    // Run in composition scope (safe; cancels if screen leaves)
                    // This is UI-only local storage; does not affect backend.
                    scope.launch(Dispatchers.IO) { repo.clearAll() }
                }) { Text("Clear") }
            },
            dismissButton = {
                OutlinedButton(onClick = { confirmClear = false }) { Text("Cancel") }
            },
        )
    }
}

@Composable
private fun EmptyState() {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text("No predictions saved yet.", fontWeight = FontWeight.Bold, color = Color(0xFF334155))
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                "Run a prediction from the form to start building history.",
                color = Color(0xFF64748B),
            )
        }
    }
}

@Composable
private fun HistoryRow(
    r: PredictionRecord,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dt = remember(r.createdAtEpochMs) { formatTs(r.createdAtEpochMs) }
    val riskLabel = if (r.prediction == 1) "High risk" else "Low risk"
    val riskBg = if (r.prediction == 1) Color(0xFFFFE4E6) else Color(0xFFDCFCE7)
    val riskFg = if (r.prediction == 1) Color(0xFF9F1239) else Color(0xFF166534)
    val prob = r.probability?.let { "${(it * 100.0).toInt()}%" } ?: "—"

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(dt, color = Color(0xFF64748B), fontSize = 12.sp)
                Text(
                    text = riskLabel,
                    color = riskFg,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .background(riskBg, RoundedCornerShape(999.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = r.predictedLabel,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A),
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Probability: $prob · Model: ${r.modelName ?: (r.modelUsed ?: "n/a")}",
                color = Color(0xFF334155),
                fontSize = 13.sp,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = r.message,
                color = Color(0xFF64748B),
                fontSize = 12.sp,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Tap for details",
                color = Color(0xFF94A3B8),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

private fun formatTs(ms: Long): String {
    val df = SimpleDateFormat("MMM d, HH:mm", Locale.getDefault())
    return df.format(Date(ms))
}


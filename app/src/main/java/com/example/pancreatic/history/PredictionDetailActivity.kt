package com.example.pancreatic.history

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PredictionDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val id = intent.getLongExtra(EXTRA_ID, -1L)
        val repo = PredictionHistoryRepository.get(this)
        setContent {
            PredictionDetailScreen(
                id = id,
                repo = repo,
                onBack = { finish() },
            )
        }
    }

    companion object {
        const val EXTRA_ID = "prediction_record_id"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PredictionDetailScreen(
    id: Long,
    repo: PredictionHistoryRepository,
    onBack: () -> Unit,
) {
    val rec by repo.observeById(id).collectAsState(initial = null)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Prediction details", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AnimatedVisibility(
                visible = rec != null,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                rec?.let { r ->
                    SummaryCard(r)
                    JsonCard("Inputs (features)", r.featuresJson)
                    JsonCard("Result (raw)", r.resultJson)
                    DisclaimerCard()
                }
            }

            if (rec == null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text("Record not found.", fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "It may have been deleted from local history.",
                            color = Color(0xFF64748B),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(r: PredictionRecord) {
    val dt = remember(r.createdAtEpochMs) { formatTsLong(r.createdAtEpochMs) }
    val riskLabel = if (r.prediction == 1) "High risk" else "Low risk"
    val riskBg = if (r.prediction == 1) Color(0xFFFFE4E6) else Color(0xFFDCFCE7)
    val riskFg = if (r.prediction == 1) Color(0xFF9F1239) else Color(0xFF166534)
    val prob = r.probability?.let { "${(it * 100.0).toString().take(5)}%" } ?: "—"
    val conf = r.confidence?.let { "${(it * 100.0).toString().take(5)}%" } ?: "—"

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(dt, color = Color(0xFF64748B), fontSize = 12.sp)
                Text(
                    riskLabel,
                    color = riskFg,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .background(riskBg, RoundedCornerShape(999.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(r.predictedLabel, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                "Probability: $prob · Confidence: $conf",
                color = Color(0xFF334155),
                fontSize = 13.sp,
            )
            Text(
                "Model: ${r.modelName ?: (r.modelUsed ?: "n/a")} · Request model: ${r.modelKey ?: "active"}",
                color = Color(0xFF64748B),
                fontSize = 12.sp,
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(r.message, color = Color(0xFF64748B), fontSize = 12.sp)
        }
    }
}

@Composable
private fun JsonCard(title: String, json: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                json,
                color = Color(0xFF334155),
                fontSize = 12.sp,
            )
        }
    }
}

@Composable
private fun DisclaimerCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Important medical disclaimer",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF78350F),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "This tool is for research/education only and does not provide medical advice or a diagnosis.",
                color = Color(0xFF78350F),
                fontSize = 13.sp,
            )
        }
    }
}

private fun formatTsLong(ms: Long): String {
    val df = SimpleDateFormat("MMM d, yyyy · HH:mm", Locale.getDefault())
    return df.format(Date(ms))
}


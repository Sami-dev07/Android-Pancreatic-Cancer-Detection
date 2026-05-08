package com.example.pancreatic.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pancreatic.api.ApiClient
import com.example.pancreatic.api.userFacingMessage
import kotlinx.coroutines.delay

@Composable
fun MainComposeScreen(
    onStartPrediction: () -> Unit,
    onViewDashboard: () -> Unit,
    onViewMetrics: () -> Unit,
    onSelectModel: () -> Unit,
    onOpenHistory: () -> Unit,
) {
    // Simple entrance animation
    var show by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(120)
        show = true
    }

    // Active model (same backend call as before, just moved into Compose)
    var activeModelText by remember { mutableStateOf("Active model: loading…") }
    var apiOk by remember { mutableStateOf<Boolean?>(null) }
    LaunchedEffect(Unit) {
        try {
            val active = ApiClient.pancreaticApi.getActiveModel()
            activeModelText = "Active model: ${active.model_full_name}"
            apiOk = true
        } catch (e: Exception) {
            activeModelText = "Active model: unknown"
            apiOk = false
            android.util.Log.w("MainComposeScreen", e.userFacingMessage())
        }
    }

    val pillAlpha by animateFloatAsState(
        targetValue = if (apiOk == null) 0.85f else 1f,
        label = "pillAlpha",
    )

    val bg = Brush.radialGradient(
        colors = listOf(Color(0x3322C55E), Color(0x110EA5E9), Color.Transparent),
        radius = 900f,
    )

    Surface(color = Color(0xFFF8FAFC)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(bg)
                .padding(16.dp),
        ) {
            AnimatedVisibility(
                visible = show,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 6 }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 6 }),
            ) {
                Column {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "Pancreatic cancer risk screening",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A),
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Submit clinical biomarkers to your FastAPI backend and review model performance and metrics.",
                                color = Color(0xFF334155),
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(Color(0xFFF1F5F9))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                                    .alpha(pillAlpha),
                            ) {
                                val dotColor = when (apiOk) {
                                    null -> Color(0xFFF59E0B)
                                    true -> Color(0xFF10B981)
                                    false -> Color(0xFFEF4444)
                                }
                                Spacer(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(RoundedCornerShape(99.dp))
                                        .background(dotColor),
                                )
                                Text(
                                    text = if (apiOk == true) "API online" else if (apiOk == false) "API unreachable" else "API…",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF334155),
                                )
                                Text(
                                    text = "·",
                                    fontSize = 12.sp,
                                    color = Color(0xFF94A3B8),
                                )
                                Text(
                                    text = activeModelText,
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B),
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onStartPrediction,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                    ) {
                        Text("Start prediction", fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = onViewDashboard,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                    ) {
                        Text("View dashboard", fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = onViewMetrics,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                    ) {
                        Text("Model metrics", fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = onSelectModel,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                    ) {
                        Text("Select prediction model", fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedButton(
                        onClick = onOpenHistory,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                    ) {
                        Text("Prediction history", fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Important medical disclaimer",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF78350F),
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "This app is for research/education only and does not provide medical advice or a diagnosis.",
                                color = Color(0xFF78350F),
                            )
                        }
                    }
                }
            }

            if (!show) {
                // subtle placeholder to avoid blank flash
                Spacer(modifier = Modifier.height(8.dp))
                Text("Loading…", color = Color(0xFF64748B))
            }
        }
    }
}


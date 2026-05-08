package com.example.pancreatic

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SplashScreen()
        }

        // Keep splash short and snappy
        val target = Intent(this, MainActivity::class.java)
        lifecycleScope.launch {
            delay(3100)
            startActivity(target)
            finish()
        }
    }
}

@Composable
private fun SplashScreen() {
    val bg = Brush.radialGradient(
        colors = listOf(Color(0x3322C55E), Color(0x110EA5E9), Color.Transparent),
        radius = 900f,
    )

    val inf = rememberInfiniteTransition(label = "splash")
    val rot = inf.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "rot",
    )

    Surface(color = Color(0xFFF8FAFC)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(bg),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            androidx.compose.foundation.Image(
                painter = painterResource(id = R.drawable.ic_app_mark),
                contentDescription = null,
                modifier = Modifier
                    .size(104.dp)
                    .background(Color.White, RoundedCornerShape(28.dp))
                    .rotate(rot.value),
            )
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = "Pancreatic Risk",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Clinical ML screening demo",
                fontSize = 14.sp,
                color = Color(0xFF64748B),
            )
        }
    }
}


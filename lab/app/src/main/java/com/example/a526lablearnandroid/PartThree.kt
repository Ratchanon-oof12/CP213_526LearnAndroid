package com.example.a526lablearnandroid

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.a526lablearnandroid.ui.theme._526LabLearnAndroidTheme

class PartThree : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            _526LabLearnAndroidTheme {
                PartThreeScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartThreeScreen() {
    val context = LocalContext.current as? Activity
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Donut Chart") },
                navigationIcon = {
                    IconButton(onClick = { context?.finish() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val percentages = listOf(30f, 40f, 20f, 10f)
            val colors = listOf(
                Color(0xFFE91E63), // Pink
                Color(0xFF2196F3), // Blue
                Color(0xFFFFC107), // Amber
                Color(0xFF4CAF50)  // Green
            )
            
            Text("Proportions: $percentages")
            Spacer(modifier = Modifier.height(32.dp))
            
            DonutChart(
                percentages = percentages,
                colors = colors,
                modifier = Modifier.size(250.dp)
            )
        }
    }
}

@Composable
fun DonutChart(
    percentages: List<Float>,
    colors: List<Color>,
    modifier: Modifier = Modifier
) {
    var animationPlayed by remember { mutableStateOf(false) }
    
    val totalAngle by animateFloatAsState(
        targetValue = if (animationPlayed) 360f else 0f,
        animationSpec = tween(durationMillis = 1500),
        label = "sweepAngle"
    )

    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }

    val total = percentages.sum()

    Canvas(modifier = modifier) {
        var startAngle = -90f
        
        for (i in percentages.indices) {
            val percentageSweep = (percentages[i] / total) * 360f
            
            // We scale down the sweep logic based on the current animated totalAngle
            // So if totalAngle is 180 (halfway), everything draws up to that point
            
            // To achieve the gradual drawing of the ENTIRE circle piece by piece:
            val currentSweep = if (startAngle + percentageSweep + 90f <= totalAngle) {
                percentageSweep
            } else if (startAngle + 90f < totalAngle) {
                totalAngle - (startAngle + 90f)
            } else {
                0f
            }

            if (currentSweep > 0) {
                drawArc(
                    color = colors.getOrElse(i) { Color.Gray },
                    startAngle = startAngle,
                    sweepAngle = currentSweep,
                    useCenter = false,
                    style = Stroke(width = size.width * 0.15f, cap = StrokeCap.Butt)
                )
            }
            startAngle += percentageSweep
        }
    }
}
package com.example.financialassistant.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AiInsightsCard(
    modifier: Modifier = Modifier,
    title: String = "AI Insights",
    loading: Boolean,
    error: String?,
    summary: String,
    suggestions: List<String>,
    onRefresh: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = Color(0xFF003D9B).copy(alpha = 0.08f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color(0xFF003D9B),
                            modifier = Modifier.padding(10.dp).size(18.dp)
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF191C1E))
                        Text("Personalized suggestions from your data", fontSize = 12.sp, color = Color(0xFF434654))
                    }
                }
                IconButton(onClick = onRefresh, enabled = !loading) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                }
            }

            if (loading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp),
                    color = Color(0xFF0052CC),
                    trackColor = Color(0xFF0052CC).copy(alpha = 0.12f)
                )
            }

            if (!error.isNullOrBlank()) {
                Surface(
                    color = Color(0xFFBA1A1A).copy(alpha = 0.06f),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("AI not available", fontWeight = FontWeight.Bold, color = Color(0xFFBA1A1A))
                        Text(error, fontSize = 12.sp, color = Color(0xFFBA1A1A).copy(alpha = 0.8f))
                    }
                }
            } else {
                if (summary.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color(0xFF003D9B).copy(alpha = 0.06f), Color(0xFF0052CC).copy(alpha = 0.04f))
                                ),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .padding(14.dp)
                    ) {
                        Text(summary, fontSize = 13.sp, color = Color(0xFF191C1E))
                    }
                }

                if (suggestions.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        suggestions.take(4).forEach { s ->
                            Row(verticalAlignment = Alignment.Top) {
                                Text("•", fontSize = 16.sp, color = Color(0xFF003D9B))
                                Spacer(Modifier.width(8.dp))
                                Text(s, fontSize = 13.sp, color = Color(0xFF191C1E))
                            }
                        }
                    }
                } else if (!loading) {
                    Text("No suggestions yet.", fontSize = 13.sp, color = Color(0xFF434654))
                }
            }
        }
    }
}


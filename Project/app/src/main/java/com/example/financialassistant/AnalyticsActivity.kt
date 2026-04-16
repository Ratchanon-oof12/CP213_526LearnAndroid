package com.example.financialassistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financialassistant.ui.theme.FinancialAssistantTheme

class AnalyticsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinancialAssistantTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { AnalyticsTopBar() },
                    bottomBar = { AnalyticsBottomBar() }
                ) { innerPadding ->
                    AnalyticsScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun AnalyticsTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xE6F8FAFC))
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = surfaceContainerLow
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile Picture",
                    tint = Color.Gray,
                    modifier = Modifier.padding(8.dp)
                )
            }
            Text(
                text = "Financial Architect",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = onSurfaceColor
            )
        }
        IconButton(
            onClick = { /* TODO */ },
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = onSurfaceVariantColor
            )
        }
    }
}

@Composable
fun AnalyticsScreen(modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(surfaceColor)
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        AnalyticsHeader()
        Spacer(modifier = Modifier.height(32.dp))
        SummaryBentoGrid()
        Spacer(modifier = Modifier.height(32.dp))
        ChartSection()
        Spacer(modifier = Modifier.height(32.dp))
        BreakdownList()
        Spacer(modifier = Modifier.height(48.dp))
    }
}

@Composable
fun AnalyticsHeader() {
    Column {
        Text(
            text = "FINANCIAL ANALYSIS",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = onSurfaceVariantColor,
            letterSpacing = 2.sp,
            modifier = Modifier.padding(start = 8.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Spending Architecture",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = onSurfaceColor,
            lineHeight = 36.sp,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
fun SummaryBentoGrid() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Income
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = surfaceContainerLowest,
            modifier = Modifier.weight(1f)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFFDAE2FF), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.ArrowDownward, contentDescription = null, tint = primaryColor, modifier = Modifier.size(20.dp))
                    }
                    Surface(
                        color = Color(0xFFDAE2FF),
                        shape = CircleShape
                    ) {
                        Text(
                            "Total Income",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = primaryColor,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Current Month",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = onSurfaceVariantColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$12,450.00",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = onSurfaceColor
                )
            }
        }
        
        // Expenses
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = surfaceContainerLowest,
            modifier = Modifier.weight(1f)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFFFFDAD6), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.ArrowUpward, contentDescription = null, tint = Color(0xFFBA1A1A), modifier = Modifier.size(20.dp))
                    }
                    Surface(
                        color = Color(0xFFFFDAD6).copy(alpha = 0.5f),
                        shape = CircleShape
                    ) {
                        Text(
                            "Total Expenses",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFBA1A1A),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Current Month",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = onSurfaceVariantColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$8,215.42",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = onSurfaceColor
                )
            }
        }
    }
}

@Composable
fun ChartSection() {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = surfaceContainerLow,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "Weekly Spending Trend",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = onSurfaceColor
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Reviewing outflows from the past 7 weeks",
                        fontSize = 12.sp,
                        color = onSurfaceVariantColor
                    )
                }
                
                // Toggle
                Row(
                    modifier = Modifier
                        .background(Color(0xFFE6E8EA), RoundedCornerShape(8.dp))
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(surfaceContainerLowest)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("WEEKLY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("MONTHLY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = onSurfaceVariantColor)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Chart mock
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Bottom
            ) {
                val data = listOf(
                    Triple("W1", 0.4f, "$1.2k"),
                    Triple("W2", 0.65f, "$2.1k"),
                    Triple("W3", 0.55f, "$1.8k"),
                    Triple("W4", 0.85f, "$3.4k"),
                    Triple("W5", 0.95f, "$4.1k"), // highlight
                    Triple("W6", 0.45f, "$1.4k"),
                    Triple("W7", 0.3f, "$0.9k")
                )
                
                data.forEachIndexed { index, item ->
                    val isHighlighted = index == 4
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        if (isHighlighted) {
                            Surface(
                                color = onSurfaceColor,
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier.padding(bottom = 8.dp)
                            ) {
                                Text(
                                    item.third,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .fillMaxHeight(item.second)
                                .background(
                                    brush = if (isHighlighted) Brush.verticalGradient(listOf(primaryContainerColor, primaryColor)) else Brush.verticalGradient(listOf(primaryColor.copy(alpha = 0.2f), primaryColor.copy(alpha = 0.2f))),
                                    shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                                )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = item.first,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isHighlighted) primaryColor else onSurfaceVariantColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BreakdownList() {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Category Breakdown",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = onSurfaceColor
            )
            Text(
                text = "View All",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = primaryColor
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            BreakdownItem(
                icon = Icons.Default.Restaurant,
                title = "Dining & Groceries",
                subtitle = "18 Transactions",
                amount = "$1,240.50",
                statusText = "12% Over",
                statusColor = Color(0xFFBA1A1A)
            )
            BreakdownItem(
                icon = Icons.Default.Home,
                title = "Housing & Rent",
                subtitle = "2 Transactions",
                amount = "$3,200.00",
                statusText = "On Budget",
                statusColor = primaryColor
            )
            BreakdownItem(
                icon = Icons.Default.ShoppingBag,
                title = "Lifestyle & Shopping",
                subtitle = "34 Transactions",
                amount = "$890.15",
                statusText = "8% Under",
                statusColor = Color(0xFF4C5D8D) // secondary
            )
        }
    }
}

@Composable
fun BreakdownItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    amount: String,
    statusText: String,
    statusColor: Color
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = surfaceContainerLowest,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFFE0E3E5), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = onSurfaceVariantColor)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = onSurfaceColor)
                    Text(subtitle, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = onSurfaceVariantColor)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(amount, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = onSurfaceColor)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(modifier = Modifier.size(6.dp).background(statusColor, CircleShape))
                    Text(statusText.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = statusColor, letterSpacing = 1.sp)
                }
            }
        }
    }
}

fun navigateWithFadeFromAnalytics(context: android.content.Context, targetClass: Class<*>) {
    val intent = android.content.Intent(context, targetClass).apply { 
        flags = android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP 
    }
    val options = android.app.ActivityOptions.makeCustomAnimation(context, android.R.anim.fade_in, android.R.anim.fade_out).toBundle()
    androidx.core.content.ContextCompat.startActivity(context, intent, options)
}

@Composable
fun AnalyticsBottomBar() {
    val context = androidx.compose.ui.platform.LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .shadow(24.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .navigationBarsPadding(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomBarItemAnalytics("Home", Icons.Default.AddCircle, false) {
            navigateWithFadeFromAnalytics(context, QuickAddActivity::class.java)
        }
        BottomBarItemAnalytics("History", Icons.Default.Refresh, false) {
            navigateWithFadeFromAnalytics(context, HistoryActivity::class.java)
        }
        BottomBarItemAnalytics("Analytics", Icons.Default.BarChart, true) {}
        BottomBarItemAnalytics("Categories", Icons.Default.List, false) {
            navigateWithFadeFromAnalytics(context, CategoryActivity::class.java)
        }
    }
}

@Composable
fun BottomBarItemAnalytics(label: String, icon: ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) Color(0xFFEFF6FF) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) primaryContainerColor else Color.Gray,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label.uppercase(),
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isSelected) primaryContainerColor else Color.Gray,
            letterSpacing = 1.sp
        )
    }
}

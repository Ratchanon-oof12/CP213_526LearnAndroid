package com.example.financialassistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.financialassistant.ai.AiClient
import com.example.financialassistant.ai.AiInsightsCard
import com.example.financialassistant.ai.AiInsightsRequest
import com.example.financialassistant.ai.AiSnapshotBuilder
import com.example.financialassistant.data.CategorySummary
import com.example.financialassistant.data.DaySummary
import com.example.financialassistant.data.MonthSummary
import com.example.financialassistant.ui.theme.FinancialAssistantTheme
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.launch

// Curated color palette for pie slices
val pieColors = listOf(
    Color(0xFF1E88E5), Color(0xFFE53935), Color(0xFF43A047),
    Color(0xFFF4511E), Color(0xFF8E24AA), Color(0xFFF9A825),
    Color(0xFF00ACC1), Color(0xFF6D4C41), Color(0xFF546E7A)
)

class AnalyticsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinancialAssistantTheme {
                val vm: FinancialViewModel = viewModel()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { AnalyticsTopBar() },
                    bottomBar = { AnalyticsBottomBar() }
                ) { innerPadding ->
                    AnalyticsScreen(modifier = Modifier.padding(innerPadding), vm = vm)
                }
            }
        }
    }
}

@Composable
fun AnalyticsTopBar() {
    val context = LocalContext.current
    val assistantName by rememberAssistantName()
    val assistantIconKey by rememberAssistantIconKey()
    Row(
        modifier = Modifier.fillMaxWidth().background(Color(0xE6F8FAFC))
            .statusBarsPadding().padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Surface(modifier = Modifier.size(40.dp), shape = CircleShape, color = surfaceContainerLow) {
                Icon(
                    imageVector = assistantIconForKey(assistantIconKey),
                    contentDescription = "Assistant Icon",
                    tint = onSurfaceVariantColor,
                    modifier = Modifier.padding(8.dp)
                )
            }
            Text(assistantName, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = onSurfaceColor)
        }
        IconButton(onClick = { context.startActivity(android.content.Intent(context, SettingsActivity::class.java)) }, modifier = Modifier.size(40.dp).clip(CircleShape)) {
            Icon(Icons.Default.Settings, "Settings", tint = onSurfaceVariantColor)
        }
    }
}

@Composable
fun AnalyticsScreen(modifier: Modifier = Modifier, vm: FinancialViewModel) {
    val scrollState = rememberScrollState()
    val categoryData by vm.categoryExpenseSummary.collectAsState()
    val dailyData by vm.dailyExpenseSummary.collectAsState()
    val monthlyData by vm.monthlyExpenseSummary.collectAsState()
    val monthlyIncome by vm.monthlyIncome.collectAsState()
    val monthlyExpense by vm.monthlyExpense.collectAsState()
    val selectedYear by vm.selectedYear.collectAsState()
    val selectedMonth by vm.selectedMonth.collectAsState()
    var barMode by remember { mutableStateOf("DAILY") } // DAILY or MONTHLY

    val assistantName by rememberAssistantName()
    val userName by rememberUserName()
    val aiClient = remember { AiClient() }
    var aiLoading by remember { mutableStateOf(false) }
    var aiError by remember { mutableStateOf<String?>(null) }
    var aiTitle by remember { mutableStateOf("AI Insights") }
    var aiSummary by remember { mutableStateOf("") }
    var aiSuggestions by remember { mutableStateOf(emptyList<String>()) }
    val scope = rememberCoroutineScope()

    // Month picker state
    val currentDate = remember { Calendar.getInstance() }
    var pickerYear by remember { mutableStateOf(currentDate.get(Calendar.YEAR)) }
    var pickerMonth by remember { mutableStateOf(currentDate.get(Calendar.MONTH) + 1) } // 1-based

    fun refreshAiAsync() {
        scope.launch {
            if (!aiClient.isConfigured()) {
                aiError =
                    "AI not configured. Add `GEMINI_API_KEY=...` to `local.properties` (recommended) or set env var `GEMINI_API_KEY`, then Sync/Rebuild. " +
                    "Alternatively set `AI_PROXY_BASE_URL` in `app/build.gradle.kts`."
                return@launch
            }
            aiLoading = true
            aiError = null
            val yearMonth = "${selectedYear}-${selectedMonth}"
            val snapshot = AiSnapshotBuilder.build(
                yearMonth = yearMonth,
                incomeTotal = monthlyIncome,
                expenseTotal = monthlyExpense,
                categoryExpenseSummary = categoryData,
                dailyExpenseSummary = dailyData
            )
            val req = AiInsightsRequest(
                kind = "analytics",
                userName = userName.ifBlank { "there" },
                assistantName = assistantName,
                snapshot = snapshot
            )
            val res = aiClient.getInsights(req)
            res.onSuccess {
                aiTitle = it.title.ifBlank { "AI Insights" }
                aiSummary = it.summary
                aiSuggestions = it.suggestions
            }.onFailure {
                aiError = it.message ?: "Unknown error."
            }
            aiLoading = false
        }
    }

    Column(
        modifier = modifier.fillMaxSize().background(surfaceColor)
            .verticalScroll(scrollState).padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        // Header
        Text("FINANCIAL ANALYSIS", fontSize = 12.sp, fontWeight = FontWeight.Medium,
            color = onSurfaceVariantColor, letterSpacing = 2.sp)
        Spacer(Modifier.height(8.dp))
        Text("Spending Architecture", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = onSurfaceColor, lineHeight = 36.sp)
        Spacer(Modifier.height(24.dp))

        // Month Picker
        MonthPicker(
            year = pickerYear, month = pickerMonth,
            onPrevious = {
                if (pickerMonth == 1) { pickerYear--; pickerMonth = 12 } else pickerMonth--
                vm.selectMonth(pickerYear.toString(), "%02d".format(pickerMonth))
            },
            onNext = {
                val now = Calendar.getInstance()
                if (pickerYear < now.get(Calendar.YEAR) || (pickerYear == now.get(Calendar.YEAR) && pickerMonth < now.get(Calendar.MONTH) + 1)) {
                    if (pickerMonth == 12) { pickerYear++; pickerMonth = 1 } else pickerMonth++
                    vm.selectMonth(pickerYear.toString(), "%02d".format(pickerMonth))
                }
            }
        )
        Spacer(Modifier.height(24.dp))

        // Summary Cards
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            SummaryCard(label = "Income", value = "฿${"%.2f".format(monthlyIncome)}", color = primaryColor, modifier = Modifier.weight(1f))
            SummaryCard(label = "Expense", value = "฿${"%.2f".format(monthlyExpense)}", color = Color(0xFFBA1A1A), modifier = Modifier.weight(1f))
        }
        Spacer(Modifier.height(12.dp))
        val balance = monthlyIncome - monthlyExpense
        Surface(shape = RoundedCornerShape(12.dp), color = surfaceContainerLowest, modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Net Balance", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = onSurfaceColor)
                Text("฿${"%.2f".format(balance)}", fontSize = 20.sp, fontWeight = FontWeight.Bold,
                    color = if (balance >= 0) primaryColor else Color(0xFFBA1A1A))
            }
        }
        Spacer(Modifier.height(32.dp))

        // PIE CHART
        Surface(shape = RoundedCornerShape(20.dp), color = surfaceContainerLowest, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Spending by Category", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = onSurfaceColor)
                Spacer(Modifier.height(4.dp))
                Text("Where your money goes this month", fontSize = 12.sp, color = onSurfaceVariantColor)
                Spacer(Modifier.height(24.dp))
                if (categoryData.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.PieChart, null, tint = onSurfaceVariantColor.copy(alpha = 0.3f), modifier = Modifier.size(64.dp))
                            Spacer(Modifier.height(8.dp))
                            Text("No expense data yet", color = onSurfaceVariantColor, fontSize = 14.sp)
                        }
                    }
                } else {
                    PieChart(data = categoryData)
                    Spacer(Modifier.height(20.dp))
                    PieLegend(data = categoryData)
                }
            }
        }
        Spacer(Modifier.height(24.dp))

        // BAR CHART
        Surface(shape = RoundedCornerShape(20.dp), color = surfaceContainerLowest, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                    Column {
                        Text("Spending Over Time", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = onSurfaceColor)
                        Spacer(Modifier.height(4.dp))
                        Text(if (barMode == "DAILY") "Daily expenses this month" else "Monthly expenses (last 12mo)", fontSize = 12.sp, color = onSurfaceVariantColor)
                    }
                    // Toggle
                    Row(modifier = Modifier.background(Color(0xFFE6E8EA), RoundedCornerShape(8.dp)).padding(4.dp)) {
                        listOf("DAILY", "MONTHLY").forEach { mode ->
                            Box(
                                modifier = Modifier.clip(RoundedCornerShape(6.dp))
                                    .background(if (barMode == mode) surfaceContainerLowest else Color.Transparent)
                                    .clickable { barMode = mode }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(mode, fontSize = 10.sp, fontWeight = FontWeight.Bold,
                                    color = if (barMode == mode) primaryColor else onSurfaceVariantColor)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))
                if (barMode == "DAILY") {
                    if (dailyData.isEmpty()) {
                        EmptyChartPlaceholder()
                    } else {
                        BarChart(labels = dailyData.map { it.day }, values = dailyData.map { it.total.toFloat() })
                    }
                } else {
                    if (monthlyData.isEmpty()) {
                        EmptyChartPlaceholder()
                    } else {
                        BarChart(labels = monthlyData.map { it.month.takeLast(2) }, values = monthlyData.map { it.total.toFloat() })
                    }
                }
            }
        }
        Spacer(Modifier.height(24.dp))

        AiInsightsCard(
            title = aiTitle,
            loading = aiLoading,
            error = aiError,
            summary = aiSummary,
            suggestions = aiSuggestions,
            onRefresh = { refreshAiAsync() }
        )
        Spacer(Modifier.height(48.dp))
    }

}

@Composable
fun MonthPicker(year: Int, month: Int, onPrevious: () -> Unit, onNext: () -> Unit) {
    val monthName = SimpleDateFormat("MMMM", Locale.getDefault()).apply {
        timeZone = TimeZone.getDefault()
    }.format(Calendar.getInstance().also { it.set(Calendar.MONTH, month - 1) }.time)

    Row(
        modifier = Modifier.fillMaxWidth().background(surfaceContainerLowest, RoundedCornerShape(12.dp)).padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPrevious) { Icon(Icons.Default.ChevronLeft, "Previous month", tint = primaryColor) }
        Text("$monthName $year", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = onSurfaceColor)
        IconButton(onClick = onNext) { Icon(Icons.Default.ChevronRight, "Next month", tint = primaryColor) }
    }
}

@Composable
fun PieChart(data: List<CategorySummary>) {
    val total = data.sumOf { it.total }

    // Global sweep progress for sequential pie drawing (0f → 360f)
    val animProgress = remember(data) { androidx.compose.animation.core.Animatable(0f) }
    LaunchedEffect(data) {
        animProgress.snapTo(0f)
        if (data.isNotEmpty()) {
            animProgress.animateTo(
                targetValue = 360f,
                animationSpec = androidx.compose.animation.core.tween(
                    durationMillis = 1500,
                    easing = androidx.compose.animation.core.FastOutSlowInEasing
                )
            )
        }
    }

    // *** Read value OUTSIDE Canvas so Compose tracks it and triggers redraw ***
    val currentProgress = animProgress.value

    Box(modifier = Modifier.fillMaxWidth().height(220.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(200.dp)) {
            val diameter = size.minDimension
            val topLeft = Offset((size.width - diameter) / 2f, (size.height - diameter) / 2f)
            val arcSize = Size(diameter, diameter)
            val strokeWidth = diameter * 0.22f

            var startAngle = -90f
            var drawnSoFar = 0f // total degrees drawn so far in animation

            data.forEachIndexed { index, item ->
                val fullSweep = ((item.total / total) * 360f).toFloat()
                // How much of this slice the animation has reached
                val remainingBudget = (currentProgress - drawnSoFar).coerceIn(0f, fullSweep)

                if (remainingBudget > 0f) {
                    val color = pieColors[index % pieColors.size]
                    drawArc(
                        color = color,
                        startAngle = startAngle,
                        sweepAngle = remainingBudget,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth)
                    )
                }
                startAngle += fullSweep
                drawnSoFar += fullSweep
            }
        }
        // Center text — shown only when animation is sufficiently progressed
        if (currentProgress > 30f) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Total", fontSize = 12.sp, color = onSurfaceVariantColor)
                Text("฿${"%.0f".format(total)}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = onSurfaceColor)
            }
        }
    }
}

@Composable
fun PieLegend(data: List<CategorySummary>) {
    val total = data.sumOf { it.total }
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        data.forEachIndexed { index, item ->
            val color = pieColors[index % pieColors.size]
            val pct = if (total > 0) (item.total / total * 100).toInt() else 0
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(12.dp).background(color, CircleShape))
                Spacer(Modifier.width(10.dp))
                Text(item.categoryName, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = onSurfaceColor, modifier = Modifier.weight(1f))
                Text("$pct%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
                Spacer(Modifier.width(8.dp))
                Text("฿${"%.2f".format(item.total)}", fontSize = 12.sp, color = onSurfaceVariantColor)
            }
        }
    }
}

@Composable
fun BarChart(labels: List<String>, values: List<Float>) {
    val maxValue = values.maxOrNull() ?: 1f
    val step = (labels.size / 6).coerceAtLeast(1)
    
    // Staggered animation states
    val animatedProgresses = values.mapIndexed { index, _ ->
        val progress = remember(values) { androidx.compose.animation.core.Animatable(0f) }
        LaunchedEffect(values) {
            progress.snapTo(0f)
            kotlinx.coroutines.delay(index * 60L) // Staggered appearance
            progress.animateTo(
                targetValue = 1f, 
                animationSpec = androidx.compose.animation.core.tween(
                    durationMillis = 600, 
                    easing = androidx.compose.animation.core.FastOutSlowInEasing
                )
            )
        }
        progress
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth().height(160.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            values.forEachIndexed { index, value ->
                val animProg = animatedProgresses[index].value
                val fraction = if (maxValue > 0) (value / maxValue) * animProg else 0f
                val isMax = value == maxValue
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    if (isMax && animProg > 0.8f) { // Only show label when nearly fully grown
                        Surface(color = onSurfaceColor, shape = RoundedCornerShape(4.dp), modifier = Modifier.padding(bottom = 4.dp)) {
                            Text(formatCompactMoney(value.toDouble()), fontSize = 8.sp, fontWeight = FontWeight.Bold,
                                color = Color.White, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                        }
                    }
                    Box(
                        modifier = Modifier.weight(fraction.coerceAtLeast(0.01f), fill = false)
                            .fillMaxWidth(0.85f)
                            .fillMaxHeight(fraction.coerceAtLeast(0.01f))
                            .background(
                                brush = if (isMax) Brush.verticalGradient(listOf(primaryContainerColor, primaryColor))
                                else Brush.verticalGradient(listOf(primaryColor.copy(alpha = 0.25f), primaryColor.copy(alpha = 0.15f))),
                                shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)
                            )
                    )
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            labels.forEachIndexed { index, label ->
                Text(if (index % step == 0 || index == labels.lastIndex) label else "", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = onSurfaceVariantColor,
                    textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
            }
        }
    }
}

fun formatCompactMoney(value: Double): String {
    val abs = kotlin.math.abs(value)
    val (num, suffix) = when {
        abs >= 1_000_000 -> (value / 1_000_000.0) to "M"
        abs >= 1_000 -> (value / 1_000.0) to "k"
        else -> value to ""
    }
    return "฿${"%.1f".format(num).trimEnd('0').trimEnd('.')}$suffix"
}

@Composable
fun EmptyChartPlaceholder() {
    Box(modifier = Modifier.fillMaxWidth().height(160.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.BarChart, null, tint = onSurfaceVariantColor.copy(alpha = 0.3f), modifier = Modifier.size(48.dp))
            Spacer(Modifier.height(8.dp))
            Text("No data yet for this period", color = onSurfaceVariantColor, fontSize = 14.sp)
        }
    }
}

fun navigateWithFadeFromAnalytics(context: android.content.Context, targetClass: Class<*>) {
    val intent = android.content.Intent(context, targetClass).apply { flags = android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP }
    val options = android.app.ActivityOptions.makeCustomAnimation(context, android.R.anim.fade_in, android.R.anim.fade_out).toBundle()
    androidx.core.content.ContextCompat.startActivity(context, intent, options)
}

@Composable
fun AnalyticsBottomBar() {
    val context = LocalContext.current
    Row(
        modifier = Modifier.fillMaxWidth().background(Color.White)
            .shadow(24.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .padding(horizontal = 8.dp, vertical = 12.dp).navigationBarsPadding(),
        horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.CenterVertically
    ) {
        BottomBarItemAnalytics("Home", Icons.Default.AddCircle, false) { navigateWithFadeFromAnalytics(context, QuickAddActivity::class.java) }
        BottomBarItemAnalytics("History", Icons.Default.Refresh, false) { navigateWithFadeFromAnalytics(context, HistoryActivity::class.java) }
        BottomBarItemAnalytics("Analytics", Icons.Default.BarChart, true) {}
        BottomBarItemAnalytics("Goals", Icons.Default.Flag, false) { navigateWithFadeFromAnalytics(context, GoalActivity::class.java) }
        BottomBarItemAnalytics("Categories", Icons.Default.List, false) { navigateWithFadeFromAnalytics(context, CategoryActivity::class.java) }
    }
}

@Composable
fun BottomBarItemAnalytics(label: String, icon: ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) Color(0xFFEFF6FF) else Color.Transparent)
            .clickable(onClick = onClick).padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Icon(icon, label, tint = if (isSelected) primaryContainerColor else Color.Gray, modifier = Modifier.size(24.dp))
        Spacer(Modifier.height(4.dp))
        Text(label.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.SemiBold,
            color = if (isSelected) primaryContainerColor else Color.Gray, letterSpacing = 0.8.sp)
    }
}

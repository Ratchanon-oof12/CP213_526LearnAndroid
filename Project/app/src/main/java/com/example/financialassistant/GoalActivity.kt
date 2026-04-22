package com.example.financialassistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.financialassistant.ai.AiClient
import com.example.financialassistant.ai.AiInsightsCard
import com.example.financialassistant.ai.AiInsightsRequest
import com.example.financialassistant.ai.AiSnapshotBuilder
import com.example.financialassistant.ui.theme.FinancialAssistantTheme
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class GoalActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinancialAssistantTheme {
                val vm: FinancialViewModel = viewModel()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { GoalTopBar() },
                    bottomBar = { GoalBottomBar() }
                ) { innerPadding ->
                    GoalScreen(modifier = Modifier.padding(innerPadding), vm = vm)
                }
            }
        }
    }
}

data class UserGoal(
    val id: String,
    val title: String,
    val targetAmount: Double,
    val currentAmount: Double,
    val dueMonth: String,
    val completed: Boolean
)

@Composable
fun GoalTopBar() {
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
fun GoalScreen(modifier: Modifier = Modifier, vm: FinancialViewModel) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("financial_prefs", android.content.Context.MODE_PRIVATE) }
    val goals = remember { mutableStateListOf<UserGoal>() }
    var showAdd by remember { mutableStateOf(false) }
    var goalForProgress by remember { mutableStateOf<UserGoal?>(null) }

    val yearMonth by vm.selectedYearMonth.collectAsState()
    val monthlyIncome by vm.monthlyIncome.collectAsState()
    val monthlyExpense by vm.monthlyExpense.collectAsState()
    val categoryData by vm.categoryExpenseSummary.collectAsState()
    val dailyData by vm.dailyExpenseSummary.collectAsState()

    val assistantName by rememberAssistantName()
    val userName by rememberUserName()
    val aiClient = remember { AiClient() }
    val scope = rememberCoroutineScope()
    var aiLoading by remember { mutableStateOf(false) }
    var aiError by remember { mutableStateOf<String?>(null) }
    var aiTitle by remember { mutableStateOf("Goal Coach") }
    var aiSummary by remember { mutableStateOf("") }
    var aiSuggestions by remember { mutableStateOf(emptyList<String>()) }

    fun saveGoals() {
        val arr = JSONArray()
        goals.forEach { g ->
            arr.put(
                JSONObject()
                    .put("id", g.id)
                    .put("title", g.title)
                    .put("targetAmount", g.targetAmount)
                    .put("currentAmount", g.currentAmount)
                    .put("dueMonth", g.dueMonth)
                    .put("completed", g.completed)
            )
        }
        prefs.edit().putString("goals_json", arr.toString()).apply()
    }

    fun loadGoals() {
        goals.clear()
        val raw = prefs.getString("goals_json", "[]") ?: "[]"
        runCatching {
            val arr = JSONArray(raw)
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                goals.add(
                    UserGoal(
                        id = o.optString("id"),
                        title = o.optString("title"),
                        targetAmount = o.optDouble("targetAmount"),
                        currentAmount = o.optDouble("currentAmount"),
                        dueMonth = o.optString("dueMonth"),
                        completed = o.optBoolean("completed", false)
                    )
                )
            }
        }
    }

    fun refreshGoalAi() {
        scope.launch {
            if (!aiClient.isConfigured()) {
                aiError = "AI not configured. Add GEMINI_API_KEY in local.properties and rebuild."
                return@launch
            }
            aiLoading = true
            aiError = null
            val snapshot = AiSnapshotBuilder.build(
                yearMonth = yearMonth,
                incomeTotal = monthlyIncome,
                expenseTotal = monthlyExpense,
                categoryExpenseSummary = categoryData,
                dailyExpenseSummary = dailyData
            )
            val lightweightSnapshot = snapshot.copy(
                topExpenseCategories = snapshot.topExpenseCategories.take(3),
                dailyExpenses = snapshot.dailyExpenses.takeLast(7)
            )
            val goalsContext = if (goals.isEmpty()) {
                "No goals yet."
            } else {
                goals.take(4).joinToString(" | ") { g ->
                    val pct = if (g.targetAmount > 0) ((g.currentAmount / g.targetAmount) * 100).toInt() else 0
                    "${g.title}: target ฿${"%.0f".format(g.targetAmount)}, saved ฿${"%.0f".format(g.currentAmount)}, due ${g.dueMonth}, progress ${pct}%${if (g.completed) " (completed)" else ""}"
                }
            }
            val req = AiInsightsRequest(
                kind = "goals",
                userName = userName.ifBlank { "there" },
                assistantName = assistantName,
                snapshot = lightweightSnapshot,
                extraContext = goalsContext
            )
            aiClient.getInsights(req)
                .onSuccess {
                    aiTitle = it.title.ifBlank { "Goal Coach" }
                    aiSummary = it.summary
                    aiSuggestions = it.suggestions
                }
                .onFailure { aiError = it.message ?: "Unknown error." }
            aiLoading = false
        }
    }

    LaunchedEffect(Unit) { loadGoals() }

    if (showAdd) {
        AddGoalDialog(
            onDismiss = { showAdd = false },
            onCreate = { title, target, current, due ->
                goals.add(
                    UserGoal(
                        id = System.currentTimeMillis().toString(),
                        title = title,
                        targetAmount = target,
                        currentAmount = current,
                        dueMonth = due,
                        completed = false
                    )
                )
                saveGoals()
                showAdd = false
            }
        )
    }

    goalForProgress?.let { selected ->
        AddGoalProgressDialog(
            goal = selected,
            onDismiss = { goalForProgress = null },
            onAddProgress = { amountToAdd ->
                val idx = goals.indexOfFirst { it.id == selected.id }
                if (idx >= 0) {
                    val updatedCurrent = (goals[idx].currentAmount + amountToAdd).coerceAtMost(goals[idx].targetAmount)
                    val completed = updatedCurrent >= goals[idx].targetAmount
                    goals[idx] = goals[idx].copy(currentAmount = updatedCurrent, completed = completed)
                    saveGoals()
                }
                goalForProgress = null
            }
        )
    }

    LazyColumn(
        modifier = modifier.fillMaxSize().background(surfaceColor),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp)
    ) {
        item {
            Text("Goal Planner", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = primaryColor)
            Spacer(Modifier.height(6.dp))
            Text("Set financial goals and let AI judge feasibility.", color = onSurfaceVariantColor, fontSize = 14.sp)
            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { showAdd = true },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
            ) {
                Icon(Icons.Default.Add, null)
                Spacer(Modifier.width(8.dp))
                Text("Add Goal")
            }
            Spacer(Modifier.height(20.dp))

            AiInsightsCard(
                title = aiTitle,
                loading = aiLoading,
                error = aiError,
                summary = aiSummary,
                suggestions = aiSuggestions,
                onRefresh = { refreshGoalAi() }
            )
            Spacer(Modifier.height(20.dp))
        }

        if (goals.isEmpty()) {
            item {
                Text("No goals yet. Add your first target above.", color = onSurfaceVariantColor)
            }
        }

        items(goals, key = { it.id }) { goal ->
            val progress = if (goal.targetAmount > 0) (goal.currentAmount / goal.targetAmount).toFloat().coerceIn(0f, 1f) else 0f
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = surfaceContainerLowest,
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = goal.completed,
                            onCheckedChange = { checked ->
                                val idx = goals.indexOfFirst { it.id == goal.id }
                                if (idx >= 0) {
                                    goals[idx] = goals[idx].copy(completed = checked)
                                    saveGoals()
                                }
                            }
                        )
                        Column(Modifier.weight(1f)) {
                            Text(goal.title, fontWeight = FontWeight.Bold, color = onSurfaceColor)
                            Text("Due ${goal.dueMonth}", fontSize = 12.sp, color = onSurfaceVariantColor)
                        }
                        IconButton(onClick = {
                            goals.removeAll { it.id == goal.id }
                            saveGoals()
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete goal", tint = Color(0xFFBA1A1A))
                        }
                    }
                    Text("฿${"%.0f".format(goal.currentAmount)} / ฿${"%.0f".format(goal.targetAmount)}", fontSize = 13.sp, color = onSurfaceVariantColor)
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth().height(8.dp),
                        color = primaryColor,
                        trackColor = primaryColor.copy(alpha = 0.15f)
                    )
                    Spacer(Modifier.height(10.dp))
                    TextButton(
                        onClick = { goalForProgress = goal },
                        contentPadding = PaddingValues(horizontal = 0.dp, vertical = 0.dp)
                    ) {
                        Icon(Icons.Default.AddCircle, contentDescription = null, tint = primaryColor, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Add Progress", color = primaryColor, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        item { Spacer(Modifier.height(40.dp)) }
    }
}

@Composable
fun AddGoalDialog(
    onDismiss: () -> Unit,
    onCreate: (title: String, target: Double, current: Double, dueMonth: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var target by remember { mutableStateOf("") }
    var current by remember { mutableStateOf("0") }
    var dueMonth by remember { mutableStateOf("2026-12") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Goal", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Goal title") }, singleLine = true)
                OutlinedTextField(
                    value = target,
                    onValueChange = { target = it },
                    label = { Text("Target amount (฿)") },
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                OutlinedTextField(
                    value = current,
                    onValueChange = { current = it },
                    label = { Text("Current saved (฿)") },
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                OutlinedTextField(value = dueMonth, onValueChange = { dueMonth = it }, label = { Text("Due month (YYYY-MM)") }, singleLine = true)
            }
        },
        confirmButton = {
            Button(onClick = {
                val t = target.toDoubleOrNull()
                val c = current.toDoubleOrNull() ?: 0.0
                if (title.isNotBlank() && t != null && t > 0.0) {
                    onCreate(title.trim(), t, c, dueMonth.trim())
                }
            }) {
                Text("Create")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun AddGoalProgressDialog(
    goal: UserGoal,
    onDismiss: () -> Unit,
    onAddProgress: (Double) -> Unit
) {
    var addAmount by remember { mutableStateOf("") }
    val remaining = (goal.targetAmount - goal.currentAmount).coerceAtLeast(0.0)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Update Goal Progress", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(goal.title, fontWeight = FontWeight.SemiBold, color = onSurfaceColor)
                Text(
                    "Remaining: ฿${"%.0f".format(remaining)}",
                    fontSize = 12.sp,
                    color = onSurfaceVariantColor
                )
                OutlinedTextField(
                    value = addAmount,
                    onValueChange = { addAmount = it },
                    label = { Text("Add amount (฿)") },
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = addAmount.toDoubleOrNull()
                    if (amount != null && amount > 0.0) onAddProgress(amount)
                }
            ) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun GoalBottomBar() {
    val context = LocalContext.current
    Row(
        modifier = Modifier.fillMaxWidth().background(Color.White)
            .shadow(24.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .padding(horizontal = 8.dp, vertical = 12.dp).navigationBarsPadding(),
        horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.CenterVertically
    ) {
        GoalBottomBarItem("Home", Icons.Default.AddCircle, false) { navigateWithFadeFromAnalytics(context, QuickAddActivity::class.java) }
        GoalBottomBarItem("History", Icons.Default.Refresh, false) { navigateWithFadeFromAnalytics(context, HistoryActivity::class.java) }
        GoalBottomBarItem("Analytics", Icons.Default.BarChart, false) { navigateWithFadeFromAnalytics(context, AnalyticsActivity::class.java) }
        GoalBottomBarItem("Goals", Icons.Default.Flag, true) {}
        GoalBottomBarItem("Categories", Icons.Default.List, false) { navigateWithFadeFromAnalytics(context, CategoryActivity::class.java) }
    }
}

@Composable
fun GoalBottomBarItem(label: String, icon: ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) Color(0xFFEFF6FF) else Color.Transparent)
            .clickable(onClick = onClick).padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Icon(icon, label, tint = if (isSelected) primaryContainerColor else Color.Gray, modifier = Modifier.size(22.dp))
        Spacer(Modifier.height(4.dp))
        Text(label.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.SemiBold,
            color = if (isSelected) primaryContainerColor else Color.Gray, letterSpacing = 0.8.sp)
    }
}


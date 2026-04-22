package com.example.financialassistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.financialassistant.data.Category
import com.example.financialassistant.data.Transaction
import com.example.financialassistant.data.TransactionType
import com.example.financialassistant.ui.theme.FinancialAssistantTheme
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.launch

class QuickAddActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinancialAssistantTheme {
                val vm: FinancialViewModel = viewModel()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { QuickAddBottomBar() },
                    topBar = { QuickAddTopBar() }
                ) { innerPadding ->
                    QuickAddScreen(modifier = Modifier.padding(innerPadding), vm = vm)
                }
            }
        }
    }
}

@Composable
fun QuickAddTopBar() {
    val context = LocalContext.current
    val assistantName by rememberAssistantName()
    val assistantIconKey by rememberAssistantIconKey()
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
        IconButton(
            onClick = { context.startActivity(android.content.Intent(context, SettingsActivity::class.java)) },
            modifier = Modifier.size(40.dp).clip(CircleShape)
        ) {
            Icon(Icons.Default.Settings, "Settings", tint = onSurfaceVariantColor)
        }
    }
}

@Composable
fun QuickAddScreen(modifier: Modifier = Modifier, vm: FinancialViewModel) {
    val categories by vm.categories.collectAsState()
    val recentTransactions by vm.allTransactions.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    var isExpense by remember { mutableStateOf(true) }
    var amountText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var noteText by remember { mutableStateOf("") }

    LaunchedEffect(categories) {
        if (selectedCategory == null && categories.isNotEmpty()) {
            selectedCategory = categories.first()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(surfaceColor)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Income / Expense Toggle
            IncomeExpenseToggle(isExpense = isExpense, onToggle = { isExpense = it })
            Spacer(modifier = Modifier.height(40.dp))

            // Amount input
            AmountInput(amountText = amountText, isExpense = isExpense, onValueChange = { amountText = it })
            Spacer(modifier = Modifier.height(24.dp))

            // Note input
            OutlinedTextField(
                value = noteText,
                onValueChange = { noteText = it },
                placeholder = { Text("Add a note (optional)", color = onSurfaceVariantColor.copy(alpha = 0.6f)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryColor,
                    unfocusedBorderColor = onSurfaceVariantColor.copy(alpha = 0.3f)
                )
            )
            Spacer(modifier = Modifier.height(40.dp))

            // Category Grid
            CategorySection(
                categories = categories,
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it }
            )
            Spacer(modifier = Modifier.height(40.dp))

            // Confirm Button
            val scope = rememberCoroutineScope()
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull()
                    val cat = selectedCategory
                    if (amount != null && amount > 0 && cat != null) {
                        vm.addTransaction(
                            Transaction(
                                amount = amount,
                                type = if (isExpense) TransactionType.EXPENSE else TransactionType.INCOME,
                                categoryId = cat.id,
                                categoryName = cat.name,
                                categoryColor = cat.colorHex,
                                note = noteText
                            )
                        )
                        amountText = ""
                        noteText = ""
                        scope.launch { snackbarHostState.showSnackbar("Transaction saved!") }
                    } else {
                        scope.launch { snackbarHostState.showSnackbar("Please enter a valid amount and select a category.") }
                    }
                },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.fillMaxWidth().height(64.dp).shadow(8.dp, RoundedCornerShape(16.dp))
            ) {
                Box(
                    modifier = Modifier.fillMaxSize().background(
                        brush = Brush.linearGradient(colors = listOf(primaryColor, primaryContainerColor)),
                        shape = RoundedCornerShape(16.dp)
                    ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Confirm Transaction", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Recent Activity
            RecentActivitySection(transactions = recentTransactions.take(5))
            Spacer(modifier = Modifier.height(32.dp))
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 80.dp)
        )
    }
}

@Composable
fun IncomeExpenseToggle(isExpense: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.width(240.dp).background(surfaceContainerLow, RoundedCornerShape(12.dp)).padding(4.dp)
    ) {
        Box(
            modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp))
                .background(if (!isExpense) primaryColor else Color.Transparent)
                .clickable { onToggle(false) }.padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Income", fontSize = 14.sp, fontWeight = FontWeight.Bold,
                color = if (!isExpense) Color.White else onSurfaceVariantColor)
        }
        Box(
            modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp))
                .background(if (isExpense) primaryColor else Color.Transparent)
                .clickable { onToggle(true) }.padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Expense", fontSize = 14.sp, fontWeight = FontWeight.Bold,
                color = if (isExpense) Color.White else onSurfaceVariantColor)
        }
    }
}

@Composable
fun AmountInput(amountText: String, isExpense: Boolean, onValueChange: (String) -> Unit) {
    val amountFontSize = when {
        amountText.length >= 13 -> 30.sp
        amountText.length >= 10 -> 38.sp
        amountText.length >= 8 -> 48.sp
        else -> 64.sp
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("TRANSACTION AMOUNT", fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
            color = onSurfaceVariantColor, letterSpacing = 2.sp)
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = amountText,
            onValueChange = { v -> if (v.matches(Regex("^\\d*\\.?\\d{0,2}$"))) onValueChange(v) },
            prefix = {
                Text(
                    text = if (isExpense) "-฿" else "+฿",
                    fontSize = amountFontSize,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isExpense) Color(0xFFBA1A1A).copy(alpha = 0.45f) else primaryColor.copy(alpha = 0.45f)
                )
            },
            placeholder = { Text("0.00", fontSize = amountFontSize, fontWeight = FontWeight.ExtraBold, color = Color(0xFFE0E3E5)) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent
            ),
            textStyle = LocalTextStyle.current.copy(
                fontSize = amountFontSize, fontWeight = FontWeight.ExtraBold,
                color = if (isExpense) Color(0xFFBA1A1A) else primaryColor,
                textAlign = TextAlign.Start
            ),
            singleLine = true,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun CategorySection(
    categories: List<Category>,
    selectedCategory: Category?,
    onCategorySelected: (Category) -> Unit
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.width(4.dp).height(24.dp).background(primaryColor, CircleShape))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Select Category", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = onSurfaceColor)
        }
        Spacer(modifier = Modifier.height(24.dp))
        if (categories.isEmpty()) {
            Text("No categories yet. Add some in the Categories tab!", color = onSurfaceVariantColor, fontSize = 14.sp)
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier.height(((categories.size / 4 + 1) * 100).dp.coerceAtMost(300.dp)),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                userScrollEnabled = false
            ) {
                items(categories) { cat ->
                    val isSelected = selectedCategory?.id == cat.id
                    val catColor = parseHexColor(cat.colorHex)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clip(RoundedCornerShape(16.dp))
                            .background(surfaceContainerLowest)
                            .border(1.dp, if (isSelected) catColor else Color.Transparent, RoundedCornerShape(16.dp))
                            .clickable { onCategorySelected(cat) }
                            .padding(8.dp)
                    ) {
                        Box(
                            modifier = Modifier.size(48.dp).background(
                                if (isSelected) catColor else catColor.copy(alpha = 0.1f),
                                RoundedCornerShape(12.dp)
                            ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = iconForName(cat.iconName),
                                contentDescription = cat.name,
                                tint = if (isSelected) Color.White else catColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(cat.name.uppercase(), fontSize = 9.sp, fontWeight = FontWeight.SemiBold,
                            color = onSurfaceVariantColor, letterSpacing = 0.5.sp,
                            textAlign = TextAlign.Center, maxLines = 1)
                    }
                }
            }
        }
    }
}

@Composable
fun RecentActivitySection(transactions: List<Transaction>) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("RECENT ACTIVITY", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = onSurfaceVariantColor, letterSpacing = 1.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))
        if (transactions.isEmpty()) {
            Text("No transactions yet. Add your first one above!", color = onSurfaceVariantColor, fontSize = 14.sp, modifier = Modifier.padding(vertical = 8.dp))
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                transactions.forEach { tx ->
                    val color = parseHexColor(tx.categoryColor)
                    val isExpense = tx.type == TransactionType.EXPENSE
                    Row(
                        modifier = Modifier.fillMaxWidth().background(surfaceContainerLowest, RoundedCornerShape(12.dp)).padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(40.dp).background(color.copy(alpha = 0.15f), CircleShape), contentAlignment = Alignment.Center) {
                                Icon(iconForName(tx.categoryName), contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(tx.categoryName, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = onSurfaceColor)
                                Text(
                                    SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date(tx.date)),
                                    fontSize = 11.sp, color = onSurfaceVariantColor
                                )
                            }
                        }
                        Text(
                            text = "${if (isExpense) "-" else "+"}฿${"%.2f".format(tx.amount)}",
                            fontSize = 14.sp, fontWeight = FontWeight.Bold,
                            color = if (isExpense) Color(0xFFBA1A1A) else primaryColor
                        )
                    }
                }
            }
        }
    }
}

fun parseHexColor(hex: String): Color {
    return try { Color(android.graphics.Color.parseColor(hex)) } catch (e: Exception) { Color(0xFF757575) }
}

fun iconForName(name: String): ImageVector = when (name.lowercase()) {
    "food", "restaurant", "dining" -> Icons.Default.Restaurant
    "transport", "directionscar", "car" -> Icons.Default.DirectionsCar
    "shopping", "shoppingbag" -> Icons.Default.ShoppingBag
    "health", "favorite" -> Icons.Default.Favorite
    "entertainment", "movie" -> Icons.Default.Movie
    "education", "school" -> Icons.Default.School
    "utilities", "bolt" -> Icons.Default.Bolt
    "salary", "accountbalance", "income" -> Icons.Default.AccountBalance
    else -> Icons.Default.MoreHoriz
}

fun navigateWithFadeFromQuickAdd(context: android.content.Context, targetClass: Class<*>) {
    val intent = android.content.Intent(context, targetClass).apply { flags = android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP }
    val options = android.app.ActivityOptions.makeCustomAnimation(context, android.R.anim.fade_in, android.R.anim.fade_out).toBundle()
    androidx.core.content.ContextCompat.startActivity(context, intent, options)
}

@Composable
fun QuickAddBottomBar() {
    val context = LocalContext.current
    Row(
        modifier = Modifier.fillMaxWidth().background(Color.White)
            .shadow(24.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .padding(horizontal = 8.dp, vertical = 12.dp).navigationBarsPadding(),
        horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.CenterVertically
    ) {
        BottomBarItem("Home", Icons.Default.AddCircle, true) {}
        BottomBarItem("History", Icons.Default.Refresh, false) { navigateWithFadeFromQuickAdd(context, HistoryActivity::class.java) }
        BottomBarItem("Analytics", Icons.Default.BarChart, false) { navigateWithFadeFromQuickAdd(context, AnalyticsActivity::class.java) }
        BottomBarItem("Goals", Icons.Default.Flag, false) { navigateWithFadeFromQuickAdd(context, GoalActivity::class.java) }
        BottomBarItem("Categories", Icons.Default.List, false) { navigateWithFadeFromQuickAdd(context, CategoryActivity::class.java) }
    }
}

@Composable
fun BottomBarItem(label: String, icon: ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) Color(0xFFEFF6FF) else Color.Transparent)
            .clickable(onClick = onClick).padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Icon(imageVector = icon, contentDescription = label, tint = if (isSelected) primaryContainerColor else Color.Gray, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(label.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.SemiBold,
            color = if (isSelected) primaryContainerColor else Color.Gray, letterSpacing = 0.8.sp)
    }
}

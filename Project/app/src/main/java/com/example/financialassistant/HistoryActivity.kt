package com.example.financialassistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.financialassistant.data.Transaction
import com.example.financialassistant.data.TransactionType
import com.example.financialassistant.ui.theme.FinancialAssistantTheme
import java.text.SimpleDateFormat
import java.util.*

class HistoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinancialAssistantTheme {
                val vm: FinancialViewModel = viewModel()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { HistoryTopBar() },
                    bottomBar = { HistoryBottomBar() }
                ) { innerPadding ->
                    HistoryScreen(modifier = Modifier.padding(innerPadding), vm = vm)
                }
            }
        }
    }
}

@Composable
fun HistoryTopBar() {
    val context = LocalContext.current
    val assistantName by rememberAssistantName()
    Row(
        modifier = Modifier.fillMaxWidth().background(Color(0xE6F8FAFC))
            .statusBarsPadding().padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Surface(modifier = Modifier.size(40.dp), shape = CircleShape, color = surfaceContainerLow) {
                Icon(Icons.Default.Person, null, tint = Color.Gray, modifier = Modifier.padding(8.dp))
            }
            Text(assistantName, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = onSurfaceColor)
        }
        IconButton(onClick = { context.startActivity(android.content.Intent(context, SettingsActivity::class.java)) }, modifier = Modifier.size(40.dp).clip(CircleShape)) {
            Icon(Icons.Default.Settings, "Settings", tint = onSurfaceVariantColor)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(modifier: Modifier = Modifier, vm: FinancialViewModel) {
    val allTransactions by vm.allTransactions.collectAsState()
    val monthlyIncome by vm.monthlyIncome.collectAsState()
    val monthlyExpense by vm.monthlyExpense.collectAsState()
    var searchText by remember { mutableStateOf("") }

    // Sheet state
    var selectedTx by remember { mutableStateOf<Transaction?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Group transactions by date label
    val filtered = allTransactions.filter {
        searchText.isBlank() || it.categoryName.contains(searchText, ignoreCase = true) || it.note.contains(searchText, ignoreCase = true)
    }
    val grouped = filtered.groupBy { tx ->
        val cal = Calendar.getInstance().also { it.timeInMillis = tx.date }
        val today = Calendar.getInstance()
        val yesterday = Calendar.getInstance().also { it.add(Calendar.DAY_OF_YEAR, -1) }
        when {
            isSameDay(cal, today) -> "TODAY"
            isSameDay(cal, yesterday) -> "YESTERDAY"
            else -> SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(tx.date))
        }
    }

    // Edit/Delete Bottom Sheet
    selectedTx?.let { tx ->
        ModalBottomSheet(
            onDismissRequest = { selectedTx = null },
            sheetState = sheetState,
            containerColor = surfaceColor,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            TransactionEditSheet(
                transaction = tx,
                onSave = { updated ->
                    vm.updateTransaction(updated)
                    selectedTx = null
                },
                onDelete = {
                    vm.deleteTransaction(tx)
                    selectedTx = null
                },
                onDismiss = { selectedTx = null }
            )
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize().background(surfaceColor),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp)
    ) {
        item {
            Column {
                Text("History", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = onSurfaceColor, letterSpacing = (-0.5).sp)
                Spacer(Modifier.height(4.dp))
                Text("Review your financial timeline with precision.", fontSize = 14.sp, color = onSurfaceVariantColor)
                Spacer(Modifier.height(16.dp))

                // Summary bento
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SummaryCard(label = "Income", value = "฿${"%.2f".format(monthlyIncome)}", color = primaryColor, modifier = Modifier.weight(1f))
                    SummaryCard(label = "Expense", value = "฿${"%.2f".format(monthlyExpense)}", color = Color(0xFFBA1A1A), modifier = Modifier.weight(1f))
                    SummaryCard(label = "Balance", value = "฿${"%.2f".format(monthlyIncome - monthlyExpense)}", color = if (monthlyIncome >= monthlyExpense) primaryColor else Color(0xFFBA1A1A), modifier = Modifier.weight(1f))
                }
                Spacer(Modifier.height(24.dp))

                // Search
                TextField(
                    value = searchText, onValueChange = { searchText = it },
                    placeholder = { Text("Search by category or note...", fontSize = 14.sp, color = onSurfaceVariantColor.copy(alpha = 0.6f)) },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = onSurfaceVariantColor) },
                    trailingIcon = { if (searchText.isNotBlank()) IconButton(onClick = { searchText = "" }) { Icon(Icons.Default.Close, null, tint = onSurfaceVariantColor) } },
                    colors = TextFieldDefaults.colors(focusedContainerColor = surfaceContainerLowest, unfocusedContainerColor = Color(0xFFE6E8EA), focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.height(24.dp))
            }
        }

        if (filtered.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 64.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.ReceiptLong, null, tint = onSurfaceVariantColor.copy(alpha = 0.4f), modifier = Modifier.size(64.dp))
                        Spacer(Modifier.height(16.dp))
                        Text("No transactions found", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = onSurfaceVariantColor)
                        Text("Add your first transaction in Quick Add", fontSize = 14.sp, color = onSurfaceVariantColor.copy(alpha = 0.7f))
                    }
                }
            }
        }

        grouped.forEach { (dateLabel, txList) ->
            stickyHeader {
                Row(
                    modifier = Modifier.fillMaxWidth().background(surfaceColor).padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(dateLabel, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = onSurfaceVariantColor.copy(alpha = 0.7f), letterSpacing = 1.sp)
                    Surface(color = primaryContainerColor.copy(alpha = 0.1f), shape = CircleShape) {
                        Text("${txList.size} TRANSACTION${if (txList.size > 1) "S" else ""}",
                            fontSize = 10.sp, fontWeight = FontWeight.Bold, color = primaryColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                    }
                }
            }
            items(txList, key = { it.id }) { tx ->
                val isExpense = tx.type == TransactionType.EXPENSE
                val catColor = parseHexColor(tx.categoryColor)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { selectedTx = tx }
                        .background(surfaceContainerLowest, RoundedCornerShape(16.dp))
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(56.dp).background(catColor.copy(alpha = 0.12f), RoundedCornerShape(16.dp)), contentAlignment = Alignment.Center) {
                        Icon(iconForName(tx.categoryName), null, tint = catColor, modifier = Modifier.size(24.dp))
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(tx.categoryName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = onSurfaceColor)
                            Text(
                                "${if (isExpense) "- " else "+ "}฿${"%.2f".format(tx.amount)}",
                                fontSize = 16.sp, fontWeight = FontWeight.Bold,
                                color = if (isExpense) Color(0xFFBA1A1A) else primaryColor
                            )
                        }
                        Spacer(Modifier.height(2.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                if (tx.note.isBlank()) SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(tx.date))
                                else "${tx.note} • ${SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(tx.date))}",
                                fontSize = 12.sp, color = onSurfaceVariantColor
                            )
                            Text(if (isExpense) "EXPENSE" else "INCOME", fontSize = 10.sp, fontWeight = FontWeight.Bold,
                                color = if (isExpense) Color(0xFFBA1A1A).copy(alpha = 0.5f) else primaryColor.copy(alpha = 0.5f))
                        }
                    }
                }
            }
        }

        item { Spacer(Modifier.height(48.dp)) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionEditSheet(
    transaction: Transaction,
    onSave: (Transaction) -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    var amountText by remember { mutableStateOf("%.2f".format(transaction.amount)) }
    var noteText by remember { mutableStateOf(transaction.note) }
    var isExpense by remember { mutableStateOf(transaction.type == TransactionType.EXPENSE) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Transaction", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete this ฿${"%.2f".format(transaction.amount)} transaction? This cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = onDelete,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBA1A1A))
                ) { Text("Delete") }
            },
            dismissButton = { TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") } }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Handle bar
        Box(modifier = Modifier.size(40.dp, 4.dp).background(onSurfaceVariantColor.copy(alpha = 0.2f), CircleShape))
        Spacer(Modifier.height(20.dp))

        // Header
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Edit Transaction", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = onSurfaceColor)
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, "Close", tint = onSurfaceVariantColor)
            }
        }
        Spacer(Modifier.height(24.dp))

        // Category badge (read-only)
        val catColor = parseHexColor(transaction.categoryColor)
        Row(
            modifier = Modifier.fillMaxWidth()
                .background(catColor.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(44.dp).background(catColor.copy(alpha = 0.15f), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                Icon(iconForName(transaction.categoryName), null, tint = catColor, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(transaction.categoryName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = onSurfaceColor)
                Text(SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault()).format(Date(transaction.date)),
                    fontSize = 12.sp, color = onSurfaceVariantColor)
            }
        }
        Spacer(Modifier.height(20.dp))

        // Income / Expense toggle
        Row(
            modifier = Modifier.fillMaxWidth().background(surfaceContainerLow, RoundedCornerShape(12.dp)).padding(4.dp)
        ) {
            Box(
                modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp))
                    .background(if (!isExpense) primaryColor else Color.Transparent)
                    .clickable { isExpense = false }.padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Income", fontSize = 14.sp, fontWeight = FontWeight.Bold,
                    color = if (!isExpense) Color.White else onSurfaceVariantColor)
            }
            Box(
                modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp))
                    .background(if (isExpense) primaryColor else Color.Transparent)
                    .clickable { isExpense = true }.padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Expense", fontSize = 14.sp, fontWeight = FontWeight.Bold,
                    color = if (isExpense) Color.White else onSurfaceVariantColor)
            }
        }
        Spacer(Modifier.height(16.dp))

        // Amount field
        OutlinedTextField(
            value = amountText,
            onValueChange = { if (it.matches(Regex("^\\d*\\.?\\d{0,2}$"))) amountText = it },
            label = { Text("Amount (฿)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            textStyle = LocalTextStyle.current.copy(
                fontSize = 28.sp, fontWeight = FontWeight.Bold,
                color = if (isExpense) Color(0xFFBA1A1A) else primaryColor,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (isExpense) Color(0xFFBA1A1A) else primaryColor,
                unfocusedBorderColor = onSurfaceVariantColor.copy(alpha = 0.3f)
            )
        )
        Spacer(Modifier.height(12.dp))

        // Note field
        OutlinedTextField(
            value = noteText,
            onValueChange = { noteText = it },
            label = { Text("Note") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primaryColor,
                unfocusedBorderColor = onSurfaceVariantColor.copy(alpha = 0.3f)
            )
        )
        Spacer(Modifier.height(24.dp))

        // Action buttons
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // Delete button
            OutlinedButton(
                onClick = { showDeleteConfirm = true },
                modifier = Modifier.weight(1f).height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFBA1A1A)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBA1A1A).copy(alpha = 0.5f))
            ) {
                Icon(Icons.Default.Delete, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Delete", fontWeight = FontWeight.Bold)
            }

            // Save button
            Button(
                onClick = {
                    val newAmount = amountText.toDoubleOrNull()
                    if (newAmount != null && newAmount > 0) {
                        onSave(
                            transaction.copy(
                                amount = newAmount,
                                type = if (isExpense) TransactionType.EXPENSE else TransactionType.INCOME,
                                note = noteText
                            )
                        )
                    }
                },
                modifier = Modifier.weight(2f).height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
            ) {
                Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Save Changes", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
fun SummaryCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Surface(shape = RoundedCornerShape(12.dp), color = surfaceContainerLowest, modifier = modifier) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(label, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = onSurfaceVariantColor)
            Spacer(Modifier.height(4.dp))
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = color, maxLines = 1)
        }
    }
}

fun isSameDay(c1: Calendar, c2: Calendar): Boolean =
    c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)

fun navigateWithFadeFromHistory(context: android.content.Context, targetClass: Class<*>) {
    val intent = android.content.Intent(context, targetClass).apply { flags = android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP }
    val options = android.app.ActivityOptions.makeCustomAnimation(context, android.R.anim.fade_in, android.R.anim.fade_out).toBundle()
    androidx.core.content.ContextCompat.startActivity(context, intent, options)
}

@Composable
fun HistoryBottomBar() {
    val context = LocalContext.current
    Row(
        modifier = Modifier.fillMaxWidth().background(Color.White)
            .shadow(24.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp).navigationBarsPadding(),
        horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.CenterVertically
    ) {
        BottomBarItemHistory("Home", Icons.Default.AddCircle, false) { navigateWithFadeFromHistory(context, QuickAddActivity::class.java) }
        BottomBarItemHistory("History", Icons.Default.Refresh, true) {}
        BottomBarItemHistory("Analytics", Icons.Default.BarChart, false) { navigateWithFadeFromHistory(context, AnalyticsActivity::class.java) }
        BottomBarItemHistory("Categories", Icons.Default.List, false) { navigateWithFadeFromHistory(context, CategoryActivity::class.java) }
    }
}

@Composable
fun BottomBarItemHistory(label: String, icon: ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) Color(0xFFEFF6FF) else Color.Transparent)
            .clickable(onClick = onClick).padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Icon(icon, label, tint = if (isSelected) primaryContainerColor else Color.Gray, modifier = Modifier.size(24.dp))
        Spacer(Modifier.height(4.dp))
        Text(label.uppercase(), fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
            color = if (isSelected) primaryContainerColor else Color.Gray, letterSpacing = 1.sp)
    }
}

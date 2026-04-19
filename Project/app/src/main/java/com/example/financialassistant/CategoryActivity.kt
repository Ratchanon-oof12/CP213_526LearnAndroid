package com.example.financialassistant

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.financialassistant.data.Category
import com.example.financialassistant.ui.theme.FinancialAssistantTheme

// Available icons for category creation
val availableIcons = listOf(
    "Restaurant" to Icons.Default.Restaurant,
    "DirectionsCar" to Icons.Default.DirectionsCar,
    "ShoppingBag" to Icons.Default.ShoppingBag,
    "Favorite" to Icons.Default.Favorite,
    "Movie" to Icons.Default.Movie,
    "School" to Icons.Default.School,
    "Bolt" to Icons.Default.Bolt,
    "AccountBalance" to Icons.Default.AccountBalance,
    "Home" to Icons.Default.Home,
    "LocalCafe" to Icons.Default.LocalCafe,
    "FitnessCenter" to Icons.Default.FitnessCenter,
    "Flight" to Icons.Default.Flight,
    "MoreHoriz" to Icons.Default.MoreHoriz
)

val availableColors = listOf(
    "#E53935", "#1E88E5", "#43A047", "#F4511E", "#8E24AA",
    "#F9A825", "#00ACC1", "#6D4C41", "#546E7A", "#0047b3", "#A33500"
)

class CategoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinancialAssistantTheme {
                val vm: FinancialViewModel = viewModel()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { CategoryTopBar() },
                    bottomBar = { CategoryBottomBar() }
                ) { innerPadding ->
                    CategoryScreen(modifier = Modifier.padding(innerPadding), vm = vm)
                }
            }
        }
    }
}

@Composable
fun CategoryTopBar() {
    Row(
        modifier = Modifier.fillMaxWidth().background(Color(0xE6F8FAFC))
            .statusBarsPadding().padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Surface(modifier = Modifier.size(40.dp), shape = CircleShape, color = surfaceContainerLow) {
                Icon(Icons.Default.Person, null, tint = Color.Gray, modifier = Modifier.padding(8.dp))
            }
            Text("Financial Architect", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = onSurfaceColor)
        }
        IconButton(onClick = {}, modifier = Modifier.size(40.dp).clip(CircleShape)) {
            Icon(Icons.Default.Settings, "Settings", tint = onSurfaceVariantColor)
        }
    }
}

@Composable
fun CategoryScreen(modifier: Modifier = Modifier, vm: FinancialViewModel) {
    val categories by vm.categories.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var categoryToDelete by remember { mutableStateOf<Category?>(null) }

    // Add Dialog
    if (showAddDialog) {
        AddCategoryDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, iconName, colorHex ->
                vm.addCategory(Category(name = name, iconName = iconName, colorHex = colorHex))
                showAddDialog = false
            }
        )
    }

    // Delete confirmation
    categoryToDelete?.let { cat ->
        AlertDialog(
            onDismissRequest = { categoryToDelete = null },
            title = { Text("Delete Category") },
            text = { Text("Delete \"${cat.name}\"? This will not delete existing transactions.") },
            confirmButton = {
                TextButton(onClick = { vm.deleteCategory(cat); categoryToDelete = null }) {
                    Text("Delete", color = Color(0xFFBA1A1A))
                }
            },
            dismissButton = { TextButton(onClick = { categoryToDelete = null }) { Text("Cancel") } }
        )
    }

    LazyColumn(
        modifier = modifier.fillMaxSize().background(surfaceColor),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp)
    ) {
        item {
            // Hero
            Text("Category Library", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = primaryColor, letterSpacing = (-0.5).sp)
            Spacer(Modifier.height(4.dp))
            Text("Organize your financial ecosystem with precision.", fontSize = 14.sp, color = onSurfaceVariantColor)
            Spacer(Modifier.height(24.dp))

            // Header Row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("OVERVIEW", fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp, color = primaryColor)
                    Spacer(Modifier.height(4.dp))
                    Text("${categories.size} Active Categories", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = onSurfaceColor)
                }
                Button(
                    onClick = { showAddDialog = true },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                            .background(Brush.linearGradient(listOf(primaryColor, primaryContainerColor)), RoundedCornerShape(10.dp))
                            .padding(horizontal = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Add Category", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }

        if (categories.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 64.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Category, null, tint = onSurfaceVariantColor.copy(alpha = 0.3f), modifier = Modifier.size(64.dp))
                        Spacer(Modifier.height(16.dp))
                        Text("No categories yet", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = onSurfaceVariantColor)
                        Text("Tap 'Add Category' to get started", fontSize = 14.sp, color = onSurfaceVariantColor.copy(alpha = 0.7f))
                    }
                }
            }
        }

        items(categories, key = { it.id }) { cat ->
            val color = parseHexColor(cat.colorHex)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = surfaceContainerLowest,
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(52.dp).background(color.copy(alpha = 0.12f), RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(iconForName(cat.iconName), null, tint = color, modifier = Modifier.size(28.dp))
                        }
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text(cat.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = onSurfaceColor)
                            if (cat.isDefault) {
                                Text("DEFAULT", fontSize = 10.sp, fontWeight = FontWeight.Bold,
                                    color = primaryColor.copy(alpha = 0.6f), letterSpacing = 1.sp)
                            }
                        }
                    }
                    if (!cat.isDefault) {
                        IconButton(onClick = { categoryToDelete = cat }) {
                            Icon(Icons.Default.Delete, "Delete", tint = Color(0xFFBA1A1A).copy(alpha = 0.7f))
                        }
                    }
                }
            }
        }

        item { Spacer(Modifier.height(48.dp)) }
    }
}

@Composable
fun AddCategoryDialog(onDismiss: () -> Unit, onConfirm: (String, String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf(availableIcons.first()) }
    var selectedColor by remember { mutableStateOf(availableColors.first()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Category", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = name, onValueChange = { name = it },
                    label = { Text("Category Name") },
                    singleLine = true, modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor)
                )

                Text("Choose Icon", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = onSurfaceVariantColor)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    modifier = Modifier.height(100.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    gridItems(availableIcons) { pair ->
                        val iconName = pair.first
                        val icon = pair.second
                        val isSelected = selectedIcon.first == iconName
                        Box(
                            modifier = Modifier.size(40.dp)
                                .background(if (isSelected) primaryColor else surfaceContainerLow, RoundedCornerShape(10.dp))
                                .clickable { selectedIcon = pair },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(icon, null, tint = if (isSelected) Color.White else onSurfaceVariantColor, modifier = Modifier.size(22.dp))
                        }
                    }
                }

                Text("Choose Color", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = onSurfaceVariantColor)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(6),
                    modifier = Modifier.height(72.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    gridItems(availableColors) { hex ->
                        val color = parseHexColor(hex)
                        Box(
                            modifier = Modifier.size(36.dp)
                                .background(color, CircleShape)
                                .clickable { selectedColor = hex },
                            contentAlignment = Alignment.Center
                        ) {
                            if (selectedColor == hex) {
                                Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank()) onConfirm(name.trim(), selectedIcon.first, selectedColor) },
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
            ) { Text("Create") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

fun navigateWithFade(context: android.content.Context, targetClass: Class<*>) {
    val intent = Intent(context, targetClass).apply { flags = Intent.FLAG_ACTIVITY_CLEAR_TOP }
    val options = ActivityOptions.makeCustomAnimation(context, android.R.anim.fade_in, android.R.anim.fade_out).toBundle()
    ContextCompat.startActivity(context, intent, options)
}

@Composable
fun CategoryBottomBar() {
    val context = LocalContext.current
    Row(
        modifier = Modifier.fillMaxWidth().background(Color.White)
            .shadow(24.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp).navigationBarsPadding(),
        horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.CenterVertically
    ) {
        BottomBarItemCategory("Home", Icons.Default.AddCircle, false) { navigateWithFade(context, QuickAddActivity::class.java) }
        BottomBarItemCategory("History", Icons.Default.Refresh, false) { navigateWithFade(context, HistoryActivity::class.java) }
        BottomBarItemCategory("Analytics", Icons.Default.BarChart, false) { navigateWithFade(context, AnalyticsActivity::class.java) }
        BottomBarItemCategory("Categories", Icons.Default.Category, true) {}
    }
}

@Composable
fun BottomBarItemCategory(label: String, icon: ImageVector, isSelected: Boolean, onClick: () -> Unit) {
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

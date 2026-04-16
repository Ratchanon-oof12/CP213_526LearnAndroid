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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financialassistant.ui.theme.FinancialAssistantTheme

class QuickAddActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinancialAssistantTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { QuickAddBottomBar() },
                    topBar = { QuickAddTopBar() }
                ) { innerPadding ->
                    QuickAddScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun QuickAddTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xE6F8FAFC)) // bg-slate-50/90
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
fun QuickAddScreen(modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(surfaceColor)
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IncomeExpenseToggle()
        Spacer(modifier = Modifier.height(40.dp))
        TransactionAmountSection()
        Spacer(modifier = Modifier.height(40.dp))
        CategorySection()
        Spacer(modifier = Modifier.height(40.dp))
        ConfirmButton()
        Spacer(modifier = Modifier.height(40.dp))
        RecentActivitySection()
        Spacer(modifier = Modifier.height(32.dp)) // Extra padding for bottom bar
    }
}

@Composable
fun IncomeExpenseToggle() {
    var isExpense by remember { mutableStateOf(true) }

    Row(
        modifier = Modifier
            .width(240.dp)
            .background(surfaceContainerLow, RoundedCornerShape(12.dp))
            .padding(4.dp)
    ) {
        // Income Button
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(if (!isExpense) primaryColor else Color.Transparent)
                .clickable { isExpense = false }
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Income",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (!isExpense) Color.White else onSurfaceVariantColor
            )
        }
        
        // Expense Button
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isExpense) primaryColor else Color.Transparent)
                .clickable { isExpense = true }
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Expense",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (isExpense) Color.White else onSurfaceVariantColor
            )
        }
    }
}

@Composable
fun TransactionAmountSection() {
    var amountText by remember { mutableStateOf("") }
    
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "TRANSACTION AMOUNT",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = onSurfaceVariantColor,
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "$",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFBA1A1A).copy(alpha = 0.3f) // text-error with opacity
            )
            
            // Using a simple TextField with transparent background
            TextField(
                value = amountText,
                onValueChange = { amountText = it },
                placeholder = { 
                    Text("0.00", fontSize = 64.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFE0E3E5)) 
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 64.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = onSurfaceColor,
                    textAlign = TextAlign.Center
                ),
                singleLine = true,
                modifier = Modifier.width(200.dp)
            )
        }
    }
}

data class CategoryItem(val name: String, val icon: ImageVector)

@Composable
fun CategorySection() {
    val categories = listOf(
        CategoryItem("Rent", Icons.Default.Home),
        CategoryItem("Fun", Icons.Default.Face),
        CategoryItem("Health", Icons.Default.Favorite)
    )

    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(24.dp)
                    .background(primaryColor, CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Select Category",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = onSurfaceColor
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        
        // 4 columns grid
        Column {
            for (i in 0 until 2) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (j in 0 until 4) {
                        val index = i * 4 + j
                        if (index < categories.size) {
                            CategoryCard(category = categories[index])
                        }
                    }
                }
                if (i == 0) Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun CategoryCard(category: CategoryItem) {
    var isSelected by remember { mutableStateOf(false) } // For interaction effect

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(72.dp)
            .background(surfaceContainerLowest, RoundedCornerShape(16.dp))
            .border(
                1.dp,
                if (isSelected) primaryContainerColor else Color.Transparent,
                RoundedCornerShape(16.dp)
            )
            .clickable { isSelected = !isSelected }
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    if (isSelected) primaryContainerColor else surfaceContainerLow,
                    RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = category.icon,
                contentDescription = category.name,
                tint = if (isSelected) Color.White else primaryColor,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = category.name.uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = onSurfaceVariantColor,
            letterSpacing = 1.sp
        )
    }
}

@Composable
fun ConfirmButton() {
    Button(
        onClick = { /* TODO */ },
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent // Will use gradient
        ),
        contentPadding = PaddingValues(0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .shadow(8.dp, RoundedCornerShape(16.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(primaryColor, primaryContainerColor)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Confirm Transaction",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun RecentActivitySection() {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "RECENT ACTIVITY",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = onSurfaceVariantColor,
                letterSpacing = 1.sp
            )
            TextButton(onClick = { /* TODO */ }) {
                Text(
                    text = "View All",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = primaryColor
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        
        RecentActivityItem("Blue Bottle Coffee", "Today, 9:41 AM", "-$4.50", Icons.Default.ShoppingCart)
        Spacer(modifier = Modifier.height(12.dp))
        RecentActivityItem("Whole Foods Market", "Yesterday, 6:12 PM", "-$64.20", Icons.Default.ShoppingCart)
    }
}

@Composable
fun RecentActivityItem(title: String, time: String, amount: String, icon: ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(surfaceContainerLowest, RoundedCornerShape(12.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(surfaceContainerLow, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = onSurfaceVariantColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = onSurfaceColor
                )
                Text(
                    text = time.uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = onSurfaceVariantColor
                )
            }
        }
        Text(
            text = amount,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = onSurfaceColor
        )
    }
}

fun navigateWithFadeFromQuickAdd(context: android.content.Context, targetClass: Class<*>) {
    val intent = android.content.Intent(context, targetClass).apply { 
        flags = android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP 
    }
    val options = android.app.ActivityOptions.makeCustomAnimation(context, android.R.anim.fade_in, android.R.anim.fade_out).toBundle()
    androidx.core.content.ContextCompat.startActivity(context, intent, options)
}

@Composable
fun QuickAddBottomBar() {
    val context = androidx.compose.ui.platform.LocalContext.current
    // Navigation items
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White) // Or dark mode equiv
            .shadow(24.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .navigationBarsPadding(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomBarItem("Home", Icons.Default.AddCircle, true) {}
        BottomBarItem("History", Icons.Default.Refresh, false) {
            navigateWithFadeFromQuickAdd(context, HistoryActivity::class.java)
        }
        BottomBarItem("Analytics", Icons.Default.BarChart, false) {
            navigateWithFadeFromQuickAdd(context, AnalyticsActivity::class.java)
        }
        BottomBarItem("Categories", Icons.Default.List, false) {
            navigateWithFadeFromQuickAdd(context, CategoryActivity::class.java)
        }
    }
}

@Composable
fun BottomBarItem(label: String, icon: ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) Color(0xFFEFF6FF) else Color.Transparent) // blue-50
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

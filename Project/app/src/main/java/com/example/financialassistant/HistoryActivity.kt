package com.example.financialassistant

import android.content.Intent
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financialassistant.ui.theme.FinancialAssistantTheme

class HistoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinancialAssistantTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { HistoryTopBar() },
                    bottomBar = { HistoryBottomBar() }
                ) { innerPadding ->
                    HistoryScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun HistoryTopBar() {
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
fun HistoryScreen(modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(surfaceColor)
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        EditorialHeader()
        Spacer(modifier = Modifier.height(32.dp))
        SearchBar()
        Spacer(modifier = Modifier.height(32.dp))
        TransactionGroupToday()
        Spacer(modifier = Modifier.height(32.dp))
        TransactionGroupYesterday()
        Spacer(modifier = Modifier.height(32.dp))
        AnalyticsBentoPreview()
        Spacer(modifier = Modifier.height(48.dp)) // padding for bottom bar
    }
}

@Composable
fun EditorialHeader() {
    Column {
        Text(
            text = "History",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = onSurfaceColor,
            letterSpacing = (-0.5).sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Review your financial timeline with precision.",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = onSurfaceVariantColor
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar() {
    var searchText by remember { mutableStateOf("") }
    
    TextField(
        value = searchText,
        onValueChange = { searchText = it },
        placeholder = { 
            Text(
                "Search transactions, tags, or merchants...", 
                fontSize = 14.sp, 
                color = onSurfaceVariantColor.copy(alpha = 0.6f)
            ) 
        },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Search", tint = onSurfaceVariantColor)
        },
        trailingIcon = {
            // "Tune" icon equivalent in Material is Tune or FilterList
            Icon(Icons.Default.Tune, contentDescription = "Filter", tint = onSurfaceVariantColor.copy(alpha = 0.6f))
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = surfaceContainerLowest,
            unfocusedContainerColor = Color(0xFFE6E8EA), // surface-container-high
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
}

@Composable
fun TransactionGroupToday() {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "TODAY",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = onSurfaceVariantColor.copy(alpha = 0.7f),
                letterSpacing = 1.sp
            )
            Surface(
                color = primaryContainerColor.copy(alpha = 0.1f),
                shape = CircleShape
            ) {
                Text(
                    text = "3 TRANSACTIONS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        
        Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
            TransactionHistoryItem(
                icon = Icons.Default.Coffee,
                iconColor = onSurfaceVariantColor,
                iconBgColor = surfaceContainerLow,
                title = "Blue Bottle Coffee",
                amount = "- $6.50",
                amountColor = Color(0xFFBA1A1A),
                subtitle = "Food & Drink • 08:42 AM",
                tag = "PERSONAL",
                tagColor = onSurfaceVariantColor.copy(alpha = 0.4f)
            )
            TransactionHistoryItem(
                icon = Icons.Default.AccountBalanceWallet,
                iconColor = primaryColor,
                iconBgColor = primaryColor.copy(alpha = 0.05f),
                title = "Client Deposit",
                amount = "+ $2,400.00",
                amountColor = primaryColor,
                subtitle = "Income • 11:15 AM",
                tag = "BUSINESS",
                tagColor = primaryColor.copy(alpha = 0.6f)
            )
            TransactionHistoryItem(
                icon = Icons.Default.ShoppingBag,
                iconColor = onSurfaceVariantColor,
                iconBgColor = surfaceContainerLow,
                title = "Apple Store",
                amount = "- $199.00",
                amountColor = Color(0xFFBA1A1A),
                subtitle = "Electronics • 02:30 PM",
                tag = "ONE-TIME",
                tagColor = onSurfaceVariantColor.copy(alpha = 0.4f)
            )
        }
    }
}

@Composable
fun TransactionGroupYesterday() {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "YESTERDAY",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = onSurfaceVariantColor.copy(alpha = 0.7f),
                letterSpacing = 1.sp
            )
            Surface(
                color = Color(0xFFE6E8EA), // surface-container-high
                shape = CircleShape
            ) {
                Text(
                    text = "2 TRANSACTIONS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = onSurfaceVariantColor.copy(alpha = 0.5f),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        
        Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
            TransactionHistoryItem(
                icon = Icons.Default.DirectionsCar,
                iconColor = onSurfaceVariantColor,
                iconBgColor = surfaceContainerLow,
                title = "Uber Trip",
                amount = "- $24.80",
                amountColor = Color(0xFFBA1A1A),
                subtitle = "Transport • 06:15 PM",
                tag = "PERSONAL",
                tagColor = onSurfaceVariantColor.copy(alpha = 0.4f)
            )
            TransactionHistoryItem(
                icon = Icons.Default.Restaurant,
                iconColor = onSurfaceVariantColor,
                iconBgColor = surfaceContainerLow,
                title = "Whole Foods",
                amount = "- $82.15",
                amountColor = Color(0xFFBA1A1A),
                subtitle = "Groceries • 05:00 PM",
                tag = "SUBSISTENCE",
                tagColor = onSurfaceVariantColor.copy(alpha = 0.4f)
            )
        }
    }
}

@Composable
fun TransactionHistoryItem(
    icon: ImageVector,
    iconColor: Color,
    iconBgColor: Color,
    title: String,
    amount: String,
    amountColor: Color,
    subtitle: String,
    tag: String,
    tagColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO */ },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(iconBgColor, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = onSurfaceColor
                )
                Text(
                    text = amount,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = amountColor
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = onSurfaceVariantColor
                )
                Text(
                    text = tag,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = tagColor,
                    letterSpacing = (-0.5).sp
                )
            }
        }
    }
}

@Composable
fun AnalyticsBentoPreview() {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = primaryContainerColor,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Decorative Element
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 40.dp, y = (-40).dp)
                    .size(160.dp)
                    .background(Color.White.copy(alpha = 0.1f), CircleShape)
            )

            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = "Monthly Insight",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Spending is 12% lower than June",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.TrendingDown,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Chart mock
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    val heights = listOf(0.4f, 0.6f, 0.55f, 0.8f, 0.45f, 0.3f)
                    heights.forEachIndexed { index, percent ->
                        val isLast = index == heights.lastIndex
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(percent)
                                .background(
                                    Color.White.copy(alpha = if (isLast) 0.8f else 0.3f),
                                    RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { /* TODO */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(alpha = 0.1f),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(12.dp)
                ) {
                    Text(
                        text = "VIEW FULL ANALYTICS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                }
            }
        }
    }
}

fun navigateWithFadeFromHistory(context: android.content.Context, targetClass: Class<*>) {
    val intent = android.content.Intent(context, targetClass).apply { 
        flags = android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP 
    }
    val options = android.app.ActivityOptions.makeCustomAnimation(context, android.R.anim.fade_in, android.R.anim.fade_out).toBundle()
    androidx.core.content.ContextCompat.startActivity(context, intent, options)
}

@Composable
fun HistoryBottomBar() {
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
        BottomBarItemHistory("Home", Icons.Default.AddCircle, false) {
            navigateWithFadeFromHistory(context, QuickAddActivity::class.java)
        }
        BottomBarItemHistory("History", Icons.Default.Refresh, true) {}
        BottomBarItemHistory("Analytics", Icons.Default.BarChart, false) {
            navigateWithFadeFromHistory(context, AnalyticsActivity::class.java)
        }
        BottomBarItemHistory("Categories", Icons.Default.List, false) {
            navigateWithFadeFromHistory(context, CategoryActivity::class.java)
        }
    }
}

@Composable
fun BottomBarItemHistory(label: String, icon: ImageVector, isSelected: Boolean, onClick: () -> Unit) {
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

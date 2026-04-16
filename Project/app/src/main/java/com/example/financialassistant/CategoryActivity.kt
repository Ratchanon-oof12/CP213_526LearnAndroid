package com.example.financialassistant

import android.app.Activity
import android.app.ActivityOptions
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.financialassistant.ui.theme.FinancialAssistantTheme

class CategoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinancialAssistantTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { CategoryTopBar() },
                    bottomBar = { CategoryBottomBar() }
                ) { innerPadding ->
                    CategoryScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun CategoryTopBar() {
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
fun CategoryScreen(modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(surfaceColor)
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        HeroSection()
        Spacer(modifier = Modifier.height(32.dp))
        ActionHeader()
        Spacer(modifier = Modifier.height(24.dp))
        BentoGrid()
        Spacer(modifier = Modifier.height(48.dp))
    }
}

@Composable
fun HeroSection() {
    Column {
        Text(
            text = "Category Library",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = primaryColor,
            letterSpacing = (-0.5).sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Organize your financial ecosystem with precision.",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = onSurfaceVariantColor
        )
    }
}

@Composable
fun ActionHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Column {
            Text(
                text = "OVERVIEW",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                color = primaryColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Active Categories",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = onSurfaceColor
            )
        }
        
        Button(
            onClick = { /* TODO */ },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            ),
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.height(32.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(primaryColor, primaryContainerColor)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add New Category", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun BentoGrid() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Essential (Full width on mobile normally, but mapped for single col constraint as standard)
        CategoryCardLarge(
            title = "Housing & Rent",
            subtitle = "12 Transactions",
            detail = "Monthly: $2,450.00",
            icon = Icons.Default.Home,
            iconColor = primaryColor,
            iconBg = primaryColor.copy(alpha = 0.05f),
            detailColor = primaryColor,
            indicatorColor = Color(0xFFC3C6D6)
        )
        
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            CategoryCardSmall(
                title = "Transport",
                subtitle = "8 Transactions",
                icon = Icons.Default.DirectionsCar,
                iconColor = Color(0xFF4C5D8D), // secondary
                iconBg = Color(0xFF4C5D8D).copy(alpha = 0.1f),
                modifier = Modifier.weight(1f)
            )
            CategoryCardSmall(
                title = "Dining Out",
                subtitle = "24 Transactions",
                iconColor = Color(0xFFA33500),
                icon = Icons.Default.Restaurant,
                iconBg = Color(0xFFA33500).copy(alpha = 0.1f),
                modifier = Modifier.weight(1f)
            )
        }

        // Entertainment
        CategoryCardEntertainment()
        
        // Minor categories list
        SmallCategoryRow(title = "Utilities", subtitle = "4 Subscriptions", icon = Icons.Default.Bolt, iconColor = primaryColor)
        SmallCategoryRow(title = "Shopping", subtitle = "19 Transactions", icon = Icons.Default.ShoppingBag, iconColor = Color(0xFF4C5D8D))
        SmallCategoryRow(title = "Health", subtitle = "2 Transactions", icon = Icons.Default.HealthAndSafety, iconColor = Color(0xFFBA1A1A))
    }
}

@Composable
fun CategoryCardLarge(
    title: String, subtitle: String, detail: String,
    icon: ImageVector, iconColor: Color, iconBg: Color,
    detailColor: Color, indicatorColor: Color
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = surfaceContainerLowest,
        modifier = Modifier.fillMaxWidth().clickable { }
    ) {
        Column(modifier = Modifier.padding(24.dp).heightIn(min = 160.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Box(modifier = Modifier.size(56.dp).background(iconBg, RoundedCornerShape(16.dp)), contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(32.dp))
                }
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = onSurfaceVariantColor.copy(alpha = 0.5f))
            }
            Spacer(modifier = Modifier.height(24.dp))
            Column {
                Text(title, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = onSurfaceColor)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(subtitle.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = onSurfaceVariantColor, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.size(4.dp).background(indicatorColor, CircleShape))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(detail, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = detailColor)
                }
            }
        }
    }
}

@Composable
fun CategoryCardSmall(
    title: String, subtitle: String, 
    iconColor: Color, iconBg: Color,
    iconVector: ImageVector? = null,
    icon: ImageVector? = null,
    modifier: Modifier = Modifier
) {
    val actualIconColor = if(icon == null) iconColor else iconColor
    val actualIcon = icon ?: iconVector ?: Icons.Default.Category
    
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = surfaceContainerLow,
        modifier = modifier.clickable { }
    ) {
        Column(modifier = Modifier.padding(24.dp).heightIn(min = 160.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Box(modifier = Modifier.size(56.dp).background(iconBg, RoundedCornerShape(16.dp)), contentAlignment = Alignment.Center) {
                    Icon(actualIcon, contentDescription = null, tint = actualIconColor, modifier = Modifier.size(32.dp))
                }
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = onSurfaceVariantColor.copy(alpha = 0.5f))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Column {
                Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = onSurfaceColor)
                Spacer(modifier = Modifier.height(4.dp))
                Text(subtitle.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = onSurfaceVariantColor, letterSpacing = 1.sp)
            }
        }
    }
}

@Composable
fun CategoryCardEntertainment() {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = primaryContainerColor,
        modifier = Modifier.fillMaxWidth().clickable { }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Icon(
                Icons.Default.LocalActivity, contentDescription = null,
                tint = Color.White.copy(alpha = 0.1f),
                modifier = Modifier.align(Alignment.TopEnd).size(120.dp).offset(x = 20.dp, y = (-20).dp)
            )
            
            Column(modifier = Modifier.padding(24.dp).heightIn(min = 160.dp), verticalArrangement = Arrangement.SpaceBetween) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                    Box(modifier = Modifier.size(56.dp).background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(16.dp)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.LocalActivity, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                    }
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.White.copy(alpha = 0.6f))
                }
                Spacer(modifier = Modifier.height(24.dp))
                Column {
                    Text("Entertainment", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("15 TRANSACTIONS", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Color.White.copy(alpha = 0.8f), letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(modifier = Modifier.size(4.dp).background(Color.White.copy(alpha = 0.4f), CircleShape))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Budget Limit: $500.00", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun SmallCategoryRow(title: String, subtitle: String, icon: ImageVector, iconColor: Color) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = surfaceContainerLow,
        modifier = Modifier.fillMaxWidth().clickable { }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(48.dp).background(Color.White, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = onSurfaceColor)
                    Text(subtitle.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = onSurfaceVariantColor)
                }
            }
            Icon(Icons.Default.MoreVert, contentDescription = "More", tint = onSurfaceVariantColor)
        }
    }
}

fun navigateWithFade(context: android.content.Context, targetClass: Class<*>) {
    val intent = Intent(context, targetClass).apply { 
        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP 
    }
    val options = ActivityOptions.makeCustomAnimation(context, android.R.anim.fade_in, android.R.anim.fade_out).toBundle()
    ContextCompat.startActivity(context, intent, options)
}

@Composable
fun CategoryBottomBar() {
    val context = LocalContext.current
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
        BottomBarItemCategory("Home", Icons.Default.AddCircle, false) {
            navigateWithFade(context, QuickAddActivity::class.java)
        }
        BottomBarItemCategory("History", Icons.Default.Refresh, false) {
            navigateWithFade(context, HistoryActivity::class.java)
        }
        BottomBarItemCategory("Analytics", Icons.Default.BarChart, false) {
            navigateWithFade(context, AnalyticsActivity::class.java)
        }
        BottomBarItemCategory("Categories", Icons.Default.Category, true) {}
    }
}

@Composable
fun BottomBarItemCategory(label: String, icon: ImageVector, isSelected: Boolean, onClick: () -> Unit) {
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

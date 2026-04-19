package com.example.financialassistant

import android.os.Bundle
import android.content.Intent
import androidx.compose.ui.text.withStyle
import android.app.ActivityOptions
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.zIndex
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financialassistant.ui.theme.FinancialAssistantTheme

// Defining colors based on HTML tailwind config
val primaryColor = Color(0xFF003D9B)
val primaryContainerColor = Color(0xFF0052CC)
val surfaceColor = Color(0xFFF7F9FB)
val onSurfaceColor = Color(0xFF191C1E)
val onSurfaceVariantColor = Color(0xFF434654)
val surfaceContainerLowest = Color(0xFFFFFFFF)
val surfaceContainerLow = Color(0xFFF2F4F6)

class WelcomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinancialAssistantTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WelcomeScreen(
                        innerPadding = innerPadding,
                        modifier = Modifier,
                        onGetStartedClick = {
                            val options = ActivityOptions.makeCustomAnimation(this, android.R.anim.fade_in, android.R.anim.fade_out).toBundle()
                            androidx.core.content.ContextCompat.startActivity(this, Intent(this, QuickAddActivity::class.java), options)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun WelcomeScreen(modifier: Modifier = Modifier, innerPadding: PaddingValues = PaddingValues(0.dp), onGetStartedClick: () -> Unit = {}) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val sharedPrefs = context.getSharedPreferences("financial_prefs", android.content.Context.MODE_PRIVATE)
    
    val savedName = sharedPrefs.getString("username", null)
    var username by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(savedName ?: "") }
    var needsInput by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(savedName == null) }
    
    val scrollState = rememberScrollState()
    var isLoading by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(true) }
    
    androidx.compose.runtime.LaunchedEffect(needsInput) {
        if (!needsInput) {
            kotlinx.coroutines.delay(1200)
            isLoading = false
        }
    }

    val contentAlpha by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isLoading) 0f else 1f,
        animationSpec = androidx.compose.animation.core.tween(800, delayMillis = 400)
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(surfaceColor)
    ) {
        // Background Asymmetry Elements
        Box(
            modifier = Modifier
                .offset(x = (-48).dp, y = 160.dp)
                .size(256.dp)
                .background(primaryContainerColor.copy(alpha = 0.05f), CircleShape)
                .blur(50.dp)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 48.dp, y = (-80).dp)
                .size(384.dp)
                .background(Color(0xFFB6C8FE).copy(alpha = 0.1f), CircleShape)
                .blur(50.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
            WelcomeTopBar(modifier = Modifier.alpha(contentAlpha))
            Spacer(modifier = Modifier.height(48.dp))
            AIOrb(isLoading = isLoading)
            Spacer(modifier = Modifier.height(32.dp))
            Column(modifier = Modifier.alpha(contentAlpha), horizontalAlignment = Alignment.CenterHorizontally) {
                GreetingSection(username = username, onGetStartedClick = onGetStartedClick)
                Spacer(modifier = Modifier.height(48.dp))
                FeatureBento()
                Spacer(modifier = Modifier.height(48.dp))
                WelcomeFooter()
                Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding() + 24.dp))
            }
        }

        // Overlay for Username Input when required
        androidx.compose.animation.AnimatedVisibility(
            visible = needsInput,
            enter = androidx.compose.animation.fadeIn(),
            exit = androidx.compose.animation.fadeOut(),
            modifier = Modifier.matchParentSize().zIndex(20f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    var inputText by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }
                    Text(
                        text = "Nice to meet you.",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = { Text("Enter your name", color = Color.White.copy(alpha = 0.5f)) },
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.White,
                            unfocusedIndicatorColor = Color.White.copy(alpha = 0.5f),
                            cursorColor = Color.White
                        ),
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.fillMaxWidth(0.8f)
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                sharedPrefs.edit().putString("username", inputText.trim()).apply()
                                username = inputText.trim()
                                needsInput = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Continue", color = primaryColor, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun WelcomeTopBar(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Financial Architect",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = onSurfaceColor
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
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
            
            Surface(
                modifier = Modifier.size(32.dp),
                shape = CircleShape,
                color = Color.LightGray
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile Picture",
                    tint = Color.Gray,
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
    }
}

@Composable
fun AIOrb(isLoading: Boolean = false) {
    val scale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isLoading) 30f else 1f,
        animationSpec = androidx.compose.animation.core.tween(
            durationMillis = 1000,
            easing = androidx.compose.animation.core.FastOutSlowInEasing
        )
    )

    Box(
        modifier = Modifier.size(192.dp),
        contentAlignment = Alignment.Center
    ) {
        // Outer rings
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(1.dp, primaryColor.copy(alpha = 0.1f), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(153.dp) // ~80%
                .border(1.dp, primaryColor.copy(alpha = 0.2f), CircleShape)
        )
        // Core orb
        Box(
            modifier = Modifier
                .size(128.dp)
                .zIndex(if (isLoading) 10f else 0f)
                .scale(scale)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(primaryContainerColor, primaryColor)
                    ),
                    shape = CircleShape
                )
        )
    }
}

@Composable
fun GreetingSection(username: String, onGetStartedClick: () -> Unit = {}) {
    val text1 = "Hello, $username!"
    val text2 = "I'm your Financial Architect."
    val text3 = "Let’s build your foundation of wealth through precision clarity and intentional design."
    
    var visibleChars1 by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(0) }
    var visibleChars2 by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(0) }
    var visibleChars3 by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(0) }
    
    androidx.compose.runtime.LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(1000) // Wait for splash to shrink
        for (i in 1..text1.length) {
            visibleChars1 = i
            kotlinx.coroutines.delay(30)
        }
        kotlinx.coroutines.delay(200)
        for (i in 1..text2.length) {
            visibleChars2 = i
            kotlinx.coroutines.delay(25)
        }
        kotlinx.coroutines.delay(200)
        for (i in 1..text3.length) {
            visibleChars3 = i
            kotlinx.coroutines.delay(15)
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = androidx.compose.ui.text.buildAnnotatedString {
                append(text1.take(visibleChars1))
                withStyle(androidx.compose.ui.text.SpanStyle(color = Color.Transparent)) { append(text1.drop(visibleChars1)) }
            },
            fontSize = 36.sp,
            fontWeight = FontWeight.ExtraBold,
            color = onSurfaceColor,
            lineHeight = 44.sp,
            textAlign = TextAlign.Center
        )
        Text(
            text = androidx.compose.ui.text.buildAnnotatedString {
                append(text2.take(visibleChars2))
                withStyle(androidx.compose.ui.text.SpanStyle(color = Color.Transparent)) { append(text2.drop(visibleChars2)) }
            },
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = primaryContainerColor, 
            lineHeight = 36.sp,
            textAlign = TextAlign.Center
        )
        Text(
            text = androidx.compose.ui.text.buildAnnotatedString {
                append(text3.take(visibleChars3))
                withStyle(androidx.compose.ui.text.SpanStyle(color = Color.Transparent)) { append(text3.drop(visibleChars3)) }
            },
            fontSize = 16.sp,
            color = onSurfaceVariantColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        // Fade in button after text is done? Or show immediately? We'll show immediately as design.
        Button(
            onClick = onGetStartedClick,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = primaryContainerColor,
                contentColor = Color.White
            ),
            contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Get Started",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun FeatureBento() {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        // Card 1
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = surfaceContainerLowest,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "STRATEGY",
                    color = primaryColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Predictive Analysis",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = onSurfaceColor
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "I analyze your spending patterns to forecast future savings opportunities before you even think of them.",
                    fontSize = 14.sp,
                    color = onSurfaceVariantColor
                )
                Spacer(modifier = Modifier.height(24.dp))
                
                // Progress bar mock
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(Color(0xFFECEEF0), RoundedCornerShape(8.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.75f)
                            .fillMaxHeight()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(primaryColor, primaryContainerColor)
                                ),
                                shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp, topEnd = 24.dp, bottomEnd = 24.dp)
                            )
                    )
                }
            }
        }
        
        // Card 2
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = surfaceContainerLow,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(primaryColor.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = primaryColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Bank-Grade Security",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = onSurfaceColor
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Your data is encrypted using architectural standards of precision. Safe, secure, and invisible.",
                    fontSize = 14.sp,
                    color = onSurfaceVariantColor
                )
            }
        }
    }
}

@Composable
fun WelcomeFooter() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalDivider(color = onSurfaceVariantColor.copy(alpha = 0.15f))
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "© 2024 Financial Architect AI. All rights reserved.",
            fontSize = 12.sp,
            color = onSurfaceVariantColor
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Privacy Protocol",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = onSurfaceVariantColor
            )
            Text(
                text = "Terms of Architecture",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = onSurfaceVariantColor
            )
        }
    }
}

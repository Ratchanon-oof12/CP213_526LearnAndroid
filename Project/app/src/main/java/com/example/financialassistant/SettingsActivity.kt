package com.example.financialassistant

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financialassistant.ui.theme.FinancialAssistantTheme

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinancialAssistantTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { SettingsTopBar { finish() } }
                ) { innerPadding ->
                    SettingsScreen(modifier = Modifier.padding(innerPadding)) {
                        finish()
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xE6F8FAFC))
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack, modifier = Modifier.size(40.dp).clip(CircleShape)) {
            Icon(Icons.Default.ArrowBack, "Back", tint = onSurfaceVariantColor)
        }
        Text("Settings", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = onSurfaceColor)
        Box(modifier = Modifier.size(40.dp)) // Empty box for balancing flex
    }
}

@Composable
fun SettingsScreen(modifier: Modifier = Modifier, onSaveComplete: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("financial_prefs", Context.MODE_PRIVATE) }
    
    var username by remember { mutableStateOf(sharedPrefs.getString("username", "") ?: "") }
    var assistantName by remember { mutableStateOf(sharedPrefs.getString("assistant_name", "Financial Architect") ?: "Financial Architect") }
    var assistantIconKey by remember { mutableStateOf(sharedPrefs.getString("assistant_icon", "Person") ?: "Person") }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(surfaceColor)
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Icon
        Surface(modifier = Modifier.size(80.dp), shape = CircleShape, color = primaryColor.copy(alpha = 0.1f)) {
            Icon(Icons.Default.Settings, null, tint = primaryColor, modifier = Modifier.padding(20.dp))
        }
        Spacer(modifier = Modifier.height(32.dp))
        
        // Form
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Your Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primaryColor,
                unfocusedBorderColor = onSurfaceVariantColor.copy(alpha = 0.3f)
            )
        )
        Spacer(modifier = Modifier.height(20.dp))
        
        OutlinedTextField(
            value = assistantName,
            onValueChange = { assistantName = it },
            label = { Text("Assistant Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primaryColor,
                unfocusedBorderColor = onSurfaceVariantColor.copy(alpha = 0.3f)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Assistant Profile Icon",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = onSurfaceVariantColor,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            assistantIconOptions.forEach { key ->
                FilterChip(
                    selected = assistantIconKey == key,
                    onClick = { assistantIconKey = key },
                    label = {
                        Text(
                            when (key) {
                                "SmartToy" -> "Bot"
                                "AutoGraph" -> "Graph"
                                "AccountBalance" -> "Bank"
                                else -> "User"
                            },
                            fontSize = 11.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = assistantIconForKey(key),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(36.dp))
        
        // Save Button
        Button(
            onClick = {
                sharedPrefs.edit().apply {
                    putString("username", username.trim())
                    putString("assistant_name", if (assistantName.isNotBlank()) assistantName.trim() else "Financial Architect")
                    putString("assistant_icon", assistantIconKey)
                    apply()
                }
                onSaveComplete()
            },
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Icon(Icons.Default.Check, null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Save Changes", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

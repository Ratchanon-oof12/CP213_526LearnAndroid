package com.example.financialassistant

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext

val assistantIconOptions = listOf("Person", "SmartToy", "AutoGraph", "AccountBalance")

@Composable
fun rememberAssistantName(): State<String> {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("financial_prefs", Context.MODE_PRIVATE) }
    val state = remember { mutableStateOf(prefs.getString("assistant_name", "Financial Architect") ?: "Financial Architect") }

    DisposableEffect(prefs) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key == "assistant_name") {
                state.value = sharedPreferences.getString("assistant_name", "Financial Architect") ?: "Financial Architect"
            }
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        onDispose {
            prefs.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }
    return state
}

@Composable
fun rememberAssistantIconKey(): State<String> {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("financial_prefs", Context.MODE_PRIVATE) }
    val state = remember { mutableStateOf(prefs.getString("assistant_icon", "Person") ?: "Person") }

    DisposableEffect(prefs) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key == "assistant_icon") {
                state.value = sharedPreferences.getString("assistant_icon", "Person") ?: "Person"
            }
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        onDispose {
            prefs.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }
    return state
}

fun assistantIconForKey(key: String): ImageVector = when (key) {
    "SmartToy" -> Icons.Default.SmartToy
    "AutoGraph" -> Icons.Default.AutoGraph
    "AccountBalance" -> Icons.Default.AccountBalance
    else -> Icons.Default.Person
}

@Composable
fun rememberUserName(): State<String> {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("financial_prefs", Context.MODE_PRIVATE) }
    val state = remember { mutableStateOf(prefs.getString("username", "") ?: "") }

    DisposableEffect(prefs) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key == "username") {
                state.value = sharedPreferences.getString("username", "") ?: ""
            }
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        onDispose {
            prefs.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }
    return state
}

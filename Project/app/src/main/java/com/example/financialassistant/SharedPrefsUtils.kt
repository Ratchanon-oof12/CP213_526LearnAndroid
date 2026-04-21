package com.example.financialassistant

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext

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

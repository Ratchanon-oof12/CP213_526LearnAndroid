package com.example.a526lablearnandroid.Architecture

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.example.a526lablearnandroid.Architecture.ui.theme._526LabLearnAndroidTheme

class CounterScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            _526LabLearnAndroidTheme {
                CounterScreen(
                    onNavigateBack = { finish() }
                )
            }
        }
    }
}

@Composable
fun CounterScreen(
    counterViewModel: CounterViewModel = CounterViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val state by counterViewModel.state.collectAsState()

    CounterView(state = state, onNavigateBack = onNavigateBack) { intent ->
        counterViewModel.processIntent(intent)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CounterView(
    state: CounterState,
    onNavigateBack: () -> Unit,
    onIntent: (CounterIntent) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Counter") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Count: ${state.count}",
                fontSize = 32.sp
            )

            Button(onClick = {
                onIntent(CounterIntent.IncrementCounter)
            }) {
                Text("Add +1")
            }
        }
    }
}
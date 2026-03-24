package com.example.a526lablearnandroid

import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import android.app.Activity
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

// 1. Hardware Part (SensorTracker)
class AccelerometerTracker(context: Context) {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    fun getAccelerometerData(): Flow<FloatArray> = callbackFlow {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
                    trySend(event.values)
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_UI)

        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }
}

// 2. ViewModel Part
data class AccelerometerData(val x: Float = 0f, val y: Float = 0f, val z: Float = 0f)

class SensorViewModel(application: Application) : AndroidViewModel(application) {
    private val _sensorData = MutableStateFlow(AccelerometerData())
    val sensorData: StateFlow<AccelerometerData> = _sensorData.asStateFlow()

    private val tracker = AccelerometerTracker(application)

    init {
        viewModelScope.launch {
            tracker.getAccelerometerData().collect { values ->
                if (values.size >= 3) {
                    _sensorData.value = AccelerometerData(x = values[0], y = values[1], z = values[2])
                }
            }
        }
    }
}

// 3. Activity & UI Part
class SensorActivity : ComponentActivity() {
    private val viewModel: SensorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                SensorScreen(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SensorScreen(viewModel: SensorViewModel) {
    val sensorData by viewModel.sensorData.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = { 
            TopAppBar(
                title = { Text("Sensor & MVVM") },
                navigationIcon = {
                    IconButton(onClick = { (context as? Activity)?.finish() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            ) 
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Accelerometer", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(24.dp))
            Text("X: ${String.format("%.2f", sensorData.x)}", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Y: ${String.format("%.2f", sensorData.y)}", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Z: ${String.format("%.2f", sensorData.z)}", style = MaterialTheme.typography.headlineMedium)
        }
    }
}

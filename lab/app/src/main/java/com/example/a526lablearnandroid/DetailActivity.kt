package com.example.a526lablearnandroid

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.a526lablearnandroid.ui.theme._526LabLearnAndroidTheme

class DetailActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Retrieve the extra string sent from Intent
        val receivedMessage = intent.getStringExtra("EXTRA_MESSAGE") ?: "No Message"

        setContent {
            _526LabLearnAndroidTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Detail Screen (Activity 2)") },
                            navigationIcon = {
                                IconButton(onClick = { 
                                    finishWithAnimation()
                                }) {
                                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                                }
                            }
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Message Received:", modifier = Modifier.padding(bottom = 8.dp))
                        Text(text = receivedMessage, modifier = Modifier.padding(bottom = 32.dp))
                        
                        Button(onClick = { 
                            finishWithAnimation()
                        }) {
                            Text("Close (Slide Down)")
                        }
                    }
                }
            }
        }
    }
    
    // Create a custom finish function since the slide down must be orchestrated
    private fun finishWithAnimation() {
        finish()
        // Call the built-in system transition override after finishing
        overridePendingTransition(R.anim.stay, R.anim.slide_out_down)
    }
    
    @Deprecated("Deprecated in Java", ReplaceWith("super.onBackPressed()"))
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.stay, R.anim.slide_out_down)
    }
}

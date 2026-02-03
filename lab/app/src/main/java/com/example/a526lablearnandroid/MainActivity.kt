package com.example.a526lablearnandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CheckboxDefaults.colors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a526lablearnandroid.ui.theme._526LabLearnAndroidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            _526LabLearnAndroidTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Gray)
                        .padding(32.dp)
                ) {
                    //hp
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(32.dp)
                            .background(Color.White)
                    ) {
                        Text(
                            text = "hp : 526",
                            modifier = Modifier
                                .align(alignment = Alignment.CenterStart)
                                .fillMaxWidth(fraction = 0.526f)
                                .background(color = Color.Red)
                                .padding(8.dp)
                        )
                    }

                    //image
                    Image(
                        painter = painterResource(id = R.drawable.rr),
                        modifier = Modifier
                            .size(400.dp)
                            .padding(16.dp)
                            .align(alignment = Alignment.CenterHorizontally),
                        contentDescription = null
                    )
                    //Status
                    var str by remember { mutableStateOf(526) }
                    var agi by remember { mutableStateOf(526) }
                    var int by remember { mutableStateOf(526) }

                    Row {
                        Column {
                            Text(text = "Status", fontSize = 50.sp)

                            // STR row with buttons
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "STR : $str", fontSize = 30.sp, modifier = Modifier.padding(horizontal = 16.dp))
                                Button(
                                    onClick = { str++ },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.Green,
                                    )
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.baseline_plus_one_24),
                                        contentDescription = "Increase",
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Button(
                                    onClick = { str-- },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.Red,
                                    )
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.baseline_remove_24),
                                        contentDescription = "Decrease",
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            // AGI row with buttons
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "AGI : $agi", fontSize = 30.sp, modifier = Modifier.padding(horizontal = 16.dp))
                                Button(
                                    onClick = { agi++ },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.Green,
                                    )
                                    ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.baseline_plus_one_24),
                                        contentDescription = "Increase",
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Button(
                                    onClick = { agi-- },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.Red,
                                    )
                                    ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.baseline_remove_24),
                                        contentDescription = "Decrease",
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            // INT row with buttons
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "INT : $int", fontSize = 30.sp, modifier = Modifier.padding(horizontal = 16.dp))
                                Button(
                                    onClick = { int++ },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.Green,
                                    )
                                    ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.baseline_plus_one_24),
                                        contentDescription = "Increase",
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Button(
                                    onClick = { int-- },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.Red,
                                    )
                                    ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.baseline_remove_24),
                                        contentDescription = "Decrease",
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

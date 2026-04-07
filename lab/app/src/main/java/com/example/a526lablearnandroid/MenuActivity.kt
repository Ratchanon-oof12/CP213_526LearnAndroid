package com.example.a526lablearnandroid

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a526lablearnandroid.Architecture.CounterScreen
import com.example.a526lablearnandroid.util.SharedPreferenceActivity

class MenuActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("SYSTEM MENU", fontWeight = FontWeight.Bold, letterSpacing = 2.sp) },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Black,
                            titleContentColor = Color.White
                        )
                    )
                },
                modifier = Modifier.background(Color.Black)
            ) { paddingValues ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                        .padding(paddingValues)
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item { MenuButton("Pokedex") { startActivity(Intent(this@MenuActivity, PokedexActivity::class.java)) } }
                    item { MenuButton("RPG Card") { startActivity(Intent(this@MenuActivity, RPGCardActivity::class.java)) } }
                    item { MenuButton("Tam Jai") { startActivity(Intent(this@MenuActivity, TamJai::class.java)) } }
                    item { MenuButton("SharedPreference") { startActivity(Intent(this@MenuActivity, SharedPreferenceActivity::class.java)) } }
                    item { MenuButton("CounterScreen") { startActivity(Intent(this@MenuActivity, CounterScreen::class.java)) } }
                    item { MenuButton("Gallery & Permission") { startActivity(Intent(this@MenuActivity, GalleryActivity::class.java)) } }
                    item { MenuButton("Sensor (MVVM)") { startActivity(Intent(this@MenuActivity, SensorActivity::class.java)) } }
                    item { MenuButton("Part One (Like Button)") { startActivity(Intent(this@MenuActivity, PartOne::class.java)) } }
                    item { MenuButton("Part Two (Contacts)") { startActivity(Intent(this@MenuActivity, PartTwo::class.java)) } }
                    item { MenuButton("Part Three (Donut Chart)") { startActivity(Intent(this@MenuActivity, PartThree::class.java)) } }
                    item { MenuButton("Part Four (Swipe To Do)") { startActivity(Intent(this@MenuActivity, PartFour::class.java)) } }
                    item { MenuButton("Part Five (Side Effects/Snackbar)") { startActivity(Intent(this@MenuActivity, PartFive::class.java)) } }
                    item { MenuButton("Part Six (WebView)") { startActivity(Intent(this@MenuActivity, PartSix::class.java)) } }
                    item { MenuButton("Part Seven (Activity Transition)") { startActivity(Intent(this@MenuActivity, MainActivity::class.java)) } }
                    item { MenuButton("Part Eight (Responsive Profile)") { startActivity(Intent(this@MenuActivity, PartEight::class.java)) } }
                }
            }
        }
    }
}

@androidx.compose.runtime.Composable
fun MenuButton(text: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        border = BorderStroke(1.dp, Color.White),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Black,
            contentColor = Color.White
        )
    ) {
        Text(
            text = text.uppercase(),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            textAlign = TextAlign.Center
        )
    }
}

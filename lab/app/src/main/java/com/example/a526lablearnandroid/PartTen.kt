package com.example.a526lablearnandroid

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.Text as GlanceText
import androidx.glance.text.TextStyle
import androidx.glance.text.FontWeight
import androidx.glance.unit.ColorProvider
import com.example.a526lablearnandroid.ui.theme._526LabLearnAndroidTheme

// --- 1. Activity สำหรับแสดงคำอธิบาย (UI ปกติ) ---
class PartTen : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            _526LabLearnAndroidTheme {
                val context = LocalContext.current as? Activity
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("App Widget (Glance)") },
                            navigationIcon = {
                                IconButton(onClick = { context?.finish() }) {
                                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                                }
                            }
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(16.dp)
                    ) {
                        item {
                            Text(
                                text = "Jetpack Glance คืออะไร?\n\n" +
                                       "Glance เป็นโมดูลที่ช่วยให้เราสามารถสร้าง App Widgets ประจำหน้าจอ Home Screen ของ Android ได้ โดยใช้ Syntax ที่คล้ายกับ Jetpack Compose ปกติเลย!\n\n" +
                                       "Concept การทำงาน:\n" +
                                       "1. GlanceAppWidget: คลาสหลักที่เราต้องสืบทอด และ Override ฟังก์ชัน `provideGlance` เพื่อวาด UI (ใช้ Glance Composable เช่น GlanceText แทน Text ปกติ)\n" +
                                       "2. GlanceAppWidgetReceiver: ทำหน้าที่เป็นตัวรับ Event จากหน้าจอ Home Screen (Broadcast Receiver) เพื่อบอกว่า Widget ควรถูกอัปเดตหรือเปิดใช้งาน\n" +
                                       "3. Declaration ใน XML: ถึงแม้เราจะเขียนโค้ดด้วย Kotlin แต่ตัวระบบ Android ก็ยังต้องการการประกาศ Widget ใน res/xml แบบดั้งเดิม และใส่ Receiver ใน AndroidManifest.xml\n\n" +
                                       "ลองกดปุ่มโฮมออกไป แล้วกดค้างที่หน้าจอเพื่อเพิ่ม Widget ชื่อ 'LearnAndroid Widget' ดูสิครับ!",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- 2. คลาส Widget (GlanceAppWidget) ---
class MyGlanceWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            // สังเกตว่าเราใช้คำสั่งของ Glance (เช่น GlanceModifier, GlanceText) แทน Compose ปกติ
            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(ColorProvider(android.graphics.Color.DKGRAY))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GlanceText(
                    text = "Hello Glance!",
                    style = TextStyle(
                        color = ColorProvider(android.graphics.Color.WHITE),
                        fontWeight = FontWeight.Bold
                    )
                )
                GlanceText(
                    text = "Widget สร้างด้วย Compose",
                    style = TextStyle(color = ColorProvider(android.graphics.Color.LTGRAY))
                )
            }
        }
    }
}

// --- 3. Receiver สำหรับรอรับคำสั่งให้ Widget ทำงาน ---
class MyGlanceWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = MyGlanceWidget()
}
package com.example.a526lablearnandroid

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.a526lablearnandroid.ui.theme._526LabLearnAndroidTheme
import kotlinx.coroutines.delay

class PartEleven : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            _526LabLearnAndroidTheme {
                SkeletonExampleScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkeletonExampleScreen() {
    val context = LocalContext.current as? Activity
    var isLoading by remember { mutableStateOf(true) }

    // จำลองการโหลดข้อมูล 4 วินาที และจะสลับไปโชว์ข้อมูลจริง
    LaunchedEffect(Unit) {
        delay(4000)
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Skeleton Loading") },
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
                    text = "Concept ของ Skeleton Loading:\n\n" +
                           "Skeleton Loading หรือ Shimmer Effect คือเทคนิคการแสดงผลโครงสร้าง UI เปล่าๆ (มักจะเป็นแถบสีเทาที่มีแอนิเมชันเงาวิ่งผ่าน) ในระหว่างที่แอปกำลังดึงข้อมูลจาก Server\n\n" +
                           "หลักการทำงานใน Compose:\n" +
                           "1. สร้าง `Modifier` พิเศษ (ผมสร้างฟังก์ชัน .shimmerEffect() ไว้ด้านล่าง) ที่ใช้ `rememberInfiniteTransition` ร่วมกับ `Brush.linearGradient` เพื่อทำแอนิเมชันให้แถบสีเลื่อนไปมาอย่างต่อเนื่อง\n" +
                           "2. นำ Modifier ดังกล่าวไปแปะลงบน Component ตัวเปล่าอย่างเช่นเปลือก `Box` เพื่อจัดเรียง Layout จำลองให้เหมือนโครงสร้างเนื้อหาจริง\n" +
                           "3. ผูกเงื่อนไข `if (isLoading)` เอาไว้ควบคุม เมื่อโหลดข้อมูลเสร็จก็จะ Recompose อัปเดตสลับตัว Component ภายในเป็นหน้าเนื้อหาของจริง\n\n" +
                           "ข้อดี:\n" +
                           "ช่วยในเรื่องการรับรู้ของผู้ใช้ (Perceived Performance) เพราะมันให้ความรู้สึกว่ากำลังดาวน์โหลดไวกว่าการทิ้งหน้าจอขาวงงๆ หรือโชว์เพียงแค่ลูกข่าง Loading หมุนๆ ตรงกลางจอ",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(text = "ตัวอย่างการโหลด:", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))
            }

            // จำลองลูปแสดงรายการ 5 รายการ
            items(5) {
                if (isLoading) {
                    SkeletonItem()
                } else {
                    LoadedItem()
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

// ---------------------------------------------
// Component หลอกที่เอาไว้โชว์ตอนข้อมูลยังไม่มา
// ---------------------------------------------
@Composable
fun SkeletonItem() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        // กล่องรูปโปรไฟล์จำลอง
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .shimmerEffect()
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            // กล่องบรรทัดแรกจำลอง
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(20.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmerEffect()
            )
            Spacer(modifier = Modifier.height(8.dp))
            // กล่องบรรทัดที่สองจำลอง
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(20.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmerEffect()
            )
        }
    }
}

// ---------------------------------------------
// Component ของมีอยู่จริงที่จะโชว์เมื่อโหลดข้อมูลจบ
// ---------------------------------------------
@Composable
fun LoadedItem() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(Color(0xFF3F51B5)),
            contentAlignment = Alignment.Center
        ) {
            Text("IMG", color = Color.White)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "ชื่อผู้ใช้งาน แบบโหลดเสร็จ", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "รายละเอียดข้อมูลส่วนตัวเบื้องต้น...", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        }
    }
}

// ---------------------------------------------
// กลไกหลักที่ใช้พ่นสี Shimmer (ประกายไฟจำลอง)
// ---------------------------------------------
fun Modifier.shimmerEffect(): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "shimmer_transition")
    val translateAnim by transition.animateFloat(
        initialValue = -500f,
        targetValue = 1500f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_anim"
    )

    background(
        brush = Brush.linearGradient(
            colors = listOf(
                Color.LightGray.copy(alpha = 0.6f),
                Color.LightGray.copy(alpha = 0.2f), // ส่วนนี้คือเส้นขาวๆ ตรงกลางที่วิ่งพาดไปมาระหว่างเกรเดียนท์
                Color.LightGray.copy(alpha = 0.6f)
            ),
            start = Offset(translateAnim, translateAnim),
            end = Offset(translateAnim + 200f, translateAnim + 200f) // ทำองศาเอียง 45 องศา เลื่อนพาดผ่านกล่อง
        )
    )
}
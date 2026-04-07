package com.example.a526lablearnandroid

import android.app.Activity
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
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.a526lablearnandroid.ui.theme._526LabLearnAndroidTheme

class PartNine : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            _526LabLearnAndroidTheme {
                CollapsingExampleScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollapsingExampleScreen() {
    val context = LocalContext.current as? Activity
    
    // 1. สร้าง State สำหรับควบคุมการย่อขยาย AppBar (ScrollBehavior)
    // exitUntilCollapsed จะค่อยๆ ย่อจนเหลือขนาดปกติของ TopAppBar
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        // 2. จัดการเรื่อง Nested Scroll เพื่อให้ Scroll จากเนื้อหาควบคุม AppBar ได้
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            // ใช้ LargeTopAppBar ซึ่งสามารถย่อขนาดให้เล็กลงเวลาผู้ใช้ Scroll ลง
            LargeTopAppBar(
                title = { Text("Collapsing TopAppBar") },
                navigationIcon = {
                    IconButton(onClick = { context?.finish() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            item {
                Text(
                    text = "Concept ของ Collapsing Layout \n\n" +
                           "Collapsing TopAppBar คือรูปแบบ UI ที่หน้าแถบด้านบน (App Bar) สามารถ \"ย่อขนาด\" หรือ \"ขยายขนาด\" ได้โดยอัตโนมัติเมื่อผู้ใช้ทำการเลื่อน (Scroll) ดูเนื้อหาบนหน้าจอ\n\n" +
                           "หลักการทำงานใน Jetpack Compose:\n" +
                           "1. ScrollBehavior: เป็นหัวใจสำคัญ ใช้ `TopAppBarDefaults.exitUntilCollapsedScrollBehavior()` (หรือโหมดอื่นๆ เช่น enterAlways, pinned) เพื่อสร้างพฤติกรรมนี้\n" +
                           "2. Modifier.nestedScroll: เราต้องส่งผ่าน `scrollBehavior.nestedScrollConnection` ไปยัง Modifier ของ `Scaffold` หน้าที่ของมันคือทำให้ Component ที่เลื่อนได้ (เช่น LazyColumn) สามารถรายงานระยะการเลื่อนไปให้ App Bar รับรู้\n" +
                           "3. LargeTopAppBar: รองรับฟังก์ชันการหดตัว เมื่อถูกไถขึ้น มันจะหดกลายเป็น TopAppBar ขนาดธรรมดา\n\n" +
                           "ประโยชนที่ได้:\n" +
                           "- ประหยัดพื้นที่หน้าจอ เพิ่ม Focus ให้เนื้อหา\n" +
                           "- ได้ UI ที่ลื่นไหลและดูทันสมัยมากขึ้น",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
            
            // ใส่ Content หลอกๆ เพื่อให้เกิด Scroll ได้
            items(20) { index ->
                Text(
                    text = "เนื้อหาจำลองลำดับที่ $index (ลองเลื่อนจอขึ้นดู)",
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }
        }
    }
}
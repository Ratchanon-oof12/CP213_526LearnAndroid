package com.example.a526lablearnandroid

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.a526lablearnandroid.ui.theme._526LabLearnAndroidTheme
import kotlinx.coroutines.launch

class Part12Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            _526LabLearnAndroidTheme {
                DialogsExampleScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogsExampleScreen() {
    val context = LocalContext.current as? Activity
    
    // State สำหรับควบคุมการแสดงผลของ Modal Bottom Sheet และ Dialog
    var showBottomSheet by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    
    // State สำหรับ ModalBottomSheet
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dialogs & Bottom Sheet") },
                navigationIcon = {
                    IconButton(onClick = { context?.finish() }) {
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
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "การแสดงผลแบบ Pop-up Overlay",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Middle Dialog (AlertDialog):\n" +
                       "คือหน้าต่างแจ้งเตือนที่เด้งขึ้นมาตรง 'กลางหน้าจอ' มักจะใช้สำหรับให้ผู้ใช้ยืนยันการกระทำบางอย่าง (เช่น ยืนยันการลบข้อมูล) บังคับให้ผู้ใช้ต้องเลือกหรือปิดก่อน ถึงจะทำอะไรกับหน้าหลักต่อได้",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { showDialog = true }) {
                Text("Show Middle Dialog")
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Text(
                text = "Modal Bottom Sheet:\n" +
                       "คือแผ่น UI ที่เลื่อนหรือเด้งขึ้นมาจาก 'ด้านล่างของหน้าจอ' มักใช้สำหรับแสดงเมนูย่อย, ตัวเลือกเสริม, หรือแชร์ข้อมูล ปัจจุบันนิยมใช้มากกว่า Dialog แบบเก่าเพราะสามารถใช้นิ้วปัด (Swipe) เพื่อปิด และดูเข้ากับการใช้งานมือถือมากยิ่งขึ้น",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { showBottomSheet = true }) {
                Text("Show Bottom Sheet")
            }
        }
    }

    // --- ส่วนแสดงผล Middle Dialog ---
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("ข้อความเตือน (Middle Dialog)") },
            text = { Text("นี่คือตัวอย่าง AlertDialog ที่บังคับให้ผู้ใช้ต้องตอบสนอง คุณสามารถกดยกเลิกหรือตกลงก็ได้ หรือกดพื้นผิวด้านนอกเพื่อปิดรบตัวด้วย `onDismissRequest`") },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("ตกลง")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("ยกเลิก")
                }
            }
        )
    }

    // --- ส่วนแสดงผล Modal Bottom Sheet ---
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {
            // เนื้อหาล่างแผ่นที่เด้งขึ้นมา
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(bottom = 32.dp) // เพื่อเว้นระยะจากขอบจอด้านล่างเล็กน้อยกันหน้าจอทับ
            ) {
                Text(
                    text = "เนื้อหาของ Modal Bottom Sheet",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "ลองใช้นิ้วของคุณปัดแผงนี้ลงไปด้านล่างดูสิ! ตัวแปร state ด้านหลังจะโดนอัปเดตเป็น false อัตโนมัติเมื่อปัดแผงจนหายพ้นขอบจอไป",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(24.dp))
                
                // ปุ่มสำหรับสั่งปิดแผ่นด้วยโค้ด (Programmatically)
                Button(
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showBottomSheet = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("ซ่อนแผงนี้ผ่านคลิก (Hide Sheet)")
                }
            }
        }
    }
}

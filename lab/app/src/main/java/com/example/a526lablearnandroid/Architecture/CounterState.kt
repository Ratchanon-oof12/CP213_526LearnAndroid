package com.example.a526lablearnandroid.Architecture

/**
 * Model หรือ State
 * ใช้ Data Class ในการเก็บสถานะทั้งหมดของหน้าจอนี้
 * ในที่นี้คือเก็บแค่ตัวเลขนับ (count)
 */
data class CounterState(
    val count: Int = 0 // ค่าเริ่มต้นคือ 0
)
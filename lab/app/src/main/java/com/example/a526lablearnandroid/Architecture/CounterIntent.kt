package com.example.a526lablearnandroid.Architecture

/**
 * Intent (ไม่ใช่ Intent ของ Android)
 * คือการกระทำหรือความตั้งใจของผู้ใช้ที่เรากำหนดขึ้นมา
 * ใช้ Sealed Class เพื่อจำกัดว่ามี Action อะไรได้บ้าง
 */
sealed class CounterIntent {
    // ในตัวอย่างนี้มีแค่ Intent เดียวคือการกดปุ่มเพิ่มค่า
    object IncrementCounter : CounterIntent()
}
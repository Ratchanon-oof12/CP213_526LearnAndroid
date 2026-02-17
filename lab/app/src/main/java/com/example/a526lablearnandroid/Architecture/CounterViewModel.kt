package com.example.a526lablearnandroid.Architecture

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel: ตัวกลางที่รับ Intent และสร้าง State ใหม่
 */
class CounterViewModel : ViewModel() {

    // สร้าง StateFlow สำหรับเก็บ State ปัจจุบัน (private, แก้ไขได้ภายใน)
    private val _state = MutableStateFlow(CounterState())

    // เปิด StateFlow ให้อ่านได้อย่างเดียว (public) เพื่อให้ View นำไป Observe
    val state: StateFlow<CounterState> = _state

    /**
     * ฟังก์ชันหลักสำหรับรับ Intent จาก View
     */
    fun processIntent(intent: CounterIntent) {
        viewModelScope.launch {
            when (intent) {
                is CounterIntent.IncrementCounter -> {
                    incrementCounter()
                }
            }
        }
    }

    /**
     * Logic การบวกเลข
     * สร้าง State ใหม่โดยการ copy ของเก่าแล้วบวกค่าเพิ่มเข้าไป
     */
    private fun incrementCounter() {
        val currentState = _state.value
        _state.value = currentState.copy(count = currentState.count + 1)
    }
}
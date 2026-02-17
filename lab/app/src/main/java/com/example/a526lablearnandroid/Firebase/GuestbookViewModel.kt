package com.example.a526lablearnandroid.Firebase

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class GuestbookViewModel : ViewModel() {

    private val _messages = MutableStateFlow<List<GuestMessage>>(emptyList())
    val messages = _messages.asStateFlow()

    init {
        // ทันทีที่เปิดหน้านี้ ให้เริ่ม "ฟัง" ข้อมูลจาก Firebase
        FirestoreHelper.listenToMessages { updatedList ->
            _messages.value = updatedList
        }
    }

    fun sendMessage(name: String, text: String) {
        if (name.isNotBlank() && text.isNotBlank()) {
            FirestoreHelper.addMessage(name, text) {
                // ทำอะไรต่อหลังส่งเสร็จไหม? (เช่น เคลียร์ช่องพิมพ์)
            }
        }
    }

    fun deleteMessage(id: String) {
        FirestoreHelper.deleteMessage(id)
    }
}
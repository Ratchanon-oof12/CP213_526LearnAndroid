package com.example.a526lablearnandroid.util

import android.content.Context
import android.content.SharedPreferences
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations

class SharedPreferencesUtilTest {

    @Mock
    private lateinit var mockContext: Context
    @Mock
    private lateinit var mockPrefs: SharedPreferences
    @Mock
    private lateinit var mockEditor: SharedPreferences.Editor

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        
        `when`(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockPrefs)
        `when`(mockPrefs.edit()).thenReturn(mockEditor)
        `when`(mockEditor.putString(anyString(), anyString())).thenReturn(mockEditor)
        `when`(mockEditor.putBoolean(anyString(), org.mockito.ArgumentMatchers.anyBoolean())).thenReturn(mockEditor)
        
        SharedPreferencesUtil.init(mockContext)
    }

    @Test
    fun `test save and get string success`() {
        val key = "test_key"
        val value = "test_value"
        
        // Mock พฤติกรรมตอน Get
        `when`(mockPrefs.getString(key, "")).thenReturn(value)

        SharedPreferencesUtil.saveString(key, value)
        val result = SharedPreferencesUtil.getString(key)

        // Success Case: ค่าที่ได้ต้องตรงกับที่บันทึก
        assertEquals(value, result)
    }

    @Test
    fun `test get string fail or mismatch`() {
        val key = "user_name"
        val realValue = "John Doe"
        val expectedWrongValue = "Jane Doe"

        `when`(mockPrefs.getString(key, "")).thenReturn(realValue)

        val result = SharedPreferencesUtil.getString(key)

        // Fail Scenario: ตรวจสอบว่าค่าไม่เท่ากับตัวที่เราไม่ได้คาดหวัง
        assertNotEquals(expectedWrongValue, result)
    }
}

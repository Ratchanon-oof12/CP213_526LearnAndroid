package com.example.a526lablearnandroid

import com.example.a526lablearnandroid.Architecture.CounterIntent
import com.example.a526lablearnandroid.Architecture.CounterViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CounterViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: CounterViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = CounterViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test increment counter success`() = runTest {
        // Given: เริ่มต้นด้วย count = 0
        assertEquals(0, viewModel.state.value.count)

        // When: ส่ง Intent IncrementCounter
        viewModel.processIntent(CounterIntent.IncrementCounter)
        advanceUntilIdle() // รอให้ Coroutine ทำงานเสร็จ

        // Then: count ควรจะเป็น 1 (Success Case)
        assertEquals(1, viewModel.state.value.count)
    }

    @Test
    fun `test increment counter fail`() = runTest {
        // Given: เริ่มต้นด้วย count = 0
        assertEquals(0, viewModel.state.value.count)

        // When: ส่ง Intent IncrementCounter
        viewModel.processIntent(CounterIntent.IncrementCounter)
        advanceUntilIdle()

        // Then: สมมติว่าเราคาดหวังผลลัพธ์ที่ผิด (Fail Case Scenario)
        // ในการทดสอบจริง เราจะเช็คว่ามัน "ไม่เท่ากับ" ค่าที่ผิด
        assertNotEquals(0, viewModel.state.value.count)
        
        // หรือถ้าอยากให้ Test นี้ Fail จริงๆ ให้แก้เลข 1 เป็นเลขอื่น เช่น 5
        // assertEquals(5, viewModel.state.value.count) 
    }
}

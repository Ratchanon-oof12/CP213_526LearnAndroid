package com.example.financialassistant

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.financialassistant.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class FinancialViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repo = FinancialRepository(db.transactionDao(), db.categoryDao())

    // Current selected month/year for filters
    private val _selectedYear = MutableStateFlow(SimpleDateFormat("yyyy", Locale.getDefault()).format(Date()))
    private val _selectedMonth = MutableStateFlow(SimpleDateFormat("MM", Locale.getDefault()).format(Date()))
    val selectedYear: StateFlow<String> = _selectedYear
    val selectedMonth: StateFlow<String> = _selectedMonth

    val selectedYearMonth: StateFlow<String> = combine(_selectedYear, _selectedMonth) { y, m -> "$y-$m" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), currentYearMonth())

    // All data flows
    val allTransactions: StateFlow<List<Transaction>> = repo.getAllTransactions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val monthlyTransactions: StateFlow<List<Transaction>> = combine(_selectedYear, _selectedMonth) { y, m -> Pair(y, m) }
        .flatMapLatest { (y, m) -> repo.getTransactionsByMonth(y, m) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories: StateFlow<List<Category>> = repo.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Chart data: Pie chart — category breakdown
    val categoryExpenseSummary: StateFlow<List<CategorySummary>> = combine(_selectedYear, _selectedMonth) { y, m -> Pair(y, m) }
        .flatMapLatest { (y, m) -> repo.getCategoryExpenseSummary(y, m) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Chart data: Bar chart — daily spending this month
    val dailyExpenseSummary: StateFlow<List<DaySummary>> = combine(_selectedYear, _selectedMonth) { y, m -> Pair(y, m) }
        .flatMapLatest { (y, m) -> repo.getDailyExpenseSummary(y, m) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Chart data: Bar chart — monthly spending (last 12 months)
    val monthlyExpenseSummary: StateFlow<List<MonthSummary>> = repo.getMonthlyExpenseSummary()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Monthly totals for summary card
    val monthlyIncome: StateFlow<Double> = selectedYearMonth
        .flatMapLatest { repo.getMonthlyIncome(it) }
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val monthlyExpense: StateFlow<Double> = selectedYearMonth
        .flatMapLatest { repo.getMonthlyExpense(it) }
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Actions
    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch { repo.insertTransaction(transaction) }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch { repo.deleteTransaction(transaction) }
    }

    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch { repo.insertTransaction(transaction) } // REPLACE strategy handles update
    }

    fun addCategory(category: Category) {
        viewModelScope.launch { repo.insertCategory(category) }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch { repo.deleteCategory(category) }
    }

    fun selectMonth(year: String, month: String) {
        _selectedYear.value = year
        _selectedMonth.value = month
    }

    private fun currentYearMonth(): String {
        val sdf = SimpleDateFormat("yyyy-MM", Locale.getDefault())
        return sdf.format(Date())
    }
}

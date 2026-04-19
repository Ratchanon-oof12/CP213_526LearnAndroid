package com.example.financialassistant.data

import kotlinx.coroutines.flow.Flow

class FinancialRepository(
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao
) {
    // Transactions
    fun getAllTransactions(): Flow<List<Transaction>> = transactionDao.getAllTransactions()
    fun getTransactionsByMonth(year: String, month: String) = transactionDao.getTransactionsByMonth(year, month)
    fun getDailyExpenseSummary(year: String, month: String) = transactionDao.getDailyExpenseSummary(year, month)
    fun getMonthlyExpenseSummary() = transactionDao.getMonthlyExpenseSummary()
    fun getCategoryExpenseSummary(year: String, month: String) = transactionDao.getCategoryExpenseSummary(year, month)
    fun getMonthlyIncome(yearMonth: String) = transactionDao.getMonthlyIncome(yearMonth)
    fun getMonthlyExpense(yearMonth: String) = transactionDao.getMonthlyExpense(yearMonth)

    suspend fun insertTransaction(transaction: Transaction) = transactionDao.insertTransaction(transaction)
    suspend fun deleteTransaction(transaction: Transaction) = transactionDao.deleteTransaction(transaction)

    // Categories
    fun getAllCategories(): Flow<List<Category>> = categoryDao.getAllCategories()
    suspend fun insertCategory(category: Category) = categoryDao.insertCategory(category)
    suspend fun updateCategory(category: Category) = categoryDao.updateCategory(category)
    suspend fun deleteCategory(category: Category) = categoryDao.deleteCategory(category)
}

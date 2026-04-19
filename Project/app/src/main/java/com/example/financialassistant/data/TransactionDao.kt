package com.example.financialassistant.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Query("""
        SELECT * FROM transactions 
        WHERE strftime('%Y', date/1000, 'unixepoch') = :year 
          AND strftime('%m', date/1000, 'unixepoch') = :month
        ORDER BY date DESC
    """)
    fun getTransactionsByMonth(year: String, month: String): Flow<List<Transaction>>

    @Query("""
        SELECT strftime('%d', date/1000, 'unixepoch') as day, SUM(amount) as total
        FROM transactions
        WHERE type = 'EXPENSE'
          AND strftime('%Y', date/1000, 'unixepoch') = :year 
          AND strftime('%m', date/1000, 'unixepoch') = :month
        GROUP BY day
        ORDER BY day ASC
    """)
    fun getDailyExpenseSummary(year: String, month: String): Flow<List<DaySummary>>

    @Query("""
        SELECT strftime('%Y-%m', date/1000, 'unixepoch') as month, SUM(amount) as total
        FROM transactions
        WHERE type = 'EXPENSE'
        GROUP BY month
        ORDER BY month ASC
        LIMIT 12
    """)
    fun getMonthlyExpenseSummary(): Flow<List<MonthSummary>>

    @Query("""
        SELECT categoryName, SUM(amount) as total
        FROM transactions
        WHERE type = 'EXPENSE'
          AND strftime('%Y', date/1000, 'unixepoch') = :year 
          AND strftime('%m', date/1000, 'unixepoch') = :month
        GROUP BY categoryName
        ORDER BY total DESC
    """)
    fun getCategoryExpenseSummary(year: String, month: String): Flow<List<CategorySummary>>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'INCOME' AND strftime('%Y-%m', date/1000, 'unixepoch') = :yearMonth")
    fun getMonthlyIncome(yearMonth: String): Flow<Double?>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'EXPENSE' AND strftime('%Y-%m', date/1000, 'unixepoch') = :yearMonth")
    fun getMonthlyExpense(yearMonth: String): Flow<Double?>
}

data class DaySummary(val day: String, val total: Double)
data class MonthSummary(val month: String, val total: Double)
data class CategorySummary(val categoryName: String, val total: Double)

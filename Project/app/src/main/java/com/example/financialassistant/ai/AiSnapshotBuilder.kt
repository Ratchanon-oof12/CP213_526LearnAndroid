package com.example.financialassistant.ai

import com.example.financialassistant.data.CategorySummary
import com.example.financialassistant.data.DaySummary
import kotlin.math.roundToInt

object AiSnapshotBuilder {
    fun build(
        yearMonth: String,
        incomeTotal: Double,
        expenseTotal: Double,
        categoryExpenseSummary: List<CategorySummary>,
        dailyExpenseSummary: List<DaySummary>
    ): FinancialSnapshot {
        val topCats = categoryExpenseSummary
            .sortedByDescending { it.total }
            .take(5)
            .map { CategorySpend(categoryName = it.categoryName, total = it.total) }

        val days = dailyExpenseSummary
            .sortedBy { it.day.toIntOrNull() ?: Int.MAX_VALUE }
            .takeLast(14) // keep payload small; still useful for trend language
            .map { DaySpend(day = it.day.padStart(2, '0'), total = it.total) }

        // keep numbers stable (avoid sending huge decimals)
        fun Double.money2(): Double = ((this * 100.0).roundToInt() / 100.0)

        return FinancialSnapshot(
            yearMonth = yearMonth,
            incomeTotal = incomeTotal.money2(),
            expenseTotal = expenseTotal.money2(),
            topExpenseCategories = topCats.map { it.copy(total = it.total.money2()) },
            dailyExpenses = days.map { it.copy(total = it.total.money2()) }
        )
    }
}


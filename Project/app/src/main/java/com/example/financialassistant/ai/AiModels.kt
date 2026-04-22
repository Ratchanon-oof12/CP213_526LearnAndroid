package com.example.financialassistant.ai

data class FinancialSnapshot(
    val yearMonth: String,
    val incomeTotal: Double,
    val expenseTotal: Double,
    val topExpenseCategories: List<CategorySpend>,
    val dailyExpenses: List<DaySpend>
)

data class CategorySpend(
    val categoryName: String,
    val total: Double
)

data class DaySpend(
    val day: String,
    val total: Double
)

data class AiInsightsRequest(
    val kind: String, // "welcome" | "analytics"
    val userName: String,
    val assistantName: String,
    val snapshot: FinancialSnapshot,
    val extraContext: String = ""
)

data class AiInsightsResponse(
    val title: String,
    val summary: String,
    val suggestions: List<String>
)


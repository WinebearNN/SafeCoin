package com.safecoin.safecoin.domain.model

data class CategoryBreakdown(
    val category: String,
    val amount: Double,
    val percentage: Float,
)

data class MonthlyFlow(
    val monthLabel: String,
    val income: Double,
    val expense: Double,
)

data class AnalyticsSummary(
    val totalIncome: Double,
    val totalExpense: Double,
    val netBalance: Double,
    val categoryBreakdown: List<CategoryBreakdown>,
    val monthlyFlows: List<MonthlyFlow>,
)

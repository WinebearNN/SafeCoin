package com.safecoin.safecoin.data.ai

import com.safecoin.safecoin.domain.model.AnalyticsSummary
import com.safecoin.safecoin.domain.model.AnalysisSkill
import com.safecoin.safecoin.domain.model.Transaction
import com.safecoin.safecoin.domain.model.TransactionType
import java.text.NumberFormat
import java.util.Locale

object FallbackAnalysisGenerator {

    private val currency = NumberFormat.getCurrencyInstance(Locale.US)

    fun generate(
        skill: AnalysisSkill,
        summary: AnalyticsSummary,
        transactions: List<Transaction>,
    ): String = when (skill) {
        AnalysisSkill.TRANSACTION_REPORT -> fullReport(summary, transactions)
        AnalysisSkill.CATEGORY_BREAKDOWN -> categoryReport(summary)
        AnalysisSkill.MONTHLY_TRENDS -> monthlyReport(summary)
    }

    private fun fullReport(summary: AnalyticsSummary, transactions: List<Transaction>): String {
        val deposits = transactions.count { it.type == TransactionType.DEPOSIT }
        val withdrawals = transactions.count { it.type == TransactionType.WITHDRAW }
        val topCategory = summary.categoryBreakdown.firstOrNull()

        return buildString {
            appendLine("📊 SafeCoin Analysis (offline summary)")
            appendLine()
            appendLine("Executive summary")
            appendLine(
                "You recorded $deposits deposits and $withdrawals withdrawals. " +
                    "Net cash flow is ${currency.format(summary.netBalance)}.",
            )
            appendLine()
            appendLine("Totals")
            appendLine("• Income: ${currency.format(summary.totalIncome)}")
            appendLine("• Expenses: ${currency.format(summary.totalExpense)}")
            appendLine("• Net: ${currency.format(summary.netBalance)}")
            appendLine()
            appendLine("Top spending categories")
            summary.categoryBreakdown.take(5).forEach { item ->
                appendLine(
                    "• ${item.category}: ${currency.format(item.amount)} " +
                        "(${"%.0f".format(item.percentage)}%)",
                )
            }
            if (topCategory != null) {
                appendLine()
                appendLine(
                    "Largest category: ${topCategory.category} at " +
                        "${currency.format(topCategory.amount)}.",
                )
            }
            appendLine()
            appendLine("Tips")
            appendLine("• Review recurring ${topCategory?.category ?: "expense"} charges.")
            appendLine("• Set a weekly spending cap for discretionary categories.")
            appendLine("• Move surplus to savings when net flow is positive.")
            appendLine()
            appendLine(
                "Tip: Configure Yandex GPT in ai_config.properties (project root) " +
                    "or Profile → AI settings.",
            )
        }
    }

    private fun categoryReport(summary: AnalyticsSummary): String = buildString {
        appendLine("Category breakdown")
        summary.categoryBreakdown.forEach { item ->
            appendLine(
                "• ${item.category}: ${currency.format(item.amount)} " +
                    "(${"%.1f".format(item.percentage)}%)",
            )
        }
    }

    private fun monthlyReport(summary: AnalyticsSummary): String = buildString {
        appendLine("Monthly trends")
        summary.monthlyFlows.forEach { flow ->
            val net = flow.income - flow.expense
            appendLine(
                "• ${flow.monthLabel}: income ${currency.format(flow.income)}, " +
                    "expense ${currency.format(flow.expense)}, net ${currency.format(net)}",
            )
        }
    }
}

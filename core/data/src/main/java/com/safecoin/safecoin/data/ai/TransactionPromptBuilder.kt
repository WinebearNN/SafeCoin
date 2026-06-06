package com.safecoin.safecoin.data.ai

import com.safecoin.safecoin.domain.model.AnalyticsSummary
import com.safecoin.safecoin.domain.model.AnalysisSkill
import com.safecoin.safecoin.domain.model.BankAccount
import com.safecoin.safecoin.domain.model.Transaction
import com.safecoin.safecoin.domain.model.TransactionType
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TransactionPromptBuilder {

    private val currency = NumberFormat.getCurrencyInstance(Locale.US)
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    fun buildSystemPrompt(skill: AnalysisSkill): String = skill.systemPrompt

    fun buildUserMessage(
        summary: AnalyticsSummary,
        transactions: List<Transaction>,
        accounts: List<BankAccount>,
    ): String = buildString {
        appendLine("=== USER FINANCIAL DATA ===")
        appendLine()
        appendLine("Accounts:")
        accounts.forEach { account ->
            appendLine(
                "- ${account.name} (${account.currency}): balance ${currency.format(account.balance)}",
            )
        }
        appendLine()
        appendLine("Summary:")
        appendLine("- Total income: ${currency.format(summary.totalIncome)}")
        appendLine("- Total expenses: ${currency.format(summary.totalExpense)}")
        appendLine("- Net: ${currency.format(summary.netBalance)}")
        appendLine()
        appendLine("Expenses by category:")
        if (summary.categoryBreakdown.isEmpty()) {
            appendLine("- (none)")
        } else {
            summary.categoryBreakdown.forEach { item ->
                appendLine(
                    "- ${item.category}: ${currency.format(item.amount)} (${"%.1f".format(item.percentage)}%)",
                )
            }
        }
        appendLine()
        appendLine("Monthly flow:")
        summary.monthlyFlows.forEach { flow ->
            appendLine(
                "- ${flow.monthLabel}: income ${currency.format(flow.income)}, " +
                    "expense ${currency.format(flow.expense)}",
            )
        }
        appendLine()
        appendLine("Transactions (${transactions.size} total, newest first):")
        transactions.take(40).forEach { tx ->
            val type = if (tx.type == TransactionType.DEPOSIT) "DEPOSIT" else "WITHDRAW"
            appendLine(
                "- [$type] ${currency.format(tx.amount)} | ${tx.category} | ${tx.description} | " +
                    dateFormat.format(Date(tx.timestamp)),
            )
        }
        if (transactions.size > 40) {
            appendLine("... and ${transactions.size - 40} more transactions omitted.")
        }
        appendLine()
        appendLine("=== END DATA ===")
        appendLine()
        append("Write your analysis now:")
    }
}

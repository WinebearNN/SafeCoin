package com.safecoin.safecoin.data.repository

import com.safecoin.safecoin.data.local.dao.BankAccountDao
import com.safecoin.safecoin.data.local.dao.TransactionDao
import com.safecoin.safecoin.data.local.entity.TransactionEntity
import com.safecoin.safecoin.data.mapper.toDomain
import com.safecoin.safecoin.domain.model.AnalyticsSummary
import com.safecoin.safecoin.domain.model.BankAccount
import com.safecoin.safecoin.domain.model.CategoryBreakdown
import com.safecoin.safecoin.domain.model.MonthlyFlow
import com.safecoin.safecoin.domain.model.Transaction
import com.safecoin.safecoin.domain.model.TransactionType
import com.safecoin.safecoin.domain.repository.FinanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FinanceRepositoryImpl(
    private val accountDao: BankAccountDao,
    private val transactionDao: TransactionDao,
) : FinanceRepository {

    override fun observeAccounts(): Flow<List<BankAccount>> =
        accountDao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeTransactions(limit: Int): Flow<List<Transaction>> =
        transactionDao.observeRecent(limit).map { list -> list.map { it.toDomain() } }

    override fun observeAllTransactions(): Flow<List<Transaction>> =
        transactionDao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun deposit(
        accountId: Long,
        amount: Double,
        description: String,
        category: String,
    ) {
        require(amount > 0) { "Amount must be positive" }
        applyTransaction(accountId, amount, TransactionType.DEPOSIT, description, category)
    }

    override suspend fun withdraw(
        accountId: Long,
        amount: Double,
        description: String,
        category: String,
    ) {
        require(amount > 0) { "Amount must be positive" }
        val account = accountDao.getById(accountId) ?: error("Account not found")
        require(account.balance >= amount) { "Insufficient funds" }
        applyTransaction(accountId, amount, TransactionType.WITHDRAW, description, category)
    }

    private suspend fun applyTransaction(
        accountId: Long,
        amount: Double,
        type: TransactionType,
        description: String,
        category: String,
    ) {
        val account = accountDao.getById(accountId) ?: error("Account not found")
        val delta = if (type == TransactionType.DEPOSIT) amount else -amount
        accountDao.update(account.copy(balance = account.balance + delta))
        transactionDao.insert(
            TransactionEntity(
                accountId = accountId,
                amount = amount,
                type = type.name,
                category = category,
                description = description,
                timestamp = System.currentTimeMillis(),
            ),
        )
    }

    override suspend fun getAnalyticsSummary(): AnalyticsSummary {
        val transactions = transactionDao.observeAll().first().map { it.toDomain() }
        val income = transactions.filter { it.type == TransactionType.DEPOSIT }.sumOf { it.amount }
        val expense = transactions.filter { it.type == TransactionType.WITHDRAW }.sumOf { it.amount }

        val expenseByCategory = transactions
            .filter { it.type == TransactionType.WITHDRAW }
            .groupBy { it.category }
            .mapValues { (_, list) -> list.sumOf { it.amount } }

        val totalExpense = expenseByCategory.values.sum().coerceAtLeast(0.01)
        val categoryBreakdown = expenseByCategory.entries
            .sortedByDescending { it.value }
            .map { (category, amount) ->
                CategoryBreakdown(
                    category = category,
                    amount = amount,
                    percentage = (amount / totalExpense * 100).toFloat(),
                )
            }

        val monthFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())
        val monthlyFlows = transactions
            .groupBy { monthFormat.format(Date(it.timestamp)) }
            .map { (label, list) ->
                MonthlyFlow(
                    monthLabel = label,
                    income = list.filter { it.type == TransactionType.DEPOSIT }.sumOf { it.amount },
                    expense = list.filter { it.type == TransactionType.WITHDRAW }.sumOf { it.amount },
                )
            }
            .sortedBy { it.monthLabel }

        return AnalyticsSummary(
            totalIncome = income,
            totalExpense = expense,
            netBalance = income - expense,
            categoryBreakdown = categoryBreakdown,
            monthlyFlows = monthlyFlows,
        )
    }
}

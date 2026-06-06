package com.safecoin.safecoin.domain.repository

import com.safecoin.safecoin.domain.model.AnalyticsSummary
import com.safecoin.safecoin.domain.model.BankAccount
import com.safecoin.safecoin.domain.model.Transaction
import com.safecoin.safecoin.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow

interface FinanceRepository {
    fun observeAccounts(): Flow<List<BankAccount>>
    fun observeTransactions(limit: Int = 50): Flow<List<Transaction>>
    fun observeAllTransactions(): Flow<List<Transaction>>
    suspend fun deposit(accountId: Long, amount: Double, description: String, category: String)
    suspend fun withdraw(accountId: Long, amount: Double, description: String, category: String)
    suspend fun getAnalyticsSummary(): AnalyticsSummary
}

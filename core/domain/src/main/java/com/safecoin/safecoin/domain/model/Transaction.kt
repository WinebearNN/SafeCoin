package com.safecoin.safecoin.domain.model

data class Transaction(
    val id: Long,
    val accountId: Long,
    val amount: Double,
    val type: TransactionType,
    val category: String,
    val description: String,
    val timestamp: Long,
)

enum class TransactionType {
    DEPOSIT,
    WITHDRAW,
}

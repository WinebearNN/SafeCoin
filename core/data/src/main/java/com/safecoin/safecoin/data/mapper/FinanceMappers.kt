package com.safecoin.safecoin.data.mapper

import com.safecoin.safecoin.data.local.entity.BankAccountEntity
import com.safecoin.safecoin.data.local.entity.TransactionEntity
import com.safecoin.safecoin.domain.model.BankAccount
import com.safecoin.safecoin.domain.model.Transaction
import com.safecoin.safecoin.domain.model.TransactionType

fun BankAccountEntity.toDomain(): BankAccount = BankAccount(
    id = id,
    name = name,
    balance = balance,
    currency = currency,
    cardColorArgb = cardColorArgb,
    lastFourDigits = lastFourDigits,
)

fun TransactionEntity.toDomain(): Transaction = Transaction(
    id = id,
    accountId = accountId,
    amount = amount,
    type = TransactionType.valueOf(type),
    category = category,
    description = description,
    timestamp = timestamp,
)

fun Transaction.toEntity(): TransactionEntity = TransactionEntity(
    id = id,
    accountId = accountId,
    amount = amount,
    type = type.name,
    category = category,
    description = description,
    timestamp = timestamp,
)

package com.safecoin.safecoin.domain.model

data class BankAccount(
    val id: Long,
    val name: String,
    val balance: Double,
    val currency: String,
    val cardColorArgb: Long,
    val lastFourDigits: String,
)

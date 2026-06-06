package com.safecoin.safecoin.data.local

import com.safecoin.safecoin.data.local.dao.BankAccountDao
import com.safecoin.safecoin.data.local.dao.TransactionDao
import com.safecoin.safecoin.data.local.entity.BankAccountEntity
import com.safecoin.safecoin.data.local.entity.TransactionEntity
import com.safecoin.safecoin.domain.model.TransactionType
import java.util.Calendar

class DatabaseSeeder(
    private val accountDao: BankAccountDao,
    private val transactionDao: TransactionDao,
) {
    suspend fun seedIfEmpty() {
        val existing = accountDao.getById(1)
        if (existing != null) return

        val accounts = listOf(
            BankAccountEntity(
                id = 1,
                name = "Main Wallet",
                balance = 18_240.35,
                currency = "USD",
                cardColorArgb = 0xFF2563EB,
                lastFourDigits = "4821",
            ),
            BankAccountEntity(
                id = 2,
                name = "Savings",
                balance = 41_850.00,
                currency = "USD",
                cardColorArgb = 0xFF059669,
                lastFourDigits = "9034",
            ),
            BankAccountEntity(
                id = 3,
                name = "Travel Fund",
                balance = 7_920.50,
                currency = "EUR",
                cardColorArgb = 0xFF7C3AED,
                lastFourDigits = "1178",
            ),
        )
        accountDao.insertAll(accounts)

        val transactions = buildMockTransactions()
        transactionDao.insertAll(transactions)
    }

    private fun buildMockTransactions(): List<TransactionEntity> {
        val list = mutableListOf<TransactionEntity>()
        var id = 1L

        fun add(
            accountId: Long,
            amount: Double,
            type: TransactionType,
            category: String,
            description: String,
            monthsAgo: Int,
            day: Int,
            hour: Int = 12,
        ) {
            list += tx(
                id = id++,
                accountId = accountId,
                amount = amount,
                type = type,
                category = category,
                description = description,
                timestamp = timestampMonthsAgo(monthsAgo, day, hour),
            )
        }

        // ── Current month (0 months ago) ──
        add(1, 3_200.0, TransactionType.DEPOSIT, "Salary", "Monthly salary — May", 0, 1, 9)
        add(1, 89.40, TransactionType.WITHDRAW, "Food", "Supermarket", 0, 2)
        add(1, 42.00, TransactionType.WITHDRAW, "Transport", "Metro pass", 0, 3)
        add(1, 15.90, TransactionType.WITHDRAW, "Food", "Coffee & bakery", 0, 4, 8)
        add(1, 120.00, TransactionType.WITHDRAW, "Utilities", "Electricity bill", 0, 5)
        add(1, 65.00, TransactionType.WITHDRAW, "Subscriptions", "Streaming bundle", 0, 6)
        add(2, 500.0, TransactionType.DEPOSIT, "Transfer", "From main wallet", 0, 7)
        add(1, 240.00, TransactionType.WITHDRAW, "Shopping", "Clothing store", 0, 9)
        add(3, 180.00, TransactionType.WITHDRAW, "Travel", "Flight booking deposit", 0, 10)
        add(1, 55.00, TransactionType.WITHDRAW, "Entertainment", "Concert tickets", 0, 12)
        add(1, 28.50, TransactionType.WITHDRAW, "Food", "Restaurant dinner", 0, 14, 20)

        // ── 1 month ago (April) ──
        add(1, 3_200.0, TransactionType.DEPOSIT, "Salary", "Monthly salary — April", 1, 1, 9)
        add(1, 450.0, TransactionType.WITHDRAW, "Rent", "Apartment rent", 1, 2)
        add(1, 112.30, TransactionType.WITHDRAW, "Food", "Grocery store", 1, 5)
        add(1, 78.00, TransactionType.WITHDRAW, "Health", "Pharmacy", 1, 7)
        add(1, 199.99, TransactionType.WITHDRAW, "Shopping", "Electronics accessory", 1, 9)
        add(2, 1_200.0, TransactionType.DEPOSIT, "Investment", "Bond interest", 1, 10)
        add(1, 36.00, TransactionType.WITHDRAW, "Transport", "Taxi rides", 1, 11)
        add(1, 24.00, TransactionType.WITHDRAW, "Subscriptions", "Cloud storage", 1, 12)
        add(3, 320.0, TransactionType.WITHDRAW, "Travel", "Hotel — weekend trip", 1, 15)
        add(1, 850.0, TransactionType.DEPOSIT, "Freelance", "Design project payment", 1, 18)
        add(1, 95.00, TransactionType.WITHDRAW, "Entertainment", "Cinema & games", 1, 22)

        // ── 2 months ago (March) ──
        add(1, 3_200.0, TransactionType.DEPOSIT, "Salary", "Monthly salary — March", 2, 1, 9)
        add(1, 450.0, TransactionType.WITHDRAW, "Rent", "Apartment rent", 2, 2)
        add(1, 134.80, TransactionType.WITHDRAW, "Food", "Grocery store", 2, 4)
        add(1, 210.00, TransactionType.WITHDRAW, "Education", "Online course", 2, 6)
        add(1, 67.50, TransactionType.WITHDRAW, "Utilities", "Internet + mobile", 2, 8)
        add(1, 145.00, TransactionType.WITHDRAW, "Shopping", "Home supplies", 2, 10)
        add(2, 800.0, TransactionType.DEPOSIT, "Transfer", "Quarterly savings", 2, 12)
        add(1, 41.00, TransactionType.WITHDRAW, "Food", "Lunch delivery", 2, 14)
        add(1, 180.00, TransactionType.WITHDRAW, "Health", "Dental checkup", 2, 16)
        add(3, 95.00, TransactionType.WITHDRAW, "Travel", "Train tickets", 2, 20)
        add(1, 1_500.0, TransactionType.DEPOSIT, "Freelance", "Consulting invoice", 2, 25)

        // ── 3 months ago (February) ──
        add(1, 3_200.0, TransactionType.DEPOSIT, "Salary", "Monthly salary — February", 3, 1, 9)
        add(1, 450.0, TransactionType.WITHDRAW, "Rent", "Apartment rent", 3, 2)
        add(1, 98.20, TransactionType.WITHDRAW, "Food", "Grocery store", 3, 6)
        add(1, 310.00, TransactionType.WITHDRAW, "Shopping", "Winter jacket", 3, 8)
        add(1, 52.00, TransactionType.WITHDRAW, "Transport", "Fuel", 3, 10)
        add(1, 120.00, TransactionType.WITHDRAW, "Utilities", "Heating bill", 3, 12)
        add(2, 2_000.0, TransactionType.DEPOSIT, "Investment", "Stock dividend", 3, 14)
        add(1, 75.00, TransactionType.WITHDRAW, "Entertainment", "Theatre", 3, 16)
        add(1, 33.00, TransactionType.WITHDRAW, "Subscriptions", "Music service", 3, 18)
        add(1, 420.0, TransactionType.DEPOSIT, "Cashback", "Card cashback rewards", 3, 20)
        add(3, 150.0, TransactionType.WITHDRAW, "Travel", "Museum & tours", 3, 22)

        // ── 4 months ago (January) ──
        add(1, 3_200.0, TransactionType.DEPOSIT, "Salary", "Monthly salary — January", 4, 1, 9)
        add(1, 450.0, TransactionType.WITHDRAW, "Rent", "Apartment rent", 4, 2)
        add(1, 156.00, TransactionType.WITHDRAW, "Food", "New Year groceries", 4, 4)
        add(1, 890.00, TransactionType.WITHDRAW, "Shopping", "Holiday gifts", 4, 6)
        add(1, 200.00, TransactionType.WITHDRAW, "Entertainment", "NY party supplies", 4, 8)
        add(1, 88.00, TransactionType.WITHDRAW, "Health", "Gym membership", 4, 10)
        add(2, 1_500.0, TransactionType.DEPOSIT, "Transfer", "Year-start savings", 4, 12)
        add(1, 62.00, TransactionType.WITHDRAW, "Transport", "Airport taxi", 4, 15)
        add(3, 540.0, TransactionType.WITHDRAW, "Travel", "Ski resort booking", 4, 18)
        add(1, 3_500.0, TransactionType.DEPOSIT, "Salary", "Year-end bonus", 4, 20)

        // ── 5 months ago (December) ──
        add(1, 3_200.0, TransactionType.DEPOSIT, "Salary", "Monthly salary — December", 5, 1, 9)
        add(1, 450.0, TransactionType.WITHDRAW, "Rent", "Apartment rent", 5, 2)
        add(1, 178.50, TransactionType.WITHDRAW, "Food", "Holiday dinner prep", 5, 10)
        add(1, 420.00, TransactionType.WITHDRAW, "Shopping", "Electronics — headphones", 5, 12)
        add(1, 95.00, TransactionType.WITHDRAW, "Utilities", "Water bill", 5, 14)
        add(1, 250.00, TransactionType.WITHDRAW, "Education", "Certification exam", 5, 16)
        add(2, 600.0, TransactionType.DEPOSIT, "Investment", "Mutual fund payout", 5, 18)
        add(1, 48.00, TransactionType.WITHDRAW, "Food", "Office lunches", 5, 20)
        add(1, 130.00, TransactionType.WITHDRAW, "Entertainment", "Streaming annual plan", 5, 22)
        add(3, 275.0, TransactionType.WITHDRAW, "Travel", "City break — hotel", 5, 24)
        add(1, 72.00, TransactionType.WITHDRAW, "Health", "Vitamins", 5, 26)
        add(1, 1_100.0, TransactionType.DEPOSIT, "Freelance", "Website maintenance", 5, 28)

        // ── 6 months ago (November) — extra history for charts ──
        add(1, 3_100.0, TransactionType.DEPOSIT, "Salary", "Monthly salary — November", 6, 1, 9)
        add(1, 440.0, TransactionType.WITHDRAW, "Rent", "Apartment rent", 6, 2)
        add(1, 102.00, TransactionType.WITHDRAW, "Food", "Grocery store", 6, 8)
        add(1, 185.00, TransactionType.WITHDRAW, "Shopping", "Office chair", 6, 12)
        add(1, 58.00, TransactionType.WITHDRAW, "Transport", "Bus card", 6, 15)
        add(1, 90.00, TransactionType.WITHDRAW, "Subscriptions", "Software license", 6, 18)
        add(2, 400.0, TransactionType.DEPOSIT, "Transfer", "From main wallet", 6, 20)

        return list
    }

    private fun timestampMonthsAgo(monthsAgo: Int, dayOfMonth: Int, hour: Int): Long {
        val calendar = Calendar.getInstance().apply {
            add(Calendar.MONTH, -monthsAgo)
            set(Calendar.DAY_OF_MONTH, dayOfMonth.coerceIn(1, 28))
            set(Calendar.HOUR_OF_DAY, hour.coerceIn(0, 23))
            set(Calendar.MINUTE, (dayOfMonth * 3 % 60))
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    private fun tx(
        id: Long,
        accountId: Long,
        amount: Double,
        type: TransactionType,
        category: String,
        description: String,
        timestamp: Long,
    ) = TransactionEntity(
        id = id,
        accountId = accountId,
        amount = amount,
        type = type.name,
        category = category,
        description = description,
        timestamp = timestamp,
    )
}

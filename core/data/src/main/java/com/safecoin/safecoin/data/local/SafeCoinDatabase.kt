package com.safecoin.safecoin.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.safecoin.safecoin.data.local.dao.BankAccountDao
import com.safecoin.safecoin.data.local.dao.TransactionDao
import com.safecoin.safecoin.data.local.entity.BankAccountEntity
import com.safecoin.safecoin.data.local.entity.TransactionEntity

@Database(
    entities = [BankAccountEntity::class, TransactionEntity::class],
    version = 2,
    exportSchema = false,
)
abstract class SafeCoinDatabase : RoomDatabase() {
    abstract fun bankAccountDao(): BankAccountDao
    abstract fun transactionDao(): TransactionDao
}

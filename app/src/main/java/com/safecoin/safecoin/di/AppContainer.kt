package com.safecoin.safecoin.di

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.safecoin.safecoin.data.export.CsvReportExporter
import com.safecoin.safecoin.data.local.DatabaseSeeder
import com.safecoin.safecoin.data.local.SafeCoinDatabase
import com.safecoin.safecoin.data.repository.AiAnalysisRepositoryImpl
import com.safecoin.safecoin.data.repository.FinanceRepositoryImpl
import com.safecoin.safecoin.data.repository.SettingsRepositoryImpl
import com.safecoin.safecoin.data.repository.YandexConfigRepositoryImpl
import com.safecoin.safecoin.domain.repository.AiAnalysisRepository
import com.safecoin.safecoin.domain.repository.FinanceRepository
import com.safecoin.safecoin.domain.repository.ReportExporter
import com.safecoin.safecoin.domain.repository.SettingsRepository
import com.safecoin.safecoin.domain.repository.YandexConfigRepository
import com.safecoin.safecoin.presentation.SafeCoinDependencies
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

private val Context.dataStore by preferencesDataStore(name = "safecoin_settings")

class AppContainer(context: Context) : SafeCoinDependencies {
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val database: SafeCoinDatabase = Room.databaseBuilder(
        context,
        SafeCoinDatabase::class.java,
        "safecoin.db",
    )
        .fallbackToDestructiveMigration(dropAllTables = true)
        .build()

    override val financeRepository: FinanceRepository = FinanceRepositoryImpl(
        accountDao = database.bankAccountDao(),
        transactionDao = database.transactionDao(),
    )

    override val settingsRepository: SettingsRepository = SettingsRepositoryImpl(
        dataStore = context.dataStore,
    )

    override val reportExporter: ReportExporter = CsvReportExporter(context)

    override val yandexConfigRepository: YandexConfigRepository = YandexConfigRepositoryImpl(
        dataStore = context.dataStore,
    )

    override val aiAnalysisRepository: AiAnalysisRepository = AiAnalysisRepositoryImpl(
        financeRepository = financeRepository,
        yandexConfigRepository = yandexConfigRepository,
    )

    init {
        appScope.launch {
            DatabaseSeeder(
                accountDao = database.bankAccountDao(),
                transactionDao = database.transactionDao(),
            ).seedIfEmpty()
            aiAnalysisRepository.refreshServiceStatus()
        }
    }
}

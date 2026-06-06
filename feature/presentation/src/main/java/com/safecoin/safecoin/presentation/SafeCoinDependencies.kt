package com.safecoin.safecoin.presentation

import com.safecoin.safecoin.domain.repository.AiAnalysisRepository
import com.safecoin.safecoin.domain.repository.FinanceRepository
import com.safecoin.safecoin.domain.repository.ReportExporter
import com.safecoin.safecoin.domain.repository.SettingsRepository
import com.safecoin.safecoin.domain.repository.YandexConfigRepository

interface SafeCoinDependencies {
    val financeRepository: FinanceRepository
    val settingsRepository: SettingsRepository
    val reportExporter: ReportExporter
    val yandexConfigRepository: YandexConfigRepository
    val aiAnalysisRepository: AiAnalysisRepository
}

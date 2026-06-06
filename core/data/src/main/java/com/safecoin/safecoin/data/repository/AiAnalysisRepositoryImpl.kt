package com.safecoin.safecoin.data.repository

import com.safecoin.safecoin.data.ai.AiLogger
import com.safecoin.safecoin.data.ai.FallbackAnalysisGenerator
import com.safecoin.safecoin.data.ai.TransactionPromptBuilder
import com.safecoin.safecoin.data.ai.remote.YandexGptStreamingService
import com.safecoin.safecoin.domain.model.AiAnalysisResult
import com.safecoin.safecoin.domain.model.AiProvider
import com.safecoin.safecoin.domain.model.AiServiceStatus
import com.safecoin.safecoin.domain.model.AnalysisSkill
import com.safecoin.safecoin.domain.repository.AiAnalysisRepository
import com.safecoin.safecoin.domain.repository.FinanceRepository
import com.safecoin.safecoin.domain.repository.YandexConfigRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class AiAnalysisRepositoryImpl(
    private val financeRepository: FinanceRepository,
    private val yandexConfigRepository: YandexConfigRepository,
    private val yandexGptService: YandexGptStreamingService = YandexGptStreamingService(),
) : AiAnalysisRepository {

    private val _serviceStatus = MutableStateFlow(AiServiceStatus.NOT_CONFIGURED)
    override val serviceStatus: StateFlow<AiServiceStatus> = _serviceStatus.asStateFlow()

    override suspend fun refreshServiceStatus() {
        val config = yandexConfigRepository.getConfig()
        val status = if (config.isConfigured()) {
            AiServiceStatus.READY
        } else {
            AiServiceStatus.NOT_CONFIGURED
        }
        _serviceStatus.value = status
        AiLogger.i("Service status refreshed: $status")
        AiLogger.logConfig(config, source = "status-check")
        if (!config.isConfigured()) {
            AiLogger.w(
                "AI not configured. Set yandex.folder.id and api key or IAM token in ai_config.properties",
            )
        }
    }

    override suspend fun analyzeTransactions(
        skill: AnalysisSkill,
        onPartial: (String) -> Unit,
    ): AiAnalysisResult = withContext(Dispatchers.IO) {
        AiLogger.i("========== analyzeTransactions start ==========")
        AiLogger.logSkill(skill)

        val summary = financeRepository.getAnalyticsSummary()
        val transactions = financeRepository.observeAllTransactions().first()
        val accounts = financeRepository.observeAccounts().first()

        AiLogger.d(
            "Data loaded: accounts=${accounts.size}, transactions=${transactions.size}, " +
                "income=${summary.totalIncome}, expense=${summary.totalExpense}",
        )

        val systemPrompt = TransactionPromptBuilder.buildSystemPrompt(skill)
        val userMessage = TransactionPromptBuilder.buildUserMessage(
            summary = summary,
            transactions = transactions,
            accounts = accounts,
        )

        val config = yandexConfigRepository.getConfig()
        if (!config.isConfigured()) {
            AiLogger.w("Using offline fallback — Yandex GPT not configured")
            val fallback = FallbackAnalysisGenerator.generate(skill, summary, transactions)
            onPartial(fallback)
            AiLogger.i("========== analyzeTransactions end (fallback) ==========")
            return@withContext AiAnalysisResult(
                report = fallback,
                skill = skill,
                usedCloudAi = false,
            )
        }

        try {
            _serviceStatus.value = AiServiceStatus.ANALYZING
            AiLogger.i("Calling YandexGPT streaming API…")
            onPartial("")

            val report = yandexGptService.streamCompletion(
                config = config,
                systemPrompt = systemPrompt,
                userMessage = userMessage,
                onPartial = onPartial,
            )

            _serviceStatus.value = AiServiceStatus.READY
            AiLogger.i("Analysis complete: reportLength=${report.length}, usedCloudAi=true")
            AiLogger.i("========== analyzeTransactions end (success) ==========")
            AiAnalysisResult(
                report = report,
                skill = skill,
                usedCloudAi = true,
                provider = AiProvider.YANDEX_GPT,
            )
        } catch (e: Exception) {
            _serviceStatus.value = AiServiceStatus.ERROR
            AiLogger.e("Analysis failed, showing fallback + error", e)
            val fallback = FallbackAnalysisGenerator.generate(skill, summary, transactions) +
                "\n\n(Yandex GPT: ${e.message})"
            onPartial(fallback)
            AiLogger.i("========== analyzeTransactions end (error) ==========")
            AiAnalysisResult(
                report = fallback,
                skill = skill,
                usedCloudAi = false,
            )
        }
    }
}

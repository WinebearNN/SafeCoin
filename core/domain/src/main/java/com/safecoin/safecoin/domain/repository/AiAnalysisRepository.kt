package com.safecoin.safecoin.domain.repository

import com.safecoin.safecoin.domain.model.AiAnalysisResult
import com.safecoin.safecoin.domain.model.AiServiceStatus
import com.safecoin.safecoin.domain.model.AnalysisSkill
import kotlinx.coroutines.flow.StateFlow

interface AiAnalysisRepository {
    val serviceStatus: StateFlow<AiServiceStatus>
    suspend fun refreshServiceStatus()
    suspend fun analyzeTransactions(
        skill: AnalysisSkill = AnalysisSkill.TRANSACTION_REPORT,
        onPartial: (String) -> Unit = {},
    ): AiAnalysisResult
}

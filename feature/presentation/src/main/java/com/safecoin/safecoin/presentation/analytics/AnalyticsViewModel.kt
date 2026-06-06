package com.safecoin.safecoin.presentation.analytics

import android.net.Uri
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.safecoin.safecoin.domain.model.AiServiceStatus
import com.safecoin.safecoin.domain.model.AnalysisSkill
import com.safecoin.safecoin.domain.model.AnalyticsSummary
import com.safecoin.safecoin.domain.repository.AiAnalysisRepository
import com.safecoin.safecoin.domain.repository.FinanceRepository
import com.safecoin.safecoin.domain.repository.ReportExporter
import com.safecoin.safecoin.presentation.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AnalyticsUiState(
    val summary: AnalyticsSummary? = null,
    val isLoading: Boolean = true,
    val isExporting: Boolean = false,
    val exportedUri: Uri? = null,
    @StringRes val messageRes: Int? = null,
    val messageArg: String? = null,
    val aiServiceStatus: AiServiceStatus = AiServiceStatus.NOT_CONFIGURED,
    val selectedSkill: AnalysisSkill = AnalysisSkill.TRANSACTION_REPORT,
    val aiReport: String = "",
    val isAnalyzing: Boolean = false,
    val usedCloudAi: Boolean = false,
)

class AnalyticsViewModel(
    private val financeRepository: FinanceRepository,
    private val reportExporter: ReportExporter,
    private val aiAnalysisRepository: AiAnalysisRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    init {
        loadAnalytics()
        viewModelScope.launch { aiAnalysisRepository.refreshServiceStatus() }
        viewModelScope.launch {
            aiAnalysisRepository.serviceStatus.collect { status ->
                _uiState.update { it.copy(aiServiceStatus = status) }
            }
        }
    }

    fun loadAnalytics() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val summary = financeRepository.getAnalyticsSummary()
                _uiState.update { it.copy(summary = summary, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, messageRes = R.string.failed_load_analytics)
                }
            }
        }
    }

    fun selectSkill(skill: AnalysisSkill) {
        _uiState.update { it.copy(selectedSkill = skill) }
    }

    fun runAiAnalysis() {
        if (_uiState.value.isAnalyzing) return
        viewModelScope.launch {
            _uiState.update {
                it.copy(isAnalyzing = true, aiReport = "", usedCloudAi = false)
            }
            try {
                val result = aiAnalysisRepository.analyzeTransactions(
                    skill = _uiState.value.selectedSkill,
                    onPartial = { partial ->
                        _uiState.update { state ->
                            state.copy(aiReport = partial, isAnalyzing = true)
                        }
                    },
                )
                _uiState.update {
                    it.copy(
                        aiReport = result.report,
                        isAnalyzing = false,
                        usedCloudAi = result.usedCloudAi,
                        messageRes = when {
                            result.usedCloudAi -> R.string.report_from_provider
                            it.aiServiceStatus == AiServiceStatus.NOT_CONFIGURED ->
                                R.string.configure_yandex_gpt
                            else -> R.string.offline_summary_shown
                        },
                        messageArg = if (result.usedCloudAi) result.provider.displayName else null,
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isAnalyzing = false, messageRes = R.string.ai_analysis_failed)
                }
            }
        }
    }

    fun exportReport() {
        val summary = _uiState.value.summary ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isExporting = true, messageRes = null, messageArg = null) }
            try {
                val uri = reportExporter.exportToExcel(summary)
                _uiState.update {
                    it.copy(isExporting = false, exportedUri = uri, messageRes = R.string.report_exported)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isExporting = false, messageRes = R.string.export_failed)
                }
            }
        }
    }

    fun onExportHandled() {
        _uiState.update { it.copy(exportedUri = null) }
    }

    fun clearMessage() {
        _uiState.update { it.copy(messageRes = null, messageArg = null) }
    }

    companion object {
        fun factory(
            financeRepository: FinanceRepository,
            reportExporter: ReportExporter,
            aiAnalysisRepository: AiAnalysisRepository,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                AnalyticsViewModel(financeRepository, reportExporter, aiAnalysisRepository) as T
        }
    }
}

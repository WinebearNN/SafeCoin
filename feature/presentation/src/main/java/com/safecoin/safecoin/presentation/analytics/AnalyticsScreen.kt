package com.safecoin.safecoin.presentation.analytics

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.safecoin.safecoin.design.components.MetricCard
import com.safecoin.safecoin.design.components.SectionHeader
import com.safecoin.safecoin.design.theme.ExpenseRed
import com.safecoin.safecoin.design.theme.IncomeGreen
import com.safecoin.safecoin.design.theme.PrimaryBlue
import com.safecoin.safecoin.presentation.R
import com.safecoin.safecoin.presentation.components.CategoryPieChart
import com.safecoin.safecoin.presentation.components.MonthlyBarChart
import java.text.NumberFormat
import java.util.Locale

@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel,
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState()}
    val formatter = NumberFormat.getCurrencyInstance(Locale.US)

    LaunchedEffect(uiState.messageRes, uiState.messageArg) {
        uiState.messageRes?.let { resId ->
            val text = uiState.messageArg?.let { arg ->
                context.getString(resId, arg)
            } ?: context.getString(resId)
            snackbarHostState.showSnackbar(text)
            viewModel.clearMessage()
        }
    }

    LaunchedEffect(uiState.exportedUri) {
        uiState.exportedUri?.let { uri ->
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, context.getString(R.string.share_report)))
            viewModel.onExportHandled()
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        when {
            uiState.isLoading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CircularProgressIndicator()
                }
            }
            uiState.summary != null -> {
                val summary = uiState.summary!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState()),
                ) {
                    SectionHeader(title = stringResource(R.string.financial_analytics))
                    SummaryCards(
                        income = summary.totalIncome,
                        expense = summary.totalExpense,
                        net = summary.netBalance,
                        formatter = formatter,
                    )

                    AiAnalysisSection(
                        serviceStatus = uiState.aiServiceStatus,
                        selectedSkill = uiState.selectedSkill,
                        aiReport = uiState.aiReport,
                        isAnalyzing = uiState.isAnalyzing,
                        usedCloudAi = uiState.usedCloudAi,
                        onSkillSelected = viewModel::selectSkill,
                        onRunAnalysis = viewModel::runAiAnalysis,
                    )

                    CategoryPieChart(
                        breakdown = summary.categoryBreakdown,
                        modifier = Modifier.padding(vertical = 8.dp),
                    )

                    MonthlyBarChart(
                        flows = summary.monthlyFlows,
                        modifier = Modifier.padding(bottom = 16.dp),
                    )

                    FilledTonalButton(
                        onClick = viewModel::exportReport,
                        enabled = !uiState.isExporting,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 8.dp),
                    ) {
                        Icon(Icons.Default.FileDownload, contentDescription = null)
                        Text(
                            text = if (uiState.isExporting) {
                                stringResource(R.string.exporting)
                            } else {
                                stringResource(R.string.export_excel)
                            },
                            modifier = Modifier.padding(start = 8.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryCards(
    income: Double,
    expense: Double,
    net: Double,
    formatter: NumberFormat,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        MetricCard(
            label = stringResource(R.string.income),
            value = formatter.format(income),
            modifier = Modifier.weight(1f),
            accentColor = IncomeGreen,
        )
        MetricCard(
            label = stringResource(R.string.expense),
            value = formatter.format(expense),
            modifier = Modifier.weight(1f),
            accentColor = ExpenseRed,
        )
        MetricCard(
            label = stringResource(R.string.net),
            value = formatter.format(net),
            modifier = Modifier.weight(1f),
            accentColor = PrimaryBlue,
        )
    }
}

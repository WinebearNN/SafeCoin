package com.safecoin.safecoin.presentation.analytics

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.safecoin.safecoin.design.components.MarkdownText
import com.safecoin.safecoin.presentation.R
import com.safecoin.safecoin.domain.model.AiServiceStatus
import com.safecoin.safecoin.domain.model.AnalysisSkill

@Composable
fun AiAnalysisSection(
    serviceStatus: AiServiceStatus,
    selectedSkill: AnalysisSkill,
    aiReport: String,
    isAnalyzing: Boolean,
    usedCloudAi: Boolean,
    onSkillSelected: (AnalysisSkill) -> Unit,
    onRunAnalysis: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
        ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = stringResource(R.string.ai_insights),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp),
                )
            }

            Text(
                text = serviceStatusText(serviceStatus, usedCloudAi, isAnalyzing),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 6.dp, bottom = 8.dp),
            )

            if (isAnalyzing) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                )
            }

            if (serviceStatus == AiServiceStatus.NOT_CONFIGURED) {
                Text(
                    text = stringResource(R.string.api_key_setup_hint),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 12.dp),
                )
            }

            Text(
                text = stringResource(R.string.analysis_skill),
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(bottom = 6.dp),
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                AnalysisSkill.entries.forEach { skill ->
                    FilterChip(
                        selected = skill == selectedSkill,
                        onClick = { onSkillSelected(skill) },
                        label = { Text(skill.title) },
                        enabled = !isAnalyzing,
                    )
                }
            }

            Button(
                onClick = onRunAnalysis,
                enabled = !isAnalyzing,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
            ) {
                if (isAnalyzing) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(end = 8.dp),
                        strokeWidth = 2.dp,
                    )
                } else {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null)
                }
                Text(
                    text = if (isAnalyzing) {
                        stringResource(R.string.ai_analyzing)
                    } else {
                        stringResource(R.string.make_ai_analyze)
                    },
                    modifier = Modifier.padding(start = 8.dp),
                )
            }

            if (aiReport.isNotBlank()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                    ),
                ) {
                    MarkdownText(
                        markdown = aiReport,
                        modifier = Modifier
                            .padding(12.dp)
                            .heightIn(min = 80.dp, max = 360.dp)
                            .verticalScroll(rememberScrollState()),
                    )
                }
            }
        }
    }
}

@Composable
private fun serviceStatusText(
    status: AiServiceStatus,
    usedCloudAi: Boolean,
    isAnalyzing: Boolean,
): String = when {
    isAnalyzing -> stringResource(R.string.ai_status_streaming)
    usedCloudAi -> stringResource(R.string.ai_status_cloud_ready)
    status == AiServiceStatus.NOT_CONFIGURED -> stringResource(R.string.ai_status_not_configured)
    status == AiServiceStatus.ERROR -> stringResource(R.string.ai_status_error)
    else -> stringResource(R.string.ai_status_ready)
}

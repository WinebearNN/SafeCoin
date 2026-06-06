package com.safecoin.safecoin.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.safecoin.safecoin.design.components.SectionHeader
import com.safecoin.safecoin.design.theme.ExpenseRed
import com.safecoin.safecoin.design.theme.IncomeGreen
import com.safecoin.safecoin.design.theme.PrimaryBlue
import com.safecoin.safecoin.domain.model.CategoryBreakdown
import com.safecoin.safecoin.domain.model.MonthlyFlow
import com.safecoin.safecoin.presentation.R

private val chartColors = listOf(
    PrimaryBlue,
    IncomeGreen,
    Color(0xFF7C3AED),
    ExpenseRed,
    Color(0xFFD97706),
    Color(0xFF0891B2),
)

@Composable
fun CategoryPieChart(
    breakdown: List<CategoryBreakdown>,
    modifier: Modifier = Modifier,
) {
    if (breakdown.isEmpty()) return

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            SectionHeader(title = stringResource(R.string.expenses_by_category))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Canvas(modifier = Modifier.size(140.dp)) {
                    var startAngle = -90f
                    breakdown.forEachIndexed { index, item ->
                        val sweep = item.percentage / 100f * 360f
                        drawArc(
                            color = chartColors[index % chartColors.size],
                            startAngle = startAngle,
                            sweepAngle = sweep,
                            useCenter = true,
                            size = Size(size.width, size.height),
                        )
                        startAngle += sweep
                    }
                }
                Column(
                    modifier = Modifier.padding(start = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    breakdown.forEachIndexed { index, item ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                modifier = Modifier.size(12.dp),
                                color = chartColors[index % chartColors.size],
                                shape = RoundedCornerShape(3.dp),
                            ) {}
                            Text(
                                text = "${item.category} (${"%.0f".format(item.percentage)}%)",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 8.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MonthlyBarChart(
    flows: List<MonthlyFlow>,
    modifier: Modifier = Modifier,
) {
    if (flows.isEmpty()) return

    val maxValue = flows.maxOf { maxOf(it.income, it.expense) }.coerceAtLeast(1.0)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            SectionHeader(title = stringResource(R.string.monthly_income_expense))
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                val barWidth = size.width / (flows.size * 3f)
                val gap = barWidth * 0.3f
                val chartHeight = size.height - 24f

                flows.forEachIndexed { index, flow ->
                    val groupX = index * (barWidth * 2 + gap * 2) + gap
                    val incomeHeight = (flow.income / maxValue * chartHeight).toFloat()
                    val expenseHeight = (flow.expense / maxValue * chartHeight).toFloat()

                    drawRoundRect(
                        color = IncomeGreen,
                        topLeft = Offset(groupX, size.height - incomeHeight - 12f),
                        size = Size(barWidth, incomeHeight),
                        cornerRadius = CornerRadius(6f, 6f),
                    )
                    drawRoundRect(
                        color = ExpenseRed,
                        topLeft = Offset(groupX + barWidth + 4f, size.height - expenseHeight - 12f),
                        size = Size(barWidth, expenseHeight),
                        cornerRadius = CornerRadius(6f, 6f),
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                flows.forEach { flow ->
                    Text(
                        text = flow.monthLabel.take(3),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                LegendDot(color = IncomeGreen, label = stringResource(R.string.income))
                LegendDot(color = ExpenseRed, label = stringResource(R.string.expense))
            }
        }
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            modifier = Modifier.size(10.dp),
            color = color,
            shape = RoundedCornerShape(2.dp),
        ) {}
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(start = 6.dp),
        )
    }
}

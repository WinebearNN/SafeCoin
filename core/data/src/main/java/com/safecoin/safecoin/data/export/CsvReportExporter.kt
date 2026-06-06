package com.safecoin.safecoin.data.export

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.safecoin.safecoin.domain.model.AnalyticsSummary
import com.safecoin.safecoin.domain.repository.ReportExporter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CsvReportExporter(
    private val context: Context,
) : ReportExporter {

    override suspend fun exportToExcel(summary: AnalyticsSummary): Uri = withContext(Dispatchers.IO) {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val fileName = "safecoin_report_$timestamp.csv"
        val dir = File(context.cacheDir, "reports").apply { mkdirs() }
        val file = File(dir, fileName)

        file.bufferedWriter().use { writer ->
            writer.appendLine("SafeCoin Financial Report")
            writer.appendLine("Generated,${SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())}")
            writer.appendLine()
            writer.appendLine("Summary")
            writer.appendLine("Metric,Value")
            writer.appendLine("Total Income,${summary.totalIncome}")
            writer.appendLine("Total Expense,${summary.totalExpense}")
            writer.appendLine("Net Balance,${summary.netBalance}")
            writer.appendLine()
            writer.appendLine("Expenses by Category")
            writer.appendLine("Category,Amount,Percentage")
            summary.categoryBreakdown.forEach { item ->
                writer.appendLine("${item.category},${item.amount},${"%.1f".format(item.percentage)}%")
            }
            writer.appendLine()
            writer.appendLine("Monthly Flow")
            writer.appendLine("Month,Income,Expense")
            summary.monthlyFlows.forEach { flow ->
                writer.appendLine("${flow.monthLabel},${flow.income},${flow.expense}")
            }
        }

        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file,
        )
    }
}

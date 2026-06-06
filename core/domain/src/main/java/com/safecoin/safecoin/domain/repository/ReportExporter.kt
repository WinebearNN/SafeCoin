package com.safecoin.safecoin.domain.repository

import android.net.Uri
import com.safecoin.safecoin.domain.model.AnalyticsSummary

interface ReportExporter {
    suspend fun exportToExcel(summary: AnalyticsSummary): Uri
}

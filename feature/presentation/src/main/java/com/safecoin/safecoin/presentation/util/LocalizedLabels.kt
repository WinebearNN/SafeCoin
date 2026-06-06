package com.safecoin.safecoin.presentation.util

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.safecoin.safecoin.domain.model.AppLanguage
import com.safecoin.safecoin.domain.model.ThemeMode
import com.safecoin.safecoin.domain.model.AnalysisSkill
import com.safecoin.safecoin.presentation.R

@StringRes
fun ThemeMode.labelRes(): Int = when (this) {
    ThemeMode.SYSTEM -> R.string.theme_system
    ThemeMode.LIGHT -> R.string.theme_light
    ThemeMode.DARK -> R.string.theme_dark
}

@Composable
fun ThemeMode.label(): String = stringResource(labelRes())

@StringRes
fun AppLanguage.labelRes(): Int = when (this) {
    AppLanguage.ENGLISH -> R.string.language_english
    AppLanguage.RUSSIAN -> R.string.language_russian
}

@Composable
fun AppLanguage.label(): String = stringResource(labelRes())

@StringRes
fun AnalysisSkill.titleRes(): Int = when (this) {
    AnalysisSkill.TRANSACTION_REPORT -> R.string.skill_transaction_report
    AnalysisSkill.CATEGORY_BREAKDOWN -> R.string.skill_category_breakdown
    AnalysisSkill.MONTHLY_TRENDS -> R.string.skill_monthly_trends
}

@Composable
fun AnalysisSkill.localizedTitle(): String = stringResource(titleRes())

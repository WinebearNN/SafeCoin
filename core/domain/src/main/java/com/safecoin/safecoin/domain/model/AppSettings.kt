package com.safecoin.safecoin.domain.model

enum class AppLanguage(val code: String) {
    ENGLISH("en"),
    RUSSIAN("ru"),
}

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK,
}

data class AppSettings(
    val darkTheme: ThemeMode,
    val language: AppLanguage,
    val notificationsEnabled: Boolean,
)

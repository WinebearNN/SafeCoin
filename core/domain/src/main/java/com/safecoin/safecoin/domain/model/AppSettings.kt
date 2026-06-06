package com.safecoin.safecoin.domain.model

enum class AppLanguage(val code: String, val displayName: String) {
    ENGLISH("en", "English"),
    RUSSIAN("ru", "Русский"),
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

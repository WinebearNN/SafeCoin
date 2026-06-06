package com.safecoin.safecoin.domain.repository

import com.safecoin.safecoin.domain.model.AppLanguage
import com.safecoin.safecoin.domain.model.AppSettings
import com.safecoin.safecoin.domain.model.ThemeMode
import com.safecoin.safecoin.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeSettings(): Flow<AppSettings>
    fun observeProfile(): Flow<UserProfile>
    suspend fun updateTheme(theme: ThemeMode)
    suspend fun updateLanguage(language: AppLanguage)
    suspend fun updateNotifications(enabled: Boolean)
    suspend fun updateProfile(profile: UserProfile)
}

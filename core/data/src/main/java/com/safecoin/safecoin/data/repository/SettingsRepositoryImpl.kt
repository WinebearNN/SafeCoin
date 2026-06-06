package com.safecoin.safecoin.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.safecoin.safecoin.domain.model.AppLanguage
import com.safecoin.safecoin.domain.model.AppSettings
import com.safecoin.safecoin.domain.model.ThemeMode
import com.safecoin.safecoin.domain.model.UserProfile
import com.safecoin.safecoin.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
) : SettingsRepository {

    override fun observeSettings(): Flow<AppSettings> = dataStore.data.map { prefs ->
        AppSettings(
            darkTheme = ThemeMode.valueOf(
                prefs[KEY_THEME] ?: ThemeMode.SYSTEM.name,
            ),
            language = AppLanguage.entries.firstOrNull {
                it.code == prefs[KEY_LANGUAGE]
            } ?: AppLanguage.ENGLISH,
            notificationsEnabled = prefs[KEY_NOTIFICATIONS] ?: true,
        )
    }

    override fun observeProfile(): Flow<UserProfile> = dataStore.data.map { prefs ->
        UserProfile(
            name = prefs[KEY_NAME] ?: "Alex Morgan",
            email = prefs[KEY_EMAIL] ?: "alex.morgan@email.com",
            phone = prefs[KEY_PHONE] ?: "+1 555 0123",
        )
    }

    override suspend fun updateTheme(theme: ThemeMode) {
        dataStore.edit { it[KEY_THEME] = theme.name }
    }

    override suspend fun updateLanguage(language: AppLanguage) {
        dataStore.edit { it[KEY_LANGUAGE] = language.code }
    }

    override suspend fun updateNotifications(enabled: Boolean) {
        dataStore.edit { it[KEY_NOTIFICATIONS] = enabled }
    }

    override suspend fun updateProfile(profile: UserProfile) {
        dataStore.edit {
            it[KEY_NAME] = profile.name
            it[KEY_EMAIL] = profile.email
            it[KEY_PHONE] = profile.phone
        }
    }

    companion object {
        private val KEY_THEME = stringPreferencesKey("theme_mode")
        private val KEY_LANGUAGE = stringPreferencesKey("language")
        private val KEY_NOTIFICATIONS = booleanPreferencesKey("notifications")
        private val KEY_NAME = stringPreferencesKey("profile_name")
        private val KEY_EMAIL = stringPreferencesKey("profile_email")
        private val KEY_PHONE = stringPreferencesKey("profile_phone")
    }
}

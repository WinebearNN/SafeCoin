package com.safecoin.safecoin.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.safecoin.safecoin.data.ai.AiLogger
import com.safecoin.safecoin.data.ai.config.AiConfigDefaults
import com.safecoin.safecoin.domain.model.AiRequestConfig
import com.safecoin.safecoin.domain.repository.YandexConfigRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class YandexConfigRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
) : YandexConfigRepository {

    private val defaults = AiConfigDefaults.fromBuildConfig().also {
        AiLogger.i("Loaded BuildConfig defaults from ai_config.properties")
        AiLogger.logConfig(it, source = "build-config")
    }

    override fun observeConfig(): Flow<AiRequestConfig> = dataStore.data.map { prefs ->
        merge(defaults, prefs)
    }

    override suspend fun getConfig(): AiRequestConfig {
        val prefs = dataStore.data.first()
        val merged = merge(defaults, prefs)
        val hasOverrides = prefs[KEY_API_KEY] != null ||
            prefs[KEY_IAM_TOKEN] != null ||
            prefs[KEY_FOLDER_ID] != null ||
            prefs[KEY_USE_IAM] != null
        AiLogger.d("getConfig: hasProfileOverrides=$hasOverrides")
        return merged
    }

    override suspend fun saveOverrides(
        apiKey: String,
        iamToken: String,
        folderId: String,
        useIamAuth: Boolean,
    ) {
        AiLogger.i(
            "Saving profile overrides: folderId=${AiLogger.maskValue(folderId)}, " +
                "useIam=$useIamAuth, " +
                "apiKey=${if (apiKey.isBlank()) "unchanged" else "updated"}, " +
                "iamToken=${if (iamToken.isBlank()) "unchanged" else "updated"}",
        )
        dataStore.edit {
            if (apiKey.isNotBlank()) it[KEY_API_KEY] = apiKey.trim()
            if (iamToken.isNotBlank()) it[KEY_IAM_TOKEN] = iamToken.trim()
            if (folderId.isNotBlank()) it[KEY_FOLDER_ID] = folderId.trim()
            it[KEY_USE_IAM] = useIamAuth
        }
        AiLogger.logConfig(getConfig(), source = "after-save")
    }

    override suspend fun clearOverrides() {
        AiLogger.i("Clearing profile AI overrides")
        dataStore.edit {
            it.remove(KEY_API_KEY)
            it.remove(KEY_IAM_TOKEN)
            it.remove(KEY_FOLDER_ID)
            it.remove(KEY_USE_IAM)
        }
    }

    private fun merge(defaults: AiRequestConfig, prefs: Preferences): AiRequestConfig {
        val apiKey = prefs[KEY_API_KEY]?.takeIf { it.isNotBlank() } ?: defaults.apiKey
        val iamToken = prefs[KEY_IAM_TOKEN]?.takeIf { it.isNotBlank() } ?: defaults.iamToken
        val folderId = prefs[KEY_FOLDER_ID]?.takeIf { it.isNotBlank() } ?: defaults.folderId
        val useIam = prefs[KEY_USE_IAM] ?: defaults.useIamAuth
        return defaults.copy(
            apiKey = apiKey,
            iamToken = iamToken,
            folderId = folderId,
            useIamAuth = useIam,
        )
    }

    companion object {
        private val KEY_API_KEY = stringPreferencesKey("yandex_api_key_override")
        private val KEY_IAM_TOKEN = stringPreferencesKey("yandex_iam_token_override")
        private val KEY_FOLDER_ID = stringPreferencesKey("yandex_folder_id_override")
        private val KEY_USE_IAM = booleanPreferencesKey("yandex_use_iam_override")
    }
}

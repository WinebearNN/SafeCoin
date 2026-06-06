package com.safecoin.safecoin.domain.repository

import com.safecoin.safecoin.domain.model.AiRequestConfig
import kotlinx.coroutines.flow.Flow

interface YandexConfigRepository {
    fun observeConfig(): Flow<AiRequestConfig>
    suspend fun getConfig(): AiRequestConfig
    suspend fun saveOverrides(
        apiKey: String,
        iamToken: String,
        folderId: String,
        useIamAuth: Boolean,
    )
    suspend fun clearOverrides()
}

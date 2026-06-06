package com.safecoin.safecoin.domain.model

/**
 * Yandex GPT request configuration.
 * Defaults are loaded from [ai_config.properties] at build time (see [com.safecoin.safecoin.BuildConfig]).
 * Profile settings can override credentials at runtime.
 */
data class AiRequestConfig(
    val apiKey: String = "",
    val iamToken: String = "",
    val useIamAuth: Boolean = false,
    val folderId: String = "",
    val modelName: String = "yandexgpt/latest",
    val temperature: Double = 0.3,
    val maxTokens: String = "2000",
    val stream: Boolean = true,
) {
    val modelUri: String
        get() = "gpt://$folderId/$modelName"

    val authorizationHeader: String?
        get() = when {
            useIamAuth && iamToken.isNotBlank() -> "Bearer $iamToken"
            !useIamAuth && apiKey.isNotBlank() -> "Api-Key $apiKey"
            iamToken.isNotBlank() -> "Bearer $iamToken"
            apiKey.isNotBlank() -> "Api-Key $apiKey"
            else -> null
        }

    fun isConfigured(): Boolean =
        folderId.isNotBlank() && !authorizationHeader.isNullOrBlank()
}

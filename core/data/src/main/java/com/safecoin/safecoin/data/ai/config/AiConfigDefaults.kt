package com.safecoin.safecoin.data.ai.config

import com.safecoin.safecoin.data.BuildConfig
import com.safecoin.safecoin.domain.model.AiRequestConfig

/** Loads defaults from BuildConfig (injected from ai_config.properties at compile time). */
object AiConfigDefaults {

    fun fromBuildConfig(): AiRequestConfig = AiRequestConfig(
        apiKey = BuildConfig.YANDEX_API_KEY,
        iamToken = BuildConfig.YANDEX_IAM_TOKEN,
        useIamAuth = BuildConfig.YANDEX_USE_IAM,
        folderId = BuildConfig.YANDEX_FOLDER_ID,
        modelName = BuildConfig.YANDEX_MODEL,
        temperature = BuildConfig.YANDEX_TEMPERATURE,
        maxTokens = BuildConfig.YANDEX_MAX_TOKENS,
        stream = BuildConfig.YANDEX_STREAM,
    )
}

package com.safecoin.safecoin.data.ai

import android.util.Log
import com.safecoin.safecoin.domain.model.AiRequestConfig
import com.safecoin.safecoin.domain.model.AnalysisSkill

/**
 * Centralized logging for AI / YandexGPT flows.
 * Filter Logcat by tag: [SafeCoinAI]
 */
object AiLogger {

    const val TAG = "SafeCoinAI"

    fun d(message: String) = Log.d(TAG, message)

    fun i(message: String) = Log.i(TAG, message)

    fun w(message: String, throwable: Throwable? = null) {
        if (throwable != null) Log.w(TAG, message, throwable) else Log.w(TAG, message)
    }

    fun e(message: String, throwable: Throwable? = null) {
        if (throwable != null) Log.e(TAG, message, throwable) else Log.e(TAG, message)
    }

    fun logConfig(config: AiRequestConfig, source: String = "resolved") {
        i(
            buildString {
                append("Config ($source): ")
                append("modelUri=${config.modelUri}, ")
                append("stream=${config.stream}, ")
                append("temperature=${config.temperature}, ")
                append("maxTokens=${config.maxTokens}, ")
                append("useIam=${config.useIamAuth}, ")
                append("folderId=${maskValue(config.folderId)}, ")
                append("apiKey=${maskSecret(config.apiKey)}, ")
                append("iamToken=${maskSecret(config.iamToken)}, ")
                append("configured=${config.isConfigured()}")
            },
        )
    }

    fun logSkill(skill: AnalysisSkill) {
        d("Skill selected: id=${skill.id}, title=${skill.title}")
    }

    fun maskSecret(value: String): String = when {
        value.isBlank() -> "(empty)"
        value.length <= 8 -> "****"
        else -> "${value.take(4)}…${value.takeLast(2)} (${value.length} chars)"
    }

    fun maskValue(value: String): String = when {
        value.isBlank() -> "(empty)"
        value.length <= 6 -> value
        else -> "${value.take(4)}…${value.takeLast(2)}"
    }

    /** Truncate long text for Logcat (max ~4KB per line safe). */
    fun truncate(text: String, maxLength: Int = 500): String =
        if (text.length <= maxLength) text else text.take(maxLength) + "… (${text.length} chars total)"
}

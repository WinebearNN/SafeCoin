package com.safecoin.safecoin.data.ai.remote

import com.safecoin.safecoin.data.ai.AiLogger
import com.safecoin.safecoin.domain.model.AiRequestConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.util.concurrent.TimeUnit

/**
 * Yandex Foundation Models completion API with streaming.
 * Supports both SSE (`data: {...}`) and newline-delimited JSON (`{"result":{...}}`).
 */
class YandexGptStreamingService(
    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(180, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build(),
) {

    suspend fun streamCompletion(
        config: AiRequestConfig,
        systemPrompt: String,
        userMessage: String,
        onPartial: (accumulatedText: String) -> Unit,
    ): String = withContext(Dispatchers.IO) {
        val startMs = System.currentTimeMillis()
        AiLogger.i("─── YandexGPT stream start ───")
        AiLogger.logConfig(config, source = "request")

        val auth = config.authorizationHeader
            ?: run {
                val msg = "Yandex credentials missing. Fill ai_config.properties or Profile → AI settings."
                AiLogger.e(msg)
                error(msg)
            }

        require(config.folderId.isNotBlank()) {
            val msg = "Yandex folder ID missing. Set yandex.folder.id in ai_config.properties."
            AiLogger.e(msg)
            msg
        }

        AiLogger.d("Prompt sizes: system=${systemPrompt.length} chars, user=${userMessage.length} chars")

        val messages = JSONArray().apply {
            if (systemPrompt.isNotBlank()) {
                put(JSONObject().put("role", "system").put("text", systemPrompt))
            }
            put(JSONObject().put("role", "user").put("text", userMessage))
        }

        val body = JSONObject().apply {
            put("modelUri", config.modelUri)
            put(
                "completionOptions",
                JSONObject()
                    .put("stream", config.stream)
                    .put("temperature", config.temperature)
                    .put("maxTokens", config.maxTokens),
            )
            put("messages", messages)
        }

        val request = Request.Builder()
            .url(COMPLETION_URL)
            .addHeader("Authorization", auth)
            .addHeader("Content-Type", "application/json")
            .post(body.toString().toRequestBody(JSON_MEDIA))
            .build()

        var latestText = ""
        var chunkCount = 0
        var lineCount = 0
        var lastStatus: String? = null

        try {
            httpClient.newCall(request).execute().use { response ->
                AiLogger.i("HTTP response: code=${response.code}, message=${response.message}")

                if (!response.isSuccessful) {
                    val errorBody = response.body?.string().orEmpty()
                    AiLogger.e("Error body: ${AiLogger.truncate(errorBody, 1000)}")
                    val message = parseErrorMessage(errorBody) ?: "HTTP ${response.code}"
                    error("Yandex GPT: $message")
                }

                AiLogger.d("Content-Type: ${response.header("Content-Type")}")

                val reader: BufferedReader = response.body?.charStream()?.buffered()
                    ?: run {
                        AiLogger.e("Response body is null")
                        error("Empty response body")
                    }

                reader.useLines { lines ->
                    for (rawLine in lines) {
                        lineCount++
                        val json = extractJsonPayload(rawLine) ?: continue
                        if (json == "[DONE]") {
                            AiLogger.d("Stream [DONE] after $chunkCount chunks")
                            break
                        }

                        val chunk = parseStreamChunk(json)
                        if (chunk == null) {
                            AiLogger.w("Unparsed stream line #$lineCount: ${AiLogger.truncate(rawLine, 200)}")
                            continue
                        }

                        if (chunk.text.isEmpty()) continue

                        chunkCount++
                        lastStatus = chunk.status

                        // Yandex sends cumulative full text in each chunk (not deltas).
                        latestText = chunk.text

                        if (chunkCount == 1 || chunkCount % 5 == 0 || isTerminalStatus(chunk.status)) {
                            AiLogger.d(
                                "Stream chunk #$chunkCount status=${chunk.status}, " +
                                    "textLen=${chunk.text.length}",
                            )
                        }

                        onPartial(latestText)

                        when (chunk.status) {
                            STATUS_CONTENT_FILTER -> {
                                AiLogger.w("Content filter triggered — generation stopped")
                                break
                            }
                            STATUS_FINAL, STATUS_TRUNCATED -> {
                                AiLogger.i("Stream finished with status=${chunk.status}")
                                break
                            }
                        }
                    }
                }
            }

            val result = latestText.trim()
            val elapsed = System.currentTimeMillis() - startMs

            if (result.isBlank()) {
                AiLogger.e(
                    "Empty response after ${elapsed}ms " +
                        "(lines=$lineCount, chunks=$chunkCount, lastStatus=$lastStatus)",
                )
                error("Yandex GPT returned empty response")
            }

            AiLogger.i(
                "─── YandexGPT stream success ─── " +
                    "${elapsed}ms, chunks=$chunkCount, responseLen=${result.length}, lastStatus=$lastStatus",
            )
            AiLogger.d("Response preview: ${AiLogger.truncate(result, 400)}")
            result
        } catch (e: Exception) {
            val elapsed = System.currentTimeMillis() - startMs
            AiLogger.e(
                "─── YandexGPT stream failed ─── ${elapsed}ms, chunks=$chunkCount, lines=$lineCount",
                e,
            )
            throw e
        }
    }

    /** Supports `data: {...}` SSE lines and raw JSON lines from Yandex streaming API. */
    private fun extractJsonPayload(line: String): String? {
        val trimmed = line.trim()
        if (trimmed.isEmpty()) return null
        return when {
            trimmed.startsWith(SSE_DATA_PREFIX) -> trimmed.removePrefix(SSE_DATA_PREFIX).trim()
            trimmed.startsWith("{") -> trimmed
            else -> null
        }
    }

    private data class StreamChunk(val text: String, val status: String?)

    private fun parseStreamChunk(json: String): StreamChunk? = runCatching {
        val root = JSONObject(json)
        val alternatives = root.optJSONObject("result")?.optJSONArray("alternatives")
            ?: root.optJSONArray("alternatives")
            ?: return null

        if (alternatives.length() == 0) return null

        val alternative = alternatives.getJSONObject(0)
        val text = alternative.optJSONObject("message")?.optString("text").orEmpty()
        val status = alternative.optString("status").takeIf { it.isNotBlank() }
        StreamChunk(text = text, status = status)
    }.getOrElse { e ->
        AiLogger.w("Failed to parse stream chunk: ${e.message}")
        null
    }

    private fun isTerminalStatus(status: String?): Boolean =
        status == STATUS_FINAL || status == STATUS_TRUNCATED

    private fun parseErrorMessage(json: String): String? = runCatching {
        JSONObject(json).optJSONObject("error")?.optString("message")
            ?: JSONObject(json).optString("message").takeIf { it.isNotBlank() }
    }.getOrNull()

    companion object {
        private const val COMPLETION_URL =
            "https://llm.api.cloud.yandex.net/foundationModels/v1/completion"
        private const val SSE_DATA_PREFIX = "data: "
        private const val STATUS_FINAL = "ALTERNATIVE_STATUS_FINAL"
        private const val STATUS_TRUNCATED = "ALTERNATIVE_STATUS_TRUNCATED_FINAL"
        private const val STATUS_CONTENT_FILTER = "ALTERNATIVE_STATUS_CONTENT_FILTER"
        private val JSON_MEDIA = "application/json; charset=utf-8".toMediaType()
    }
}

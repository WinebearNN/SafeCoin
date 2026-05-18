# Yandex Foundation Models Text Generation API: Streaming Integration for Android (Kotlin)

This guide describes how to integrate the **YandexGPT Text Generation API** into an Android application using Kotlin, with a focus on **streaming completions**. The API enables you to generate text synchronously or via a real‑time stream of events.

---

## 1. Base URL & Authentication

**Endpoint (REST)**
```
POST https://llm.api.cloud.yandex.net/foundationModels/v1/completion
```

**Authentication**
Yandex Cloud expects one of the following headers:

- **IAM token**
  `Authorization: Bearer <IAM_TOKEN>`
  (recommended for production; obtain a token for a service account)

- **API key**
  `Authorization: Api-Key <API_KEY>`
  (simpler for prototyping, but keep it secure)

> [!NOTE]
> In a mobile app, avoid embedding long‑lived credentials directly. A common approach is to have your backend proxy the requests to Yandex Cloud and forward the token or use a secure storage mechanism.

---

## 2. Request Structure

The body is a JSON object with the following top‑level fields:

| Field              | Type          | Description |
|--------------------|---------------|-------------|
| `modelUri`         | string        | Model ID, e.g. `gpt://<folder-id>/yandexgpt/latest`. |
| `completionOptions`| object        | Generation settings. |
| `messages`         | array[Message]| Conversation history (prompt). |
| `jsonObject`       | boolean       | (optional) Force JSON output. |
| `jsonSchema`       | object        | (optional) Enforce a specific JSON schema. |
| `tools`            | array[Tool]   | **Not yet supported** – will be ignored. |

### `completionOptions`

| Field            | Type    | Description |
|------------------|---------|-------------|
| `stream`         | boolean | `true` for streaming mode. |
| `temperature`    | number  | Between `0` and `1`. Default `0.3`. |
| `maxTokens`      | string  | Maximum tokens to generate (must be > 0). Passed as a string representing an integer. |
| `reasoningOptions`| object | Optional reasoning mode (`DISABLED` or `ENABLED_HIDDEN`). |

### `Message`

| Field          | Type   | Description |
|----------------|--------|-------------|
| `role`         | string | `system`, `user`, or `assistant`. |
| `text`         | string | Text content of the message. |

Only one content field can be present per message (`text`, `toolCallList`, or `toolResultList`). For standard text prompts, use `text`.

---

## 3. Streaming Mode (Server‑Sent Events)

When `completionOptions.stream = true`, the API returns a stream of **Server‑Sent Events (SSE)** with content type `text/event-stream`.
Each event contains a JSON fragment similar to the final response, but with partial data. The stream ends with a `data: [DONE]` line (optional, depending on implementation) or when the connection closes.

### SSE Event Format

A single chunk looks like:
```
data: {"alternatives":[{"message":{"role":"assistant","text":"Hello"},"status":"ALTERNATIVE_STATUS_PARTIAL"}],"usage":{...},"modelVersion":"..."}
```

To extract the generated text incrementally, parse each `data:` line and concatenate the `alternatives[0].message.text` values.

> [!IMPORTANT]
> The `status` field inside `Alternative` indicates the generation status:
> - `ALTERNATIVE_STATUS_PARTIAL` – intermediate chunk.
> - `ALTERNATIVE_STATUS_FINAL` – final chunk of the response.
> - `ALTERNATIVE_STATUS_TRUNCATED_FINAL` – response truncated due to token limit.
> - `ALTERNATIVE_STATUS_CONTENT_FILTER` – generation stopped by content filter.
> - `ALTERNATIVE_STATUS_TOOL_CALLS` – model returned tool calls instead of text.

---

## 4. Kotlin Implementation (Android)

Below is a complete, minimal example using **OkHttp** and **kotlinx.coroutines**.
Add the following dependencies to your `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")
    implementation("com.google.code.gson:gson:2.10.1") // or use kotlinx.serialization
}
```

### 4.1 Data Classes

```kotlin
data class CompletionRequest(
    val modelUri: String,
    val completionOptions: CompletionOptions,
    val messages: List<Message>
)

data class CompletionOptions(
    val stream: Boolean,
    val temperature: Double,
    val maxTokens: String
)

data class Message(
    val role: String,
    val text: String
)

// Response fragment (same structure for both final and streaming chunks)
data class CompletionResponse(
    val alternatives: List<Alternative>?,
    val usage: Usage?,
    val modelVersion: String?
)

data class Alternative(
    val message: ResponseMessage?,
    val status: String?
)

data class ResponseMessage(
    val role: String?,
    val text: String?
)

data class Usage(
    val inputTextTokens: String?,
    val completionTokens: String?,
    val totalTokens: String?
)
```

### 4.2 Streaming Service

```kotlin
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.IOException

class YandexGptStreamingService(
    private val client: OkHttpClient = OkHttpClient(),
    private val gson: Gson = Gson()
) {
    // Call this from a coroutine (e.g., viewModelScope)
    suspend fun streamCompletion(
        iamToken: String,
        modelUri: String,
        systemPrompt: String?,
        userMessage: String,
        onChunk: (text: String) -> Unit,
        onComplete: (usage: Usage?) -> Unit,
        onError: (error: String) -> Unit
    ) {
        val messages = mutableListOf<Message>()
        systemPrompt?.let { messages.add(Message("system", it)) }
        messages.add(Message("user", userMessage))

        val requestBody = CompletionRequest(
            modelUri = modelUri,
            completionOptions = CompletionOptions(
                stream = true,
                temperature = 0.3,
                maxTokens = "2000"
            ),
            messages = messages
        )

        val jsonBody = gson.toJson(requestBody)
        val req = Request.Builder()
            .url("https://llm.api.cloud.yandex.net/foundationModels/v1/completion")
            .addHeader("Authorization", "Bearer $iamToken")
            .post(jsonBody.toRequestBody("application/json".toMediaType()))
            .build()

        withContext(Dispatchers.IO) {
            try {
                client.newCall(req).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        CoroutineScope(Dispatchers.Main).launch {
                            onError(e.message ?: "Network error")
                        }
                    }

                    override fun onResponse(call: Call, response: Response) {
                        if (!response.isSuccessful) {
                            CoroutineScope(Dispatchers.Main).launch {
                                onError("HTTP ${response.code}: ${response.message}")
                            }
                            return
                        }

                        val body = response.body ?: run {
                            CoroutineScope(Dispatchers.Main).launch { onError("Empty body") }
                            return
                        }

                        try {
                            val reader = body.charStream().buffered()
                            var line: String?
                            while (reader.readLine().also { line = it } != null) {
                                val dataLine = line ?: continue
                                if (!dataLine.startsWith("data: ")) continue

                                val json = dataLine.removePrefix("data: ")
                                if (json == "[DONE]") break

                                try {
                                    val chunk = gson.fromJson(json, CompletionResponse::class.java)
                                    val text = chunk.alternatives?.firstOrNull()?.message?.text ?: ""
                                    if (text.isNotEmpty()) {
                                        CoroutineScope(Dispatchers.Main).launch {
                                            onChunk(text)
                                        }
                                    }
                                    // Check for final status
                                    val status = chunk.alternatives?.firstOrNull()?.status
                                    if (status == "ALTERNATIVE_STATUS_FINAL" || status == "ALTERNATIVE_STATUS_TRUNCATED_FINAL") {
                                        CoroutineScope(Dispatchers.Main).launch {
                                            onComplete(chunk.usage)
                                        }
                                        break
                                    }
                                } catch (e: Exception) {
                                    // Skip unparseable chunks (they may be empty or invalid)
                                    continue
                                }
                            }
                            reader.close()
                        } catch (e: Exception) {
                            CoroutineScope(Dispatchers.Main).launch {
                                onError("Stream reading error: ${e.message}")
                            }
                        }
                    }
                })
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError(e.message ?: "Unknown error")
                }
            }
        }
    }
}
```

### 4.3 Usage in a ViewModel

```kotlin
class ChatViewModel : ViewModel() {
    private val gptService = YandexGptStreamingService()
    private val _responseText = MutableLiveData("")
    val responseText: LiveData<String> = _responseText

    fun sendMessage(userInput: String) {
        viewModelScope.launch {
            val currentText = StringBuilder()
            gptService.streamCompletion(
                iamToken = "your-iam-token",
                modelUri = "gpt://<folder-id>/yandexgpt/latest",
                systemPrompt = "You are a helpful assistant.",
                userMessage = userInput,
                onChunk = { chunk ->
                    currentText.append(chunk)
                    _responseText.postValue(currentText.toString())
                },
                onComplete = { usage ->
                    // final usage statistics
                    Log.d("GPT", "Total tokens: ${usage?.totalTokens}")
                },
                onError = { error ->
                    Log.e("GPT", "Error: $error")
                }
            )
        }
    }
}
```

> [!TIP]
> For production apps, consider using a **coroutine‑based** approach with `suspend` and `Flow` for cleaner integration with Kotlin Flow. You can wrap the streaming logic into a `Flow<String>` using `callbackFlow`.

---

## 5. Important Notes

- **Threading**: Streaming responses are handled on `Dispatchers.IO`; UI updates are dispatched to `Dispatchers.Main`.
- **Token Limit**: `maxTokens` is a string (e.g., `"2000"`). Adjust it based on your needs.
- **Error Handling**: Always check `status` in each chunk. If you receive `ALTERNATIVE_STATUS_CONTENT_FILTER`, your prompt or the generated response triggered a content filter. You should modify the input and retry.
- **Model URI**: The `folder-id` is your Yandex Cloud folder ID. Example model URIs:
  - `gpt://b1gxxxxxxxxxxxxxxx/yandexgpt/latest`
  - `gpt://b1gxxxxxxxxxxxxxxx/yandexgpt-lite/latest`
- **Tools & JSON mode**: Tools are not yet supported; ignore the `tools` field. To force JSON output, set `jsonObject = true` and explicitly ask for JSON in the prompt.

---

## 6. Further Reading

- Official API reference: [TextGeneration.Completion](https://yandex.cloud/en/docs/ai-studio/text-generation/api-ref/TextGeneration/completion)
- Available models: [Model IDs](https://yandex.cloud/en/docs/foundation-models/concepts/yandexgpt/models)
- IAM token documentation: [Getting an IAM token](https://yandex.cloud/en/docs/iam/operations/iam-token/create)

This documentation should help you get started quickly with streaming text generation in your Android application. For any questions or advanced scenarios, refer to the official Yandex Cloud documentation.
package com.safecoin.safecoin.domain.model

enum class AiServiceStatus {
    NOT_CONFIGURED,
    READY,
    ANALYZING,
    ERROR,
}

enum class AiProvider(val displayName: String) {
    YANDEX_GPT("YandexGPT"),
}

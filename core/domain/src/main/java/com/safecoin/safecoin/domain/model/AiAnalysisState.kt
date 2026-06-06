package com.safecoin.safecoin.domain.model

data class AiAnalysisResult(
    val report: String,
    val skill: AnalysisSkill,
    val usedCloudAi: Boolean,
    val provider: AiProvider = AiProvider.YANDEX_GPT,
)

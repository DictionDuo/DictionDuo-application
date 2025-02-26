package com.example.dd_app.api

data class AiResponse(
    val accuracy: Float,  // 발음 정확도 (0.85 → 85%)
    val feedback: String  // AI의 피드백 (예: "발음이 자연스럽습니다.")
)

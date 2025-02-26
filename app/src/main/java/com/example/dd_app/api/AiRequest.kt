package com.example.dd_app.api

data class AiRequest(
    val userId: String,  // 사용자 ID
    val audioFileUrl: String  // 분석할 오디오 파일 URL
)

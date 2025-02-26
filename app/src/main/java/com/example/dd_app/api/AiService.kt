package com.example.dd_app.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AiService {
    @Headers("Content-Type: application/json")
    @POST("predict")  // AI 분석 요청을 보낼 서버 API 경로
    fun getPronunciationResult(@Body request: AiRequest): Call<AiResponse>
}

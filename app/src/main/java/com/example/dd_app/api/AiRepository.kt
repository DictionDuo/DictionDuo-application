package com.example.dd_app.api

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object AiRepository {
    fun fetchPronunciationResult(userId: String, audioFileUrl: String, callback: (AiResponse?) -> Unit) {
        val request = AiRequest(userId, audioFileUrl)
        val api = RetrofitClient.instance

        api.getPronunciationResult(request).enqueue(object : Callback<AiResponse> {
            override fun onResponse(call: Call<AiResponse>, response: Response<AiResponse>) {
                if (response.isSuccessful) {
                    callback(response.body())  // 성공하면 결과 전달
                } else {
                    Log.e("AI Error", "서버 응답 오류: ${response.errorBody()?.string()}")
                    callback(null)
                }
            }

            override fun onFailure(call: Call<AiResponse>, t: Throwable) {
                Log.e("AI Error", "네트워크 오류: ${t.message}")
                callback(null)
            }
        })
    }
}


package com.example.androidlab2.network

import android.content.Context
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.*
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

object LambdaApiClient {
    fun requestPronunciationResult(context: Context, fileKey: String, onResult: (String) -> Unit, onError: (Exception) -> Unit) {
        val json = JSONObject().apply {
            put("file_key", fileKey)
        }

        val requestBody = json.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url("https://[].execute-api.ap-northeast-2.amazonaws.com/predictPronunciation")
            .post(requestBody)
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onError(e)
            }

            override fun onResponse(call: Call, response: Response) {
                val responseText = response.body?.string() ?: "응답 없음"
                val jsonResult = JSONObject(responseText)
                val resultText = jsonResult.getString("result")
                onResult(resultText)
            }
        })
    }
}
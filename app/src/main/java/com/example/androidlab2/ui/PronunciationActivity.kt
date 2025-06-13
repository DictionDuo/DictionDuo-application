package com.example.androidlab2.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.example.androidlab2.R
import com.example.androidlab2.audio.AudioRecorder
import com.example.androidlab2.db.RecordHistoryEntity
import com.example.androidlab2.db.RecordViewModel
import com.example.androidlab2.network.LambdaApiClient
import com.example.androidlab2.s3.S3Uploader
import com.example.androidlab2.ui.RecordingActivity
import com.example.androidlab2.util.PermissionUtils
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class PronunciationActivity : AppCompatActivity() {

    private val viewModel: RecordViewModel by viewModels()

    private lateinit var micButton: ImageButton
    private lateinit var recordingInstruction: TextView
    private lateinit var recordingsListButton: Button

    private lateinit var recorder: AudioRecorder
    private lateinit var uploader: S3Uploader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pronunciation)

        micButton = findViewById(R.id.mic_button)
        recordingInstruction = findViewById(R.id.recording_instruction)
        recordingsListButton = findViewById(R.id.recordings_list_button)

        recorder = AudioRecorder(this)
        uploader = S3Uploader(applicationContext)

        PermissionUtils.checkPermissions(this, recordingInstruction)

        micButton.setOnClickListener {
            if (recorder.outputFile.isNotEmpty()) {
                stopRecording()
            } else {
                if (recorder.startRecording()) {
                    micButton.setImageResource(R.drawable.ic_btn_speak_now)
                    recordingInstruction.text = "녹음 중입니다."
                } else {
                    recordingInstruction.text = "녹음 시작 오류가 발생했습니다."
                }
            }
        }

        recordingsListButton.setOnClickListener {
            startActivity(Intent(this, RecordingActivity::class.java))
        }
    }

    private fun stopRecording() {
        val recordedFile = recorder.stopRecording()
        if (recordedFile != null) {
            micButton.setImageResource(R.drawable.ic_loading)
            recordingInstruction.text = "발음 분석중..."

            uploader.uploadFile(recordedFile,
                onComplete = { key ->
                    requestPronunciationAndStoreResult(key, recordedFile)
                },
                onError = { e ->
                    showToast("업로드 실패: ${e.message}")
                })
        } else {
            recordingInstruction.text = "녹음 중 오류가 발생했습니다."
        }
    }

    private fun requestPronunciationAndStoreResult(key: String, recordedFile: File) {
        LambdaApiClient.requestPronunciationResult(
            context = this,
            fileKey = key,
            onResult = { result ->
                runOnUiThread {
                    handleResult(result, recordedFile)
                }
            },
            onError = { e ->
                showToast("API 호출 실패: ${e.message}")
            }
        )
    }

    private fun handleResult(result: String, recordedFile: File) {
        val record = RecordHistoryEntity(
            title = recordedFile.name,
            time = recordedFile.lastModified().toString(),
            testResult = result,
            filePath = recordedFile.absolutePath
        )
        viewModel.insertRecord(record)

        micButton.setImageResource(R.drawable.ic_result)
        recordingInstruction.text = "결과: $result"
    }

    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}

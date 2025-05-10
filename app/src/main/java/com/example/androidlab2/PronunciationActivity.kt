package com.example.androidlab2

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.mobileconnectors.s3.transferutility.*
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class PronunciationActivity : AppCompatActivity() {

    private lateinit var micButton: ImageButton
    private lateinit var recordingInstruction: TextView
    private lateinit var recordingsListButton: Button
    private var isRecording = false
    private var mediaRecorder: MediaRecorder? = null
    private lateinit var outputFile: String

    private val RECORD_AUDIO_PERMISSION_CODE = 100
    private val WRITE_STORAGE_PERMISSION_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pronunciation)

        // UI 요소 연결
        micButton = findViewById(R.id.mic_button)
        recordingInstruction = findViewById(R.id.recording_instruction)
        recordingsListButton = findViewById(R.id.recordings_list_button)

        // 권한 체크 및 요청
        checkPermissions()

        // 마이크 버튼 클릭 리스너 (발음 녹음 시작 및 종료)
        micButton.setOnClickListener {
            if (isRecording) {
                stopRecording()
            } else {
                startRecording()
            }
        }

        recordingsListButton.setOnClickListener {
            val intent = Intent(this, RecordingActivity::class.java)
            startActivity(intent)
        }
    }

    // 권한 체크 및 요청
    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), RECORD_AUDIO_PERMISSION_CODE)
        } else {
            recordingInstruction.text = "마이크 버튼을 클릭하여 발음을 녹음하세요."
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), WRITE_STORAGE_PERMISSION_CODE)
        }
    }

    // 권한 요청 결과 처리
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RECORD_AUDIO_PERMISSION_CODE || requestCode == WRITE_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                recordingInstruction.text = "마이크 버튼을 클릭하여 발음을 녹음하세요."
            } else {
                recordingInstruction.text = "마이크 버튼을 클릭하여 발음을 녹음하세요."
            }
        }
    }

    // 녹음 시작
    private fun startRecording() {
        try {
            val dir = File(getExternalFilesDir(null), "Recordings")  // 앱 외부 저장소의 "Recordings" 폴더
            if (!dir.exists()) {
                dir.mkdirs()
            }
            val currentTime = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault()).format(Date())
            outputFile = "${dir.absolutePath}/recording_$currentTime.3gp"

            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setOutputFile(outputFile)
                prepare()
                start()
            }

            // UI 업데이트
            isRecording = true
            micButton.setImageResource(R.drawable.ic_btn_speak_now)
            recordingInstruction.text = "녹음 중입니다."

        } catch (e: IOException) {
            e.printStackTrace()
            recordingInstruction.text = "녹음 시작 오류가 발생했습니다."
        }
    }

    // 녹음 중지
    private fun stopRecording() {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null

            // UI 업데이트
            isRecording = false
            micButton.setImageResource(R.drawable.ic_voice)
            recordingInstruction.text = "마이크 버튼을 클릭하여 발음을 녹음하세요."

            uploadFileToS3()

        } catch (e: Exception) {
            e.printStackTrace()
            recordingInstruction.text = "녹음 중 오류가 발생했습니다."
        }
    }

    // S3에 녹음 파일 업로드
    private fun uploadFileToS3() {
        try {
            // val awsAccessKey =
            // val awsSecretKey =

            val credentials = BasicAWSCredentials(awsAccessKey, awsSecretKey)
            val s3Client = AmazonS3Client(credentials)
            s3Client.setRegion(com.amazonaws.regions.Region.getRegion(Regions.AP_NORTHEAST_2))

            val transferUtility = TransferUtility.builder()
                .context(applicationContext)
                .s3Client(s3Client)
                .defaultBucket("app-request-bucket")
                .build()

            val file = File(outputFile)
            val key = file.name

            val uploadObserver = transferUtility.upload(key, file)

            uploadObserver.setTransferListener(object : TransferListener {
                override fun onStateChanged(id: Int, state: TransferState?) {
                    if (state == TransferState.COMPLETED) {
                        Log.d("AWS", "업로드 완료: $key")
                        Toast.makeText(applicationContext, "업로드 완료", Toast.LENGTH_SHORT).show()
                    } else if (state == TransferState.FAILED) {
                        Log.e("AWS", "업로드 실패")
                        Toast.makeText(applicationContext, "업로드 실패", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                    val percentDone = (bytesCurrent.toDouble() / bytesTotal * 100).toInt()
                    Log.d("AWS", "진행률: $percentDone%")
                }

                override fun onError(id: Int, ex: Exception?) {
                    ex?.printStackTrace()
                    Toast.makeText(applicationContext, "에러 발생: ${ex?.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(applicationContext, "예외 발생: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

}

package com.example.androidlab2  // 패키지명 맞추기

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.collection.emptyLongSet
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
    private lateinit var outputFile: String  // 녹음 파일 경로 변수

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
        // 권한이 없으면 요청
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), RECORD_AUDIO_PERMISSION_CODE)
        } else {
            // 권한이 이미 승인되었으면 첫 화면 상태 설정
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
                // 권한이 승인되었을 때 첫 화면 상태 설정
                recordingInstruction.text = "마이크 버튼을 클릭하여 발음을 녹음하세요."
            } else {
                // 권한이 거부되었을 때 처리할 로직
                recordingInstruction.text = "마이크 버튼을 클릭하여 발음을 녹음하세요."
            }
        }
    }

    // 녹음 시작
    private fun startRecording() {
        try {
            // 파일 경로 설정 (앱 전용 외부 저장소에 "Recordings" 폴더 만들기)
            val dir = File(getExternalFilesDir(null), "Recordings")  // 앱 외부 저장소의 "Recordings" 폴더
            if (!dir.exists()) {
                dir.mkdirs()  // 폴더가 없으면 생성
            }

            // 현재 시간으로 파일명을 설정 (녹음 시작 시간 기준)
            val currentTime = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault()).format(Date())
            outputFile = "${dir.absolutePath}/recording_$currentTime.3gp"  // 파일명에 날짜 및 시간 추가

            // MediaRecorder 설정
            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)  // 마이크에서 오디오 입력
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)  // 파일 포맷 설정
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)  // 오디오 인코딩 설정
                setOutputFile(outputFile)  // 저장할 파일 경로 지정
                prepare()  // 초기화
                start()  // 녹음 시작
            }

            // UI 업데이트
            isRecording = true
            micButton.setImageResource(R.drawable.ic_btn_speak_now)  // 녹음 중 아이콘 변경
            recordingInstruction.text = "녹음 중입니다."  // 안내 문구 변경

        } catch (e: IOException) {
            e.printStackTrace()
            recordingInstruction.text = "녹음 시작 오류가 발생했습니다."  // 오류 처리
        }
    }

    // 녹음 멈추기
    private fun stopRecording() {
        try {
            mediaRecorder?.apply {
                stop()  // 녹음 종료
                release()  // 리소스 해제
            }
            mediaRecorder = null  // 객체 초기화

            // UI 업데이트
            isRecording = false
            micButton.setImageResource(R.drawable.ic_voice)  // 원래의 마이크 아이콘으로 되돌리기
            recordingInstruction.text = "마이크 버튼을 클릭하여 발음을 녹음하세요."  // 원래 문구로 변경

        } catch (e: Exception) {
            e.printStackTrace()
            recordingInstruction.text = "녹음 중 오류가 발생했습니다."  // 오류 발생 시 안내 문구
        }
    }
}
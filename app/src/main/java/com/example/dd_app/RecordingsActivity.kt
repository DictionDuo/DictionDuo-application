package com.example.dd_app

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class RecordingsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecordingAdapter
    private lateinit var accuracyButton: Button
    private lateinit var recordButton: Button
    private val recordings = mutableListOf<File>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recording_list)

        initializeViews()

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = RecordingAdapter(recordings) { playRecording(it) }
        recyclerView.adapter = adapter

        loadRecordings()

        accuracyButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        recordButton.setOnClickListener {
            startActivity(Intent(this, RecordingsActivity::class.java))
        }
    }

    private fun initializeViews() {
        recyclerView = findViewById(R.id.recording_recycler_view)
        accuracyButton = findViewById(R.id.accuracy_button)
        recordButton = findViewById(R.id.record_button)
    }

    private fun loadRecordings() {
        val directory = filesDir
        recordings.clear()
        recordings.addAll(directory.listFiles { file -> file.extension == "mp3" } ?: emptyArray())
        adapter.notifyDataSetChanged()
    }

    private fun playRecording(file: File) {
        try {
            val mediaPlayer = MediaPlayer().apply {
                setDataSource(file.absolutePath)
                prepare()
                start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "재생 실패", Toast.LENGTH_SHORT).show()
        }
    }
}




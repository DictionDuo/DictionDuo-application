package com.example.androidlab2.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidlab2.R
import com.example.androidlab2.model.RecordingItem
import com.example.androidlab2.audio.AudioPlayer
import com.example.androidlab2.ui.adapter.RecordingAdapter
import com.example.androidlab2.util.RecordingFileManager
import com.example.androidlab2.util.RecordingSorter
import java.io.File

class RecordingActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecordingAdapter
    private lateinit var spinnerSort: Spinner
    private lateinit var btnSortToggle: ImageButton
    private lateinit var pronunciationButton: Button

    private lateinit var audioPlayer: AudioPlayer

    private val recordings = mutableListOf<RecordingItem>()
    private var isAscending = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recording)

        recyclerView = findViewById(R.id.recording_recycler_view)
        spinnerSort = findViewById(R.id.spinner_sort)
        btnSortToggle = findViewById(R.id.btn_sort_toggle)
        pronunciationButton = findViewById(R.id.pronunciation_button)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = RecordingAdapter(recordings, ::playRecording, ::showEditFileNameDialog, ::removeRecording)
        recyclerView.adapter = adapter

        audioPlayer = AudioPlayer(this)

        val sortOptions = listOf("시간순", "이름순")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sortOptions)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSort.adapter = spinnerAdapter

        spinnerSort.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> sortByTime()
                    1 -> sortByName()
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {}
        }

        btnSortToggle.setOnClickListener {
            isAscending = !isAscending
            if (spinnerSort.selectedItemPosition == 0) {
                sortByTime()
            } else {
                sortByName()
            }
        }

        pronunciationButton.setOnClickListener {
            startActivity(Intent(this, PronunciationActivity::class.java))
        }

        loadRecordingFiles()
    }

    override fun onDestroy() {
        super.onDestroy()
        audioPlayer.release()
    }

    private fun sortByTime() {
        RecordingSorter.sortByTime(recordings, isAscending)
        adapter.notifyDataSetChanged()
    }

    private fun sortByName() {
        RecordingSorter.sortByName(recordings, isAscending)
        adapter.notifyDataSetChanged()
    }

    private fun loadRecordingFiles() {
        recordings.clear()
        val dir = getExternalFilesDir(null)?.resolve("Recordings") ?: return
        recordings.addAll(RecordingFileManager.loadRecordings(dir))
        adapter.notifyDataSetChanged()
    }

    private fun playRecording(file: File) {
        audioPlayer.play(file)
    }

    private fun showEditFileNameDialog(position: Int) {
        val recording = recordings[position]
        val editText = EditText(this)
        editText.setText(recording.name)

        AlertDialog.Builder(this)
            .setCustomTitle(View(this))
            .setView(editText)
            .setPositiveButton("확인") { _, _ ->
                val newFileName = editText.text.toString()
                val renamedItem = RecordingFileManager.rename(recording, newFileName)
                if (renamedItem != null) {
                    recordings[position] = renamedItem
                    adapter.notifyItemChanged(position)
                } else {
                    Toast.makeText(this, "파일명 수정에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("취소", null)
            .setCancelable(false)
            .create()
            .show()
    }

    private fun removeRecording(position: Int) {
        val item = recordings[position]
        if (RecordingFileManager.delete(item)) {
            recordings.removeAt(position)
            adapter.notifyItemRemoved(position)
        }
    }
}

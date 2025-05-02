package com.example.androidlab2

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class RecordingActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecordingAdapter
    private lateinit var spinnerSort: Spinner
    private lateinit var btnSortToggle: ImageButton
    private lateinit var pronunciationButton: Button

    private val recordings = mutableListOf<RecordingItem>()
    private var isAscending = true
    private var mediaPlayer: MediaPlayer? = null  // ✅ MediaPlayer 선언

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recording)

        recyclerView = findViewById(R.id.recording_recycler_view)
        spinnerSort = findViewById(R.id.spinner_sort)
        btnSortToggle = findViewById(R.id.btn_sort_toggle)
        pronunciationButton = findViewById(R.id.pronunciation_button)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = RecordingAdapter(recordings)
        recyclerView.adapter = adapter

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
            val intent = Intent(this, PronunciationActivity::class.java)
            startActivity(intent)
        }

        loadRecordingFiles()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun sortByTime() {
        if (isAscending) {
            recordings.sortBy { it.date }
        } else {
            recordings.sortByDescending { it.date }
        }
        adapter.notifyDataSetChanged()
    }

    private fun sortByName() {
        if (isAscending) {
            recordings.sortBy { it.name }
        } else {
            recordings.sortByDescending { it.name }
        }
        adapter.notifyDataSetChanged()
    }

    private fun loadRecordingFiles() {
        val dir = File(getExternalFilesDir(null), "Recordings")
        val files = dir.listFiles { _, name -> name.endsWith(".3gp") }

        if (files != null) {
            for (file in files) {
                recordings.add(RecordingItem(file.name, file.lastModified(), file))
            }
        }
        adapter.notifyDataSetChanged()
    }

    private fun playRecording(file: File) {
        mediaPlayer?.release()
        mediaPlayer = null

        mediaPlayer = MediaPlayer().apply {
            try {
                setDataSource(file.absolutePath)
                prepare()
                start()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@RecordingActivity, "오디오 재생 실패", Toast.LENGTH_SHORT).show()
            }
        }

        mediaPlayer?.setOnCompletionListener {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    private fun showEditFileNameDialog(position: Int) {
        val recording = recordings[position]
        val builder = AlertDialog.Builder(this)
        builder.setCustomTitle(View(this))  // 타이틀 제거

        val editText = EditText(this)
        editText.setText(recording.name)

        builder.setView(editText)
            .setPositiveButton("확인") { _, _ ->
                val newFileName = editText.text.toString()
                val newFile = File(recording.file.parent, newFileName)

                val renamed = recording.file.renameTo(newFile)
                if (renamed) {
                    recordings[position] = recording.copy(name = newFileName, file = newFile)
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

    data class RecordingItem(val name: String, val date: Long, val file: File)

    inner class RecordingAdapter(private val recordings: MutableList<RecordingItem>) :
        RecyclerView.Adapter<RecordingAdapter.RecordingViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordingViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.recording_item, parent, false)
            return RecordingViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecordingViewHolder, position: Int) {
            val recording = recordings[position]

            holder.recordingName.text = recording.name
            holder.recordingTime.text = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(recording.date))

            holder.deleteButton.setOnClickListener {
                if (recording.file.exists()) {
                    val deleted = recording.file.delete()
                    if (deleted) {
                        recordings.removeAt(position)
                        notifyItemRemoved(position)
                    }
                }
            }

            holder.recordingName.setOnClickListener {
                showEditFileNameDialog(position)
            }

            // ✅ 재생 기능 연결
            holder.playButton.setOnClickListener {
                playRecording(recording.file)
            }
        }

        override fun getItemCount(): Int = recordings.size

        inner class RecordingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val recordingName: TextView = itemView.findViewById(R.id.recording_name)
            val recordingTime: TextView = itemView.findViewById(R.id.recording_time)
            val playButton: ImageButton = itemView.findViewById(R.id.play_button)       // ✅ 추가
            val deleteButton: ImageButton = itemView.findViewById(R.id.delete_button)
        }
    }
}

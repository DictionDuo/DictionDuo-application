package com.example.androidlab2.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.androidlab2.R
import com.example.androidlab2.model.RecordingItem
import java.text.SimpleDateFormat
import java.util.*

class RecordingAdapter(
    private val recordings: MutableList<RecordingItem>,
    private val onPlay: (file: java.io.File) -> Unit,
    private val onRename: (position: Int) -> Unit,
    private val onDelete: (position: Int) -> Unit
) : RecyclerView.Adapter<RecordingAdapter.RecordingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recording_item, parent, false)
        return RecordingViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecordingViewHolder, position: Int) {
        val recording = recordings[position]

        holder.recordingName.text = recording.name
        holder.recordingTime.text = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(recording.date))

        holder.playButton.setOnClickListener { onPlay(recording.file) }
        holder.recordingName.setOnClickListener { onRename(position) }
        holder.deleteButton.setOnClickListener { onDelete(position) }
    }

    override fun getItemCount(): Int = recordings.size

    inner class RecordingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recordingName: TextView = itemView.findViewById(R.id.recording_name)
        val recordingTime: TextView = itemView.findViewById(R.id.recording_time)
        val playButton: ImageButton = itemView.findViewById(R.id.play_button)
        val deleteButton: ImageButton = itemView.findViewById(R.id.delete_button)
    }
}
package com.example.androidlab2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecordingAdapter(private val recordings: List<RecordingItem>) : RecyclerView.Adapter<RecordingAdapter.RecordingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recording_item, parent, false)
        return RecordingViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecordingViewHolder, position: Int) {
        val recording = recordings[position]
        holder.recordingName.text = recording.name
        holder.recordingTime.text = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(
            Date(recording.date)
        )
    }

    override fun getItemCount(): Int = recordings.size

    inner class RecordingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recordingName: TextView = itemView.findViewById(R.id.recording_name)
        val recordingTime: TextView = itemView.findViewById(R.id.recording_time)
    }
}

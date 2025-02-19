package com.example.dd_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class RecordingAdapter(
    private val recordings: List<File>,
    private val onItemClick: (File) -> Unit
) : RecyclerView.Adapter<RecordingAdapter.RecordingViewHolder>() {

    private var selectedPosition: Int = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recording_item, parent, false)
        return RecordingViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecordingViewHolder, position: Int) {
        val file = recordings[position]
        holder.bind(file, position == selectedPosition, onItemClick)

        holder.itemView.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = if (selectedPosition == position) RecyclerView.NO_POSITION else position
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)
        }
    }

    override fun getItemCount(): Int = recordings.size

    class RecordingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val recordingTime: TextView = itemView.findViewById(R.id.recording_time)
        private val accuracyText: TextView = itemView.findViewById(R.id.accuracy_text)
        private val playButton: ImageButton = itemView.findViewById(R.id.play_button)

        fun bind(file: File, isSelected: Boolean, onItemClick: (File) -> Unit) {
            recordingTime.text = formatDate(file.lastModified())
            accuracyText.text = "정확도: ${getRandomAccuracy()}%" // 정확도 AI 연동 시 수정 예정
            playButton.visibility = if (isSelected) View.VISIBLE else View.GONE
            playButton.setOnClickListener { onItemClick(file) }
        }

        private fun formatDate(timestamp: Long): String {
            val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }

        private fun getRandomAccuracy(): Int {
            return (50..100).random()
        }
    }
}



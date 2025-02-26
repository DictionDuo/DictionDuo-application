package com.example.dd_app

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class RecordingAdapter(
    private val context: Context,
    private val recordings: MutableList<File>,
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

    inner class RecordingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val recordingName: EditText = itemView.findViewById(R.id.recording_name)
        private val recordingTime: TextView = itemView.findViewById(R.id.recording_time)
        private val accuracyText: TextView = itemView.findViewById(R.id.accuracy_text)
        private val playButton: ImageButton = itemView.findViewById(R.id.play_button)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.delete_button)

        fun bind(file: File, isSelected: Boolean, onItemClick: (File) -> Unit) {
            recordingName.setText(file.nameWithoutExtension)
            recordingTime.text = formatDate(file.lastModified())
            accuracyText.text = "정확도: ${getRandomAccuracy()}%"
            playButton.visibility = if (isSelected) View.VISIBLE else View.GONE

            playButton.setOnClickListener { onItemClick(file) }

            // 파일 삭제 버튼 클릭 시 삭제 기능 실행
            deleteButton.setOnClickListener {
                deleteRecording(file, adapterPosition)
            }

            // 파일명 수정 후 Enter 입력 시 변경 적용
            recordingName.setOnEditorActionListener { _, _, _ ->
                renameRecording(file, recordingName.text.toString())
                true
            }
        }

        private fun deleteRecording(file: File, position: Int) {
            if (file.exists()) {
                file.delete()
                recordings.removeAt(position)
                notifyItemRemoved(position)
                Toast.makeText(context, "파일 삭제됨", Toast.LENGTH_SHORT).show()
            }
        }

        private fun renameRecording(file: File, newName: String) {
            val newFile = File(file.parent, "$newName.mp3")
            if (file.exists() && !newFile.exists()) {
                val renamed = file.renameTo(newFile)
                if (renamed) {
                    recordings[adapterPosition] = newFile
                    notifyItemChanged(adapterPosition)
                    Toast.makeText(context, "파일명 변경됨", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "파일명 변경 실패", Toast.LENGTH_SHORT).show()
                }
            }
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



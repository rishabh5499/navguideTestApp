package com.example.testapplication

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.testapplication.R
import com.example.testapplication.databinding.ItemDownloadTopicBinding
import com.example.testapplication.payload.Topic
import java.io.File

class DownloadAdapter(
    private val topics: List<Topic>,
    private val downloadActivity: DownloadActivity
) : RecyclerView.Adapter<DownloadAdapter.DownloadViewHolder>() {

    private val progressMap = mutableMapOf<String, Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DownloadViewHolder {
        val binding = ItemDownloadTopicBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DownloadViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DownloadViewHolder, position: Int) {
        val topic = topics[position]
        val progress = progressMap.getOrDefault(topic.name, 0)
        holder.bind(topic, progress)
    }

    override fun getItemCount(): Int = topics.size

    fun updateProgress(topic: Topic, progress: Int) {
        progressMap[topic.name] = progress
        val position = topics.indexOf(topic)
        if (position != -1) {
            notifyItemChanged(position)
        }
    }

    inner class DownloadViewHolder(private val binding: ItemDownloadTopicBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(topic: Topic, progress: Int) {
            binding.downloadTopicName.text = topic.name

            val topicImagesDir = File(itemView.context.filesDir, "images/${topic.name}")
            val isDownloaded = topicImagesDir.exists() && topicImagesDir.isDirectory && topicImagesDir.list()?.isNotEmpty() == true

            when {
                isDownloaded -> {
                    binding.downloadStatusText.text = "Downloaded"
                    binding.downloadStatusText.setTextColor(itemView.context.getColor(R.color.md_theme_tertiary))
                    binding.downloadProgressContainer.visibility = View.GONE
                    binding.downloadButton.visibility = View.VISIBLE
                    binding.buttonProgressSpinner.visibility = View.GONE
                    binding.downloadButton.text = "View"
                    binding.downloadButton.isEnabled = true
                    binding.downloadButton.setOnClickListener {
                        val intent = Intent(itemView.context, SubtopicActivity::class.java).apply {
                            putExtra("topic_name", topic.name)
                            putExtra("start_id", topic.startId)
                            putExtra("end_id", topic.endId)
                        }
                        itemView.context.startActivity(intent)
                    }
                }
                progress == -1 -> {
                    binding.downloadStatusText.text = "Download Failed"
                    binding.downloadStatusText.setTextColor(itemView.context.getColor(R.color.md_theme_error))
                    binding.downloadProgressContainer.visibility = View.GONE
                    binding.downloadButton.visibility = View.VISIBLE
                    binding.buttonProgressSpinner.visibility = View.GONE
                    binding.downloadButton.text = "Retry"
                    binding.downloadButton.isEnabled = true
                    binding.downloadButton.setOnClickListener {
                        downloadActivity.startDownload(topic)
                    }
                }
                progress > 0 && progress < 100 -> {
                    binding.downloadStatusText.text = "Downloading... $progress%"
                    binding.downloadStatusText.setTextColor(itemView.context.getColor(R.color.md_theme_primary))
                    binding.downloadProgressContainer.visibility = View.VISIBLE
                    binding.downloadProgressBar.progress = progress
                    binding.progressPercentageText.text = "$progress%"
                    binding.downloadButton.visibility = View.GONE
                    binding.buttonProgressSpinner.visibility = View.VISIBLE
                }
                else -> {
                    binding.downloadStatusText.text = "Ready to download"
                    binding.downloadStatusText.setTextColor(itemView.context.getColor(R.color.md_theme_onSurfaceVariant))
                    binding.downloadProgressContainer.visibility = View.GONE
                    binding.downloadButton.visibility = View.VISIBLE
                    binding.buttonProgressSpinner.visibility = View.GONE
                    binding.downloadButton.text = "Download"
                    binding.downloadButton.isEnabled = true
                    binding.downloadButton.setOnClickListener {
                        downloadActivity.startDownload(topic)
                    }
                }
            }
        }
    }
}
package com.example.testapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.work.WorkInfo
import com.bumptech.glide.Glide
import com.example.testapplication.R
import com.example.testapplication.databinding.ItemTopicBinding
import com.example.testapplication.payload.Topic
import java.io.File

class TopicAdapter(
    private val topics: List<Topic>,
    private val onTopicClick: (Topic) -> Unit
) : RecyclerView.Adapter<TopicAdapter.TopicViewHolder>() {

    private val workInfoMap = mutableMapOf<String, WorkInfo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder {
        val binding = ItemTopicBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TopicViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TopicViewHolder, position: Int) {
        holder.bind(topics[position])
    }

    override fun getItemCount(): Int = topics.size

    fun updateProgress(topic: Topic, workInfo: WorkInfo) {
        workInfoMap[topic.name] = workInfo
        val position = topics.indexOf(topic)
        if (position != -1) {
            notifyItemChanged(position)
        }
    }

    inner class TopicViewHolder(private val binding: ItemTopicBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(topic: Topic) {
            binding.topicName.text = topic.name

            val topicImagesDir = File(itemView.context.filesDir, "images/${topic.name}")
            val isDownloaded = topicImagesDir.exists() && topicImagesDir.isDirectory && topicImagesDir.list()?.isNotEmpty() == true

            val workInfo = workInfoMap[topic.name]
            val progress = workInfo?.progress?.getInt(DownloadWorker.PROGRESS_KEY, 0)

            when {
                isDownloaded -> {
                    binding.viewButton.text = "View"
                    binding.downloadProgressContainer.visibility = View.GONE

                    val imageFiles = topicImagesDir.listFiles { file -> file.name.endsWith(".png") || file.name.endsWith(".jpg") }
                    val firstImageFile = imageFiles?.minByOrNull { it.name }

                    if (firstImageFile != null) {
                        Glide.with(itemView.context)
                            .load(firstImageFile)
                            .centerCrop()
                            .into(binding.topicThumbnail)
                    } else {
//                        binding.topicThumbnail.setImageResource(R.drawable.placeholder_image)
                    }

                    binding.viewButton.setOnClickListener {
                        onTopicClick(topic)
                    }
                }
                workInfo?.state == WorkInfo.State.RUNNING -> {
                    // Downloading State
                    binding.viewButton.text = "Downloading..."
                    binding.downloadProgressContainer.visibility = View.VISIBLE
                    binding.downloadProgressBar.progress = progress ?: 0
                    binding.progressPercentageText.text = "$progress%"
//                    binding.topicThumbnail.setImageResource(R.drawable.placeholder_image)
                    binding.viewButton.setOnClickListener(null)
                }
                else -> {
                    // Initial or Failed State
                    binding.viewButton.text = "Download"
                    binding.downloadProgressContainer.visibility = View.GONE
//                    binding.topicThumbnail.setImageResource(R.drawable.placeholder_image)
                    binding.viewButton.setOnClickListener {
                        onTopicClick(topic)
                    }
                }
            }
        }
    }
}
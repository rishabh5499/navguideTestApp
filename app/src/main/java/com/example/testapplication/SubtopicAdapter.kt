package com.example.testapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.testapplication.databinding.ItemSubtopicGridBinding
import java.io.File

class SubtopicAdapter(
    private val imagePaths: List<String>,
    private val startId: Int,
    private val context: Context,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<SubtopicAdapter.SubtopicViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubtopicViewHolder {
        val binding = ItemSubtopicGridBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SubtopicViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SubtopicViewHolder, position: Int) {
        val imagePath = imagePaths[position]
        holder.bind(imagePath, position)
    }

    override fun getItemCount(): Int = imagePaths.size

    inner class SubtopicViewHolder(private val binding: ItemSubtopicGridBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(imagePath: String, position: Int) {
            val imageFile = File(imagePath)

            Glide.with(itemView.context)
                .load(imageFile)
                .centerCrop()
                .into(binding.subtopicThumbnail)

            val displayId = if (startId == 1) {
                position + 1
            } else {
                startId + position
            }

            binding.subtopicName.text = context.getString(R.string.subtopic, displayId)

            binding.root.setOnClickListener {
                onItemClick(imagePath)
            }
        }
    }
}

//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
//import com.example.testapplication.databinding.ItemSubtopicGridBinding
//import java.io.File
//
//class SubtopicAdapter(
//    private val imagePaths: List<String>,
//    private val topicName: String,
//    private val onItemClick: (String) -> Unit
//) : RecyclerView.Adapter<SubtopicAdapter.SubtopicViewHolder>() {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubtopicViewHolder {
//        val binding = ItemSubtopicGridBinding.inflate(
//            LayoutInflater.from(parent.context),
//            parent,
//            false
//        )
//        return SubtopicViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: SubtopicViewHolder, position: Int) {
//        val imagePath = imagePaths[position]
//        holder.bind(imagePath)
//    }
//
//    override fun getItemCount(): Int = imagePaths.size
//
//    inner class SubtopicViewHolder(private val binding: ItemSubtopicGridBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//
//        fun bind(imagePath: String) {
//            val imageFile = File(imagePath)
//            val fileName = imageFile.nameWithoutExtension
//            val subtopicId = fileName.substringAfter("image-").toIntOrNull()
//
//            // Load thumbnail using Glide
//            Glide.with(itemView.context)
//                .load(imageFile)
//                .centerCrop()
//                .into(binding.subtopicThumbnail)
//
//            binding.subtopicName.text = "Image #$subtopicId"
//            binding.subtopicDescription.text = "A downloaded image from this topic."
//
//            binding.root.setOnClickListener {
//                onItemClick(imagePath)
//            }
//        }
//    }
//}
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
//import com.example.testapplication.databinding.ItemSubtopicGridBinding
//import java.io.File
//
//class SubtopicAdapter(
//    private val subtopics: List<String>,
//    private val topicName: String,
//    private val onItemClick: (String) -> Unit
//) : RecyclerView.Adapter<SubtopicAdapter.SubtopicViewHolder>() {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubtopicViewHolder {
//        val binding = ItemSubtopicGridBinding.inflate(
//            LayoutInflater.from(parent.context),
//            parent,
//            false
//        )
//        return SubtopicViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: SubtopicViewHolder, position: Int) {
//        val subtopic = subtopics[position]
//        holder.bind(subtopic)
//    }
//
//    override fun getItemCount(): Int = subtopics.size
//
//    inner class SubtopicViewHolder(private val binding: ItemSubtopicGridBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//
//        fun bind(subtopic: String) {
//            val subtopicId = subtopic.substringAfter("subtopic-").toIntOrNull()
//            val imageName = "image-$subtopicId.jpg"
//            val imageFile = File(itemView.context.filesDir, "images/$topicName/$imageName")
//
//            if (imageFile.exists()) {
//                Glide.with(itemView.context)
//                    .load(imageFile)
//                    .centerCrop()
//                    .into(binding.subtopicThumbnail)
//            }
//
//            binding.subtopicName.text = buildString {
//                append("Subtopic #")
//                append(subtopicId)
//            }
//
//            binding.root.setOnClickListener {
//                onItemClick(subtopic)
//            }
//        }
//    }
//}
package com.example.testapplication

import android.content.Context
import android.util.Log
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
                .placeholder(R.drawable.topic2)
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
package com.example.testapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SubtopicAdapter(
    private val subtopics: List<String>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<SubtopicAdapter.SubtopicViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubtopicViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return SubtopicViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubtopicViewHolder, position: Int) {
        val subtopic = subtopics[position]
        holder.bind(subtopic)
    }

    override fun getItemCount(): Int = subtopics.size

    inner class SubtopicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val subtopicName: TextView = itemView.findViewById(android.R.id.text1)

        fun bind(subtopic: String) {
            subtopicName.text = subtopic
            itemView.setOnClickListener { onItemClick(subtopic) }
        }
    }
}
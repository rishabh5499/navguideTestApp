package com.example.testapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.testapplication.payload.Topic

class TopicAdapter(
    private val topics: List<Topic>,
    private val onItemClick: (Topic) -> Unit
) : RecyclerView.Adapter<TopicAdapter.TopicViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return TopicViewHolder(view)
    }

    override fun onBindViewHolder(holder: TopicViewHolder, position: Int) {
        val topic = topics[position]
        holder.bind(topic)
    }

    override fun getItemCount(): Int = topics.size

    inner class TopicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val topicName: TextView = itemView.findViewById(android.R.id.text1)

        fun bind(topic: Topic) {
            topicName.text = topic.name
            itemView.setOnClickListener { onItemClick(topic) }
        }
    }
}
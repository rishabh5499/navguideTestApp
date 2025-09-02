package com.example.testapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.testapplication.R
import com.example.testapplication.databinding.ActivityMainBinding
import com.example.testapplication.payload.Topic

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var topicAdapter: TopicAdapter
    private lateinit var topics: List<Topic>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Define the list of topics
        topics = listOf(
            Topic("Topic 1: Nature", "https://navreports.blob.core.windows.net/navtestmedia/topic2.zip?sp=r&st=2024-11-09T13:34:59Z&se=2025-12-30T21:34:59Z&spr=https&sv=2022-11-02&sr=b&sig=xTfY1Vh15FekUvD3QOI2mq45hIp6utyrhdscf6f2Nuw%3D", 1, 200, "Stunning landscapes and vibrant flora and fauna."),
            Topic("Topic 2: Architecture", "https://navreports.blob.core.windows.net/navtestmedia/topic2.zip?sp=r&st=2024-11-09T13:34:59Z&se=2025-12-30T21:34:59Z&spr=https&sv=2022-11-02&sr=b&sig=xTfY1Vh15FekUvD3QOI2mq45hIp6utyrhdscf6f2Nuw%3D", 201, 400, "Iconic buildings and modern design marvels."),
            Topic("Topic 3: Wildlife", "https://navreports.blob.core.windows.net/navtestmedia/topic3.zip?sp=r&st=2024-11-09T13:35:23Z&se=2025-12-30T21:35:23Z&spr=https&sv=2022-11-02&sr=b&sig=iWrw6FtIqG9mESIDEw2DtLXe%2BC4Be4ieZerdbv8DoOk%3D", 401, 706, "Captivating images of animals in their natural habitats.")
        )

        // Set up the RecyclerView
        binding.topicList.layoutManager = LinearLayoutManager(this)
        topicAdapter = TopicAdapter(topics) { topic ->
//            val intent = Intent(this, SubtopicActivity::class.java).apply {
//                putExtra("topic_name", topic.name)
//                putExtra("start_id", topic.startId)
//                putExtra("end_id", topic.endId)
//            }
//            startActivity(intent)
        }
        binding.topicList.adapter = topicAdapter

        // Set up the Download button click listener
        binding.downloadButton.setOnClickListener {
            val intent = Intent(this, DownloadActivity::class.java)
            startActivity(intent)
        }
    }
}
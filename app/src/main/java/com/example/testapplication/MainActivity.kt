package com.example.testapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.*
import com.example.testapplication.databinding.ActivityMainBinding
import java.io.File
import com.example.testapplication.payload.Topic

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var topicAdapter: TopicAdapter
    private lateinit var topics: List<Topic>
    private val workInfoMap = mutableMapOf<String, WorkInfo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        topics = listOf(
            Topic("Topic 1", "https://navreports.blob.core.windows.net/navtestmedia/topic2.zip?sp=r&st=2024-11-09T13:34:59Z&se=2025-12-30T21:34:59Z&spr=https&sv=2022-11-02&sr=b&sig=xTfY1Vh15FekUvD3QOI2mq45hIp6utyrhdscf6f2Nuw%3D", 1, 200),
            Topic("Topic 2", "https://navreports.blob.core.windows.net/navtestmedia/topic2.zip?sp=r&st=2024-11-09T13:34:59Z&se=2025-12-30T21:34:59Z&spr=https&sv=2022-11-02&sr=b&sig=xTfY1Vh15FekUvD3QOI2mq45hIp6utyrhdscf6f2Nuw%3D", 201, 400),
            Topic("Topic 3", "https://navreports.blob.core.windows.net/navtestmedia/topic3.zip?sp=r&st=2024-11-09T13:35:23Z&se=2025-12-30T21:35:23Z&spr=https&sv=2022-11-02&sr=b&sig=iWrw6FtIqG9mESIDEw2DtLXe%2BC4Be4ieZerdbv8DoOk%3D", 401, 706)
        )

        binding.topicList.layoutManager = LinearLayoutManager(this)
        topicAdapter = TopicAdapter(topics) { topic ->
            val topicImagesDir = File(filesDir, "images/${topic.name}")
            if (topicImagesDir.exists() && topicImagesDir.isDirectory && topicImagesDir.list()?.isNotEmpty() == true) {
                val intent = Intent(this, SubtopicActivity::class.java).apply {
                    putExtra("topic_name", topic.name)
                    putExtra("start_id", topic.startId)
                    putExtra("end_id", topic.endId)
                }
                startActivity(intent)
            } else {
                // If not downloaded, start the download
                startDownload(topic)
            }
        }
        binding.topicList.adapter = topicAdapter

        // Observe all existing downloads when the activity starts
        observeDownloads()
    }

    private fun startDownload(topic: Topic) {
        val downloadWorkRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setInputData(workDataOf(
                "topic_name" to topic.name,
                "url" to topic.url
            ))
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .addTag(topic.name) // Add a tag to easily find this work
            .build()

        WorkManager.getInstance(this).enqueue(downloadWorkRequest)
        Toast.makeText(this, "Starting download for ${topic.name}", Toast.LENGTH_SHORT).show()

        // Observe the new download
        observeWork(downloadWorkRequest.id, topic)
    }

    private fun observeDownloads() {
        for (topic in topics) {
            val workInfos = WorkManager.getInstance(this).getWorkInfosByTag(topic.name).get()
            for (workInfo in workInfos) {
                if (workInfo.state == WorkInfo.State.RUNNING) {
                    observeWork(workInfo.id, topic)
                }
            }
        }
    }

    private fun observeWork(workId: java.util.UUID, topic: Topic) {
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(workId)
            .observe(this) { workInfo ->
                if (workInfo != null) {
                    workInfoMap[topic.name] = workInfo
                    topicAdapter.updateProgress(topic, workInfo)
                }
            }
    }
}
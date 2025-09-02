package com.example.testapplication

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.*
import com.example.testapplication.R
import com.example.testapplication.databinding.ActivityDownloadBinding
import com.example.testapplication.payload.Topic
import java.io.File

class DownloadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDownloadBinding
    private lateinit var adapter: DownloadAdapter
    private lateinit var topics: List<Topic>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDownloadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        topics = listOf(
            Topic("Topic 1: Nature", "https://navreports.blob.core.windows.net/navtestmedia/topic2.zip?sp=r&st=2024-11-09T13:34:59Z&se=2025-12-30T21:34:59Z&spr=https&sv=2022-11-02&sr=b&sig=xTfY1Vh15FekUvD3QOI2mq45hIp6utyrhdscf6f2Nuw%3D", 1, 200, "Download stunning nature photos."),
            Topic("Topic 2: Architecture", "https://navreports.blob.core.windows.net/navtestmedia/topic2.zip?sp=r&st=2024-11-09T13:34:59Z&se=2025-12-30T21:34:59Z&spr=https&sv=2022-11-02&sr=b&sig=xTfY1Vh15FekUvD3QOI2mq45hIp6utyrhdscf6f2Nuw%3D", 201, 400, "Download photos of architectural marvels."),
            Topic("Topic 3: Wildlife", "https://navreports.blob.core.windows.net/navtestmedia/topic3.zip?sp=r&st=2024-11-09T13:35:23Z&se=2025-12-30T21:35:23Z&spr=https&sv=2022-11-02&sr=b&sig=iWrw6FtIqG9mESIDEw2DtLXe%2BC4Be4ieZerdbv8DoOk%3D", 401, 706, "Download captivating wildlife photography.")
        )

        binding.downloadList.layoutManager = LinearLayoutManager(this)
        adapter = DownloadAdapter(topics, this)
        binding.downloadList.adapter = adapter
    }

    fun startDownload(topic: Topic) {
        val downloadWorkRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setInputData(workDataOf(
                "topic_name" to topic.name,
                "url" to topic.url,
                "start_id" to topic.startId,
                "end_id" to topic.endId
            ))
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(this).enqueue(downloadWorkRequest)

        WorkManager.getInstance(this).getWorkInfoByIdLiveData(downloadWorkRequest.id)
            .observe(this) { workInfo ->
                if (workInfo != null) {
                    val progress = workInfo.progress.getInt(DownloadWorker.PROGRESS_KEY, 0)
                    when (workInfo.state) {
                        WorkInfo.State.RUNNING -> {
                            adapter.updateProgress(topic, progress)
                        }
                        WorkInfo.State.SUCCEEDED -> {
                            Toast.makeText(this, "${topic.name} download complete!", Toast.LENGTH_SHORT).show()
                            Handler(Looper.getMainLooper()).postDelayed({
                                adapter.updateProgress(topic, 100)
                            }, 500)
                        }
                        WorkInfo.State.FAILED -> {
                            Toast.makeText(this, "${topic.name} download failed.", Toast.LENGTH_SHORT).show()
                            adapter.updateProgress(topic, -1)
                        }
                        WorkInfo.State.CANCELLED -> {
                            Toast.makeText(this, "${topic.name} download cancelled.", Toast.LENGTH_SHORT).show()
                            adapter.updateProgress(topic, 0)
                        }
                        else -> {
                            adapter.updateProgress(topic, progress)
                        }
                    }
                }
            }
    }
}
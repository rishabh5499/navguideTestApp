package com.example.testapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.testapplication.databinding.ActivitySubtopicBinding
import java.io.File

class SubtopicActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySubtopicBinding
    private lateinit var topicName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubtopicBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val startId = intent.getIntExtra("start_id", 0)
        val endId = intent.getIntExtra("end_id", 0)
        topicName = intent.getStringExtra("topic_name") ?: "Default Topic"

        supportActionBar?.title = topicName

        val topicImagesDir = File(filesDir, "images/$topicName")
        val subtopics = if (topicImagesDir.exists() && topicImagesDir.isDirectory) {
            topicImagesDir.listFiles { file ->
                file.isFile && (file.name.endsWith(".png") || file.name.endsWith(".jpg"))
            }?.map { it.absolutePath }?.sorted() ?: emptyList()
        } else {
            emptyList()
        }

        binding.subtopicList.layoutManager = GridLayoutManager(this, 2)

        val adapter = SubtopicAdapter(subtopics, startId, applicationContext) { imagePath ->
            val imageFile = File(imagePath)
            if (imageFile.exists()) {
                val intent = Intent(this, ImageActivity::class.java).apply {
                    putExtra("image_path", imageFile.absolutePath)
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "Image not found.", Toast.LENGTH_SHORT).show()
            }
        }
        binding.subtopicList.adapter = adapter

        if (subtopics.isEmpty()) {
            Toast.makeText(this, "No images found for this topic.", Toast.LENGTH_SHORT).show()
        }
    }
}
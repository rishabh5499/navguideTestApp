package com.example.testapplication

import com.example.testapplication.databinding.ActivitySubtopicBinding
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import java.io.File

class SubtopicActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySubtopicBinding
    private lateinit var subtopics: List<String>
    private lateinit var topicName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubtopicBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val startId = intent.getIntExtra("start_id", 0)
        val endId = intent.getIntExtra("end_id", 0)
        topicName = intent.getStringExtra("topic_name") ?: "Default Topic"

        supportActionBar?.title = topicName

        subtopics = (startId..endId).map { "subtopic-$it" }

        binding.subtopicList.layoutManager = LinearLayoutManager(this)
        val adapter = SubtopicAdapter(subtopics) { subtopic ->
            val subtopicId = subtopic.substringAfter("subtopic-").toIntOrNull()
            val imageName = "image-$subtopicId.png"

            // Check for the image in the correct local directory
            val imageFile = File(filesDir, "$topicName/$imageName")

            if (imageFile.exists()) {
                val intent = Intent(this, ImageActivity::class.java).apply {
                    putExtra("image_path", imageFile.absolutePath)
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "Image not found. Please download the topic first.", Toast.LENGTH_SHORT).show()
            }
        }
        binding.subtopicList.adapter = adapter
    }
}
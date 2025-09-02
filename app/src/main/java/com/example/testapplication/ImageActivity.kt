package com.example.testapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.testapplication.databinding.ActivityImageBinding
import java.io.File

class ImageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imagePath = intent.getStringExtra("image_path")

        if (imagePath != null) {
            val imageFile = File(imagePath)
            if (imageFile.exists()) {
                Glide.with(this)
                    .load(imageFile)
                    .into(binding.imageView)
            } else {
                // Handle case where image path is invalid or file is missing
            }
        }
    }
}
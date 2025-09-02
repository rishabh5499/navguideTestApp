package com.example.testapplication

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.example.testapplication.R
import com.example.testapplication.databinding.ActivityImageBinding
import java.io.File
import java.io.FileInputStream
import java.io.OutputStream

class ImageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImageBinding
    private var imageFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Image Viewer"

        val imagePath = intent.getStringExtra("image_path")
        if (imagePath != null) {
            imageFile = File(imagePath)
            if (imageFile!!.exists()) {
                Glide.with(this)
                    .load(imageFile)
                    .into(binding.imageView)
            } else {
                Toast.makeText(this, "Image file not found.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_image_page, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save -> {
                saveImageToGallery()
                true
            }
            R.id.action_share -> {
                shareImage()
                true
            }
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveImageToGallery() {
        if (imageFile == null) return

        val resolver = contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, imageFile!!.name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/ImageGallery")
        }

        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        if (uri != null) {
            try {
                val outputStream: OutputStream? = resolver.openOutputStream(uri)
                if (outputStream != null) {
                    FileInputStream(imageFile!!).use { inputStream ->
                        inputStream.copyTo(outputStream)
                    }
                    outputStream.close()
                    Toast.makeText(this, "Image saved to Gallery.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Failed to save image.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun shareImage() {
        if (imageFile == null) {
            Toast.makeText(this, "Image not found.", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val imageUri: Uri = FileProvider.getUriForFile(
                this,
                "com.example.testapplication.provider",
                imageFile!!
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/jpg"
                putExtra(Intent.EXTRA_STREAM, imageUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(Intent.createChooser(shareIntent, "Share image via"))

        } catch (e: IllegalArgumentException) {
            Toast.makeText(this, "Failed to share image.", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }
}
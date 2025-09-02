package com.example.testapplication

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

class DownloadWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    companion object {
        const val PROGRESS_KEY = "progress"
        private const val TAG = "DownloadWorker"
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val topicName = inputData.getString("topic_name") ?: return@withContext Result.failure()
        val url = inputData.getString("url") ?: return@withContext Result.failure()

        Log.d(TAG, "Starting download for $topicName from $url")

        try {
            val client = OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .build()

            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                Log.e(TAG, "Download failed with code: ${response.code}")
                return@withContext Result.failure()
            }

            val body = response.body ?: return@withContext Result.failure()
            val totalBytes = body.contentLength()

            val zipFile = File(applicationContext.filesDir, "$topicName.zip")

            FileOutputStream(zipFile).use { fos ->
                val inputStream: InputStream = body.byteStream()
                var bytesDownloaded = 0L
                val buffer = ByteArray(4096)
                var bytesRead: Int

                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    fos.write(buffer, 0, bytesRead)
                    bytesDownloaded += bytesRead
                    val progress = ((bytesDownloaded * 100) / totalBytes).toInt()
                    if (progress < 90) {
                        setProgress(workDataOf(PROGRESS_KEY to progress))
                    }
                }
            }

            Log.d(TAG, "Download complete. Starting extraction.")
            setProgress(workDataOf(PROGRESS_KEY to 90))

            val destinationDir = File(applicationContext.filesDir, "images/$topicName")
            if (!destinationDir.exists()) {
                destinationDir.mkdirs()
                Log.d(TAG, "Created directory: ${destinationDir.absolutePath}")
            }

            val buffer = ByteArray(1024)
            ZipInputStream(zipFile.inputStream()).use { zis ->
                var zipEntry: ZipEntry? = zis.nextEntry
                while (zipEntry != null) {
                    if (zipEntry.isDirectory) {
                        zipEntry = zis.nextEntry
                        continue
                    }
                    val originalFileName = zipEntry.name
                    val newFile = File(destinationDir, originalFileName)
                    Log.d(TAG, "Extracting file: $originalFileName to ${newFile.absolutePath}")

                    try {
                        FileOutputStream(newFile).use { fos ->
                            zis.copyTo(fos)
                        }
                        Log.d(TAG, "Successfully extracted ${newFile.name}")
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to write file ${newFile.name}: ${e.message}", e)
                        newFile.delete()
                    }
                    zipEntry = zis.nextEntry
                }
            }
            Log.d(TAG, "Extraction complete. Deleting temp file.")
            zipFile.delete()

            setProgress(workDataOf(PROGRESS_KEY to 100))
            Log.d(TAG, "Worker finished successfully.")
            return@withContext Result.success()

        } catch (e: Exception) {
            Log.e(TAG, "Download and extraction failed: ${e.message}", e)
            return@withContext Result.failure()
        }
    }
}
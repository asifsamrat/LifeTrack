package com.example.lifetrack.viewModel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.lifetrack.data.model.Memory
import com.example.lifetrack.data.repository.MemoryRepository


class MemoryViewModel : ViewModel() {

    private val repository = MemoryRepository()

    var isSaving = mutableStateOf(false)
        private set

    var message = mutableStateOf("")
        private set

    var isSuccess = mutableStateOf(false)
        private set

    fun saveMemory(
        context: Context,
        title: String,
        description: String,
        date: String,
        time: String,
        images: List<Uri>,
        videos: List<Uri>,
        userId: String,
        id: String = ""
    ) {
        if (title.isEmpty() || userId.isEmpty()) {
            message.value = "Title and login required"
            isSuccess.value = false
            return
        }

        isSaving.value = true

        // For update, we might already have URLs. 
        // This simple implementation re-uploads everything if they are Uris.
        // A more complex one would check if the Uri is actually a remote URL string.
        
        uploadMultipleToCloudinary(images, "image") { imageUrls ->
            uploadMultipleToCloudinary(videos, "video") { videoUrls ->

                val memory = Memory(
                    id = id,
                    title = title,
                    description = description,
                    date = date,
                    time = time,
                    imageUrls = imageUrls,
                    videoUrls = videoUrls,
                    userId = userId
                )

                repository.saveMemory(memory) { success ->
                    isSaving.value = false
                    isSuccess.value = success
                    message.value = if (success) "Memory Saved!" else "Failed to save"
                }
            }
        }
    }

    fun deleteMemory(memoryId: String, onComplete: (Boolean) -> Unit = {}) {
        repository.deleteMemory(memoryId) { success ->
            if (success) {
                message.value = "Memory deleted"
            } else {
                message.value = "Failed to delete"
            }
            onComplete(success)
        }
    }

    private fun uploadMultipleToCloudinary(uris: List<Uri>, resourceType: String, onComplete: (List<String>) -> Unit) {
        if (uris.isEmpty()) {
            onComplete(emptyList())
            return
        }

        val urls = mutableListOf<String>()
        var processedCount = 0

        uris.forEach { uri ->
            // Check if it's already a web url
            if (uri.toString().startsWith("http")) {
                urls.add(uri.toString())
                processedCount++
                if (processedCount == uris.size) onComplete(urls)
            } else {
                MediaManager.get().upload(uri)
                    .option("resource_type", resourceType)
                    .unsigned("lifetrack_upload")
                    .callback(object : UploadCallback {
                        override fun onStart(requestId: String?) {}
                        override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}
                        override fun onSuccess(requestId: String?, resultData: Map<*, *>?) {
                            val url = resultData?.get("secure_url") as? String
                            if (url != null) urls.add(url)
                            checkCompletion()
                        }
                        override fun onError(requestId: String?, error: ErrorInfo?) {
                            android.util.Log.e("CloudinaryUpload", "Error uploading $resourceType: ${error?.description}")
                            checkCompletion()
                        }
                        override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                            checkCompletion()
                        }

                        private fun checkCompletion() {
                            processedCount++
                            if (processedCount == uris.size) {
                                onComplete(urls)
                            }
                        }
                    }).dispatch()
            }
        }
    }

    fun getMemoriesByUserId(userId: String, onResult: (List<Memory>) -> Unit) {
        repository.getMemoriesByUserId(userId) { memories ->
            onResult(memories)
        }
    }

    fun getMemoryById(memoryId: String, onResult: (Memory?) -> Unit) {
        repository.getMemoryById(memoryId, onResult)
    }

    fun resetState() {
        message.value = ""
        isSuccess.value = false
    }
}

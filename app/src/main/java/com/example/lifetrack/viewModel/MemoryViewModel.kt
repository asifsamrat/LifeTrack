package com.example.lifetrack.viewModel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.lifetrack.data.model.Memory
import com.example.lifetrack.data.repository.MemoryRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration


class MemoryViewModel : ViewModel() {

    private val repository = MemoryRepository()
    private val db = FirebaseFirestore.getInstance()
    private var memoryListener: ListenerRegistration? = null

    private val _memories = mutableStateOf<List<Memory>>(emptyList())
    val memories: State<List<Memory>> = _memories

    var isSaving = mutableStateOf(false)
        private set

    var message = mutableStateOf("")
        private set

    var isSuccess = mutableStateOf(false)
        private set

    fun startListening(userId: String) {
        if (memoryListener != null) return
        
        memoryListener = db.collection("memories")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot != null) {
                    _memories.value = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Memory::class.java)?.copy(id = doc.id)
                    }
                }
            }
    }

    override fun onCleared() {
        super.onCleared()
        memoryListener?.remove()
    }

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
            message.value = if (success) "Memory deleted" else "Failed to delete"
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

    fun getMemoryById(memoryId: String, onResult: (Memory?) -> Unit) {
        val cached = _memories.value.find { it.id == memoryId }
        if (cached != null) {
            onResult(cached)
        } else {
            repository.getMemoryById(memoryId, onResult)
        }
    }

    fun resetState() {
        message.value = ""
        isSuccess.value = false
    }
}

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
        
        // Generate a new ID if it's a new memory
        val memoryId = if (id.isEmpty()) db.collection("memories").document().id else id

        // Convert Uris to strings. These might be local content:// uris initially.
        val currentImageUrls = images.map { it.toString() }
        val currentVideoUrls = videos.map { it.toString() }

        val initialMemory = Memory(
            id = memoryId,
            title = title,
            description = description,
            date = date,
            time = time,
            imageUrls = currentImageUrls,
            videoUrls = currentVideoUrls,
            userId = userId
        )

        // 1. Save locally to Firestore immediately (Offline support)
        repository.saveMemory(initialMemory) { success ->
            if (success) {
                isSuccess.value = true
                message.value = "Memory Saved Locally!"
                
                // 2. Trigger background upload
                startBackgroundUpload(images, videos, initialMemory)
            } else {
                isSaving.value = false
                isSuccess.value = false
                message.value = "Failed to save"
            }
        }
    }

    private fun startBackgroundUpload(images: List<Uri>, videos: List<Uri>, memory: Memory) {
        uploadMultipleToCloudinary(images, "image") { imageUrls ->
            uploadMultipleToCloudinary(videos, "video") { videoUrls ->
                
                // If any URLs changed (meaning uploads completed), update Firestore
                if (imageUrls != memory.imageUrls || videoUrls != memory.videoUrls) {
                    val updatedMemory = memory.copy(
                        imageUrls = imageUrls,
                        videoUrls = videoUrls
                    )
                    repository.saveMemory(updatedMemory) { 
                        isSaving.value = false
                    }
                } else {
                    isSaving.value = false
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
            val uriString = uri.toString()
            if (uriString.startsWith("http")) {
                urls.add(uriString)
                processedCount++
                if (processedCount == uris.size) onComplete(urls)
            } else {
                // Background upload via Cloudinary MediaManager
                MediaManager.get().upload(uri)
                    .option("resource_type", resourceType)
                    .unsigned("lifetrack_upload")
                    .callback(object : UploadCallback {
                        override fun onStart(requestId: String?) {}
                        override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}
                        override fun onSuccess(requestId: String?, resultData: Map<*, *>?) {
                            val url = resultData?.get("secure_url") as? String
                            if (url != null) urls.add(url) else urls.add(uriString)
                            checkCompletion()
                        }
                        override fun onError(requestId: String?, error: ErrorInfo?) {
                            urls.add(uriString) // Keep local URI on error
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

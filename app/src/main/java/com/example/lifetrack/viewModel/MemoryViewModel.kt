package com.example.lifetrack.viewModel

import MemoryRepository
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lifetrack.Uploads.MemoryFiles
import com.example.lifetrack.data.model.Memory


class MemoryViewModel : ViewModel() {

    private val repository = MemoryRepository()

    var isSaving = mutableStateOf(false)
        private set

    var message = mutableStateOf("")
        private set

    var isSuccess = mutableStateOf(false)
        private set

    fun saveMemory(
        title: String,
        description: String,
        date: String,
        time: String,
        images: List<Uri>,
        videos: List<Uri>,
        userId: String
    ) {
        if (title.isEmpty() || userId.isEmpty()) {
            message.value = "Title and login required"
            isSuccess.value = false
            return
        }

        isSaving.value = true

        MemoryFiles(images, "images") { imageUrls ->
            MemoryFiles(videos, "videos") { videoUrls ->

                val memory = Memory(
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

    fun resetState() {
        message.value = ""
        isSuccess.value = false
    }
}
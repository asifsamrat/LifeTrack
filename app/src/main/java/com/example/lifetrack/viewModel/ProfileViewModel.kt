package com.example.lifetrack.viewModel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lifetrack.data.model.UserProfile
import com.example.lifetrack.data.repository.ProfileRepository
import com.google.firebase.auth.FirebaseAuth

class ProfileViewModel : ViewModel() {
    private val profileRepository = ProfileRepository()
    private val auth = FirebaseAuth.getInstance()

    var userProfile = mutableStateOf<UserProfile?>(null)
        private set

    var isLoading = mutableStateOf(false)
        private set

    var message = mutableStateOf("")
        private set

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        val userId = auth.currentUser?.uid ?: return
        isLoading.value = true
        profileRepository.getUserProfile(userId) { profile ->
            userProfile.value = profile
            isLoading.value = false
        }
    }

    fun updateProfile(context: Context, name: String, age: String, occupation: String, imageUri: Uri?) {
        val userId = auth.currentUser?.uid ?: return
        isLoading.value = true

        if (imageUri != null) {
            profileRepository.uploadImageToCloudinary(context, imageUri) { imageUrl ->
                if (imageUrl != null) {
                    val newProfile = UserProfile(userId, name, age, occupation, imageUrl)
                    saveToFirestore(newProfile)
                } else {
                    isLoading.value = false
                    message.value = "Failed to upload image"
                }
            }
        } else {
            val currentImageUrl = userProfile.value?.profileImageUrl ?: ""
            val newProfile = UserProfile(userId, name, age, occupation, currentImageUrl)
            saveToFirestore(newProfile)
        }
    }

    fun updateProfileImage(context: Context, imageUri: Uri) {
        val userId = auth.currentUser?.uid ?: return
        val currentProfile = userProfile.value ?: UserProfile(userId = userId)
        isLoading.value = true

        profileRepository.uploadImageToCloudinary(context, imageUri) { imageUrl ->
            if (imageUrl != null) {
                val newProfile = currentProfile.copy(profileImageUrl = imageUrl)
                saveToFirestore(newProfile)
            } else {
                isLoading.value = false
                message.value = "Failed to upload image"
            }
        }
    }

    private fun saveToFirestore(profile: UserProfile) {
        profileRepository.saveUserProfile(profile) { success, msg ->
            if (success) {
                userProfile.value = profile
            }
            message.value = msg
            isLoading.value = false
        }
    }
}

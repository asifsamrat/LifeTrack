package com.example.lifetrack.data.repository

import android.content.Context
import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.lifetrack.data.model.UserProfile
import com.google.firebase.firestore.FirebaseFirestore

class ProfileRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val profileCollection = firestore.collection("profiles")

    fun getUserProfile(userId: String, onResult: (UserProfile?) -> Unit) {
        profileCollection.document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    onResult(document.toObject(UserProfile::class.java))
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    fun saveUserProfile(profile: UserProfile, onComplete: (Boolean, String) -> Unit) {
        profileCollection.document(profile.userId).set(profile)
            .addOnSuccessListener {
                onComplete(true, "Profile saved successfully")
            }
            .addOnFailureListener { e ->
                onComplete(false, e.message ?: "Failed to save profile")
            }
    }

    fun uploadImageToCloudinary(context: Context, imageUri: Uri, onResult: (String?) -> Unit) {
        // Note: Cloudinary must be initialized in the Application class or before use.
        // For this implementation, we assume MediaManager is already initialized.
        
        MediaManager.get().upload(imageUri)
            .unsigned("lifetrack_upload") // Replace with your actual preset
            .callback(object : UploadCallback {
                override fun onStart(requestId: String) {}
                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}
                override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                    val url = resultData["secure_url"] as? String
                    onResult(url)
                }
                override fun onError(requestId: String, error: ErrorInfo) {
                    onResult(null)
                }
                override fun onReschedule(requestId: String, error: ErrorInfo) {}
            }).dispatch()
    }
}

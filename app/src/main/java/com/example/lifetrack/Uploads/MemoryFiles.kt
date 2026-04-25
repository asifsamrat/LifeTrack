package com.example.lifetrack.Uploads

import android.net.Uri
import java.util.UUID

fun MemoryFiles(
    uris: List<Uri>,
    folder: String,
    onComplete: (List<String>) -> Unit
) {
    // If you have multiple buckets or the default isn't being found,
    // you can pass the URL: FirebaseStorage.getInstance("gs://your-bucket-url")
    val storage = com.google.firebase.storage.FirebaseStorage.getInstance()
    val urls = mutableListOf<String>()

    if (uris.isEmpty()) {
        onComplete(emptyList())
        return
    }

    var processedCount = 0

    uris.forEach { uri ->
        val fileName = "${UUID.randomUUID()}"
        val ref = storage.reference.child("$folder/$fileName")

        ref.putFile(uri)
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let { throw it }
                }
                ref.downloadUrl
            }
            .addOnSuccessListener { downloadUrl ->
                urls.add(downloadUrl.toString())
            }
            .addOnFailureListener { e ->
                // Log the specific error for debugging
                android.util.Log.e("UploadError", "Failed to upload $fileName: ${e.message}")
            }
            .addOnCompleteListener {
                processedCount++
                if (processedCount == uris.size) {
                    onComplete(urls)
                }
            }
    }
}

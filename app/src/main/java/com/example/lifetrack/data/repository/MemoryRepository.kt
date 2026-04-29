package com.example.lifetrack.data.repository

import com.example.lifetrack.data.model.Memory
import com.google.firebase.firestore.FirebaseFirestore

class MemoryRepository {

    private val db = FirebaseFirestore.getInstance()

    fun saveMemory(memory: Memory, onResult: (Boolean) -> Unit) {
        db.collection("memories")
            .add(memory)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun getMemoriesByUserId(userId: String, onResult: (List<Memory>) -> Unit) {
        db.collection("memories")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                val memories = result.toObjects(Memory::class.java)
                onResult(memories)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }
}

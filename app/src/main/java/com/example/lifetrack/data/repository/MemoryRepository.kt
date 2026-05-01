package com.example.lifetrack.data.repository

import com.example.lifetrack.data.model.Memory
import com.google.firebase.firestore.FirebaseFirestore

class MemoryRepository {

    private val db = FirebaseFirestore.getInstance()
    private val memoryCollection = db.collection("memories")

    fun saveMemory(memory: Memory, onResult: (Boolean) -> Unit) {
        val id = if (memory.id.isEmpty()) memoryCollection.document().id else memory.id
        val finalMemory = memory.copy(id = id)
        
        memoryCollection.document(id).set(finalMemory)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun deleteMemory(memoryId: String, onResult: (Boolean) -> Unit) {
        if (memoryId.isEmpty()) {
            onResult(false)
            return
        }
        memoryCollection.document(memoryId).delete()
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun getMemoriesByUserId(userId: String, onResult: (List<Memory>) -> Unit) {
        memoryCollection
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                val memories = result.mapNotNull { document ->
                    document.toObject(Memory::class.java).copy(id = document.id)
                }
                onResult(memories)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    fun getMemoryById(memoryId: String, onResult: (Memory?) -> Unit) {
        if (memoryId.isEmpty()) {
            onResult(null)
            return
        }
        memoryCollection.document(memoryId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    onResult(document.toObject(Memory::class.java)?.copy(id = document.id))
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener {
                onResult(null)
            }
    }
}

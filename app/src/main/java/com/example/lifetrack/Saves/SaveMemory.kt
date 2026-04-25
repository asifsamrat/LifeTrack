package com.example.lifetrack.Saves

import com.example.lifetrack.DataModel.Memory

fun saveMemory(memory: Memory, onResult: (Boolean) -> Unit) {
    val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()

    db.collection("memories")
        .add(memory)
        .addOnSuccessListener { onResult(true) }
        .addOnFailureListener { onResult(false) }
}
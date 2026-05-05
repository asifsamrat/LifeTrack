package com.example.lifetrack.data.repository

import com.example.lifetrack.data.model.Note
import com.google.firebase.firestore.FirebaseFirestore

class NoteRepository{
    private val db = FirebaseFirestore.getInstance()
    private val noteCollection = db.collection("notes")

    //Saving or Updating note
    fun saveOrupdateNote(note: Note, onResult: (Boolean, String) -> Unit) {
        var isUpdate = false
        val id = if (note.id.isEmpty()) {
            noteCollection.document().id
        } else {
            isUpdate = true
            note.id
        }
        val finalNote = note.copy(id = id)

        noteCollection.document(id).set(finalNote)
            .addOnSuccessListener {
                onResult(true,
                    if (isUpdate) "Note updated successfully" else "Note saved successfully")
            }
            .addOnFailureListener {
                onResult(false,
                    if (isUpdate) "Failed to update note" else "Failed to saving note")
            }
    }

    //Delete Note
    fun deleteNote(noteId: String, onResult: (Boolean) -> Unit) {
        if (noteId.isNullOrEmpty()) {
            onResult(false)
            return
        }
        noteCollection.document(noteId).delete()
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    //Get Note from the firebase based of noteType
    fun getNotesByType(userId: String, noteType: String, onResult: (List<Note>) -> Unit) {
        noteCollection
            .whereEqualTo("userId", userId)
            .whereEqualTo("noteType", noteType)
            .get()
            .addOnSuccessListener { result ->
                val notes = result.mapNotNull { document ->
                    document.toObject(Note::class.java).copy(id = document.id)
                }
                onResult(notes)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    fun getNoteById(noteId: String, onResult: (Note?) -> Unit) {
        if (noteId.isEmpty()) {
            onResult(null)
            return
        }
        noteCollection.document(noteId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    onResult(document.toObject(Note::class.java)?.copy(id = document.id))
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener {
                onResult(null)
            }
    }
}

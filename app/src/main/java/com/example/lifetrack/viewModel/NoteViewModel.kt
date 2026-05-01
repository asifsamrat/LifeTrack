package com.example.lifetrack.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lifetrack.data.model.Note
import com.example.lifetrack.data.repository.NoteRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class NoteViewModel : ViewModel() {
    private val noteRepository = NoteRepository()
    private val db = FirebaseFirestore.getInstance()
    private var notesListener: ListenerRegistration? = null

    private val _notes = mutableStateOf<List<Note>>(emptyList())
    val notes: State<List<Note>> = _notes

    var message = mutableStateOf("")
        private set
    var isSuccess = mutableStateOf(false)
        private set

    fun startListening(userId: String) {
        if (notesListener != null) return // Already listening
        
        notesListener = db.collection("notes")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot != null) {
                    _notes.value = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Note::class.java)?.copy(id = doc.id)
                    }
                }
            }
    }

    override fun onCleared() {
        super.onCleared()
        notesListener?.remove()
    }

    fun saveNote(note: Note) {
        noteRepository.saveNote(note) { success, msg ->
            message.value = msg
            isSuccess.value = success
        }
    }

    fun deleteNote(noteId: String, onComplete: (Boolean) -> Unit = {}) {
        noteRepository.deleteNote(noteId) { success ->
            message.value = if (success) "Note deleted successfully" else "Failed to delete note"
            onComplete(success)
        }
    }

    fun getNoteById(noteId: String, onResult: (Note?) -> Unit) {
        // First check cache
        val cached = _notes.value.find { it.id == noteId }
        if (cached != null) {
            onResult(cached)
        } else {
            noteRepository.getNoteById(noteId, onResult)
        }
    }

    fun resetState() {
        message.value = ""
        isSuccess.value = false
    }
}

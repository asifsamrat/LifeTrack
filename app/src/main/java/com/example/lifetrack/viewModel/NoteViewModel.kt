package com.example.lifetrack.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lifetrack.data.model.Note
import com.example.lifetrack.data.repository.NoteRepository

class NoteViewModel : ViewModel() {
    private val noteRepository = NoteRepository()

    var message = mutableStateOf("")
        private set
    var isSuccess = mutableStateOf(false)
        private  set

    //Saving or updating note to the firebase
    fun saveNote(note: Note) {
        noteRepository.saveNote(note) { success, msg ->
            message.value = msg
            isSuccess.value = success
        }
    }

    //Delete Note
    fun deleteNote(noteId: String, onComplete: (Boolean) -> Unit = {}) {
        noteRepository.deleteNote(noteId) { success ->
            if (success) {
                message.value = "Note deleted successfully"
            } else {
                message.value = "Failed to delete note"
            }
            onComplete(success)
        }
    }

    //Get Note from the firebase based of noteType
    fun getNotesByType(userId: String, noteType: String, onResult: (List<Note>) -> Unit) {
        noteRepository.getNotesByType(userId, noteType) { notes ->
            onResult(notes)
        }
    }

    fun getNoteById(noteId: String, onResult: (Note?) -> Unit) {
        noteRepository.getNoteById(noteId) { note ->
            onResult(note)
        }
    }

    fun resetState() {
        message.value = ""
        isSuccess.value = false
    }
}

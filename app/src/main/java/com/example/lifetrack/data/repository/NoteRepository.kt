import com.example.lifetrack.data.model.Note
import com.google.firebase.firestore.FirebaseFirestore

class NoteRepository{
    private val db = FirebaseFirestore.getInstance()

    //Saving note to the firebase
    fun saveNote(note: Note, onResult: (Boolean, String) -> Unit) {
        db.collection("notes")
            .add(note)
            .addOnSuccessListener { onResult(true, "Note saved successfully") }
            .addOnFailureListener { onResult(false, "Error! saving note") }
    }

    //Get Note from the firebase based of noteType
    fun getNotesByType(userId: String, noteType: String, onResult: (List<Note>) -> Unit) {
        db.collection("notes")
            .whereEqualTo("userId", userId)
            .whereEqualTo("noteType", noteType)
            .get()
            .addOnSuccessListener { result ->

                val notes = result.documents.mapNotNull {
                    it.toObject(Note::class.java)
                }

                onResult(notes)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }
}
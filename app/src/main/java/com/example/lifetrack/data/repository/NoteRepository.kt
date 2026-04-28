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
}
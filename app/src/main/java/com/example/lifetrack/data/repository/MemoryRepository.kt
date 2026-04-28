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
}
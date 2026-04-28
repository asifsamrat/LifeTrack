import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lifetrack.data.model.Note


class NoteViewModel : ViewModel() {
    private val noteRepository = NoteRepository()

    var message = mutableStateOf("")
        private set
    var isSuccess = mutableStateOf(false)
        private  set

    //Saving note to the firebase
    fun saveNote(note: Note) {
        noteRepository.saveNote(note) { success, msg ->
            message.value = msg
            isSuccess.value = success
        }
    }
}
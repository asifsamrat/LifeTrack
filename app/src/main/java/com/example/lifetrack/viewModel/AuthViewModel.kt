import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lifetrack.data.auth.AuthRepository

class AuthViewModel: ViewModel() {
     val authRepository = AuthRepository()

     //Login Message and Success
     var loginMessage = mutableStateOf("")
         private set
     var loginSuccess = mutableStateOf(false)
         private set

     //Register Message and Success
     var registerMessage = mutableStateOf("")
         private set
     var registerSuccess = mutableStateOf(false)
         private set

     //Forgot Message and Success
     var forgotMessage = mutableStateOf("")
         private set
     var forgotSuccess = mutableStateOf(false)
         private set



    //Login
    fun login(email: String, password: String) {
        authRepository.login(email, password){ success, msg ->
            loginMessage.value = msg
            loginSuccess.value = success
        }
    }

    //Register
    fun register(email: String, password: String) {
        authRepository.register(email, password){ success, msg ->
            registerMessage.value = msg
            registerSuccess.value = success
        }
    }

    //Forgot Password
    fun forgotPassword(email: String) {
        authRepository.forgotPassword(email){ success, msg ->
            forgotMessage.value = msg
            forgotSuccess.value = success
        }
    }

}
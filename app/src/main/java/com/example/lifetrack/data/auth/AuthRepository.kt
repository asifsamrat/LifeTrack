package com.example.lifetrack.data.auth

import com.google.firebase.auth.FirebaseAuth

class AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    //User login and verification
    fun login(email: String, password: String, onResult: (Boolean, String) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                println(auth)
                if (it.isSuccessful) {
                    val user = auth.currentUser

                    // Reload user to get latest verification status
                    user?.reload()?.addOnCompleteListener {

                        if (user != null && user.isEmailVerified) {
                            onResult(true, "Login Successful")
                        } else if (user != null && !user.isEmailVerified) {
                            onResult(false, "Please verify your email first")
                        } else {
                            onResult(false, "Authentication failed")
                        }
                    }

                } else {
                    onResult(false, "Enter Valid Email and Password")
                }
            }
    }


    //User Register and verification
    fun register(email: String, password: String, onResult: (Boolean, String) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    // Send verification email
                    val user = auth.currentUser
                    user?.sendEmailVerification()
                        ?.addOnCompleteListener { verifyTask ->
                            if (verifyTask.isSuccessful) {
                                onResult(true, "Verification email sent. Please check your inbox.")
                                println("Send verification email: ${verifyTask.isSuccessful}")
                            } else {
                                onResult(false, "Failed to send verification email")
                                println("Failed to send verification email: ${verifyTask.exception}")
                            }
                        }
                } else {
                    onResult(false, it.exception?.message ?: "Error")
                }
            }
    }

    //Reset forgot Password
    fun forgotPassword(email: String, onResult: (Boolean, String) -> Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener {
                println("Sent to mail : ${it.isSuccessful}")
                if (it.isSuccessful) {
                    onResult(true, "Sent to mail")
                } else {
                    onResult(false, it.exception?.message ?: "Error")
                }
            }
    }

    // Logout
    fun logout() {
        auth.signOut()
    }

    // Check if user is logged in
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null && auth.currentUser!!.isEmailVerified
    }
}
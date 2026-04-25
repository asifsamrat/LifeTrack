package com.example.lifetrack.auth

import com.google.firebase.auth.FirebaseAuth

class AuthManager {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun login(email: String, password: String, onResult: (Boolean, String) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onResult(true, "Login Successful")
                } else {
                    onResult(false, it.exception?.message ?: "Error")
                }
            }
    }


    fun register(email: String, password: String, onResult: (Boolean, String) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onResult(true, "Registration Successful")
                } else {
                    onResult(false, it.exception?.message ?: "Error")
                }
            }
    }

    fun forgotPassword(email: String, onResult: (Boolean, String) -> Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onResult(true, "Reset email sent")
                } else {
                    onResult(false, it.exception?.message ?: "Error")
                }
            }
    }
}
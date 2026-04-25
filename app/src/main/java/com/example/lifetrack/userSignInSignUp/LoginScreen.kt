package com.example.lifetrack.userSignInSignUp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import com.example.lifetrack.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.lifetrack.auth.AuthManager

@Composable
fun LoginScreen(navController: NavController) {

    val authManager = AuthManager()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    Box() {
        Column(
            modifier = Modifier.fillMaxSize().padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            //LifeTrack Top Logo
            Image(
                painter = painterResource(id = R.drawable.life_track_logo_text_transperant),
                contentDescription = "Top Logo",
                modifier = Modifier.size(120.dp)
                    .clip(shape = RoundedCornerShape(15.dp))
                    .background(Color.Transparent)
            )

            Spacer(modifier = Modifier.height(30.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = {
                    Text("Email")
                },

                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = {
                    Text("Password")
                },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    authManager.login(email, password) { success, msg ->
                        message = msg
                        if (success) {
                            navController.navigate("home_main") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    }
                },
                modifier = Modifier.padding(top = 20.dp)
            ) {
                Text("Login")
            }

            Text(text = message)

            Row() {
                TextButton(onClick = {
                    navController.navigate("forgot")
                }) {
                    Text("Forgot Password?")
                }

                TextButton(onClick = {
                    navController.navigate("register")
                }) {
                    Text("Create Account")
                }
            }
        }


        //Life Track Bottom Logo
        Box(
            modifier = Modifier.fillMaxSize()
                .padding(bottom = 20.dp),
        ) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart) // bottom of screen
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.life_track_logo_transperant), // your logo
                    contentDescription = "Bottom Logo",
                    modifier = Modifier.size(60.dp)
                        .clip(shape = RoundedCornerShape(15.dp))
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column() {
                    Text(
                        text = "LifeTrack",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Organize Life and Boost Productivity",
                        fontSize = 10.sp
                    )
                }
            }
        }
    }


}

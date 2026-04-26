package com.example.lifetrack.userSignInSignUp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import com.example.lifetrack.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.lifetrack.auth.AuthManager
import com.example.lifetrack.ui.theme.BrightGreen
import com.example.lifetrack.ui.theme.DarkGreen
import com.example.lifetrack.ui.theme.Green
import com.example.lifetrack.ui.theme.GreenLime

@Composable
fun LoginScreen(navController: NavController) {

    val authManager = AuthManager()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    Box(
        modifier = Modifier.fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            //LifeTrack Top Logo
            Image(
                painter = painterResource(id = R.drawable.life_track_logo_text_transperant),
                contentDescription = "Top Logo",
                modifier = Modifier.size(150.dp)
                    .clip(shape = RoundedCornerShape(15.dp))
                    .background(Color.Transparent)
            )

            Spacer(modifier = Modifier.height(40.dp))

            //User Email
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Email",
                    textAlign = TextAlign.Start
                )

                Text(
                    text = " *",
                    color = Color.Red
                )
            }

            TextField(
                value = email,
                onValueChange = { email = it },
                placeholder = {
                    Text(
                        "Enter your email",
                        fontSize = 14.sp
                    )
                },

                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(10.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF5F5F5),
                    unfocusedContainerColor = Color(0xFFF5F5F5),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                textStyle = TextStyle(
                    color = Color.Black,
                    fontSize = 16.sp
                )
            )
            Spacer(modifier = Modifier.height(20.dp))

            //User Password
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Password",
                    textAlign = TextAlign.Start
                )

                Text(
                    text = " *",
                    color = Color.Red
                )
            }
            TextField(
                value = password,
                onValueChange = { password = it },
                placeholder = {
                    Text(
                        "Enter your password",
                        fontSize = 14.sp
                    )
                },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(10.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF5F5F5),
                    unfocusedContainerColor = Color(0xFFF5F5F5),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                textStyle = TextStyle(
                    color = Color.Black,
                    fontSize = 16.sp
                )
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
                modifier = Modifier.padding(top = 20.dp),
                shape = RoundedCornerShape(10.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = BrightGreen,
                    contentColor = Color.White
                )
            ) {
                Text(
                    "LOGIN",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            //Text(text = message)
            println(message)
            Spacer(modifier = Modifier.height(20.dp))
            Row() {
                TextButton(onClick = {
                    navController.navigate("forgot")
                }) {
                    Text(
                        "Forgot Password?",
                        color = Color.Red
                    )
                }

                TextButton(onClick = {
                    navController.navigate("register")
                }) {
                    Text(
                        "Create Account",
                        color = GreenLime
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))


        //Life Track Bottom Logo
        Box(
            modifier = Modifier.fillMaxSize()
                .padding(bottom = 15.dp),
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

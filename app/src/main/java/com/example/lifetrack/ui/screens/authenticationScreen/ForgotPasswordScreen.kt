package com.example.lifetrack.ui.screens.authenticationScreen

import AuthViewModel
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.lifetrack.R
import com.example.lifetrack.data.auth.AuthRepository
import com.example.lifetrack.ui.theme.BrightGreen
import com.example.lifetrack.ui.theme.GreenLime

@Composable
fun ForgotPasswordScreen(navController: NavController, viewModel: AuthViewModel) {

    var email by remember { mutableStateOf("") }
    var message by viewModel.forgotMessage
    var success by viewModel.forgotSuccess

    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.life_track_logo_text_transperant),
            contentDescription = "Top Logo",
            modifier = Modifier.size(150.dp)
                .clip(shape = RoundedCornerShape(15.dp))
                .background(Color.Transparent)
        )

        Spacer(modifier = Modifier.height(30.dp))

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

        //Authentication Message
        if (message != "" && !success) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = " *",
                    color = Color.Red
                )

                Text(
                    text = message,
                    textAlign = TextAlign.Start,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = Color.Red
                )
            }
        }

        //Forgot Password and navigate to Login Screen
        LaunchedEffect(success) {
            if (success) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                navController.navigate("login") {
                    popUpTo("forgot") {
                        inclusive = true
                    }
                }
                viewModel.forgotMessage.value = ""
                viewModel.forgotSuccess.value = false
            }
        }

        Button(
            onClick = {
                if (email.isBlank()) {
                    viewModel.forgotMessage.value = "Email is required"
                    return@Button
                }
                viewModel.forgotPassword(email)
            },
            modifier = Modifier.padding(top = 20.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BrightGreen,
                contentColor = Color.White
            )
        ) {
            Text(
                "Reset Password",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

        }


        TextButton(onClick = {
            navController.popBackStack()
            viewModel.forgotMessage.value = ""
        }) {
            Text(
                "Back to Login",
                color = GreenLime
            )
        }
    }
}
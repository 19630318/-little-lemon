package com.dybala.littelemon

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun Onboarding(navHostController: NavHostController) {

    val context = LocalContext.current

    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MiPref", Context.MODE_PRIVATE)
    val editor: SharedPreferences.Editor = sharedPreferences.edit()

    var firstName by remember {
        mutableStateOf("")
    }
    var lastName by remember {
        mutableStateOf("")
    }
    var email by remember {
        mutableStateOf("")
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
                .height(70.dp),
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo"
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF485E57))
                .height(120.dp)
        ) {

            Text(
                modifier = Modifier
                    .padding(20.dp)
                    .align(Alignment.Center),
                text = "Let's get to khow you",
                fontSize = 30.sp,
                color = Color.White
            )
        }
        Text(
            modifier = Modifier.padding(20.dp),
            text = "Personal information",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Column(
            modifier = Modifier.padding(15.dp),
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = firstName,
                onValueChange = {
                    firstName = it
                },
                label = {
                    Text(text = "First name", fontSize = 15.sp)
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "First name",
                        tint = Color.Black
                    )
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                maxLines = 1,
                singleLine = true,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    unfocusedBorderColor = Color.Black,
                    unfocusedLabelColor = Color.Black
                )
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = lastName,
                onValueChange = {
                    lastName = it
                },
                label = {
                    Text(text = "Last name", fontSize = 15.sp)
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Last name",
                        tint = Color.Black
                    )
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                maxLines = 1,
                singleLine = true,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    unfocusedBorderColor = Color.Black,
                    unfocusedLabelColor = Color.Black
                )
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = email,
                onValueChange = {
                    email = it
                },
                label = {
                    Text(text = "Email", fontSize = 15.sp)
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "email",
                        tint = Color.Black
                    )
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Go
                ),
                maxLines = 1,
                singleLine = true,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    unfocusedBorderColor = Color.Black,
                    unfocusedLabelColor = Color.Black
                )
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            onClick = {
                if (firstName.isNotEmpty() && lastName.isNotEmpty() && email.isNotEmpty()) {
                    editor.putString("firstName", firstName)
                    editor.putString("lastName", lastName)
                    editor.putString("email", email)
                    editor.putBoolean("Login", true)
                    editor.apply()
                    Toast.makeText(context, "Registration successful!", Toast.LENGTH_SHORT).show()
                    navHostController.navigate(route = Home.route) {
                        popUpTo(route = Onboarding.route) {
                            inclusive = true
                        }
                    }
                }else {
                    Toast.makeText(context, "Registration unsuccessful. Please enter all data.", Toast.LENGTH_SHORT).show()
                }
            },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Yellow
            )
        ) {
            Text(text = "Register")
        }
    }
}
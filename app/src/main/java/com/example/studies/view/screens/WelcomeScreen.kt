package com.example.studies.view.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.studies.R
import com.example.studies.ui.theme.StudiesTheme

@Composable
fun WelcomeScreen(navController: NavController) {
    var userName by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(id = R.string.bem_vindo),
            fontSize = 32.sp,
            color = Color(0xFF424242),
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = userName,
            onValueChange = { userName = it },
            label = { Text(stringResource(id = R.string.nome_label), color = Color(0xFF424242)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF424242),
                unfocusedBorderColor = Color(0xFF6B6969),
                cursorColor = Color(0xFF424242),
                focusedTextColor = Color(0xFF424242),
                unfocusedTextColor = Color(0xFF424242),
                focusedLabelColor = Color(0xFF424242),
                unfocusedLabelColor = Color(0xFF424242)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        )


        Button(
            onClick = {
                saveUserName(context, userName)
                navController.navigate("home") {
                    popUpTo("welcome") { inclusive = true }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B6969)),
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(50.dp)
        ) {
            Text(text = stringResource(id = R.string.entrar_button), color = Color.White, fontSize = 18.sp)
        }
    }
}

fun saveUserName(context: Context, userName: String) {
    val sharedPref = context.getSharedPreferences("studies_prefs", Context.MODE_PRIVATE)
    with(sharedPref.edit()) {
        putString("user_name", userName)
        apply()
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewWelcomeScreen() {
    StudiesTheme {
        val navController = rememberNavController()
        WelcomeScreen(navController = navController)
    }
}
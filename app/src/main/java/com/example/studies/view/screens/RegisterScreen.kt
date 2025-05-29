package com.example.studies.view.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.studies.R
import com.example.studies.ui.theme.StudiesTheme
import com.example.studies.auth.UserAuthRepository
import android.widget.Toast

@Composable
fun RegisterScreen(navController: NavController) {
    var userName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val context = LocalContext.current
    val userAuthRepository = remember { UserAuthRepository(context) }
    //strings para internacionalização
    val msgCamposObrigatorios = stringResource(R.string.campos_obrigatorios_toast)
    val msgSenhasNaoCoincidem = stringResource(R.string.senhas_nao_coincidem_toast)
    val msgRegistroSucesso = stringResource(R.string.registro_sucesso_toast)
    val msgRegistroFalha = stringResource(R.string.registro_falha_toast)


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(id = R.string.criar_conta_title),
            fontSize = 32.sp,
            color = Color(0xFF424242),
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = userName,
            onValueChange = { userName = it },
            label = { Text(stringResource(id = R.string.nome_usuario_label), color = Color(0xFF424242)) },
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
                .padding(bottom = 16.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(id = R.string.senha_label), color = Color(0xFF424242)) },
            visualTransformation = PasswordVisualTransformation(),
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
                .padding(bottom = 16.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text(stringResource(id = R.string.confirmar_senha_label), color = Color(0xFF424242)) },
            visualTransformation = PasswordVisualTransformation(),
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
                .padding(bottom = 32.dp),
            singleLine = true
        )

        Button(
            onClick = {
                 if (userName.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                    Toast.makeText(context, msgCamposObrigatorios, Toast.LENGTH_SHORT).show()
                    return@Button
                 }
                 if (password != confirmPassword) {
                    Toast.makeText(context, msgSenhasNaoCoincidem, Toast.LENGTH_SHORT).show()
                    return@Button
                 }
                 if (userAuthRepository.registerUser(userName, password)) {
                    Toast.makeText(context, msgRegistroSucesso, Toast.LENGTH_SHORT).show()
                    navController.navigate("welcome") {
                        popUpTo("register") { inclusive = true }
                    }
                 } else {
                    Toast.makeText(context, msgRegistroFalha, Toast.LENGTH_LONG).show()
                 }

            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B6969)),
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(50.dp)
        ) {
            Text(text = stringResource(id = R.string.cadastrar_button), color = Color.White, fontSize = 18.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = { navController.popBackStack() }) {
            Text(stringResource(id = R.string.ja_tenho_conta_button), color = Color(0xFF424242))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRegisterScreen() {
    StudiesTheme {
        val navController = rememberNavController()
        RegisterScreen(navController = navController)
    }
}
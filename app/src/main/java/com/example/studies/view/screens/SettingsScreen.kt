package com.example.studies.view.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.studies.R // Make sure this R is correctly imported from your app's package
import com.example.studies.view.components.Footer
import com.example.studies.ui.theme.StudiesTheme // Assuming you have this from your project
import com.example.studies.view.components.SmallButton

@Composable
fun SettingsScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(top= 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.configuracoes_main_title),
                fontSize = 30.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = FontFamily.Serif,
                color = Color(0xFF424242),
                modifier = Modifier.padding(top = 24.dp, bottom = 4.dp)
            )
            HorizontalDivider(
                modifier = Modifier
                    .width(200.dp)
                    .padding(bottom = 40.dp),
                thickness = 1.dp,
                color = Color(0xFF424242)
            )

            SmallButton(
                text = stringResource(id = R.string.sobre_button),
                icon = Icons.Filled.Info,
                contentDescription = stringResource(id = R.string.sobre_icon_desc),
                onClick = { /* nav para Sobre */ }
            )

            Spacer(modifier = Modifier.height(16.dp))

            SmallButton(
                text = stringResource(id = R.string.contato_button),
                icon = Icons.Filled.Phone,
                contentDescription = stringResource(id = R.string.contato_icon_desc),
                onClick = { /* nav para Contato */ }
            )
        }
        Footer(navController = navController, currentRoute = "settings")
    }
}
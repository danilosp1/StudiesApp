package com.example.studies.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.studies.ui.theme.StudiesTheme

@Composable
fun Footer(navController: NavController, currentRoute: String?) {
    HorizontalDivider(
        color = Color(0xFF0E0E0E),
        thickness = 2.5.dp
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color(0xFFC4C4C4)),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        FooterItem(
            icon = Icons.Default.Home,
            label = "",
            isSelected = currentRoute == "home",
            onClick = { navController.navigate("home") }
        )
        FooterItem(
            icon = Icons.AutoMirrored.Filled.List,
            label = "",
            isSelected = currentRoute == "tasks",
            onClick = { navController.navigate("tasks") }
        )
        FooterItem(
            icon = Icons.Default.DateRange,
            label = "",
            isSelected = currentRoute == "disciplines",
            onClick = { navController.navigate("disciplines") }
        )
        FooterItem(
            icon = Icons.Default.Settings,
            label = "Configurações",
            isSelected = currentRoute == "settings",
            onClick = { navController.navigate("settings") }
        )
    }
}

@Composable
fun FooterItem(icon: ImageVector, label: String, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) Color.White else Color(0xFF393939),
            modifier = Modifier.size(35.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewStudiesFooter() {
    StudiesTheme {
        val navController = rememberNavController()
        Footer(navController = navController, currentRoute = "home")
    }
}
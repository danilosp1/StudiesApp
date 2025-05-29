package com.example.studies.view.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.LocaleListCompat
import androidx.navigation.NavController
import com.example.studies.R
import com.example.studies.view.components.Footer
import com.example.studies.view.components.SmallButton
import java.util.Locale
import androidx.core.content.edit

private fun getSharedPreferences(context: Context) =
    context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE)

private fun saveLanguagePreference(context: Context, languageCode: String) {
    getSharedPreferences(context).edit { putString("AppLanguage", languageCode) }
}

private fun loadLanguagePreference(context: Context): String {
    return getSharedPreferences(context).getString("AppLanguage", "pt") ?: "pt"
}

fun setLocale(context: Context, languageCode: String) {
    saveLanguagePreference(context, languageCode)
    val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(languageCode)
    AppCompatDelegate.setApplicationLocales(appLocale)

    if (context is Activity) {
        context.recreate()
        Log.d("LocaleDebug", "Context is Activity, calling recreate()")
    } else {
        Log.w("LocaleDebug", "Context is NOT Activity (${context.javaClass.name}), attempting app restart.")
        val packageManager = context.packageManager
        val intent = packageManager.getLaunchIntentForPackage(context.packageName)
        val componentName = intent?.component
        val mainIntent = Intent.makeRestartActivityTask(componentName)
        context.startActivity(mainIntent)
        Runtime.getRuntime().exit(0)
    }
}


@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current
    var languageDropdownExpanded by remember { mutableStateOf(false) }
    val currentLanguageName = when (loadLanguagePreference(context)) {
        "en" -> "English"
        "pt" -> "Português"
        else -> "Português"
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(top = 64.dp),
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

            Text(
                text = stringResource(id = R.string.language_label),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Box(modifier = Modifier.padding(bottom = 24.dp)) {
                Row(
                    modifier = Modifier
                        .clickable { languageDropdownExpanded = true }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = stringResource(id = R.string.select_language_description),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(currentLanguageName, style = MaterialTheme.typography.bodyLarge)
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = stringResource(id = R.string.open_language_selection_description),
                    )
                }
                DropdownMenu(
                    expanded = languageDropdownExpanded,
                    onDismissRequest = { languageDropdownExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Português") },
                        onClick = {
                            setLocale(context, "pt")
                            languageDropdownExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("English") },
                        onClick = {
                            setLocale(context, "en")
                            languageDropdownExpanded = false
                        }
                    )
                }
            }

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

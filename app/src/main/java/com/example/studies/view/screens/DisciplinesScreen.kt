package com.example.studies.view.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.studies.MainActivity
import com.example.studies.R
import com.example.studies.StudiesApplication
import com.example.studies.data.model.DisciplineEntity
import com.example.studies.ui.theme.StudiesTheme
import com.example.studies.view.components.Footer
import com.example.studies.viewmodel.DisciplineViewModel
import com.example.studies.viewmodel.DisciplineViewModelFactory

@Composable
fun DisciplinesScreen(
    navController: NavController,
    viewModel: DisciplineViewModel = viewModel(
        factory = DisciplineViewModelFactory((LocalContext.current.applicationContext as StudiesApplication).repository)
    )
) {
    val uiState by viewModel.disciplinesUiState.collectAsState()
    val disciplines = uiState.disciplines

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(45.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.DisciplinasTitulo),
                    fontSize = 30.sp,
                    color = primaryTextColor
                )
                FloatingActionButton(
                    onClick = { navController.navigate("add_discipline") },
                    containerColor = Color(0xFF6B6969),
                    contentColor = Color.White,
                    modifier = Modifier.size(40.dp),
                    shape = RoundedCornerShape(percent = 50)
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = stringResource(id = R.string.add_disciplina_fab_desc)
                    )
                }
            }

            Spacer(modifier = Modifier.height(7.dp))

            HorizontalDivider(
                color = primaryTextColor,
                thickness = 2.dp,
                modifier = Modifier.padding(bottom = 45.dp)
            )

            if (disciplines.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Nenhuma disciplina adicionada ainda.", fontSize = 18.sp, color = secondaryTextColor)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(disciplines, key = { it.id }) { disciplineEntity ->
                        DisciplineCard(discipline = disciplineEntity) {
                            navController.navigate("disciplineDetail/${disciplineEntity.id}")
                        }
                    }
                }
            }
        }

        Footer(navController = navController, currentRoute = "disciplines")
    }
}

@Composable
fun DisciplineCard(discipline: DisciplineEntity, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .border(1.dp, borderColor, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.DateRange,
                    contentDescription = "Imagem da Disciplina",
                    tint = borderColor,
                    modifier = Modifier.size(30.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = discipline.name,
                    fontSize = 22.sp,
                    color = primaryTextColor
                )
                if (!discipline.professor.isNullOrBlank()) {
                    Text(
                        text = "Prof: ${discipline.professor}",
                        fontSize = 14.sp,
                        color = secondaryTextColor
                    )
                }
                if (!discipline.location.isNullOrBlank()) {
                    Text(
                        text = "Local: ${discipline.location}",
                        fontSize = 14.sp,
                        color = secondaryTextColor
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF0F0F0)
@Composable
fun PreviewDisciplinesScreen() {
    StudiesTheme {
        val navController = rememberNavController()
        DisciplinesScreen(navController = navController)
    }
}
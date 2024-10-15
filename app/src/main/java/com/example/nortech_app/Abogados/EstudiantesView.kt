package com.example.nortech_app.Abogados

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import viewmodel.UserViewModel

// Casos
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstudiantesView(navController: NavHostController, viewModel: UserViewModel) {
    val Students by viewModel.estudiantes.collectAsState()
    val scrollState = rememberScrollState()

    // Variables para almacenar los términos de búsqueda
    val searchName = remember { mutableStateOf("") }
    val searchMatricula = remember { mutableStateOf("") }
    val searchSemestre = remember { mutableStateOf("") }

    var isFilterVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchEstudiantes()
    }

    // Aplicar filtro a la lista de estudiantes
    val filteredEstudiantes = Students.filter { estudiante ->
        (searchName.value.isEmpty() || estudiante.name.contains(searchName.value, ignoreCase = true)) &&
                (searchMatricula.value.isEmpty() || estudiante.Matricula.contains(searchMatricula.value, ignoreCase = true)) &&
                (searchSemestre.value.isEmpty() || estudiante.Semestre.contains(searchSemestre.value, ignoreCase = true))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "ESTUDIANTES",
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp, // Tamaño reducido
                            color = Color(0xFF1E88E5) // Azul suave
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFE3F2FD) // Fondo azul claro
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("AddEstudiante") },
                containerColor = Color(0xFF1976D2)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo Estudiante", tint = Color.White)
            }
        },
        bottomBar = {
            BottomBarAbogado(navController, 4)
        }
    ) { paddingValues ->

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isFilterVisible = !isFilterVisible },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Filtros de búsqueda",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Icon(
                        imageVector = if (isFilterVisible) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (isFilterVisible) "Ocultar Filtros" else "Mostrar Filtros"
                    )
                }
                Divider(
                    color = Color.Gray,
                    thickness = 2.dp,
                    modifier = Modifier.padding(vertical = 3.dp)
                )

                // Filtros con visibilidad animada
                AnimatedVisibility(visible = isFilterVisible) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        // Campos de búsqueda
                        OutlinedTextField(
                            value = searchName.value,
                            onValueChange = { searchName.value = it },
                            label = { Text("Buscar por Nombre") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Search, contentDescription = null)
                            }
                        )
                        OutlinedTextField(
                            value = searchMatricula.value,
                            onValueChange = { searchMatricula.value = it },
                            label = { Text("Buscar por Matricula") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Search, contentDescription = null)
                            }
                        )
                        OutlinedTextField(
                            value = searchSemestre.value,
                            onValueChange = { searchSemestre.value = it },
                            label = { Text("Buscar por Semestre") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Search, contentDescription = null)
                            }
                        )
                    }
                }

                // Mostrar lista de estudiantes filtrados
                for (estudiante in filteredEstudiantes) {
                    EstudianteItem(
                        matricula = estudiante.Matricula,
                        title = estudiante.name,
                        semestre = "Semestre ${estudiante.Semestre}",
                        onClick = {
                            viewModel.Matricula.value = estudiante.Matricula
                            viewModel.Semestre.value = estudiante.Semestre
                            viewModel.CorreoAlumno.value = estudiante.Correo_alumno
                            viewModel.nameAlumno.value = estudiante.name
                            navController.navigate("VerEstudianteAbogado") }
                    )
                }
            }
        }
    }
}


@Composable
fun EstudianteItem(
    matricula: String,
    title: String,
    semestre: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = matricula,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = semestre,
                    fontSize = 12.sp,
                    color = Color.Gray,
                )

            }
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF546E7A))
            ) {
                Text(text = "ver", color = Color.White)
            }
        }
    }
}

package com.example.nortech_app.Abogados

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun AddEstudiante(navController: NavController, viewModel: UserViewModel) {
    val context = LocalContext.current
    var mail by remember { mutableStateOf("") }
    var matricula by remember { mutableStateOf("") }
    var Semestre by remember { mutableStateOf("") }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Añadir estudiante",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        },
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Nuevo Estudiante",
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                    color = Color(0xFF1E88E5), // Color azul para resaltar el nombre
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                EditableField(label = "Correo Estudiante", value = mail) { mail = it }
                EditableField(label = "Matricula Estudiante", value = matricula) { matricula = it }
                EditableField(label = "Semestre Estudiante", value = Semestre) { Semestre = it }
                Button(
                    onClick = { viewModel.InsertEstudiante(matricula,Semestre,mail)
                        Toast.makeText(
                            context,
                            "Se añadio el estudiante",
                            Toast.LENGTH_SHORT
                        ).show()
                        navController.navigateUp()
                              }, // Mostrar el diálogo al hacer clic
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Blue // Cambiar el color de fondo a rojo
                    ),
                    enabled = mail!=""&&matricula!=""&&Semestre!=""
                ) {
                    Text(
                        text = "Añadir Estudiante",
                        color = Color.White
                    ) // Cambiar el texto a blanco
                }
            }
        }
    }
}
package com.example.nortech_app.Abogados

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nortech_app.R
import viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerCasoView(navController: NavController, viewModel: UserViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Detalles del Caso", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("CasosAbogado") }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Regresar"
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomBarAbogado(navController, 2)
        }
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
                    text = viewModel.ALIAScaso.value,
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                    color = Color(0xFF1E88E5), // Color azul para resaltar el nombre
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Sección de información del caso
                SectionTitle("INFORMACIÓN DE CASO")
                Divider(color = Color.Gray, thickness = 2.dp, modifier = Modifier.padding(vertical = 3.dp)) // Línea divisora
                InfoRow2(label = "NUC", value = viewModel.nucCaso.value)
                Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(vertical = 3.dp)) // Línea divisora
                InfoRow2(label = "Nombre ${viewModel.tipocaso.value}", value = viewModel.namecaso.value)
                Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(vertical = 3.dp)) // Línea divisora
                InfoRow2(label = "Descripción del caso", value = "")
                SectionDes(viewModel.descCaso.value)
                Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(vertical = 3.dp)) // Línea divisora
                InfoRow2(label = "Carpeta Judicial", value = viewModel.caroJudicialCaso.value)
                Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(vertical = 3.dp)) // Línea divisora
                InfoRow2(label = "Carpeta Investigación", value = viewModel.carpInvestCaso.value)
                Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(vertical = 3.dp)) // Línea divisora
                InfoRow2(label = "Acceso FV", value = viewModel.accFVCaso.value)
                Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(vertical = 3.dp)) // Línea divisora
                InfoRow2(label = "Password FV", value = viewModel.passFVCaso.value)
                Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(vertical = 3.dp)) // Línea divisora
                InfoRow2(label = "Fiscal Titular", value = viewModel.fiscalTituCaso.value)
                Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(vertical = 3.dp)) // Línea divisora
                InfoRow2(label = "Unidad Investigación (UI)", value = viewModel.unidadInvestCaso.value)
                Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(vertical = 3.dp)) // Línea divisora
                InfoRow2(label = "Dirección UI", value = viewModel.dirUIcaso.value)
                Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(vertical = 3.dp)) // Línea divisora
                InfoRow2(label = "Carpeta Drive", value = viewModel.carpDriveCaso.value)
                Divider(color = Color.Gray, thickness = 2.dp, modifier = Modifier.padding(vertical = 3.dp)) // Línea divisora

                Spacer(modifier = Modifier.height(32.dp)) // Espaciado final
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp), // Ajuste del margen superior e inferior
                    horizontalArrangement = Arrangement.Center, // Centra el contenido horizontalmente
                    verticalAlignment = Alignment.CenterVertically // Alineación vertical central
                ) {
                    Text(
                        text = "Archivos relacionados",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF616161)
                    )

                    Spacer(modifier = Modifier.weight(10f)) // Empuja el botón hacia el borde derecho

                    Button(
                        onClick = {  },
                        colors = ButtonDefaults.buttonColors(Color(0xFF1E88E5)),
                        modifier = Modifier.height(35.dp) // Ajusta la altura del botón para que no sea demasiado grande
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Agregar Noticia",
                            tint = Color.White
                        )
                        Text(text = "New File", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun InfoRow2(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        if (value.isNotEmpty()) {
            Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Light, textAlign = TextAlign.End)
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color(0xFF616161),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        textAlign = TextAlign.Start
    )
}
@Composable
fun SectionDes(title: String) {
    Text(
        text = title,
        fontSize = 16.sp,
        color = Color(0xFF616161),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        textAlign = TextAlign.Start
    )
}
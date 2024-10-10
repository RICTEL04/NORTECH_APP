package com.example.nortech_app.Abogados

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import model.Caso
import viewmodel.UserViewModel

// Casos
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CasosView(navController: NavHostController, viewModel: UserViewModel) {
    val allCasos by viewModel.casos.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.getAllCasos()
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
                            text = "CASOS",
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp, // Reduce ligeramente el tamaño
                            color = Color(0xFF1E88E5) // Azul suave
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFE3F2FD) // Fondo azul claro
                )
            )
        },
        bottomBar = {
            BottomBarAbogado(navController, 2)
        },
        floatingActionButton = {
            if (viewModel.rol.value == "2") {
                FloatingActionButton(onClick = { navController.navigate("CrearCaso") }) {
                    Icon(Icons.Default.Add, contentDescription = "NUEVO CASO")
                }
            }
        },
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                for (rowIndex in allCasos.indices) {
                    val caso1 = allCasos[rowIndex]
                    CasosItem(
                        casos = caso1,
                        viewModel = viewModel,
                        navController = navController
                    )
                }
            }
        }
    }
}

    @Composable
    fun CasosItem(
        //idCliente: String,
        casos: Caso,
        viewModel: UserViewModel,
        navController: NavController // Add this parameter
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
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
                        text = "Fecha Creado: ${casos.fecha_Creado}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Row {
                        Text(
                            text = casos.ALIAS,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Text(
                            text = "Caso: ${casos.nuc}",
                            fontSize = 12.sp,
                            color = Color.Gray,
                        )
                        Text(
                            text = "Tipo: ${casos.tipo}",
                            fontSize = 12.sp,
                            color = Color.Gray,
                        )
                    }
                }
                Button(
                    onClick = {
                        viewModel.idcaso.value = casos.id
                        viewModel.tipocaso.value = casos.tipo
                        viewModel.fechaCreadoCaso.value = casos.fecha_Creado.toString()
                        viewModel.nucCaso.value = casos.nuc
                        viewModel.descCaso.value = casos.description
                        viewModel.caroJudicialCaso.value = casos.carp_Judicial
                        viewModel.carpInvestCaso.value = casos.carp_Investigacion
                        viewModel.accFVCaso.value = casos.acc_FV
                        viewModel.passFVCaso.value = casos.pass_FV
                        viewModel.fiscalTituCaso.value = casos.fiscal_Titular
                        viewModel.unidadInvestCaso.value = casos.unidad_Invest
                        viewModel.carpDriveCaso.value = casos.carpeta_drive
                        viewModel.dirUIcaso.value = casos.dir_UI
                        viewModel.ALIAScaso.value = casos.ALIAS
                        viewModel.namecaso.value = casos.nombre

                        navController.navigate("VerCasoAbogado")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF546E7A))
                ) {
                    Text(text = "ver", color = Color.White)
                }
            }
        }
    }

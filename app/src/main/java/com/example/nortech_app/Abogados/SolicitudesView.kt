package com.example.nortech_app.Abogados


import android.util.Log
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import viewmodel.UserViewModel

import model.Solicitud
import kotlin.math.log

// Casos
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudesView(
    navController: NavHostController,
    viewModel: UserViewModel
) {

    val allSolicitudes by remember { viewModel.allSolicitudes }

    // Llamar a getHoras() cuando la pantalla se inicializa
    LaunchedEffect(Unit) {

        viewModel.getAllSolicitudes()
    }

    print(allSolicitudes)


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
            BottomBarAbogado(navController, 3)
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {


            if(allSolicitudes == null) {
                Text(
                    modifier = Modifier
                        .padding(top = 20.dp, bottom = 20.dp),
                    text = "No hay solicitudes",
                    textAlign = TextAlign.Center,
                    fontSize = 30.sp
                )
            }
            else {

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, bottom = 20.dp)
                        .background(MaterialTheme.colorScheme.primary),
                ){

                    allSolicitudes?.forEach(){(idCliente,solicitudes) ->

                        item {
                            SolicitudCard(idCliente = idCliente, solicitudes = solicitudes, viewModel)
                        }


                    }

                }

            }

        }
    }
}


@Composable
fun SolicitudCard(
    idCliente: String,
    solicitudes: List<Solicitud>,
    viewModel: UserViewModel
) {
    var expanded by remember { mutableStateOf(false) } // Controls whether to show additional information
    var selectedDate by remember { mutableStateOf<Solicitud?>(null) } // Track the selected date


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
        //.background(MaterialTheme.colorScheme.onSurface),
        ,
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Show summary (title, relationship, truncated description)


            Text(
                text = "Cliente: ${solicitudes.firstOrNull()?.nombre ?: "Sin nombre"} ",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = solicitudes.firstOrNull()?.motivo ?: "Sin motivo",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = solicitudes.firstOrNull()?.descripcion ?: "Sin descripción",
                maxLines = if (expanded) Int.MAX_VALUE else 1, // Show full text when expanded
                overflow = if (!expanded) TextOverflow.Ellipsis else TextOverflow.Visible, // Add "..." if the text is too long when not expanded
                fontSize = if (expanded) 14.sp else 12.sp, // Increase font size when expanded
                color = if (expanded) Color.Black else Color.Gray // Change color to black when expanded
            )



            Spacer(modifier = Modifier.height(8.dp))

            // Show details if expanded
            if (expanded) {
                Log.d("UserViewModel InsertSolicitud", "Before rendering date buttons")
                Column {
                    solicitudes.forEach { solicitud ->
                        // Button for each date
                        Button(
                            onClick = {
                                selectedDate = solicitud // Update selected date
                            },
                            modifier = Modifier.padding(vertical = 4.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedDate == solicitud) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.primary,
                                disabledContentColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Text(
                                text = "Fecha: ${solicitud.dia}/${solicitud.mes}/${solicitud.anio} ${solicitud.hora}",
                                color = if (selectedDate == solicitud) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onPrimary
                            )
                        }

                    }
                }
            }


            // Row for the buttons to select appointment and toggle details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                // Toggle button to expand/collapse details
                Button(
                    onClick = { expanded = !expanded },
                    modifier = Modifier
                        .padding(vertical = 8.dp)


                ) {
                    Text(if (expanded) "Ocultar" else "Mostrar más")
                }

                // Button to select the appointment, visible only when expanded
                if (expanded) {
                    Button(
                        onClick = { /* Action to select the appointment */ },
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Text("Escoger cita")
                    }
                }

            }

            }
        }
}
package com.example.nortech_app.Visits

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun SolicitudesScreen(navController: NavController) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("") }
    val options = listOf("Consulta general", "Problema legal", "Asesoría financiera", "Otro", "Seguimiento")

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController, 1)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Text in bold saying "Solicitudes"
            Text(
                text = "Solicitudes",
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Text in gray saying "Para realizar una solicitud debes completar la informacion en tu perfil"
            Text(
                text = "Para realizar una solicitud debes completar la información en tu perfil",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Little text saying "Motivo"
            Text(
                text = "Motivo",
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // TextField with dropdown list with five options to select
            Box(modifier = Modifier.fillMaxWidth()) {
                TextField(
                    value = selectedOption,
                    onValueChange = { selectedOption = it },
                    label = { Text("Seleccione un motivo") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { expanded = true }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    },
                    readOnly = true
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    options.forEach { option ->
                        DropdownMenuItem(
                            onClick = {
                                selectedOption = option
                                expanded = false
                            },
                            text = { Text(option) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // TextField to add a description
            TextField(
                value = "",
                onValueChange = { /* Handle text change */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                label = { Text("Descripción") },
                singleLine = false,
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Button with text "Enviar solicitud"
            Button(
                onClick = { navController.navigate("Calendar") },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = "Enviar solicitud", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Al realizar una solicitud, acepta que haya un estudiante presente en la asesoría",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}
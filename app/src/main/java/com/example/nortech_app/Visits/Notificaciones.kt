package com.example.nortech_app.Visits

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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.nortech_app.Students.BottomNavigationBarEstudiante

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "NOTIFICACIONES",
                            fontWeight = FontWeight.Bold,
                            fontSize = 35.sp, // Reduce ligeramente el tamaño
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
            BottomNavigationBar(navController, 3)
        }
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
                NotificationItem(
                    date = "Fecha: dd/mm/yyyy",
                    title = "Solicitud aprobada",
                    onClick = { navController.navigate("VerNot") }
                )
                NotificationItem(
                    date = "Fecha: dd/mm/yyyy",
                    title = "Solicitud denegada",
                    onClick = { /* Acción para ver la solicitud denegada */ }
                )
                NotificationItem(
                    date = "Fecha: dd/mm/yyyy",
                    title = "Confirmación de cita",
                    onClick = { /* Acción para ver la confirmación de cita */ }
                )
            }
        }
    }
}

@Composable
fun NotificationItem(
    date: String,
    title: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background((Color.Gray.copy(alpha = 0.2f)), shape = RoundedCornerShape(8.dp))
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
                    text = date,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
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

// VER NOTIF
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerNotGeneral(navController: NavController){
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Perfil",
                            fontWeight = FontWeight.Bold,
                            fontSize = 35.sp
                        )
                    }
                })
        },
        bottomBar = {
            BottomNavigationBarEstudiante(navController, 0)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(75.dp))

            Text(text = "Notificacion", fontSize = 36.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(150.dp))

            Text(text = "Fecha: dd/mm/yyyy", fontSize = 16.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(4.dp))

            Text(text = "Solicitud aprobada", fontSize = 24.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "La solicitud al caso xxx ha sido aprobada. Favor de agendar cita para continuar el proceso",
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(75.dp))

            Button(
                onClick = { /* Handle send request */ }
            ) {
                Text(text = "Enviar solicitud", fontSize = 18.sp)
            }
        }
    }
}
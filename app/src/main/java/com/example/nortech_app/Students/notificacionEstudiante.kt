package com.example.nortech_app.Students

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.nortech_app.Visits.BottomNavigationBar

// NOTIFICACIONES ESTUDIANTE--------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreenEstudiante(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Notificaciones",
                            fontWeight = FontWeight.Bold,
                            fontSize = 35.sp
                        )
                    }
                })
        },
        bottomBar = {
            BottomNavigationBarEstudiante(navController, 3)
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
                    title = "Asesoría",
                    onClick = { navController.navigate("VerNotificacionesEstudiantes") }
                )
                NotificationItem(
                    date = "Fecha: dd/mm/yyyy",
                    title = "Asesoría",
                    onClick = { navController.navigate("VerNotificacionesEstudiantes") }
                )
                NotificationItem(
                    date = "Fecha: dd/mm/yyyy",
                    title = "Asesoría",
                    onClick = { navController.navigate("VerNotificacionesEstudiantes") }
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

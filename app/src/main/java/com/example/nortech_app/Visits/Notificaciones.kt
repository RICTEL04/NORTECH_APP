package com.example.nortech_app.Visits

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.nortech_app.Abogados.FullScreenDialog
import com.example.nortech_app.Students.BottomNavigationBarEstudiante
import com.example.nortech_app.sendNotificationToUser
import model.Cita
import model.Notification
import viewmodel.UserViewModel
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.log

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(navController: NavHostController, viewModel: UserViewModel) {


    LaunchedEffect(Unit) {
        viewModel.getUserNotifications()
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
                            text = "NOTIFICACIONES",
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
            BottomNavigationBar(navController, 3)
        }
    ) { paddingValues ->

        if(viewModel.userNotifications.value.isNullOrEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(8.dp), // Espacio entre elementos
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item{
                    Text(
                        text = "No hay Notificaciones",
                        fontWeight = FontWeight.Bold, // Negrita
                        fontSize = 26.sp, // Tamaño de fuente
                        modifier = Modifier.fillMaxWidth(), // Asegura que el texto ocupe todo el ancho disponible
                        textAlign = TextAlign.Center, // Centra el texto
                        color = Color.Black // Color del texto, puedes cambiarlo según tus necesidades
                    )
                }
            }
        }
        else{
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(8.dp), // Espacio entre elementos
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                items(viewModel.userNotifications.value){ notificacion ->

                    NotificationItem(notification = notificacion)

                    Spacer(modifier = Modifier.height(8.dp))

                }

            }
        }

    }
}


@Composable
fun NotificationItem(
    notification : Notification
) {
    var expanded by remember { mutableStateOf(false) } // Controls whether to show additional information

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

        val zonedDateTime = ZonedDateTime.parse(notification.created_at, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        val mexicoZoneId = ZoneId.of("America/Mexico_City") // Zona horaria de México
        val localDateTime = zonedDateTime.withZoneSameInstant(mexicoZoneId)

        val localDate = localDateTime.toLocalDate()
        val localTime = localDateTime.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"))


        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {


                Text(
                    text = "${localTime ?: "00:00"} ",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(2f) // Hace que este texto ocupe todo el espacio restante
                )

                Text(
                    text = "${localDate ?: "0000/00/00"} ",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )


            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = notification.body ?: "Sin descripción",
                maxLines = if (expanded) Int.MAX_VALUE else 1, // Show full text when expanded
                overflow = if (!expanded) TextOverflow.Ellipsis else TextOverflow.Visible, // Add "..." if the text is too long when not expanded
                fontSize = if (expanded) 14.sp else 12.sp, // Increase font size when expanded
                color = if (expanded) Color.Black else Color.Gray // Change color to black when expanded
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    expanded = !expanded


                },
                modifier = Modifier
                    .padding(vertical = 8.dp)
            ) {
                Text(if (expanded) "Ocultar" else "Mostrar más")
            }

        }
    }
}
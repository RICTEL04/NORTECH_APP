package com.example.nortech_app.Abogados

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nortech_app.R

@Composable
fun BottomBarAbogado(navController: NavController, indexes: Int) {
    Surface(
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavigationItem(
                icon = R.drawable.home,
                label = "Home",
                selected = indexes == 0,
                onClick = {
                    navController.navigate("main") }
            )
            BottomNavigationItem(
                icon = R.drawable.citas,
                label = "Agenda",
                selected = indexes == 1,
                onClick = { navController.navigate("AgendaAbogado") }
            )
            BottomNavigationItem(
                icon = R.drawable.casos,
                label = "Casos",
                selected = indexes == 2,
                onClick = { navController.navigate("CasosAbogado") }
            )
            BottomNavigationItem(
                icon = R.drawable.chat,
                label = "Solicitudes",
                selected = indexes == 3,
                hasNotificationDot = false,
                onClick = {
                    navController.navigate("SolicitudesAbogado") }
            )
            BottomNavigationItem(
                icon = R.drawable.perfil,
                label = "Estudiantes",
                selected = indexes == 4,
                onClick = { navController.navigate("EstudiantesAbogado") }
            )
            BottomNavigationItem(
                icon = R.drawable.perfil,
                label = "Perfil",
                selected = indexes == 5,
                onClick = { navController.navigate("PerfilAbogado") }
            )
        }
    }
}

@Composable
fun BottomNavigationItem(
    icon: Int,
    label: String,
    selected: Boolean,
    hasNotificationDot: Boolean = false,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp)
    ) {
        Box {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = label,
                tint = if (selected) Color.Black else Color.Gray,
                modifier = Modifier.size(24.dp)
            )
            if (hasNotificationDot) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(Color.Red)
                        .align(Alignment.TopEnd)
                )
            }
        }
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) Color.Black else Color.Gray
        )
    }
}

package com.example.nortech_app.Abogados


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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

// Casos
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudesView(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Solicitudes",
                            fontWeight = FontWeight.Bold,
                            fontSize = 35.sp
                        )
                    }
                })
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
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CasosItem(
                    date = "Fecha: dd/mm/yyyy",
                    title = "Solicitud 1111",
                    descripcion = "Delito Contra el Honor",
                    relacion = "Victima",
                    onClick = {  navController.navigate("VerSolicitudAbogado") }
                )
                CasosItem(
                    date = "Fecha: dd/mm/yyyy",
                    title = "Solicitud 1112",
                    descripcion = "Delito Contra la Familia",
                    relacion = "Investigado",
                    onClick = { navController.navigate("VerSolicitudAbogado") }
                )
                CasosItem(
                    date = "Fecha: dd/mm/yyyy",
                    title = "Solicitud 1113",
                    descripcion = "Delito Contra el Orden Publico",
                    relacion = "Investigado",
                    onClick = { navController.navigate("VerSolicitudAbogado") }
                )
            }
        }
    }
}

@Composable
fun SolicitudesItem(
    date: String,
    title: String,
    descripcion: String,
    relacion: String,
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
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ){
                    Text(
                        text = descripcion,
                        fontSize = 10.sp,
                        color = Color.Gray,
                    )
                    Text(
                        text = relacion,
                        fontSize = 10.sp,
                        color = Color.Gray,
                    )
                }

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
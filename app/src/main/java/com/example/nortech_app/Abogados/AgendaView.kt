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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeCompilerApi
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendaView(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Agenda",
                            fontWeight = FontWeight.Bold,
                            fontSize = 35.sp
                        )
                    }
                })
        },
        bottomBar = {
            BottomBarAbogado(navController, 1)
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
                Text(text = "01/01/2024")
                AgendaItem(
                    date = "Fecha: 01/01/2024",
                    title = "1111 Caso",
                    time = "2:30 pm",
                    onClick = { navController.navigate("VerCasoAbogado") }
                )
                AgendaItem(
                    date = "Fecha: 01/01/2024",
                    title = "1112 Caso",
                    time = "4:30 pm",
                    onClick = { navController.navigate("VerCasoAbogado") }
                )
                Text(text = "02/01/2024")
                AgendaItem(
                    date = "Fecha: 02/01/2024",
                    title = "1113 Caso",
                    time = "11:30 am",
                    onClick = { navController.navigate("VerCasoAbogado") }
                )
                AgendaItem(
                    date = "Fecha: 02/01/2024",
                    title = "1114 Caso",
                    time = "2:00 pm",
                    onClick = { navController.navigate("VerCasoAbogado") }
                )
            }
        }
    }
}

@Composable
fun AgendaItem(
    date: String,
    title: String,
    time: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
            .padding(24.dp)
            .clickable(onClick = onClick),
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
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = time,
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

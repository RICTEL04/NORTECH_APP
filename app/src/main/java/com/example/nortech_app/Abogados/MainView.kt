package com.example.nortech_app.Abogados

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun MainView(navController: NavHostController) {
    Scaffold(
        bottomBar = {
            BottomBarAbogado(navController, 0)
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Selecciona una opción",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .height(150.dp)
                        .background(Color.LightGray, shape = RectangleShape)
                        .padding(16.dp)
                        .clickable {
                            navController.navigate("CasosAbogado")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Casos",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(25.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .height(150.dp)
                        .background(Color.LightGray, shape = RectangleShape)
                        .padding(16.dp)
                        .clickable {
                            navController.navigate("EstudiantesAbogado")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Estudiantes",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(25.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .height(150.dp)
                        .background(Color.LightGray, shape = RectangleShape)
                        .padding(16.dp)
                        .clickable {
                            navController.navigate("AgendaAbogado")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Agenda",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

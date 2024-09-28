package com.example.nortech_app.Visits

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import viewmodel.UserViewModel

@Composable
fun MainScreen(viewModel: UserViewModel, navController: NavController) {
    Scaffold(bottomBar = {
        BottomNavigationBar(navController, 0)
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

                // Text in bold at the top
                Text(
                    text = "Selecciona una opción",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(50.dp))

                // First Button
                Button(
                    onClick = { navController.navigate("solicitudes") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                    shape = RectangleShape
                ) {
                    Text(text = "Realizar una solicitud", fontSize = 24.sp, color = Color.Black)
                }

                Spacer(modifier = Modifier.height(50.dp))

                // Second Button
                Button(
                    onClick = { navController.navigate("Derechos") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                    shape = RectangleShape
                ) {
                    Text(text = "Consultar información", fontSize = 24.sp, color = Color.Black)
                }
            }
        }
    }
}

@Composable
fun ErrorScreen(message: String) {
    Text(message)
}


@Composable
fun LoadingScreen() {
    CircularProgressIndicator()
}


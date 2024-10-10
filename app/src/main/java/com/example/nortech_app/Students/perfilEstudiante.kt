package com.example.nortech_app.Students

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.nortech_app.R
import com.example.nortech_app.Visits.BottomNavigationBar
import viewmodel.UserViewModel

// PERFIL ESTUDIANTE -------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEstudianteScreen(viewModel: UserViewModel, navController: NavHostController) {
    LaunchedEffect(Unit) {
        viewModel.getName()
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
                            text = "Perfil",
                            fontWeight = FontWeight.Bold,
                            fontSize = 35.sp
                        )
                    }
                })
        },
        bottomBar = {
            BottomNavigationBarEstudiante(navController, 4)
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
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Icon and Name
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.profile), // Replace with your image resource
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .size(100.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = viewModel.userName.value, fontWeight = FontWeight.Bold, fontSize = 25.sp)
                        Text(text = "Estudiante", fontSize = 20.sp, color = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Account Information
                Text(text = "INFORMACIÓN DE CUENTA", fontSize = 14.sp, color = Color.Black)

                Spacer(modifier = Modifier.height(8.dp))

                InfoRow(label = "Matrícula", value = "A00123456")
                InfoRow(label = "Semestre", value = "5")
                InfoRow(label = "Abogado", value = "Abogado1")

                Spacer(modifier = Modifier.height(32.dp))

                // Logout Button
                Button(
                    onClick = { navController.navigate("Home") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Cerrar sesión")
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 16.sp)
        if (value.isNotEmpty()) {
            Text(text = value,
                fontSize = 16.sp)
        }
    }
}

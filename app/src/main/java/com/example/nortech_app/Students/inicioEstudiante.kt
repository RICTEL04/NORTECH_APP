package com.example.nortech_app.Students

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.nortech_app.Visits.BottomNavigationBar
import com.example.nortech_app.Visits.NoticiasCarousel
import com.example.nortech_app.Visits.NoticiasGrid
import viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainEstudianteScreen(viewModel: UserViewModel, navController: NavController) {
    val noticias by viewModel.noticias.collectAsState()
    val ultimasNoticias = noticias.takeLast(5)
    val scrollState = rememberScrollState()

    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.fetchNoticias()
        viewModel.getRol()
        noticias.forEach {
            Log.d("NoticiasURL", "URL de imagen: ${it.Image_URL}")
        }
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
                            text = "INICIO",
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
            BottomNavigationBarEstudiante(navController, 0)
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xffcccccc)) // Fondo color muy suave (verde claro)
                .verticalScroll(scrollState)
        ) {
            // Carrusel con las últimas 5 noticias
            Text(
                text = "Últimas Noticias",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(16.dp),
                color = Color(0xFF1B5E20) // Verde oscuro
            )
            NoticiasCarousel(noticias = ultimasNoticias, navController = navController, viewModel)

            // Espacio para la siguiente cita
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(8.dp), // Añade elevación
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp) // Bordes redondeados
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Siguiente cita",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E88E5) // Azul
                        )
                        Spacer(modifier = Modifier.height(15.dp))
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Día: 28-09-2004", fontSize = 16.sp, color = Color.Gray)
                            Text(text = "Hora: 15:00", fontSize = 16.sp, color = Color.Gray)
                        }
                    }
                }
            }

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Social",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E88E5)
                )
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(8.dp), // Añade elevación
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp) // Bordes redondeados
            ) {
                // Campo de búsqueda
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Buscar noticias", color = Color.Gray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .border(2.dp, Color.LightGray, RoundedCornerShape(8.dp)),
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Buscar")
                    }
                )
                // Filtrar las noticias según la búsqueda
                val filteredNoticias = if (searchQuery.isEmpty()) {
                    noticias
                } else {
                    noticias.filter {
                        it.Titulo.contains(searchQuery, ignoreCase = true) ||
                                it.Descripcion.contains(searchQuery, ignoreCase = true)
                    }
                }

                NoticiasGrid(noticias = filteredNoticias, navController = navController, viewModel)
            }
            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}
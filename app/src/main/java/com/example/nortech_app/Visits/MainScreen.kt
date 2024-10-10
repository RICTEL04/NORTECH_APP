package com.example.nortech_app.Visits

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import model.Noticias
import viewmodel.UserViewModel
import coil.request.ImageRequest
import com.example.nortech_app.R
import com.example.nortech_app.Students.BottomNavigationBarEstudiante
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.pager.HorizontalPager
import com.google.ai.client.generativeai.Chat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: UserViewModel, navController: NavController) {
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
            BottomNavigationBar(navController, 0)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // Acción para navegar al chatbot
                    navController.navigate("chatbot")
                },
                modifier = Modifier
                    .padding(16.dp)
                    .offset(x = (-20).dp, y = (-20).dp), // Mueve el botón a la parte inferior izquierda
                containerColor = Color(0xFF1E88E5), // Color del botón
                contentColor = Color.White // Color del contenido del botón
            ) {
                // Utilizar una imagen desde recursos o URL
                Image(
                    painter = painterResource(id = R.drawable.chatbot), // Imagen local desde recursos
                    contentDescription = "Chatbot",
                    modifier = Modifier.size(24.dp) // Tamaño de la imagen
                )
            }
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


@Composable
@OptIn(ExperimentalFoundationApi::class, ExperimentalPagerApi::class)
fun NoticiasCarousel(noticias: List<Noticias>, navController: NavController, viewModel: UserViewModel) {
    val pagerState = rememberPagerState(initialPage = 0)

    HorizontalPager(
        count = noticias.size,
        state = pagerState,
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
    ) { page ->
        val noticia = noticias[page]

        Box(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxSize()
                .clickable {
                    viewModel.id_not.value = noticia.id
                    viewModel.TituloNot.value = noticia.Titulo
                    viewModel.DescripcionNot.value = noticia.Descripcion
                    viewModel.imagenfilename.value = noticia.NombreImg
                    viewModel.URL_Not.value = noticia.Image_URL
                    navController.navigate("detalle_noticia")
                }
        ) {
            val painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(noticia.Image_URL)
                    .crossfade(true)
                    .build()
            )

            Image(
                painter = painter,
                contentDescription = noticia.Titulo,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black),
                            startY = 100f
                        )
                    ),
                contentAlignment = Alignment.BottomStart
            ) {
                Text(
                    text = noticia.Titulo,
                    color = Color.White,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}


@Composable
fun NoticiasGrid(noticias: List<Noticias>, navController: NavController,viewModel: UserViewModel) {
    Column(modifier = Modifier.fillMaxWidth()) {
        for (rowIndex in noticias.indices step 2) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                val noticia1 = noticias[rowIndex]
                NoticiasItem(noticia = noticia1, modifier = Modifier.weight(1f), navController,viewModel)
                if (rowIndex + 1 < noticias.size) {
                    val noticia2 = noticias[rowIndex + 1]
                    NoticiasItem(noticia = noticia2, modifier = Modifier.weight(1f), navController,viewModel)
                }
            }
        }
    }
}

@Composable
fun NoticiasItem(noticia: Noticias, modifier: Modifier, navController: NavController,viewModel: UserViewModel) {
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(noticia.Image_URL)
            // "https://wlrjyngxgupgafqzyxvs.supabase.co/storage/v1/object/public/imagenes/"
            .crossfade(true)
            .build()
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                viewModel.id_not.value = noticia.id
                viewModel.TituloNot.value = noticia.Titulo
                viewModel.DescripcionNot.value = noticia.Descripcion
                viewModel.imagenfilename.value = noticia.NombreImg
                viewModel.URL_Not.value = noticia.Image_URL
                navController.navigate("detalle_noticia")
            }
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painter,
                contentDescription = noticia.Titulo,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = noticia.Titulo, fontSize = 18.sp, maxLines = 1, fontWeight = FontWeight.Bold,)
            Text(text = noticia.Descripcion, fontSize = 14.sp, maxLines = 2)
        }
    }
}



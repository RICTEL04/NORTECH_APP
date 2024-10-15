package com.example.nortech_app.Abogados


import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.nortech_app.R
import viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerEstudiantesView(navController: NavController, viewModel: UserViewModel){
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    LaunchedEffect (Unit){
        viewModel.GetEstudianteFFpicG(viewModel.CorreoAlumno.value)
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detalles Estudiante",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Regresar",
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete, // Puedes cambiar el ícono aquí
                            contentDescription = "Editar Caso"
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomBarAbogado(navController, 4)
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
                Text(text = "Estudiante", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E88E5))
                Spacer(modifier = Modifier.height(30.dp))
                if (viewModel.FilepicEstudiante.value=="") {
                    Image(
                        painter = painterResource(id = R.drawable.profile), // Replace with your image resource
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .size(100.dp)
                    )
                }
                else{
                    Image(
                        painter = rememberAsyncImagePainter("https://wlrjyngxgupgafqzyxvs.supabase.co/storage/v1/object/public/profile/${viewModel.FilepicEstudiante.value}"),
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)  // Abre el selector de imágenes
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))

                Text(text = viewModel.Matricula.value, fontSize = 16.sp, color = Color.Gray)

                Spacer(modifier = Modifier.height(4.dp))

                Text(text = viewModel.nameAlumno.value, fontSize = 24.sp, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Semestre: ${viewModel.Semestre.value}",
                    fontSize = 16.sp, color = Color.Gray, textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Correo: ${viewModel.CorreoAlumno.value}",
                    fontSize = 16.sp, color = Color.Gray, textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Genero: ${viewModel.GeneroEstudiante.value}",
                    fontSize = 16.sp, color = Color.Gray, textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Fecha Nacimiento: ${viewModel.FechaNacimientoEstudiante.value}",
                    fontSize = 16.sp, color = Color.Gray, textAlign = TextAlign.Center
                )
            }
        }
    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Confirmar eliminación") },
            text = { Text("¿Estás seguro de que deseas eliminar este Estudiante?") },
            confirmButton = {
                Button(
                    onClick = {
                        // Lógica para eliminar el caso
                        viewModel.EliminateStudent(viewModel.CorreoAlumno.value)
                        Toast.makeText(
                            context,
                            "El Estudiante se eliminó",
                            Toast.LENGTH_SHORT
                        ).show()
                        navController.navigateUp() // Navegar hacia atrás
                        showDialog = false // Cerrar el diálogo
                    }
                ) {
                    Text("Sí")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("No")
                }
            }
        )
    }

}

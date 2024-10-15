package com.example.nortech_app.Students

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.nortech_app.R
import com.example.nortech_app.Visits.BottomNavigationBar
import viewmodel.UserViewModel
import java.util.UUID

// PERFIL ESTUDIANTE -------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEstudianteScreen(viewModel: UserViewModel, navController: NavHostController) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val fileName = "profile_${UUID.randomUUID()}.jpg"
            viewModel.uploadProfilePicture(it, fileName, context)
        }
    }
    LaunchedEffect(Unit) {
        viewModel.getName()
        viewModel.getEmailbyID()
        viewModel.getGenero()
        viewModel.getFechaNacimiento()
        viewModel.GetActualProfilePicture()
        viewModel.GetMatSemAbogado()
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
                            text = "PERFIL",
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
                    if(viewModel.PicActual.value=="") {
                        Image(
                            painter = painterResource(id = R.drawable.profile), // Replace with your image resource
                            contentDescription = "Profile Image",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .clickable { launcher.launch("image/*") }
                        )
                    }
                    else{
                        Image(
                            painter = rememberAsyncImagePainter("https://wlrjyngxgupgafqzyxvs.supabase.co/storage/v1/object/public/profile/${viewModel.PicActual.value}"),
                            contentDescription = "Profile Image",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .clickable { launcher.launch("image/*") }  // Abre el selector de imágenes
                        )
                    }
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
                Divider(
                    color = Color.LightGray,
                    thickness = 2.dp,
                    modifier = Modifier.padding(vertical = 3.dp)
                )

                InfoRow(label = "Matrícula", value = viewModel.MatriculaCuenta.value)
                Divider(
                    color = Color.LightGray,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 3.dp)
                )
                InfoRow(label = "Semestre", value = viewModel.SemestreCuenta.value)
                Divider(
                    color = Color.LightGray,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 3.dp)
                )
                InfoRow(label = "Abogado", value = viewModel.NombreAbogadoCuenta.value)
                Divider(
                    color = Color.LightGray,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 3.dp)
                )
                InfoRow(
                    label = "Correo",
                    value = viewModel.email.value
                )
                Divider(
                    color = Color.LightGray,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 3.dp)
                )
                InfoRow(
                    label = "Genero",
                    value = viewModel.Genero.value
                )
                Divider(
                    color = Color.LightGray,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 3.dp)
                )
                InfoRow(
                    label = "Fecha nacimiento",
                    value = viewModel.FechaNacimiento.value
                )
                Divider(
                    color = Color.LightGray,
                    thickness = 2.dp,
                    modifier = Modifier.padding(vertical = 3.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Logout Button
                Button(
                    onClick = { viewModel.signOut() },
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

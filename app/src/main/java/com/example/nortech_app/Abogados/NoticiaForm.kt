package com.example.nortech_app.Abogados

import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField // Asegúrate de importar TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import viewmodel.UserViewModel
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.unit.sp
import com.example.nortech_app.Visits.BottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotFormScreen(viewModel: UserViewModel, navController: NavController) {
    val context = LocalContext.current
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var fileName by remember { mutableStateOf("Seleccionar imagen Noticia") }
    var titulo by remember { mutableStateOf("") }
    var des by remember { mutableStateOf("") }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            if (uri != null) {
                selectedFileUri = uri
                context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    cursor.moveToFirst()
                    fileName = cursor.getString(nameIndex)
                }
                Log.d("MainScreen", "URI seleccionada: $uri")
            }
        }
    )

    val bitmap = selectedFileUri?.let { uri ->
        val inputStream = context.contentResolver.openInputStream(uri)
        BitmapFactory.decodeStream(inputStream)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Enviar noticia", color = Color.Black, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate("Main")
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Regresar",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = { BottomNavigationBar(navController, 0) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Seleccionar imagen Noticia",
                modifier = Modifier.align(Alignment.Start),
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Imagen contenedor con borde azul y un icono
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .border(2.dp, Color(0xFF007BFF), RoundedCornerShape(8.dp))
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                bitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier
                            .size(150.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                } ?: Icon(
                    imageVector = Icons.Default.AddCircle, // Icono de imagen por defecto
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de título
            TextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Titulo") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color(0xFF007BFF),
                    focusedLabelColor = Color(0xFF007BFF)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de descripción
            TextField(
                value = des,
                onValueChange = { des = it },
                label = { Text("Descripción") },
                placeholder = { Text("Ingresa una breve descripción sobre la noticia") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color(0xFF007BFF),
                    focusedLabelColor = Color(0xFF007BFF)
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botón de "Enviar noticia"
            Button(
                onClick = {
                    selectedFileUri?.let {
                        Log.d("MainScreen", "Archivo listo para subir: $selectedFileUri")
                        viewModel.uploadFile(it, fileName, context, titulo, des)
                    } ?: run {
                        Toast.makeText(context, "Seleccione un archivo primero", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF5A6C8E),
                    contentColor = Color.White
                ),
                enabled = selectedFileUri != null
            ) {
                Text(text = "Enviar noticia", fontSize = 16.sp)
            }

            // Mostrar mensaje de éxito o error
            Spacer(modifier = Modifier.height(16.dp))
            if (viewModel.uploadSuccess.value) {
                Text(text = "Noticia subida exitosamente", color = Color.Green)
            }
            if (viewModel.uploadErrorMessage.value.isNotEmpty()) {
                Text(text = "Error: ${viewModel.uploadErrorMessage.value}", color = Color.Red)
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


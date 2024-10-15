package com.example.nortech_app.Abogados

import android.graphics.Bitmap
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.nortech_app.Visits.BottomNavigationBar
import viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoticiaUPFormScreen(viewModel: UserViewModel, navController: NavController) {
    val context = LocalContext.current
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var fileName by remember { mutableStateOf("Seleccionar imagen Noticia") }
    var isDataChanged by remember { mutableStateOf(false) }
    var isImageChanged by remember { mutableStateOf(false) }
    var isTitleChanged by remember { mutableStateOf(false) }  // Detectar cambios en el título
    var isDescriptionChanged by remember { mutableStateOf(false) }  // Detectar cambios en la descripción
    var errorMessage by remember { mutableStateOf("") }
    var fileSizeValid by remember { mutableStateOf(true) }

    val currentImage = viewModel.imagenfilename.value
    val currentImageUrl = "https://wlrjyngxgupgafqzyxvs.supabase.co/storage/v1/object/public/imagenes/$currentImage"

    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(currentImageUrl)
            .crossfade(true)
            .build()
    )

    // Pre-cargar la imagen actual
    val currentBitmap = remember { mutableStateOf<Bitmap?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            if (uri != null) {
                // Comprobar si el archivo es una imagen válida y su tamaño
                context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                    cursor.moveToFirst()
                    val fileSize = cursor.getLong(sizeIndex)
                    fileSizeValid = fileSize < 20 * 1024 * 1024 // Limitar a 2MB

                    if (fileSizeValid) {
                        selectedFileUri = uri
                        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        fileName = cursor.getString(nameIndex)
                        isDataChanged = true
                        isImageChanged = true // Se marca la imagen como cambiada
                    } else {
                        errorMessage = "El archivo es demasiado grande. Debe ser menor a 20MB."
                    }
                }
            }
        }
    )

    val bitmap = selectedFileUri?.let { uri ->
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            Log.e("NoticiaUPFormScreen", "Error al cargar la nueva imagen seleccionada: ${e.message}")
            errorMessage = "Error al cargar la nueva imagen."
            null
        }
    } ?: currentBitmap.value // Usa la imagen actual si no se selecciona una nueva

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Modificar Noticia", color = Color.Black, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("Main") }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Regresar", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Seleccionar imagen Noticia", modifier = Modifier.align(Alignment.Start), color = Color.Gray)

            Spacer(modifier = Modifier.height(8.dp))

            // Contenedor de imagen
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
                            modifier = Modifier.size(150.dp).clip(RoundedCornerShape(8.dp))
                        )
                        Image(
                            painter = painter,
                            contentDescription = viewModel.TituloNot.value,
                            modifier = Modifier.size(150.dp).clip(RoundedCornerShape(8.dp))
                        )
                } ?: Image(
                    painter = painter,
                    contentDescription = viewModel.TituloNot.value,
                    modifier = Modifier.size(150.dp).clip(RoundedCornerShape(8.dp))
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de título
            TextField(
                value = viewModel.TituloNot.value,
                onValueChange = {
                    viewModel.TituloNot.value = it
                    isDataChanged = true
                    isTitleChanged = true // Detectar cambio en el título
                },
                label = { Text("Titulo") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(focusedIndicatorColor = Color(0xFF007BFF), focusedLabelColor = Color(0xFF007BFF))
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de descripción
            TextField(
                value = viewModel.DescripcionNot.value,
                onValueChange = {
                    viewModel.DescripcionNot.value = it
                    isDataChanged = true
                    isDescriptionChanged = true // Detectar cambio en la descripción
                },
                label = { Text("Descripción") },
                placeholder = { Text("Ingresa una breve descripción sobre la noticia") },
                modifier = Modifier.fillMaxWidth().height(300.dp),
                colors = TextFieldDefaults.colors(focusedIndicatorColor = Color(0xFF007BFF), focusedLabelColor = Color(0xFF007BFF))
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Mostrar mensaje de error si el archivo es inválido
            if (!fileSizeValid) {
                Text(text = "El archivo seleccionado es demasiado grande. Selecciona uno de menos de 5MB.", color = Color.Red)
            }

            // Botón para actualizar todos los cambios
            Button(
                onClick = {
                    // Actualizar el título si ha cambiado
                    if (isTitleChanged) {
                        viewModel.updatetitNoticia(viewModel.id_not.value!!, viewModel.TituloNot.value)
                        Toast.makeText(context, "Título actualizado", Toast.LENGTH_SHORT).show()
                    }

                    // Actualizar la descripción si ha cambiado
                    if (isDescriptionChanged) {
                        viewModel.updatedesNoticia(viewModel.id_not.value!!, viewModel.DescripcionNot.value)
                        Toast.makeText(context, "Descripción actualizada", Toast.LENGTH_SHORT).show()
                    }

                    // Actualizar la imagen si ha cambiado
                    if (isImageChanged && fileSizeValid) {
                        viewModel.eliminateIMG(viewModel.imagenfilename.value)
                        selectedFileUri?.let { uri ->
                            viewModel.JUSTuploadFile(uri, fileName, context)
                            viewModel.updateimgNoticia(viewModel.id_not.value!!, fileName)
                        }
                        Toast.makeText(context, "Imagen actualizada", Toast.LENGTH_SHORT).show()
                    }

                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5A6C8E), contentColor = Color.White),
                enabled = isTitleChanged || isDescriptionChanged || isImageChanged // Habilitar si hay algún cambio
            ) {
                Text(text = "Guardar cambios", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón de "Eliminar noticia"
            Button(
                onClick = {
                    if (viewModel.id_not.value != null) {
                        viewModel.eliminateNoticia(viewModel.id_not.value, fileName)
                        Toast.makeText(context, "Noticia eliminada", Toast.LENGTH_SHORT).show()
                        navController.navigate("Main")
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red, contentColor = Color.White)
            ) {
                Text(text = "Eliminar noticia", fontSize = 16.sp)
            }
        }
    }
}






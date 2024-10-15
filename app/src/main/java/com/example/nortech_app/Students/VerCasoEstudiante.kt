package com.example.nortech_app.Students

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nortech_app.Abogados.BottomBarAbogado
import com.example.nortech_app.R
import kotlinx.coroutines.launch
import viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerCasoViewEstudiante(navController: NavController, viewModel: UserViewModel) {
    val filenames by viewModel.filecaso.collectAsState()
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // Obtener el nombre del archivo desde el Uri
            val fileName = getFileNameFromUri(context, it) ?: "archivo_subido"
            viewModel.filecaso(it, fileName, context)
            viewModel.InsertFileCaso(viewModel.idcaso.value, fileName)
        }
    }
    LaunchedEffect(Unit) {
        viewModel.fetchFileNamesByCasoId(viewModel.idcaso.value)
        Log.d("UserViewModel", "Noticias fetched: $filenames")
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detalles del Caso",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Regresar"
                        )
                    }
                },
            )
        },
        bottomBar = {
            BottomNavigationBarEstudiante(navController, 1)
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = viewModel.ALIAScaso.value,
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                    color = Color(0xFF1E88E5), // Color azul para resaltar el nombre
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                if(viewModel.estadocaso.value) {
                    Text(
                        text = "Activo",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.Blue, // Color azul para resaltar el nombre
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
                else {
                    Text(
                        text = "Inactivo",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.Red, // Color azul para resaltar el nombre
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                // Sección de información del caso
                SectionTitle("INFORMACIÓN DE CASO")
                Divider(
                    color = Color.Gray,
                    thickness = 2.dp,
                    modifier = Modifier.padding(vertical = 3.dp)
                ) // Línea divisora
                InfoRow2(label = "NUC", value = viewModel.nucCaso.value)
                Divider(
                    color = Color.Gray,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 3.dp)
                ) // Línea divisora
                InfoRow2(
                    label = "Nombre ${viewModel.tipocaso.value}",
                    value = viewModel.namecaso.value
                )
                Divider(
                    color = Color.Gray,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 3.dp)
                ) // Línea divisora
                InfoRow2(label = "Descripción del caso", value = "")
                SectionDes(viewModel.descCaso.value)
                Divider(
                    color = Color.Gray,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 3.dp)
                ) // Línea divisora
                InfoRow2(label = "Carpeta Judicial", value = viewModel.caroJudicialCaso.value)
                Divider(
                    color = Color.Gray,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 3.dp)
                ) // Línea divisora
                InfoRow2(label = "Carpeta Investigación", value = viewModel.carpInvestCaso.value)
                Divider(
                    color = Color.Gray,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 3.dp)
                ) // Línea divisora
                InfoRow2(label = "Acceso FV", value = viewModel.accFVCaso.value)
                Divider(
                    color = Color.Gray,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 3.dp)
                ) // Línea divisora
                InfoRow2(label = "Password FV", value = viewModel.passFVCaso.value)
                Divider(
                    color = Color.Gray,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 3.dp)
                ) // Línea divisora
                InfoRow2(label = "Fiscal Titular", value = viewModel.fiscalTituCaso.value)
                Divider(
                    color = Color.Gray,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 3.dp)
                ) // Línea divisora
                InfoRow2(
                    label = "Unidad Investigación (UI)",
                    value = viewModel.unidadInvestCaso.value
                )
                Divider(
                    color = Color.Gray,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 3.dp)
                ) // Línea divisora
                InfoRowWithOptions(label = "Dirección UI", value = viewModel.dirUIcaso.value)
                Divider(
                    color = Color.Gray,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 3.dp)
                ) // Línea divisora
                val carpetaDrive by remember { mutableStateOf(viewModel.carpDriveCaso.value) }
                DriveFolderButton(label = "Carpeta Drive", viewModel.carpDriveCaso.value)
                Divider(
                    color = Color.Gray,
                    thickness = 2.dp,
                    modifier = Modifier.padding(vertical = 3.dp)
                ) // Línea divisora

                Spacer(modifier = Modifier.height(32.dp)) // Espaciado final
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 8.dp,
                            vertical = 8.dp
                        ), // Ajuste del margen superior e inferior
                    horizontalArrangement = Arrangement.Center, // Centra el contenido horizontalmente
                    verticalAlignment = Alignment.CenterVertically // Alineación vertical central
                ) {
                    Text(
                        text = "Archivos relacionados",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF616161)
                    )
                }

                Divider(
                    color = Color.Gray,
                    thickness = 2.dp,
                    modifier = Modifier.padding(vertical = 3.dp)
                ) // Línea divisora
                if (filenames.isEmpty()) {
                    // Mostrar mensaje cuando no hay archivos
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay archivos relacionados",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.Gray
                        )
                    }
                } else {
                    filenames.forEach { filename ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Ícono de archivo a la izquierda
                                // Imagen personalizada como ícono
                                Image(
                                    painter = painterResource(id = R.drawable.fileicon), // Reemplaza con el ID de tu imagen
                                    contentDescription = "Ícono de archivo",
                                    modifier = Modifier
                                        .size(24.dp)
                                        .padding(end = 8.dp), // Espacio entre la imagen y el texto
                                )

                                // Mostrar el nombre del archivo
                                Text(
                                    text = filename,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color.Black
                                )
                            }

                            // Botones de acción
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Botón para visualizar el archivo
                                Button(
                                    onClick = {
                                        val viewUri =
                                            "https://wlrjyngxgupgafqzyxvs.supabase.co/storage/v1/object/public/files/$filename"
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(viewUri))
                                        context.startActivity(intent)
                                    },
                                    modifier = Modifier.padding(4.dp)
                                ) {
                                    Text("Ver archivo")
                                }

                                // Botón para descargar el archivo
                                Button(
                                    onClick = {
                                        val downloadUri =
                                            "https://wlrjyngxgupgafqzyxvs.supabase.co/storage/v1/object/public/files/$filename"
                                        val downloadManager =
                                            context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                                        val request =
                                            DownloadManager.Request(Uri.parse(downloadUri))
                                                .setTitle(filename)
                                                .setDescription("Descargando archivo...")
                                                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                                                .setDestinationInExternalPublicDir(
                                                    Environment.DIRECTORY_DOWNLOADS,
                                                    filename
                                                )

                                        downloadManager.enqueue(request)
                                        Toast.makeText(
                                            context,
                                            "La descarga inicio",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    modifier = Modifier.padding(4.dp)
                                ) {
                                    Text("Descargar")
                                }
                            }

                            Divider(
                                color = Color.Gray,
                                thickness = 2.dp,
                                modifier = Modifier.padding(vertical = 3.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoRow2(label: String, value: String) {
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        if (value.isNotEmpty()) {
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.End,
                modifier = Modifier.clickable {
                    clipboardManager.setText(AnnotatedString(value))
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Copied to clipboard",
                            duration = SnackbarDuration.Short
                        )
                    }
                    Toast.makeText(context, "Se copio en el portapapeles", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

@Composable
fun InfoRowWithOptions(label: String, value: String) {
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        if (value.isNotEmpty()) {
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .clickable(onClick = { expanded = true })
                    .padding(8.dp)
            )
        }
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        DropdownMenuItem(onClick = {
            clipboardManager.setText(AnnotatedString(value))
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = "Copied to clipboard",
                    duration = SnackbarDuration.Short
                )
            }
            Toast.makeText(context, "Se copio en el portapapeles", Toast.LENGTH_SHORT).show()
            expanded = false
        }) {
            Text("Copiar al portapapeles")
        }
        DropdownMenuItem(onClick = {
            val gmmIntentUri = Uri.parse("geo:0,0?q=$value")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            context.startActivity(mapIntent)
            expanded = false
        }) {
            Text("Abrir en Google Maps")
        }
    }
}

@Composable
fun DriveFolderButton(label: String, driveUrl: String) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        Button(
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(driveUrl))
                context.startActivity(intent)
            },
            modifier = Modifier.padding(8.dp),
            enabled = driveUrl!=""
        ) {
            Text(text = "Abrir carpeta de Drive")
        }
    }
}



@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color(0xFF616161),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        textAlign = TextAlign.Start
    )
}
@Composable
fun SectionDes(title: String) {
    Text(
        text = title,
        fontSize = 16.sp,
        color = Color(0xFF616161),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        textAlign = TextAlign.Start
    )
}

// Función auxiliar para obtener el nombre del archivo desde el Uri
fun getFileNameFromUri(context: Context, uri: Uri): String? {
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    return cursor?.use {
        if (it.moveToFirst()) {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            it.getString(nameIndex)
        } else null
    }
}
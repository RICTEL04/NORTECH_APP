package com.example.nortech_app.Abogados

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nortech_app.R
import viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun UPFormCaso(navController: NavController, viewModel: UserViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Variables editables para cada campo del caso
    var alias by remember { mutableStateOf(viewModel.ALIAScaso.value) }
    var nuc by remember { mutableStateOf(viewModel.nucCaso.value) }
    var nombre by remember { mutableStateOf(viewModel.namecaso.value) }
    var descripcion by remember { mutableStateOf(viewModel.descCaso.value) }
    var carpetaJudicial by remember { mutableStateOf(viewModel.caroJudicialCaso.value) }
    var carpetaInvestigacion by remember { mutableStateOf(viewModel.carpInvestCaso.value) }
    var accesoFV by remember { mutableStateOf(viewModel.accFVCaso.value) }
    var passwordFV by remember { mutableStateOf(viewModel.passFVCaso.value) }
    var fiscalTitular by remember { mutableStateOf(viewModel.fiscalTituCaso.value) }
    var unidadInvestigacion by remember { mutableStateOf(viewModel.unidadInvestCaso.value) }
    var direccionUI by remember { mutableStateOf(viewModel.dirUIcaso.value) }
    var carpetaDrive by remember { mutableStateOf(viewModel.carpDriveCaso.value) }

    var estado by remember { mutableStateOf(viewModel.estadocaso.value) }

    val filenames by viewModel.filecaso.collectAsState()

    var tipo by remember { mutableStateOf(viewModel.tipocaso.value) }
    val options = listOf("Investigado", "Víctima")
    var expanded by remember { mutableStateOf(false) } // Estado del Dropdown

    var Casodetails by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Editar Caso", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                },
                actions = {
                    val buttonText = if (estado) "Activo" else "Inactivo"
                    Text(
                        text = buttonText,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(
                        onClick = {
                            if(estado){
                                estado = false
                                viewModel.updateEstadoCaso(viewModel.idcaso.value, estado)
                                viewModel.estadocaso.value = estado
                            }
                            else{
                                estado = true
                                viewModel.updateEstadoCaso(viewModel.idcaso.value, estado)
                                viewModel.estadocaso.value = estado
                            }
                        }, // Cambia el estado cuando se hace clic
                        content = {
                            val buttonColor = if (estado) Color.Green else Color.Red
                            Box(
                                modifier = Modifier
                                    .background(buttonColor, shape = RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    )
                }
            )
        },
        bottomBar = {
            BottomBarAbogado(navController, 2)
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp), // Espacio en los lados
                    verticalAlignment = Alignment.CenterVertically, // Centrar verticalmente
                    horizontalArrangement = Arrangement.SpaceBetween // Separar horizontalmente
                ) {
                    if(!Casodetails) {
                        Text(
                            text = "Caso",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color.Black,
                            modifier = Modifier.weight(1f)// Asigna espacio proporcional
                        )
                    }
                    else {
                        Text(
                            text = "Caso",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp,
                            color = Color.Gray,
                            modifier = Modifier.weight(1f)
                                .clickable { Casodetails = false }// Asigna espacio proporcional
                        )
                    }


                    // Añade un separador aquí si quieres más espacio entre los textos
                    Spacer(modifier = Modifier.width(10.dp)) // Espaciado entre los textos

                    if(!Casodetails) {
                        Text(
                            text = "Archivos",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp,
                            color = Color.Gray,
                            modifier = Modifier.weight(1f)
                                .clickable { Casodetails = true }// Asigna espacio proporcional
                        )
                    }
                    else{
                        Text(
                            text = "Archivos",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color.Black,
                            modifier = Modifier.weight(1f)// Asigna espacio proporcional
                        )
                    }
                }
                Divider(
                    color = Color.Gray,
                    thickness = 2.dp,
                    modifier = Modifier.padding(vertical = 3.dp)
                ) // Línea divisora

                Spacer(modifier = Modifier.height(16.dp))

                if (!Casodetails) {

                    // Editable Alias
                    EditableField(label = "Alias", value = alias) { alias = it }
                    EditableField(label = "NUC", value = nuc) { nuc = it }
                    EditableField(label = "Nombre $tipo", value = nombre) { nombre = it }
                    // Campo de Tipo de Caso con DropdownMenu
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Tipo Caso",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            TextField(
                                value = tipo,
                                onValueChange = { /* No se actualiza directamente desde el campo de texto */ },
                                readOnly = true,
                                label = { Text("") },
                                trailingIcon = {
                                    Icon(
                                        imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                                        contentDescription = null
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = TextStyle(fontSize = 16.sp),
                                colors = TextFieldDefaults.textFieldColors(
                                    backgroundColor = Color(0xFFF1F1F1),
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                )
                            )

                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                options.forEach { caseType ->
                                    DropdownMenuItem(
                                        text = { Text(text = caseType) },
                                        onClick = {
                                            tipo = caseType
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    EditableField(
                        label = "Descripción",
                        value = descripcion,
                        isMultiline = true
                    ) { descripcion = it }
                    EditableField(
                        label = "Carpeta Judicial",
                        value = carpetaJudicial
                    ) { carpetaJudicial = it }
                    EditableField(
                        label = "Carpeta Investigación",
                        value = carpetaInvestigacion
                    ) { carpetaInvestigacion = it }
                    EditableField(label = "Acceso FV", value = accesoFV) { accesoFV = it }
                    EditableField(label = "Password FV", value = passwordFV) { passwordFV = it }
                    EditableField(label = "Fiscal Titular", value = fiscalTitular) {
                        fiscalTitular = it
                    }
                    EditableField(
                        label = "Unidad Investigación",
                        value = unidadInvestigacion
                    ) { unidadInvestigacion = it }
                    EditableField(label = "Dirección UI", value = direccionUI) { direccionUI = it }
                    EditableField(label = "Carpeta Drive", value = carpetaDrive) {
                        carpetaDrive = it
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            viewModel.tipocaso.value = tipo
                            viewModel.nucCaso.value = nuc
                            viewModel.descCaso.value = descripcion
                            viewModel.caroJudicialCaso.value = carpetaJudicial
                            viewModel.carpInvestCaso.value = carpetaInvestigacion
                            viewModel.accFVCaso.value = accesoFV
                            viewModel.passFVCaso.value = passwordFV
                            viewModel.fiscalTituCaso.value = fiscalTitular
                            viewModel.unidadInvestCaso.value = unidadInvestigacion
                            viewModel.carpDriveCaso.value = carpetaDrive
                            viewModel.dirUIcaso.value = direccionUI
                            viewModel.ALIAScaso.value = alias
                            viewModel.namecaso.value = nombre
                            viewModel.estadocaso.value = estado
                            viewModel.updateCaso(
                                viewModel.idcaso.value,
                                descripcion,
                                tipo,
                                nuc,
                                carpetaJudicial,
                                carpetaInvestigacion,
                                accesoFV,
                                passwordFV,
                                fiscalTitular,
                                unidadInvestigacion,
                                direccionUI,
                                carpetaDrive,
                                alias,
                                nombre
                            )
                            Toast.makeText(
                                context,
                                "El caso se actualizo",
                                Toast.LENGTH_SHORT
                            ).show()
                            navController.navigateUp()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Guardar Cambios")
                    }
                    var showDialog by remember { mutableStateOf(false) }
                    // Botón para eliminar el caso
                    Button(
                        onClick = { showDialog = true }, // Mostrar el diálogo al hacer clic
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red // Cambiar el color de fondo a rojo
                        )
                    ) {
                        Text(
                            text = "Eliminar caso",
                            color = Color.White
                        ) // Cambiar el texto a blanco
                    }

                    // Diálogo de confirmación
                    if (showDialog) {
                        AlertDialog(
                            onDismissRequest = { showDialog = false },
                            title = { Text(text = "Confirmar eliminación") },
                            text = { Text("¿Estás seguro de que deseas eliminar este caso?") },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        // Lógica para eliminar el caso
                                        viewModel.eliminateCaso(viewModel.idcaso.value, filenames)
                                        Toast.makeText(
                                            context,
                                            "El caso se eliminó",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        navController.navigate("CasosAbogado") // Navegar hacia atrás
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
                else{
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
                            Divider(
                                color = Color.Gray,
                                thickness = 2.dp,
                                modifier = Modifier.padding(vertical = 3.dp)
                            )
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
                                            viewModel.eliminateFile(viewModel.idcaso.value, filename)
                                            Toast.makeText(
                                                context,
                                                "El archivo se eliminó",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        },
                                        modifier = Modifier.padding(4.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color.Red // Cambiar el color de fondo a rojo
                                        )
                                    ) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Regresar")
                                        Text("Borrar Archivo",
                                            color = Color.White)
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
}

@Composable
fun EditableField(
    label: String,
    value: String,
    isMultiline: Boolean = false,
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(4.dp))
        if (isMultiline) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(fontSize = 16.sp),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color(0xFFF1F1F1),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
        } else {
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(fontSize = 16.sp),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color(0xFFF1F1F1),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun EditableField2(
    label: String,
    value: String,
    isMultiline: Boolean = false,
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(4.dp))
        if (isMultiline) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(fontSize = 16.sp),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color(0xFFF1F1F1),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
        } else {
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(fontSize = 16.sp),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color(0xFFF1F1F1),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}



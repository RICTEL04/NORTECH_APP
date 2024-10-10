package com.example.nortech_app.Abogados

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nortech_app.Visits.BottomNavigationBar
import viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CasoForm(viewModel: UserViewModel, navController: NavController) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    var alias by remember { mutableStateOf("") }
    var des by remember { mutableStateOf("") }
    var nuc by remember { mutableStateOf("") }
    var carpJudicial by remember { mutableStateOf("") }
    var carpInvestigacion by remember { mutableStateOf("") }
    var Acc_FV by remember { mutableStateOf("") }
    var pass_FV by remember { mutableStateOf("") }
    var fiscal by remember { mutableStateOf("") }
    var unidadInv by remember { mutableStateOf("") }
    var dirUI by remember { mutableStateOf("") }
    var carpetaDrive by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("Tipo de caso") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Enviar noticia", color = Color.Black, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate("CasosAbogado")
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
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Campo de título
            TextField(
                value = alias,
                onValueChange = { alias = it },
                label = { Text("ALIAS del caso") },
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
                placeholder = { Text("Ingresa una descripción sobre el caso") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color(0xFF007BFF),
                    focusedLabelColor = Color(0xFF007BFF)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de título
            TextField(
                value = nuc,
                onValueChange = { input ->
                    // Filtrar la entrada para permitir solo números
                    if (input.all { it.isDigit() }) {
                        nuc = input
                    }
                },
                label = { Text("NUC") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),  // Mostrar teclado numérico
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color(0xFF007BFF),
                    focusedLabelColor = Color(0xFF007BFF)
                )
            )

            // Campo de título
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Password FV") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color(0xFF007BFF),
                    focusedLabelColor = Color(0xFF007BFF)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            var expanded by remember { mutableStateOf(false) }
            val options = listOf("Investigado", "Víctima")

            Column {
                // Este es el TextField que abre el menú
                TextField(
                    value = tipo,
                    onValueChange = { /* No permite cambios directos */ },
                    label = { Text("Tipo") },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = !expanded },
                    trailingIcon = {
                        Icon(
                            imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            modifier = Modifier .clickable { expanded = !expanded },
                        )
                    }
                )

                // Menú desplegable
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    options.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                tipo = option
                                expanded = false
                            }
                        )
                    }
                }
            }


            Spacer(modifier = Modifier.height(16.dp))

            // Campo de título
            TextField(
                value = carpJudicial,
                onValueChange = { input ->
                    // Filtrar la entrada para permitir solo números
                    if (input.all { it.isDigit() }) {
                        carpJudicial = input
                    }
                },
                label = { Text("Numero Carpeta Judicial") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),  // Mostrar teclado numérico
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color(0xFF007BFF),
                    focusedLabelColor = Color(0xFF007BFF)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de título
            TextField(
                value = carpInvestigacion,
                onValueChange = { input ->
                    // Filtrar la entrada para permitir solo números
                    if (input.all { it.isDigit() }) {
                        carpInvestigacion = input
                    }
                },
                label = { Text("Numero Carpeta Investigacion") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),  // Mostrar teclado numérico
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color(0xFF007BFF),
                    focusedLabelColor = Color(0xFF007BFF)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de título
            TextField(
                value = Acc_FV,
                onValueChange = { Acc_FV = it },
                label = { Text("Acceso FV") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color(0xFF007BFF),
                    focusedLabelColor = Color(0xFF007BFF)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de título
            TextField(
                value = pass_FV,
                onValueChange = { pass_FV = it },
                label = { Text("Password FV") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color(0xFF007BFF),
                    focusedLabelColor = Color(0xFF007BFF)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de título
            TextField(
                value = fiscal,
                onValueChange = { fiscal = it },
                label = { Text("Fiscal") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color(0xFF007BFF),
                    focusedLabelColor = Color(0xFF007BFF)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de título
            TextField(
                value = unidadInv,
                onValueChange = { unidadInv = it },
                label = { Text("Unidad Investigacion (UI)") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color(0xFF007BFF),
                    focusedLabelColor = Color(0xFF007BFF)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de título
            TextField(
                value = dirUI,
                onValueChange = { dirUI = it },
                label = { Text("Direccion Unidad Investigacion (UI)") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color(0xFF007BFF),
                    focusedLabelColor = Color(0xFF007BFF)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de título
            TextField(
                value = carpetaDrive,
                onValueChange = { carpetaDrive = it },
                label = { Text("Carpeta Drive") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color(0xFF007BFF),
                    focusedLabelColor = Color(0xFF007BFF)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botón de "Enviar noticia"
            Button(
                onClick = {
                        viewModel.InsertCaso(des,
                            tipo,
                            nuc,
                            true,
                            carpJudicial,
                            carpInvestigacion,
                            Acc_FV,
                            pass_FV,
                            fiscal,
                            unidadInv,
                            dirUI,
                            carpetaDrive,
                            alias,
                            name)
                    if (viewModel.uploadSuccessaddCaso.value) {
                        Toast.makeText(context, "Caso creado", Toast.LENGTH_SHORT).show()
                        navController.navigate("CasosAbogado")
                    }
                    else{
                        Toast.makeText(context, "Fallo al crear caso", Toast.LENGTH_SHORT).show()
                    }
                          },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF5A6C8E),
                    contentColor = Color.White
                )
            ) {
                Text(text = "Enviar noticia", fontSize = 16.sp)
            }

            // Mostrar mensaje de éxito o error
            Spacer(modifier = Modifier.height(16.dp))

            if (viewModel.uploadErrorMessageaddCaso.value.isNotEmpty()) {
                Text(text = "Error: ${viewModel.uploadErrorMessageaddCaso.value}", color = Color.Red)
            }
        }
    }
}
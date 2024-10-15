package com.example.nortech_app

import android.app.DatePickerDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import viewmodel.UserViewModel
import java.util.Calendar

@Composable
fun RegisterScreen(viewModel: UserViewModel, navController: NavHostController) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var genero by remember { mutableStateOf("Selecciona tu género") }
    var expanded by remember { mutableStateOf(false) }

    // Estado para manejar la fecha seleccionada
    var selectedDate by remember { mutableStateOf<kotlinx.datetime.LocalDate?>(null) }
    val dateFormatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")

    // Obtener la fecha actual
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    // Mostrar DatePickerDialog
    val datePickerDialog = DatePickerDialog(
        LocalContext.current,
        { _, selectedYear, selectedMonth, selectedDayOfMonth ->
            val selectedLocalDate = LocalDate(selectedYear, selectedMonth + 1, selectedDayOfMonth)
            selectedDate = selectedLocalDate
        }, year, month, day
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Registro", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Ingresa tus datos para regístrarte")
        Spacer(modifier = Modifier.height(24.dp))

        // Nombre
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Contraseña
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible) {
                    painterResource(id = R.drawable.open)
                } else {
                    painterResource(id = R.drawable.close)
                }
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Image(painter = image, contentDescription = null)
                }
            }
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Selección de género (se verá como un OutlinedTextField)
        OutlinedTextField(
            value = genero,
            onValueChange = { },
            label = { Text("Género") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }, // Click para desplegar menú
            readOnly = true,  // Para que el campo no sea editable manualmente
            trailingIcon = { Icon(Icons.Filled.ArrowDropDown, contentDescription = null,
                    modifier = Modifier
                    .clickable { expanded = true }) }
        )

        // Dropdown menu para género
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(onClick = {
                genero = "Masculino"
                expanded = false
            }) {
                Text("Masculino")
            }
            DropdownMenuItem(onClick = {
                genero = "Femenino"
                expanded = false
            }) {
                Text("Femenino")
            }
            DropdownMenuItem(onClick = {
                genero = "Otro"
                expanded = false
            }) {
                Text("Otro")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Selección de fecha de nacimiento (se verá como un OutlinedTextField)
        OutlinedTextField(
            value = selectedDate?.toString() ?: "Selecciona tu fecha de nacimiento",
            onValueChange = { },
            label = { Text("Fecha de Nacimiento") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { datePickerDialog.show() }, // Click para mostrar el DatePicker
            readOnly = true,  // Para que no sea editable manualmente
            trailingIcon = { Icon(Icons.Filled.DateRange, contentDescription = null,
                modifier = Modifier
                    .clickable { datePickerDialog.show() }) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botón de registro
        Button(
            onClick = {
                if (selectedDate != null && genero != "Selecciona tu género") {
                    viewModel.signUp(email, password, name, "1", selectedDate!!, genero)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Registrarse", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Text(text = "¿Ya tienes una cuenta?", Modifier.padding(13.dp))
            TextButton(onClick = { navController.navigate("login") }) {
                Text(text = "Login", color = Color.Blue)
            }
        }
    }
}

package com.example.nortech_app.Visits

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.nortech_app.R

@Composable
fun LegalSolutionsScreen(navController: NavHostController, opcion: MutableState<String>) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("visitante", "abogado", "estudiante")
    val cuenta = opcion.value
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Asesoría Legal\nGratuita Y Representación\nAnte Tribunales",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textDecoration = TextDecoration.Underline,
            color = Color.Black,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Apoyo en casos de las áreas\nfamiliar, civil y mercantil",
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )

        Image(
            painter = painterResource(id = R.drawable.logo_removebg_preview),
            contentDescription = "Logo Clínica Penal",
            modifier = Modifier
                .size(350.dp)
                .padding(top = 5.dp)
        )

        Button(
            onClick = { navController.navigate("main") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
                Text(text = "Continuar como $cuenta")
        }

        Text(
            text = "¿Ya tienes una cuenta? Login",
            fontSize = 14.sp,
            color = Color.Blue,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier
                .clickable {
                    navController.navigate("login")
                }
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Registrate",
            fontSize = 14.sp,
            color = Color.Blue,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier
                .clickable {
                    navController.navigate("Register")
                }
        )
        Spacer(modifier = Modifier.height(10.dp))

        // TextField with dropdown list with five options to select
        Box(modifier = Modifier.fillMaxWidth()) {
            TextField(
                value = opcion.value,
                onValueChange = { opcion.value = it },
                label = { Text("Opcion inicio") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                },
                readOnly = true
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        onClick = {
                            opcion.value = option
                            expanded = false
                        },
                        text = { Text(option) }
                    )
                }
            }
        }
    }
}

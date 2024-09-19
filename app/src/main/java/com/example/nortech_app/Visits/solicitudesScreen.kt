package com.example.nortech_app.Visits

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SolicitudesScreen(
    navController: NavController,
    agendaFullMap: Map<LocalDate, Boolean>,
    agendaAvailabilityMap: Map<LocalDate, List<LocalTime>>
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("") }
    val options =
        listOf("Consulta general", "Problema legal", "Asesoría financiera", "Otro", "Seguimiento")
    var pickedDate by remember { mutableStateOf<LocalDate?>(null) }
    var pickedTime by remember { mutableStateOf<LocalTime?>(null) }
    var selectedDateTimeList = remember { mutableStateListOf<Pair<LocalDate, LocalTime>>() }
    val dateDialogState = rememberMaterialDialogState()
    val timeDialogState = remember { mutableStateOf(false) }

    val formattedDate by remember {
        derivedStateOf {
            pickedDate?.let {
                DateTimeFormatter.ofPattern("MMM dd yyyy").format(it)
            } ?: "No hay dia seleccionado"
        }
    }

    val formattedTime by remember {
        derivedStateOf {
            pickedTime?.let {
                DateTimeFormatter.ofPattern("hh:mm").format(it)
            } ?: "No hay hora seleccionada"
        }
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController, 1)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Text in bold saying "Solicitudes"
            Text(
                text = "Solicitudes",
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Text in gray saying "Para realizar una solicitud debes completar la informacion en tu perfil"
            Text(
                text = "Para realizar una solicitud debes completar la información en tu perfil",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Little text saying "Motivo"
            Text(
                text = "Motivo",
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // TextField with dropdown list with five options to select
            Box(modifier = Modifier.fillMaxWidth()) {
                TextField(
                    value = selectedOption,
                    onValueChange = { selectedOption = it },
                    label = { Text("Seleccione un motivo") },
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
                                selectedOption = option
                                expanded = false
                            },
                            text = { Text(option) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // TextField to add a description
            TextField(
                value = "",
                onValueChange = { /* Handle text change */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                label = { Text("Descripción") },
                singleLine = false,
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = {
                    dateDialogState.show()
                }) {
                    Text(text = "Seleccionar fecha")
                }
                Spacer(modifier = Modifier.padding(10.dp))

                Button(
                    onClick = {
                        timeDialogState.value = true
                    },
                    enabled = pickedDate != null
                ) {
                    Text(text = "Seleccionar tiempo")
                }
                Spacer(modifier = Modifier.padding(10.dp))
                }
            Spacer(modifier = Modifier.padding(5.dp))
            Column (modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally){
                Row() {
                    Button(
                        onClick = {
                            selectedDateTimeList.add(Pair(pickedDate!!, pickedTime!!))
                            pickedTime = null
                            pickedDate = null
                            timeDialogState.value = false

                        },
                        enabled = pickedDate != null && pickedTime != null
                    ) {
                        Text(text = "Agregar cita")
                    }
                }
            }
            if (selectedDateTimeList.isEmpty()) {
                    Text(
                        modifier = Modifier
                            .padding(top = 20.dp, bottom = 20.dp),
                        text = "No hay citas elegidas",
                        textAlign = TextAlign.Center,
                        fontSize = 30.sp
                    )
                }
            else {
                LazyColumn(
                ) {
                    items(selectedDateTimeList) { (date, time) ->
                        Card(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(vertical = 8.dp, horizontal = 32.dp)
                                .height(64.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {

                                // Botón de eliminar
                                IconButton(
                                    onClick = {
                                        // Remover el item de la lista
                                        selectedDateTimeList.remove(Pair(date, time))
                                    },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Eliminar",
                                    )
                                }

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 16.dp, top = 16.dp) // Asegura espacio para el botón
                                ) {
                                    Text(
                                        text = "${date.format(DateTimeFormatter.ofPattern("MMM dd yyyy"))}"
                                    )

                                    Text(
                                        text = "${time.format(DateTimeFormatter.ofPattern("hh:mm a"))}"
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Column (modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally) {
                    // Button with text "Enviar solicitud"
                    Button(
                        onClick = { /* Handle send request */ },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(text = "Enviar solicitud", fontSize = 18.sp)
                    }

                    Spacer(modifier = Modifier.height(5.dp))

                    // Text saying "Al realizar una solicitud, acepta que haya un estudiante presente en la asesoría"
                    Text(
                        text = "Al realizar una solicitud, acepta que haya un estudiante presente en la asesoría",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )


            }

            if (timeDialogState.value) {
                pickedDate?.let { date ->
                    SelectTimeByDate(
                        agendaAvailabilityMap = agendaAvailabilityMap,
                        selectedDate = date,
                        onConfirm = { pickedTime = it },
                        onDismiss = { timeDialogState.value = false }
                    )
                }
            }

            MaterialDialog(
                dialogState = dateDialogState,
                buttons = {
                    positiveButton(text = "Ok") {
                        timeDialogState.value = true
                    }
                    negativeButton(text = "Cancel")
                },
                properties = DialogProperties(
                    dismissOnClickOutside = true
                )
            ) {
                datepicker(
                    initialDate = LocalDate.now(),
                    title = "Pick a Date",
                    allowedDateValidator = { selectedDate ->
                        val currentDate = LocalDate.now()
                        val isAfterOrEqualToToday = !selectedDate.isBefore(currentDate)
                        val isAgendaNotFull = agendaFullMap[selectedDate] != true
                        isAfterOrEqualToToday && isAgendaNotFull
                    }
                ) { pickedDate = it }
            }


            }
        }
    }

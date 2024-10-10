package com.example.nortech_app.Visits

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
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
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.SessionStatus
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.Dispatchers
import model.Solicitud
import viewmodel.UserViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SolicitudesScreen(
    navController: NavController,
    viewModel: UserViewModel
) {


    val scheduledDates by remember { viewModel.scheduledDates }
    val allSolicitudes by remember { viewModel.allSolicitudes }
    val nameByID by remember { viewModel.nameByID }
    val solicitudesByCliente by remember { viewModel.solicitudesByCliente }

    // Create mutable states to hold copies of the data
    var copiedScheduledDates by remember { mutableStateOf<Map<LocalDate, MutableList<LocalTime>>>(emptyMap()) }
    var copiedSolicitudesByCliente by remember { mutableStateOf<List<Solicitud>>(emptyList()) }

    Log.d("UserRepository2", "Name XDDDD: $nameByID")

    // Call getHoras() when the screen initializes
    LaunchedEffect(Unit) {
        viewModel.getHoras()
        viewModel.getAllSolicitudes()
        viewModel.getSolicitudesByClienteId()
    }

    // Copy the scheduled dates and solicitudes by cliente when they are available
    LaunchedEffect(scheduledDates, solicitudesByCliente) {
        copiedScheduledDates = (scheduledDates ?: emptyMap()) as Map<LocalDate, MutableList<LocalTime>>
        copiedSolicitudesByCliente = solicitudesByCliente ?: emptyList()
    }



    Log.d("UserSolScreenGetSolicitudesByClienteId", "By Id Solicitudes obtenidas: $solicitudesByCliente")
    Log.d("UserSolScreenGetSolicitudesByClienteId", "Todas Solicitudes obtenidas: $allSolicitudes")

    Log.d("UserRepository8", "Copied Scheduled Dates: $copiedScheduledDates")
    Log.d("UserRepository8", "Copied Solicitudes: $copiedSolicitudesByCliente")



    var agendaAvailabilityMap = generateAgendaAvailabilityMap(scheduledDates ?: emptyMap(),90L)


    copiedSolicitudesByCliente.forEach { solicitud ->
        val fecha = LocalDate.of(solicitud.anio.toInt(), solicitud.mes.toInt(), solicitud.dia.toInt())
        // Intenta analizar la hora con los dos formatos

        val formatter = DateTimeFormatter.ofPattern("h:mm a") // para "8:00 AM" o "8:00 PM"
        val formatterWithLeadingZero = DateTimeFormatter.ofPattern("HH:mm") // para "08:00"
        // Intenta analizar la hora con los dos formatos
        val hora = try {
            LocalTime.parse(solicitud.hora, formatter)
        } catch (e: DateTimeParseException) {
            LocalTime.parse(solicitud.hora, formatterWithLeadingZero)
        }

        addAvailability(agendaAvailabilityMap, fecha, hora)
    }



    var textFieldValue by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("") }
    val options =
        listOf("Consulta general", "Problema legal", "Asesoría financiera", "Otro", "Seguimiento")
    var pickedDate by remember { mutableStateOf<LocalDate?>(null) }
    var pickedTime by remember { mutableStateOf<LocalTime?>(null) }
    var selectedDateTimeList = remember { mutableStateListOf<Pair<LocalDate, LocalTime>>() }
    val dateDialogState = rememberMaterialDialogState()
    val timeDialogState = remember { mutableStateOf(false) }
    var isDateValid by remember { mutableStateOf(false) }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "SOLICITUDES",
                            fontWeight = FontWeight.Bold,
                            fontSize = 35.sp, // Reduce ligeramente el tamaño
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

            // Little text saying "Motivo"
            Text(
                text = "Motivo",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold
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
                value = textFieldValue, // El valor del campo de texto
                onValueChange = { newText -> textFieldValue = newText }, // Actualizar el valor del campo de texto cuando cambie
                modifier = Modifier
                    .fillMaxWidth()
                    .height(128.dp),
                label = { Text("Descripción") },
                singleLine = false, // Permitir múltiples líneas
                maxLines = 5 // Limitar a 5 líneas
            )

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                //Boton para agregar fecha
                Button(onClick = {
                    dateDialogState.show()
                    print(viewModel.getHoras())
                },
                    enabled = selectedDateTimeList.size < 3

                ) {
                    Text(text = "Seleccionar fecha")
                }

                Spacer(modifier = Modifier.padding(10.dp))

                Button(
                    onClick = {
                        timeDialogState.value = true
                    },
                    enabled = pickedDate != null && selectedDateTimeList.size < 3
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
                            pickedDate?.let { it1 ->
                                pickedTime?.let { it2 ->
                                    addAvailability(agendaAvailabilityMap,
                                        it1, it2
                                    )
                                }
                            }
                            selectedDateTimeList.add(Pair(pickedDate!!, pickedTime!!))
                            pickedTime = null
                            pickedDate = null
                            timeDialogState.value = false



                        },
                        enabled = pickedDate != null && pickedTime != null && selectedDateTimeList.size < 3
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)  // Permitir que la lista crezca y se ajuste dentro del diálogo
                        .padding(top = 20.dp, bottom = 20.dp)
                        .background(Color.LightGray),

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
                                        removeAvailability(agendaAvailabilityMap, date, time)
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

            Column (modifier =
            Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
                ,
                horizontalAlignment = Alignment.CenterHorizontally) {
                // Button with text "Enviar solicitud"
                Button(
                    onClick = {

                        for ((fecha, hora) in selectedDateTimeList) {
                            val anio = fecha.year.toString()
                            val mes = fecha.monthValue.toString()
                            val dia = fecha.dayOfMonth.toString()
                            val horaString = hora.toString() // Formato: HH:mm

                            viewModel.insertSolicitud(anio = anio, mes = mes, dia = dia, hora = horaString,
                                descripcion = textFieldValue, motivo = selectedOption)

                        }

                        selectedDateTimeList.clear()

                    },
                    enabled = selectedDateTimeList.size > 0 && selectedDateTimeList.size < 4 &&
                            selectedOption != "" && textFieldValue != "",
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

                    //Positive button, si la fecha no es valida se desactiva el boton ok
                    positiveButton(
                        text = "Ok",
                        disableDismiss = !isDateValid
                    ){
                        if(isDateValid)
                        {
                            timeDialogState.value = true
                            dateDialogState.showing = false
                        }
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
                    // Este parámetro espera que el validador se ejecute para cada fecha seleccionada
                    allowedDateValidator = { selectedDate ->
                        val currentDate = LocalDate.now()
                        val isAfterOrEqualToToday = !selectedDate.isBefore(currentDate)

                        val isNotWeekend = selectedDate.dayOfWeek != DayOfWeek.SATURDAY && selectedDate.dayOfWeek != DayOfWeek.SUNDAY

                        isAfterOrEqualToToday && isNotWeekend
                    },
                    // Cuando el usuario selecciona una nueva fecha, actualizamos isDateValid
                    onDateChange = { selectedDate ->
                        pickedDate = selectedDate
                        // Validar la fecha seleccionada en el cambio de fecha
                        val currentDate = LocalDate.now()
                        val isAfterOrEqualToToday = !selectedDate.isBefore(currentDate)
                        val isNotWeekend = selectedDate.dayOfWeek != DayOfWeek.SATURDAY && selectedDate.dayOfWeek != DayOfWeek.SUNDAY
                        isDateValid = isAfterOrEqualToToday &&  isNotWeekend
                    }
                )
            }
            }
        }
}
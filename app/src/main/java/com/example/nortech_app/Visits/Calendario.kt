package com.example.nortech_app.Visits

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter


val agendaFullMap = mapOf(
    LocalDate.of(2024, 9, 7) to true,
    LocalDate.of(2024, 9, 8) to true,
    LocalDate.of(2024, 9, 10) to true
)

val agendaAvailabilityMap = mapOf(
    LocalDate.of(2024, 9, 5) to
            emptyList(),
    LocalDate.of(2024, 9, 6) to
            listOf(LocalTime.of(9, 0), LocalTime.of(10, 0), LocalTime.of(11, 0), LocalTime.of(13, 0), LocalTime.of(15, 0) ),
    LocalDate.of(2024, 9, 9) to
            listOf(LocalTime.of(9, 0), LocalTime.of(10, 0), LocalTime.of(11, 0)),
    LocalDate.of(2024, 9, 11) to
            emptyList(),
    LocalDate.of(2024, 9, 12) to
            listOf(LocalTime.of(15, 0), LocalTime.of(16, 0)),
    LocalDate.of(2024, 9, 13) to
            emptyList(),
    LocalDate.of(2024, 9, 14) to
            listOf(LocalTime.of(9, 0), LocalTime.of(16, 0)),
    LocalDate.of(2024, 9, 15) to
            emptyList(),
    LocalDate.of(2024, 9, 16) to
            listOf(LocalTime.of(10, 0), LocalTime.of(13, 0)),
    LocalDate.of(2024, 9, 17) to
            emptyList(),
    LocalDate.of(2024, 9, 18) to
            emptyList(),
    LocalDate.of(2024, 9, 19) to
            emptyList(),
    LocalDate.of(2024, 9, 20) to
            emptyList(),
    LocalDate.of(2024, 9, 21) to
            emptyList(),
    LocalDate.of(2024, 9, 22) to
            emptyList(),
    LocalDate.of(2024, 9, 23) to
            emptyList(),
    LocalDate.of(2024, 9, 24) to
            emptyList(),
    LocalDate.of(2024, 9, 25) to
            emptyList(),
    LocalDate.of(2024, 9, 26) to
            emptyList(),
    LocalDate.of(2024, 9, 27) to
            emptyList(),
    LocalDate.of(2024, 9, 28) to
            emptyList(),
    LocalDate.of(2024, 9, 29) to
            emptyList(),
    LocalDate.of(2024, 9, 30) to
            emptyList(),
)

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DateAndTimePicker(
    agendaFullMap: Map<LocalDate, Boolean>,
    agendaAvailabilityMap: Map<LocalDate, List<LocalTime>>
) {
    var pickedDate by remember { mutableStateOf<LocalDate?>(null) }
    var pickedTime by remember { mutableStateOf<LocalTime?>(null) }
    val selectedDateTimeList = remember { mutableStateListOf<Pair<LocalDate, LocalTime>>() }
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
        modifier = Modifier
            .fillMaxSize()
    ) {

        Text(
            text = "Calendario",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 64.dp),
            textAlign = TextAlign.Center,
            fontSize = 30.sp

        )

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp, top = 128.dp), // Adjust this value if needed
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center

        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = {
                dateDialogState.show()
            }) {
                Text(text = "Seleccionar fecha")
            }

            Text(text = formattedDate)

            Button(
                onClick = {
                    timeDialogState.value = true
                },
                enabled = pickedDate != null
            ) {
                Text(text = "Seleccionar tiempo")
            }

            Text(text = formattedTime)

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

            //Spacer(modifier = Modifier.height(256.dp))


            if (selectedDateTimeList.isEmpty()) {

                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        modifier = Modifier
                            .padding(top = 32.dp, bottom = 64.dp),
                        text = "No hay \n citas elegidas",
                        textAlign = TextAlign.Center,
                        fontSize = 30.sp
                    )
                }


            } else {

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 32.dp, bottom = 64.dp)
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
                                        .padding(
                                            start = 16.dp,
                                            top = 16.dp
                                        ) // Asegura espacio para el botón
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

@Composable
fun SelectTimeByDate(
    agendaAvailabilityMap: Map<LocalDate, List<LocalTime>>,
    selectedDate: LocalDate,
    onConfirm: (LocalTime) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTime by remember { mutableStateOf<LocalTime?>(null) }
    val availableTimes = getAvailableTimesForDate(selectedDate, agendaAvailabilityMap)

    Dialog(
        onDismissRequest = { onDismiss() }

    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
            ,
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Select a time for $selectedDate", style = MaterialTheme.typography.bodySmall)

                if (availableTimes.isEmpty()) {
                    Text("No available times", color = MaterialTheme.colorScheme.error)
                } else {

                    availableTimes.forEach { time ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clickable {
                                    selectedTime = time
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedTime == time) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                            ),

                            ) {
                            Text(
                                text = time.toString(),
                                modifier = Modifier.padding(16.dp),
                                color = if (selectedTime == time) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(onClick = { onDismiss() }) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                selectedTime?.let {
                                    onConfirm(it)
                                    onDismiss()
                                }
                            },
                            enabled = selectedTime != null
                        ) {
                            Text("OK")
                        }
                    }
                }
            }
        }
    }
}



fun getAvailableTimesForDate(selectedDate: LocalDate, agendaMap: Map<LocalDate, List<LocalTime>>): List<LocalTime> {
    val workingHours = (9..16).map { hour -> LocalTime.of(hour, 0) }
    val bookedHours = agendaMap[selectedDate] ?: emptyList()

    return workingHours.filterNot { bookedHours.contains(it)}
}
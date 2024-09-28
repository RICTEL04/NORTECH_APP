package com.example.nortech_app.Visits

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import viewmodel.UserViewModel


// Simulación de base de datos de citas planeadas, estas son las citas que ya estan registradas en el sistema

val scheduledDates =  mapOf(
    // Día 1: 24 de septiembre de 2024 con 3 citas planeadas
    LocalDate.of(2024, 9, 27) to listOf(
        LocalTime.of(8, 30),
        LocalTime.of(10, 0),
        LocalTime.of(15, 30)
    ),

    // Día 2: 25 de septiembre de 2024 con 2 citas planeadas
    LocalDate.of(2024, 9, 30) to listOf(
        LocalTime.of(9, 0),
        LocalTime.of(13, 30)
    ),

    // Día 3: 26 de septiembre de 2024 con 4 citas planeadas
    LocalDate.of(2024, 10, 1) to listOf(
        LocalTime.of(8, 0),
        LocalTime.of(11, 30),
        LocalTime.of(14, 0),
        LocalTime.of(15, 30)
    ),

    // Día 6: 27 de septiembre de 2024 con 4 citas planeadas
    LocalDate.of(2024, 10, 2) to listOf(
        LocalTime.of(8, 0),
        LocalTime.of(9, 0),
        LocalTime.of(9, 30),
        LocalTime.of(11, 0),
        LocalTime.of(12, 30),
        LocalTime.of(14, 0),
        LocalTime.of(15, 30),

        ),

    // Día 6: 30 de septiembre de 2024 con 4 citas planeadas
    LocalDate.of(2024, 10, 4) to listOf(
        LocalTime.of(8, 0),
        LocalTime.of(9, 0),
        LocalTime.of(10, 0),
        LocalTime.of(11, 0),
        LocalTime.of(12, 0),
        LocalTime.of(13, 0),
        LocalTime.of(14, 0),
        LocalTime.of(15, 0),
        LocalTime.of(16, 0),

        )
)


// Ejemplo de uso con intervalo de 1:30 horas (90 minutos)
val intervalMinutes = 90L
// Map con todos los horarios ocupados
val agendaAvailabilityMap = generateAgendaAvailabilityMap(scheduledDates, intervalMinutes)

// Función que genera la agenda con intervalos ocupados personalizados
fun generateAgendaAvailabilityMap(
    scheduledDates: Map<LocalDate, List<LocalTime>>,
    intervalMinutes: Long
): MutableMap<LocalDate, MutableList<LocalTime>> {
    val agendaAvailabilityMap = mutableMapOf<LocalDate, MutableList<LocalTime>>()

    for ((date, times) in scheduledDates) {
        val occupiedTimes = mutableListOf<LocalTime>()

        for (time in times) {
            // Añadir la hora de inicio de la cita
            occupiedTimes.add(time)

            // Bloquear los intervalos antes de la hora de la cita
            var previousTime = time
            var remainingBefore = intervalMinutes
            remainingBefore -= 30
            while (remainingBefore > 0) {
                previousTime = previousTime.minusMinutes(30)
                remainingBefore -= 30
                // Solo bloqueamos si la hora es válida (no antes de las 8:00 AM)
                if (previousTime.isAfter(LocalTime.of(7, 30))) {
                    occupiedTimes.add(previousTime)
                }
            }

            // Añadir los intervalos de tiempo ocupados después de la cita
            var nextTime = time
            var remainingAfter = intervalMinutes

            // Mientras no se acabe el intervalo, bloqueamos los horarios posteriores
            remainingAfter -= 30
            while (remainingAfter > 0) {
                nextTime = nextTime.plusMinutes(30)
                remainingAfter -= 30
                // Limitar las citas hasta las 5:00 PM (17:00)
                if (nextTime.hour < 17) {
                    occupiedTimes.add(nextTime)
                }
            }
        }

        // Guardar la lista de horarios ocupados en el mapa de disponibilidad para ese día
        agendaAvailabilityMap[date] = occupiedTimes
    }

    return agendaAvailabilityMap
}



fun getAvailableTimesForDate(selectedDate: LocalDate, agendaMap: MutableMap<LocalDate, MutableList<LocalTime>>): List<LocalTime> {
    // Generamos los horarios de trabajo con intervalos de 30 minutos
    val workingHours = mutableListOf<LocalTime>()
    var currentTime = LocalTime.of(8, 0)  // Horario de inicio: 8:00 AM
    val endTime = LocalTime.of(16, 30)    // Última cita: 16:30 PM

    while (currentTime.isBefore(endTime)) {
        workingHours.add(currentTime)
        currentTime = currentTime.plusMinutes(30)  // Avanzamos en intervalos de 30 minutos
    }

    // Obtenemos las horas reservadas para la fecha seleccionada
    val bookedHours = agendaMap[selectedDate] ?: emptyList()

    // Filtramos las horas disponibles (que no estén reservadas)
    return workingHours.filterNot { bookedHours.contains(it) }
}

// Función para agregar una fecha y una hora a agendaAvailabilityMap
fun addAvailability(agendaAvailabilityMap: MutableMap<LocalDate, MutableList<LocalTime>>, date: LocalDate, time: LocalTime) {
    // Verificar si la fecha ya existe en el mapa
    if (agendaAvailabilityMap.containsKey(date)) {
        // Obtener la lista de horas para esa fecha
        val times = agendaAvailabilityMap[date]

        // Si la hora no está en la lista, agregarla
        if (time !in times!!) {
            times.add(time)
        } else {
            println("La hora $time ya existe para la fecha $date")
        }
    } else {
        // Si la fecha no existe, crear una nueva lista con la hora y agregarla al mapa
        agendaAvailabilityMap[date] = mutableListOf(time)
    }
}

// Función para eliminar una fecha y una hora de agendaAvailabilityMap
fun removeAvailability(agendaAvailabilityMap: MutableMap<LocalDate, MutableList<LocalTime>>, date: LocalDate, time: LocalTime) {
    // Verificar si la fecha existe en el mapa
    if (agendaAvailabilityMap.containsKey(date)) {
        // Obtener la lista de horas para esa fecha
        val times = agendaAvailabilityMap[date]

        // Si la hora está en la lista, eliminarla
        if (time in times!!) {
            times.remove(time)
            println("La hora $time ha sido eliminada de la fecha $date")

            // Si ya no quedan horas para esa fecha, eliminar la fecha del mapa
            if (times.isEmpty()) {
                agendaAvailabilityMap.remove(date)
                println("La fecha $date ha sido eliminada porque no tiene más horas disponibles")
            }
        } else {
            println("La hora $time no existe para la fecha $date")
        }
    } else {
        println("La fecha $date no existe en el mapa")
    }
}


fun printAgendaAvailability() {
    agendaAvailabilityMap.forEach { (date, times) ->
        println("Date: $date, Available times: ${times.joinToString(", ")}")
    }
}

/*
val agendaFullMap = mapOf(
    //LocalDate.of(2024, 9, 24) to true,
    //LocalDate.of(2024, 9, 28) to true,
    //LocalDate.of(2024, 9, 29) to true
)


 */


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DateAndTimePicker(
    agendaAvailabilityMap: MutableMap<LocalDate, MutableList<LocalTime>>
) {
    var pickedDate by remember { mutableStateOf<LocalDate?>(null) }
    var pickedTime by remember { mutableStateOf<LocalTime?>(null) }
    val selectedDateTimeList = remember { mutableStateListOf<Pair<LocalDate, LocalTime>>() }
    val dateDialogState = rememberMaterialDialogState()
    val timeDialogState = remember { mutableStateOf(false) }
    var isDateValid by remember { mutableStateOf(false) }

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
                enabled = pickedDate != null && pickedTime != null
            ) {
                Text(text = "Agregar cita")
            }

            if (selectedDateTimeList.isEmpty()) {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                    ,
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
                        isDateValid = isAfterOrEqualToToday && isNotWeekend
                    }
                )
            }


        }
    }
}


//Componente que va a desplegar las horas disponibles en cierto dia
@Composable
fun SelectTimeByDate(
    agendaAvailabilityMap: MutableMap<LocalDate, MutableList<LocalTime>>, //Mapa de las horas ocupadas en cierto dia
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
                .heightIn(min = 200.dp, max = 400.dp),  // Define el tamaño máximo del diálogo
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

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(onClick = { onDismiss() }) {
                        Text("Cancel")
                    }


                } else {
                    // Hacer scrolleable con LazyColumn
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)  // Permitir que la lista crezca y se ajuste dentro del diálogo
                    ) {
                        items(availableTimes) { time ->
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
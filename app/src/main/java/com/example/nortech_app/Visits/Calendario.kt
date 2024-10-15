package com.example.nortech_app.Visits

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
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

// Función que genera la agenda con intervalos ocupados personalizados
fun generateAgendaAvailabilityMap(
    scheduledDates: Map<LocalDate, List<LocalTime>>,
    intervalMinutes: Long
): MutableMap<LocalDate, MutableList<LocalTime>> {
    val agendaAvailabilityMap = mutableMapOf<LocalDate, MutableList<LocalTime>>()
    val currentTime = LocalTime.now()
    val currentDate = LocalDate.now()
    val occupiedTimes = mutableListOf<LocalTime>()

    // Siempre agregar horas ocupadas para el día actual
    for (hour in 10..16) { // Limitar de 10:00 a 17:00
        val time = LocalTime.of(hour, 0)
        if (time.isBefore(currentTime)) {
            occupiedTimes.add(time)
        }
    }
    agendaAvailabilityMap[currentDate] = occupiedTimes // Guardar horas ocupadas en currentDate
    Log.d("CalendarioGenerateAvailabilityMap", "Con scheduled en $scheduledDates")
    // Verificar si hay citas programadas
    if (scheduledDates.isNullOrEmpty()) {
        Log.d("CalendarioGenerateAvailabilityMap", "Sin citas: $occupiedTimes")
    }else{
        for ((date, times) in scheduledDates) {
            // Lista temporal para almacenar los horarios ocupados para el día actual
            val dailyOccupiedTimes = agendaAvailabilityMap.getOrPut(date) { mutableListOf() }

            for (time in times) {
                // Añadir la hora de inicio de la cita si no está ya en la lista de horarios ocupados
                if (!dailyOccupiedTimes.contains(time)) {
                    dailyOccupiedTimes.add(time)
                }

                // Bloquear los intervalos 60 minutos antes de la hora de la cita
                var previousTime = time
                var remainingBefore = intervalMinutes
                remainingBefore -= 60
                while (remainingBefore > 0) {
                    previousTime = previousTime.minusMinutes(60)
                    remainingBefore -= 60
                    // Solo bloquear si la hora es válida (no antes de las 10:00 AM)
                    if (previousTime.isAfter(LocalTime.of(10, 0)) && !dailyOccupiedTimes.contains(previousTime)) {
                        dailyOccupiedTimes.add(previousTime)
                    }
                }

                // Bloquear los intervalos 60 minutos después de la hora de la cita
                var nextTime = time
                var remainingAfter = intervalMinutes
                remainingAfter -= 60
                while (remainingAfter > 0) {
                    nextTime = nextTime.plusMinutes(60)
                    remainingAfter -= 60
                    // Limitar las citas hasta las 5:00 PM (17:00)
                    if (nextTime.hour < 17 && !dailyOccupiedTimes.contains(nextTime)) {
                        dailyOccupiedTimes.add(nextTime)
                    }
                }
            }

            // Guardar la lista de horarios ocupados actualizada para ese día
            agendaAvailabilityMap[date] = dailyOccupiedTimes
        }
    }
    Log.d("CalendarioGenerateAvailabilityMap", "AgendaAvail: $agendaAvailabilityMap")
    return agendaAvailabilityMap
}




fun getAvailableTimesForDate(selectedDate: LocalDate, agendaMap: MutableMap<LocalDate, MutableList<LocalTime>>): List<LocalTime> {
    // Generamos los horarios de trabajo con intervalos de 30 minutos
    val workingHours = mutableListOf<LocalTime>()
    var currentTime = LocalTime.of(10, 0)  // Horario de inicio: 8:00 AM
    val endTime = LocalTime.of(17, 0)    // Última cita: 16:30 PM

    while (currentTime.isBefore(endTime)) {
        workingHours.add(currentTime)
        currentTime = currentTime.plusMinutes(60)  // Avanzamos en intervalos de 30 minutos
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
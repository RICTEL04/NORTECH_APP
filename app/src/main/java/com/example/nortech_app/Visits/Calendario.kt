package com.example.nortech_app.Visits

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerExamples(navController: NavHostController) {


    //Date
    var showModal by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<Long?>(null) }
    var formattedDate : String = ""

    //Time
    var showInputExample by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(true) }
    var selectedTime: TimePickerState? by remember { mutableStateOf(null) }
    val formatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    var formattedHour : String = ""

    var cardList = remember { mutableStateListOf<Pair<String, String>>() }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate("Solicitudes")
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Regresar"
                        )
                    }
                },
                title = {})
        },
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(64.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "Agendar cita",
                fontSize = 30.sp
            )
            Button(
                onClick = {
                    showModal = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Seleccionar dia")
            }


            Text(
                text = "Hora",
                fontSize = 30.sp
            )




            Button(onClick = {
                showInputExample = true
            }) {
                Text("Input time picker")
            }

            when
            {

                showInputExample -> InputUseStateExample(
                    onDismiss = {
                        showInputExample = false
                        showMenu = true
                    },
                    onConfirm = {
                            time ->
                        selectedTime = time
                        showInputExample = false
                        showMenu = true
                    },
                )

            }

            if (showModal) {
                // [END_EXCLUDE]
                DatePickerModal(
                    onDateSelected = {
                        selectedDate = it
                        showModal = false
                    },
                    onDismiss = { showModal = false }
                )
            }

            if (selectedDate != null) {
                val calendar = Calendar.getInstance()
                calendar.time = Date(selectedDate!!)
                calendar.add(Calendar.DAY_OF_YEAR, 1) // Agrega un día
                val newDate = calendar.time
                formattedDate = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(newDate)
                Text("Fecha seleccionada: $formattedDate")
            }
            else
            {
                Text("No hay fecha seleccionada")
            }

            if (selectedTime != null) {
                val cal = Calendar.getInstance()
                cal.set(Calendar.HOUR_OF_DAY, selectedTime!!.hour)
                cal.set(Calendar.MINUTE, selectedTime!!.minute)
                cal.isLenient = false
                formattedHour = formatter.format(cal.time)
                Text("Hora seleccionada = $formattedHour")
            } else {
                Text("No hay hora seleccionada.")
            }

            Button(onClick = {
                try {
                    // Agregar un nuevo Card a la lista
                    if(selectedDate != null && selectedTime != null)
                    {
                        cardList.add(Pair("Fecha $formattedDate", "Subtitle $formattedHour"))
                        print("Par tamanio: ${cardList.size}")
                        println(cardList[cardList.size - 1])
                        selectedDate = null
                        selectedTime = null
                    }
                } catch (e: Exception) {
                    Log.e("CardListExample", "Error adding card: ${e.localizedMessage}")
                }
            }) {
                Text("Agregar opcion de cita")
            }

            // Mostrar las tarjetas
            Spacer(modifier = Modifier.height(16.dp))

            if(cardList.size > 0)
            {
                Column(modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),


                    ) {
                    for (item in cardList) {
                        CustomCard(title = item.first, subtitle = item.second)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
// [START android_compose_components_input_usestate]
@Composable
fun InputUseStateExample(
    onConfirm: (TimePickerState) -> Unit,
    onDismiss: () -> Unit,
) {
    val currentTime = Calendar.getInstance()

    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = true,
    )

    Dialog(onDismissRequest = {onDismiss( )})
    {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(375.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        )
        {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                Text(
                    text = "Selecciona la hora",
                    modifier = Modifier.padding(16.dp),
                )

                TimeInput(
                    state = timePickerState,
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    TextButton(
                        onClick = { onDismiss() },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Dismiss")
                    }
                    TextButton(
                        onClick = { onConfirm(timePickerState) },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}

@Composable
fun CustomCard(title: String, subtitle: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall)
        }
    }
}
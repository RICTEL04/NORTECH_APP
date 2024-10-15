package com.example.nortech_app.Students

import android.util.Log
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import com.example.nortech_app.Abogados.BottomBarAbogado
import com.example.nortech_app.Visits.BottomNavigationBar
import com.example.nortech_app.sendNotificationToUser
import kotlinx.coroutines.delay
import model.Cita
import viewmodel.UserViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

fun agruparCitasPorDia(citas: List<Cita>): Map<LocalDate, List<Cita>> {
    val formatter = DateTimeFormatter.ofPattern("[H:mm][HH:mm]") // Acepta ambas horas
    val today = LocalDate.now() // Obtener la fecha actual
    return citas
        .filter { it.anio != null
                && it.mes != null
                && it.dia != null
                && it.hora != null
                && LocalDate.of(it.anio.toInt(), it.mes.toInt(), it.dia.toInt()) >= today
        } // Filtrar citas válidas
        .groupBy { cita ->
            // Convertir a LocalDate
            LocalDate.of(
                cita.anio?.toInt() ?: 0,
                cita.mes?.toInt() ?: 0,
                cita.dia?.toInt() ?: 0
            )
        }
        .mapValues { entry ->
            // Ordenar las citas por LocalTime
            entry.value.sortedBy { cita ->
                // Convertir la hora a LocalTime utilizando el formateador que maneja ambos formatos
                LocalTime.parse(cita.hora, formatter)
            }
        }
        .toSortedMap() // Ordena las fechas de la más antigua a la más reciente
}

@Composable
fun LOAD_CHARGE_CITAS_ABOGADO(viewModel: UserViewModel,
                              isLoading: MutableState<Boolean>,
                              agendaCitasElements: MutableState<Map<LocalDate, List<Cita>>>
){

    LaunchedEffect(Unit) {
        delay(3000L)
        while (true) {
            viewModel.getAllCitas()
            viewModel.getUsersNotificationData()
            delay(3000L)
            isLoading.value = false;
            Log.d("UserEnviarSolicitudLoadView", "Agenda Antes de enviar solicitud")
        }
    }



    agendaCitasElements.value = agruparCitasPorDia(viewModel.allCitasPendientes.value ?: emptyList())


    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading.value) {
            CircularProgressIndicator() // Ruedita de carga
        } else {
            // Aquí puedes poner el contenido cuando no está cargando
            Text("Contenido cargado")
        }
    }

}

fun caducidadCita(viewModel: UserViewModel,
                  formatterTime: DateTimeFormatter,
                  isLoading: MutableState<Boolean>
){
    if(!viewModel.allCitasPendientes.value.isNullOrEmpty()){
        val currentDate = LocalDate.now()
        val currentTime = LocalTime.now()
        var contador : Int = 0
        viewModel.allCitasPendientes.value?.forEach{ cita ->
            if(cita != null ){

                val citaFecha = cita.let {
                    LocalDate.of(
                        it.anio?.toIntOrNull() ?: 0,
                        it.mes?.toIntOrNull() ?: 1,
                        it.dia?.toIntOrNull() ?: 1
                    )
                } ?: LocalDate.now()

                val citaHora = cita.hora?.let {
                    LocalTime.parse(it, formatterTime)
                } ?: LocalTime.MIDNIGHT

                if(cita.estado == "pendiente"){

                    if (currentDate.isAfter(citaFecha) ||
                        (currentDate.isEqual(citaFecha)
                                && citaHora?.minusMinutes(10)?.isBefore(currentTime) == true)) {
                        cita.id?.let { id ->
                            viewModel.updateEstadoCita(id, "cancelada", "No confirmó la cita a tiempo")
                            cita.user_id?.let { viewModel.insertNotifications(it, "Su cita con fecha ${cita.anio}/${cita.mes}/${cita.dia} a las ${cita.hora} no se confirmo a tiempo") }
                            viewModel.usersNotificationData.value[cita.user_id]?.let {
                                sendNotificationToUser(
                                    it, "Su cita en la fecha ${cita.anio}/${cita.mes}/${cita.dia} a las ${cita.hora} no se confirmo a tiempo"
                                )
                            }
                            contador++
                        }
                    }
                }
                else if(cita.estado == "confirmada"){
                    if(currentDate.isAfter(citaFecha) ||
                        (currentDate.isEqual(citaFecha) &&
                                citaHora.plusMinutes(60)?.isBefore(currentTime) == true
                                )){

                        cita.id?.let { id ->
                            viewModel.updateEstadoCita(id, "completada", "La cita fue completada con éxito")
                            cita.user_id?.let { viewModel.insertNotifications(it, "¡Su cita fue completada con exito!") }
                            viewModel.usersNotificationData.value[cita.user_id]?.let {
                                sendNotificationToUser(
                                    it, "¡Su cita fue completada con exito!"
                                )
                            }
                            contador++
                        }
                    }
                }
            }

        }

        if(contador > 0) isLoading.value = true

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendaScreen(navController: NavHostController, viewModel: UserViewModel) {

    val formatterTime =  DateTimeFormatter.ofPattern("H:mm")
    val isLoading = remember { mutableStateOf(false) }
    val agendaCitasElements = remember { mutableStateOf<Map<LocalDate, List<Cita>>>(emptyMap()) }

    LaunchedEffect(Unit) {
        viewModel.getAllCitas()
        viewModel.getUsersNotificationData()
    }

    com.example.nortech_app.Abogados.caducidadCita(viewModel, formatterTime, isLoading)

    agendaCitasElements.value = com.example.nortech_app.Abogados.agruparCitasPorDia(
        viewModel.allCitasPendientes.value ?: emptyList()
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "AGENDA",
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp, // Reduce ligeramente el tamaño
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
            if (!isLoading.value) { // Si no está en el modo login, muestra el BottomAppBar
                BottomNavigationBarEstudiante(navController, 2)
            }
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Mostrar Loading si se está cargando
            if (isLoading.value) {
                com.example.nortech_app.Abogados.LOAD_CHARGE_CITAS_ABOGADO(
                    viewModel = viewModel,
                    isLoading = isLoading,
                    agendaCitasElements = agendaCitasElements
                )
            } else {

                if(agendaCitasElements.value.isNullOrEmpty()){
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        verticalArrangement = Arrangement.spacedBy(8.dp), // Espacio entre elementos
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        item{
                            Text(
                                text = "No hay citas en la agenda",
                                fontWeight = FontWeight.Bold, // Negrita
                                fontSize = 26.sp, // Tamaño de fuente
                                modifier = Modifier.fillMaxWidth(), // Asegura que el texto ocupe todo el ancho disponible
                                textAlign = TextAlign.Center, // Centra el texto
                                color = Color.Black // Color del texto, puedes cambiarlo según tus necesidades
                            )
                        }
                    }

                }
                else{
                    // Mostrar la agenda cuando esté disponible
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        com.example.nortech_app.Abogados.caducidadCita(viewModel, formatterTime, isLoading)
                        items(agendaCitasElements.value.toList()) { (fecha, citas) ->


                            Text(
                                text = fecha.toString(),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )

                            citas.forEach { cita ->
                                AgendaItem(
                                    cita,
                                    viewModel,
                                    isLoading
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
fun AgendaItem(
    cita: Cita,
    viewModel: UserViewModel,
    isLoading: MutableState<Boolean>
) {
    var expanded by remember { mutableStateOf(false) } // Controls whether to show additional information
    val openFullScreenDialog = remember { mutableStateOf(false) }
    val motivo_cancelar = remember{ mutableStateOf("") }
    var textFieldValue =  remember { mutableStateOf("") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
        //.background(MaterialTheme.colorScheme.onSurface),
        ,
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {


            Row(
                modifier = Modifier.fillMaxWidth() // Asegura que el Row ocupe todo el ancho disponible
            ) {
                Text(
                    text = "${cita.hora ?: "Sin Hora"} ",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f) // Hace que este texto ocupe todo el espacio restante
                )

                Text(
                    text = "${cita.estado ?: "NoState"} ",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = cita.nombre_usuario ?: "usuario",
                fontSize = 20.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = cita.motivo ?: "Sin motivo",
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = cita.descripcion ?: "Sin descripción",
                maxLines = if (expanded) Int.MAX_VALUE else 1, // Show full text when expanded
                overflow = if (!expanded) TextOverflow.Ellipsis else TextOverflow.Visible, // Add "..." if the text is too long when not expanded
                fontSize = if (expanded) 14.sp else 12.sp, // Increase font size when expanded
                color = if (expanded) Color.Black else Color.Gray // Change color to black when expanded
            )



            Spacer(modifier = Modifier.height(8.dp))

            // Show details if expanded

            // Row for the buttons to select appointment and toggle details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                // Toggle button to expand/collapse details
                Button(
                    onClick = {
                        expanded = !expanded


                    },
                    modifier = Modifier
                        .padding(vertical = 8.dp)


                ) {
                    Text(if (expanded) "Ocultar" else "Mostrar más")
                }

            }

            if (openFullScreenDialog.value) {
                FullScreenDialog(
                    onDismissRequest = { openFullScreenDialog.value = false },
                    onConfirmation = {
                        viewModel.updateEstadoCita(cita.id ?: "", "cancelada", motivo_cancelar.value) // Lógica de confirmación
                        cita.user_id?.let { viewModel.insertNotifications(it, "Su cita con fecha ${cita.anio}/${cita.mes}/${cita.dia} a las ${cita.hora} fue cancelada por el siguiente motivo:\n ${motivo_cancelar.value}") }
                        viewModel.usersNotificationData.value[cita.user_id]?.let {
                            sendNotificationToUser(
                                it, "Su cita en la fecha: ${cita.anio}/${cita.mes}/${cita.dia} a las ${cita.hora} fue cancelada"
                            )
                        }
                        isLoading.value = true
                        openFullScreenDialog.value = false // Cerrar el diálogo
                        //Log.d("XDDDD", motivo_cancelar.value )
                        motivo_cancelar.value = ""
                        //Log.d("XDDDD", motivo_cancelar.value )
                    },
                    motivo_cancelar,
                    cita,
                )
            }

        }
    }
}


@Composable
fun FullScreenDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    motivo_cancelar: MutableState<String>,
    cita: Cita
) {
    Dialog(
        onDismissRequest = { onDismissRequest() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
        ),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant),

            ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp), // Más padding para espaciado
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Título del diálogo
                Text(
                    text = "Datos de la Cita",
                    style = MaterialTheme.typography.headlineSmall, // Estilo de título
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Información de la cita
                Text(
                    text = cita.nombre_usuario ?: "Sin nombre",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = cita.motivo ?: "Sin motivo",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "${cita.anio ?: "0000"}-${cita.mes ?: "00"}-${cita.dia ?: "00"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "${cita.hora ?: "Sin Hora"}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Descripción de la cita
                Text(
                    text = cita.descripcion ?: "Sin descripción",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Campo de texto para cancelar
                TextField(
                    value = motivo_cancelar.value,
                    onValueChange = { newText -> motivo_cancelar.value = newText },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(128.dp),
                    label = { Text("Motivo de cancelación") },
                    maxLines = 5,
                    singleLine = false,
                )

                Spacer(modifier = Modifier.height(24.dp)) // Espacio entre el campo de texto y los botones

                // Botones
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                    ) {
                        Text("Salir", color = MaterialTheme.colorScheme.secondary)
                    }
                }
            }
        }
    }
}
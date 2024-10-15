package com.example.nortech_app.Visits

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.nortech_app.sendNotificationToUser
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.SessionStatus
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import model.Cita
import model.Solicitud
import viewmodel.UserViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException


fun setUpForSolicitudesScreen(viewModel: UserViewModel,
                              agendaAvailabilityMap: MutableMap<LocalDate, MutableList<LocalTime>>,
                              scheduledDates: Map<LocalDate, List<LocalTime>>?,
                              solicitudesByCliente: List<Solicitud>?,
                              cantidadSolicitudesDeUsuario: MutableState<Int>
)
{

    agendaAvailabilityMap.clear()
    agendaAvailabilityMap.putAll(generateAgendaAvailabilityMap(scheduledDates ?: emptyMap(), 60L))

    solicitudesByCliente?.forEach { solicitud ->
        val fecha = LocalDate.of(solicitud.anio.toInt(), solicitud.mes.toInt(), solicitud.dia.toInt())

        // Intenta analizar la hora con los dos formatos
        val formatter = DateTimeFormatter.ofPattern("h:mm a") // para "8:00 AM" o "8:00 PM"
        val formatterWithLeadingZero = DateTimeFormatter.ofPattern("HH:mm") // para "08:00"
        // Intenta analizar la hora con los dos formatos
        val hora = LocalTime.parse(solicitud.hora, formatterWithLeadingZero)

        addAvailability(agendaAvailabilityMap, fecha, hora)
    }

    cantidadSolicitudesDeUsuario.value = 3 - (solicitudesByCliente?.size ?: 0)

    Log.d("UserByLaunch", "Agenda: $agendaAvailabilityMap")
    Log.d("UserByLaunch", "Cant Sol: ${cantidadSolicitudesDeUsuario.value}")

}


@Composable
fun LOAD_CHARGE_SOLICITUD(viewModel: UserViewModel, isLoading: MutableState<Boolean>,
                          agendaAvailabilityMap: MutableMap<LocalDate, MutableList<LocalTime>>,
                          cantidadSolicitudesDeUsuario: MutableState<Int>,
) {


    LaunchedEffect(Unit) {
        delay(3000L)
        while (true) {
            //isLoading.value = true
            viewModel.getHoras()
            viewModel.getAllSolicitudes()
            viewModel.getSolicitudesByClienteId()
            viewModel.getCitasUsuario()
            viewModel.getUsersNotificationData()
            delay(3000L) // Espera 3 segundos antes de volver a ejecutar la solicitud
            isLoading.value = false
            Log.d("UserEnviarSolicitudLoadView", "Agenda Antes de enviar solicitud: ${isLoading.value}")
        }
    }
    Log.d("UserEnviarSolicitudLoadView", "Agenda Antes de enviar solicitud: ${isLoading.value}")
    Log.d("UserEnviarSolicitudLoadView", "Agenda Antes de enviar solicitud: ${viewModel.solicitudesByCliente.collectAsState().value}")

    setUpForSolicitudesScreen(
        viewModel = viewModel,
        agendaAvailabilityMap = agendaAvailabilityMap,
        scheduledDates = viewModel.scheduledDates.value,
        solicitudesByCliente = viewModel.solicitudesByCliente.collectAsState().value,
        cantidadSolicitudesDeUsuario = cantidadSolicitudesDeUsuario
    )

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

fun solicitudesCaducadas( viewModel: UserViewModel,
                          solicitudesByCliente: List<Solicitud>?,
                          formatterTime: DateTimeFormatter,
                          isLoading: MutableState<Boolean>){
    Log.d("SolicitudesCaducadas", "En la funcion, antes del primer if ")
    if (!solicitudesByCliente.isNullOrEmpty()) {
        val currentDate = LocalDate.now()
        val currentTime = LocalTime.now()
        var contador : Int = 0
        Log.d("SolicitudesCaducadas", "En la funcion, antes de hacer for each: ")
        solicitudesByCliente?.forEach { solicitud ->
            if (solicitud != null) {
                val solicitudFecha = solicitud.let {
                    LocalDate.of(
                        it.anio?.toIntOrNull() ?: 0,
                        it.mes?.toIntOrNull() ?: 1,
                        it.dia?.toIntOrNull() ?: 1
                    )
                } ?: LocalDate.now()

                val solicitudHora = solicitud.hora?.let {
                    LocalTime.parse(it, formatterTime)
                } ?: LocalTime.MIDNIGHT

                if (currentDate.isAfter(solicitudFecha) ||
                    (currentDate.isEqual(solicitudFecha) && currentTime.isAfter(solicitudHora))) {
                    solicitud.id?.let { id ->
                        viewModel.updateEstadoSolicitud(id, "2")
                        viewModel.usersNotificationData.value[solicitud.id_cliente]?.let {
                            sendNotificationToUser(
                                it, "Su cita en la fecha ${solicitud.anio}/${solicitud.mes}/${solicitud.dia} a las ${solicitud.hora} no se confirmo a tiempo"
                            )
                        }
                        contador++
                    }
                }
            }
        }
        if(contador > 0) isLoading.value = true

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SolicitudesScreen(
    navController: NavController,
    viewModel: UserViewModel
) {

    val isLoading = remember { mutableStateOf(false) }
    val scheduledDates by remember {viewModel.scheduledDates}
    val allSolicitudes by  remember {viewModel.allSolicitudes}
    val nameByID by remember { viewModel.nameByID }
    val solicitudesByCliente by viewModel.solicitudesByCliente.collectAsState()
    val cantidadSolicitudesDeUsuario = remember { mutableStateOf(0) }
    var agendaAvailabilityMap = remember { mutableMapOf<LocalDate, MutableList<LocalTime>>() }
    val formatterTime =  DateTimeFormatter.ofPattern("H:mm")
    val openAcceptScreenDialog = remember { mutableStateOf(false) }

    Log.d("UserRepository2", "Name XDDDD: $nameByID")

    LaunchedEffect(Unit) {
        viewModel.getUUID()
        viewModel.getHoras()
        Log.d("UserRepository9", "Lol1")
        viewModel.getAllSolicitudes()
        Log.d("UserRepository9", "Lol2")
        viewModel.getSolicitudesByClienteId()
        Log.d("UserRepository9", "Lol3")
        viewModel.getCitasUsuario()
        Log.d("UserRepository9", "Lol4")
        viewModel.getUsersNotificationData()

    }
    Log.d("SolicitudesCaducadas", "Antes de la funcion al abrir la pantalla: $solicitudesByCliente")
    solicitudesCaducadas(viewModel, solicitudesByCliente, formatterTime, isLoading)
    Log.d("SolicitudesCaducadas", "Despues de la funcion al abrir la pantalla: $solicitudesByCliente")

    setUpForSolicitudesScreen(
        viewModel = viewModel,
        agendaAvailabilityMap = agendaAvailabilityMap,
        scheduledDates = scheduledDates,
        solicitudesByCliente = solicitudesByCliente,
        cantidadSolicitudesDeUsuario = cantidadSolicitudesDeUsuario
    )

    var textFieldValue by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("") }
    val options = listOf(
        "Delitos contra la propiedad",
        "Delitos contra las personas",
        "Violencia doméstica",
        "Fraude o estafa",
        "Asesoría penal preventiva",
        "Seguimiento de procesos judiciales",
        "Otro"
    )
    var pickedDate by remember { mutableStateOf<LocalDate?>(null) }
    var pickedTime by remember { mutableStateOf<LocalTime?>(null) }
    var selectedDateTimeList = remember { mutableStateListOf<Pair<LocalDate, LocalTime>>() }
    val dateDialogState = rememberMaterialDialogState()
    val timeDialogState = remember { mutableStateOf(false) }
    var isDateValid by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()


    solicitudesByCliente?.firstOrNull()?.let { firstSolicitud ->
        textFieldValue = firstSolicitud.descripcion
        selectedOption = firstSolicitud.motivo
        Log.d("SolicitudPrimerElementoIf", "Descripción: ${firstSolicitud.descripcion}, Motivo: ${firstSolicitud.motivo}")
    }


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
                BottomNavigationBar(navController, 1)
            }
        },
    ) { paddingValues ->

        val currentTime = LocalTime.now()
        val currentDate = LocalDate.now()
        Log.d("UserIsLoadingState", "isLoading = ${isLoading.value}")

        if (isLoading.value) {
            LOAD_CHARGE_SOLICITUD(viewModel = viewModel, isLoading = isLoading, agendaAvailabilityMap, cantidadSolicitudesDeUsuario)
        } else {

            if(viewModel.citasUsuario.value?.isEmpty() == false)
            {

                val primerCita = viewModel.citasUsuario.value?.firstOrNull()
                val primerCitaFecha = primerCita?.let {
                    LocalDate.of(
                        it.anio?.toIntOrNull() ?: 0,
                        it.mes?.toIntOrNull() ?: 1,
                        it.dia?.toIntOrNull() ?: 1
                    )
                } ?: LocalDate.now()

                val primerCitaHora = primerCita?.hora?.let {
                    LocalTime.parse(it, formatterTime)
                } ?: LocalTime.MIDNIGHT
                val enableButton = primerCita?.estado == "pendiente"
                        && primerCitaFecha == currentDate
                        && primerCitaHora?.minusMinutes(30)?.isBefore(currentTime) ?: false
                        && primerCitaHora?.minusMinutes(10)?.isAfter(currentTime) ?: false

                if (primerCita != null) {
                    if( (currentDate.isAfter(primerCitaFecha) ||
                                (currentDate.isEqual(primerCitaFecha) &&
                                        primerCitaHora.minusMinutes(10).isBefore(currentTime)
                                        )
                                ) &&
                        primerCita.estado == "pendiente"){
                        primerCita.id?.let {

                            viewModel.updateEstadoCita(it, "cancelada", "No confirmó la cita a tiempo")
                            primerCita.user_id?.let { it1 -> viewModel.insertNotifications(it1, "Su cita en la fecha ${primerCita.anio}/${primerCita.mes}/${primerCita.dia} a las ${primerCita.hora} no se confirmo a tiempo") }
                            viewModel.usersNotificationData.value[primerCita.user_id]?.let {
                                sendNotificationToUser(
                                    it, "Su cita en la fecha ${primerCita.anio}/${primerCita.mes}/${primerCita.dia} a las ${primerCita.hora} no se confirmo a tiempo"
                                )
                            }

                        }
                        isLoading.value = true
                    }
                    else if ((currentDate.isAfter(primerCitaFecha) ||
                                (currentDate.isEqual(primerCitaFecha) &&
                                        primerCitaHora.plusMinutes(60).isBefore(currentTime))
                                )
                        &&
                        primerCita.estado == "confirmada") {
                        primerCita.id?.let {
                            viewModel.updateEstadoCita(it, "completada", "La cita fue completada con éxito")
                            primerCita.user_id?.let { it1 -> viewModel.insertNotifications(it1, "¡Su cita fue completada con exito!") }
                            viewModel.usersNotificationData.value[primerCita.user_id]?.let {
                                sendNotificationToUser(
                                    it, "¡Su cita fue completada con exito!"
                                )
                            }
                        }



                        isLoading.value = true
                    }
                }


                Log.d("LOLOLOLO", "$primerCitaHora")
                Log.d("LOLOLOLO", "$primerCitaFecha")
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    verticalArrangement = Arrangement.Center, // Espacio entre elementos
                    horizontalAlignment = Alignment.CenterHorizontally,
                ){

                    Log.d("LOLOLOLO", "PrimeraCitaHora: $primerCitaHora")
                    Log.d("LOLOLOLO", "CurrentTime: $currentTime")
                    Log.d("LOLOLOLO", "Estado: ${primerCita?.estado == "pendiente"}")
                    Log.d("LOLOLOLO", "CurrentDate: ${primerCitaFecha == currentDate}")
                    Log.d("LOLOLOLO", "Before30: ${primerCitaHora?.minusMinutes(30)?.isAfter(currentTime) ?: false}")
                    Log.d("LOLOLOLO", "AfterHour: ${primerCitaHora?.isAfter(currentTime) ?: false}")
                    Log.d("LOLOLOLO", "EnableButton: $enableButton")



                    item{
                        Text(
                            text = "Ya cuenta con una cita",
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold,

                            )
                    }


                    if (primerCita != null) {
                        item{
                            Text(
                                text = "${primerCita.anio ?: "0000"}/${primerCita.mes ?: "00"}/${primerCita.dia ?: "00"}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E88E5) // Color azul para la fecha
                            )
                        }

                        item{
                            Text(
                                text = "${primerCita.hora}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Gray
                            )
                        }

                        item{
                            Text(
                                text = "Motivo",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1E88E5)
                            )
                        }

                        item{
                            Text(
                                text = primerCita.motivo ?: "No hay motivo",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal,
                                modifier = Modifier.padding(start = 8.dp),
                                color = Color.Black
                            )
                        }

                        item{
                            Text(
                                text = "Descripción",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1E88E5)
                            )
                        }

                        item{
                            Text(
                                text = primerCita.descripcion ?: "No hay descripción",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal,
                                modifier = Modifier.padding(start = 8.dp),
                                color = Color.Black
                            )
                        }

                        item{
                            Text(
                                text = primerCita.estado ?: "NoState",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal,
                                modifier = Modifier.padding(start = 8.dp),
                                color = Color.Black
                            )
                        }

                        item{
                            Button(
                                onClick = {
                                    primerCita.id?.let { viewModel.updateEstadoCita(it, "confirmada", "") }
                                    isLoading.value = true
                                },
                                enabled = enableButton,
                                modifier = Modifier
                                    .fillMaxWidth() // Ocupa todo el ancho
                                    .padding(horizontal = 16.dp, vertical = 8.dp), // Espaciado
                                shape = RoundedCornerShape(16.dp), // Esquinas redondeadas
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (enableButton) Color(0xFF1E88E5) else Color.Gray, // Color dinámico
                                    contentColor = Color.White // Color del texto
                                ),
                                border = BorderStroke(1.dp, Color(0xFF1E88E5)),
                            ) {
                                Text(
                                    text = if(primerCita.estado == "confirmada") "Su cita esta confirmada" else "Confirmar cita",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(8.dp) // Añadir padding dentro del botón
                                )
                            }
                        }

                    }
                }
            }
            else{
                if(cantidadSolicitudesDeUsuario.value == 0)
                {
                    if(solicitudesByCliente?.isEmpty() == false)
                    {
                        solicitudesCaducadas(viewModel, solicitudesByCliente, formatterTime, isLoading)
                        val primerSolicitud = solicitudesByCliente?.firstOrNull()


                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues),
                            //verticalArrangement = Arrangement.Center, // Espacio entre elementos
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ){
                            item {
                                Spacer(modifier = Modifier.height(16.dp))
                            }

                            item{
                                Text(
                                    text = "Ya ha realizado todas sus solicitudes disponibles",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Normal,
                                    modifier = Modifier
                                        .padding(start = 8.dp)
                                        .fillMaxWidth(),
                                    color = Color.Black
                                )
                            }

                            item {
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            item{
                                Text(
                                    text = "Para volver a realizar solicitudes espere a que su cita sea confirmada o a que caduquen las mismas",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Normal,
                                    modifier = Modifier
                                        .padding(start = 8.dp)
                                        .fillMaxWidth(),
                                    color = Color.Gray
                                )
                            }

                            item {
                                Spacer(modifier = Modifier.height(24.dp))
                            }


                            if (primerSolicitud != null) {

                                item{
                                    Text(
                                        text = "Fechas escogidas",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF1E88E5)
                                    )
                                }

                                viewModel.solicitudesByCliente.value?.let { solicitudesList ->
                                    items(solicitudesList) { solicitud ->
                                        // Manejo seguro de valores nulos en los campos de la solicitud
                                        Text(
                                            text = "${solicitud.anio ?: "0000"}/${solicitud.mes ?: "00"}/${solicitud.dia ?: "00"}",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black // Color negro para la fecha
                                        )

                                        Text(
                                            text = solicitud.hora ?: "Sin hora",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color.Gray
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                }


                                item {
                                    Spacer(modifier = Modifier.height(8.dp))
                                }

                                item{
                                    Text(
                                        text = "Motivo",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF1E88E5)
                                    )
                                }


                                item{
                                    Text(
                                        text = primerSolicitud.motivo ?: "No hay motivo",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Normal,
                                        modifier = Modifier.padding(start = 8.dp),
                                        color = Color.Black
                                    )
                                }

                                item {
                                    Spacer(modifier = Modifier.height(16.dp))
                                }

                                item{
                                    Text(
                                        text = "Descripción",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF1E88E5)
                                    )
                                }

                                item{
                                    Text(
                                        text = primerSolicitud.descripcion ?: "No hay descripción",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Normal,
                                        modifier = Modifier.padding(start = 8.dp),
                                        color = Color.Black
                                    )
                                }

                                item {
                                    Spacer(modifier = Modifier.height(16.dp))
                                }

                            }
                        }
                    }
                }
                else
                {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        verticalArrangement = Arrangement.spacedBy(8.dp), // Espacio entre elementos
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        if(viewModel.solicitudesByCliente.value?.isNotEmpty() == true){

                            item {
                                Text(
                                    modifier = Modifier
                                        .padding(top = 8.dp, bottom = 4.dp),
                                    text = "Solicitudes Realizadas",
                                    textAlign = TextAlign.Center,
                                    fontSize = 16.sp
                                )
                            }

                            viewModel.solicitudesByCliente.value?.let { solicitudesList ->
                                items(solicitudesList) { solicitud ->
                                    Row(
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier
                                            .padding(
                                                start = 16.dp, // Ajusta el padding izquierdo
                                                end = 16.dp    // Ajusta el padding derecho
                                            )
                                            .background(Color(0xFFE3F2FD)) // Color de fondo azul claro
                                    ) {
                                        Text(
                                            text = "${solicitud.anio ?: "0000"}/${solicitud.mes ?: "00"}/${solicitud.dia ?: "00"}: ",
                                            textAlign = TextAlign.Start, // Alineado a la izquierda
                                            fontSize = 14.sp
                                        )

                                        Text(
                                            text = solicitud.hora ?: "Sin hora", // Valor predeterminado si no hay hora
                                            fontSize = 14.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }


                            item {
                                Text(
                                    modifier = Modifier
                                        .padding(top = 8.dp,),
                                    text = "Solicitudes Restantes: ${cantidadSolicitudesDeUsuario.value}",
                                    textAlign = TextAlign.Center,
                                    fontSize = 16.sp
                                )
                            }

                        }
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        item {
                            // TextField con lista desplegable
                            TextField(
                                value = selectedOption,
                                onValueChange = { selectedOption = it },
                                label = { Text("Seleccione un motivo") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        start = 16.dp, // Ajusta el padding izquierdo
                                        end = 16.dp,   // Ajusta el padding derecho
                                    ),
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
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        start = 16.dp, // Ajusta el padding izquierdo
                                        end = 16.dp,   // Ajusta el padding derecho
                                    )
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

                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        item {
                            // TextField para la descripción
                            TextField(
                                value = textFieldValue,
                                onValueChange = { newText -> textFieldValue = newText },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(128.dp)
                                    .padding(
                                        start = 16.dp, // Ajusta el padding izquierdo
                                        end = 16.dp,   // Ajusta el padding derecho
                                    ),
                                label = { Text("Descripción") },
                                singleLine = false,
                                maxLines = 5
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        item {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .padding(
                                        start = 16.dp, // Ajusta el padding izquierdo
                                        end = 16.dp,   // Ajusta el padding derecho
                                    )
                            ) {
                                // Botón para agregar fecha
                                Button(
                                    onClick = {
                                        dateDialogState.show()
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
                        }

                        item {
                            Spacer(modifier = Modifier.padding(5.dp))
                        }

                        item {
                            // Botón para agregar la cita
                            Button(
                                onClick = {
                                    pickedDate?.let { date ->
                                        pickedTime?.let { time ->
                                            addAvailability(agendaAvailabilityMap, date, time)
                                            // Agregar la fecha y hora seleccionada a la lista
                                            selectedDateTimeList.add(Pair(date, time))
                                            pickedTime = null
                                            pickedDate = null
                                            timeDialogState.value = false
                                        }
                                    }
                                },
                                enabled = pickedDate != null && pickedTime != null &&
                                        selectedDateTimeList.size < (cantidadSolicitudesDeUsuario.value ?: 0),
                                modifier = Modifier.padding(
                                    start = 16.dp, // Ajusta el padding izquierdo
                                    end = 16.dp    // Ajusta el padding derecho
                                )
                            ) {
                                Text(text = "Agregar solicitud para cita")
                            }
                        }


                        if (selectedDateTimeList.isEmpty()) {
                            item {
                                Text(
                                    modifier = Modifier
                                        .padding(top = 20.dp, bottom = 20.dp),
                                    text = "No hay citas elegidas",
                                    textAlign = TextAlign.Center,
                                    fontSize = 30.sp
                                )
                            }
                        } else {
                            items(selectedDateTimeList) { (date, time) ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
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
                                                .padding(start = 16.dp, top = 16.dp)
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

                        item {
                            // Botón de "Enviar solicitud"
                            Button(
                                onClick = {

                                    openAcceptScreenDialog.value = true
                                },
                                enabled = selectedDateTimeList.size > 0 && selectedDateTimeList.size <= cantidadSolicitudesDeUsuario.value &&
                                        selectedOption.isNotEmpty() && textFieldValue.isNotEmpty(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        start = 16.dp, // Ajusta el padding izquierdo
                                        end = 16.dp,   // Ajusta el padding derecho
                                    )
                            ) {
                                Text(text = "Enviar solicitud", fontSize = 18.sp)
                            }
                        }

                        item{
                            Spacer(modifier = Modifier.padding(5.dp))
                        }

                        item{
                            Text(
                                text = "Al realizar una solicitud, acepta que haya un estudiante presente en la asesoría.",
                                textAlign = TextAlign.Center,
                                fontSize = 14.sp,
                                color = Color.Gray,
                                modifier = Modifier.fillMaxWidth()
                            )
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
                            positiveButton(
                                text = "Ok",
                                disableDismiss = !isDateValid
                            ) {
                                if (isDateValid) {
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
                            allowedDateValidator = { selectedDate ->
                                val currentDate = LocalDate.now()
                                val isAfterOrEqualToToday = !selectedDate.isBefore(currentDate)
                                val isNotWeekend = selectedDate.dayOfWeek != DayOfWeek.SATURDAY && selectedDate.dayOfWeek != DayOfWeek.SUNDAY

                                isAfterOrEqualToToday && isNotWeekend
                            },
                            onDateChange = { selectedDate ->
                                pickedDate = selectedDate
                                val currentDate = LocalDate.now()
                                val isAfterOrEqualToToday = !selectedDate.isBefore(currentDate)
                                val isNotWeekend = selectedDate.dayOfWeek != DayOfWeek.SATURDAY && selectedDate.dayOfWeek != DayOfWeek.SUNDAY
                                isDateValid = isAfterOrEqualToToday && isNotWeekend
                            }
                        )
                    }


                    if (openAcceptScreenDialog.value) {
                        AcceptScreenDialog(
                            onDismissRequest = { openAcceptScreenDialog.value = false },
                            onConfirmation = {
                                coroutineScope.launch {
                                    for ((fecha, hora) in selectedDateTimeList) {
                                        val anio = fecha.year.toString()
                                        val mes = fecha.monthValue.toString()
                                        val dia = fecha.dayOfMonth.toString()
                                        val horaString = hora.toString()
                                        viewModel.insertSolicitud(
                                            anio = anio,
                                            mes = mes,
                                            dia = dia,
                                            hora = horaString,
                                            descripcion = textFieldValue,
                                            motivo = selectedOption
                                        )
                                    }

                                    if (!viewModel.solicitudesByCliente.value.isNullOrEmpty()) {
                                        viewModel.solicitudesByCliente.value?.forEach { solicitud ->
                                            solicitud?.id?.let { id ->
                                                viewModel.updateSolicitud(id, textFieldValue, selectedOption)
                                            }
                                        }
                                    }

                                    selectedDateTimeList.clear()
                                    openAcceptScreenDialog.value = false
                                    isLoading.value = true
                                }
                            },

                            )
                    }

                }

            }
        }
    }
}


@Composable
fun AcceptScreenDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
) {
    Dialog(
        onDismissRequest = { onDismissRequest() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
        ),
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium, // Añadir esquinas redondeadas
            modifier = Modifier
                .padding(16.dp)
                .clip(MaterialTheme.shapes.medium) // Asegurar que se apliquen las esquinas redondeadas
                .background(MaterialTheme.colorScheme.surface), // Asegurarse de que el color de fondo se aplique después
        ) {
            Column(
                modifier = Modifier.padding(16.dp), // Padding interno
                verticalArrangement = Arrangement.spacedBy(16.dp), // Espacio entre los elementos
            ) {
                Text(
                    text = "Al realizar una solicitud, acepta que haya un estudiante presente en la asesoría.\n¿Estás de acuerdo?",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth()
                )

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

                    Button(
                        onClick = { onConfirmation() },
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp),
                    ) {
                        Text("Enviar solicitud", color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }
        }
    }
}


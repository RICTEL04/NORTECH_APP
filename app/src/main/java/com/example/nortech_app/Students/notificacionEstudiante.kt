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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.navigation.NavHostController
import com.example.nortech_app.Abogados.BottomBarAbogado
import com.example.nortech_app.Visits.BottomNavigationBar
import com.example.nortech_app.sendNotificationToUser
import com.example.nortech_app.sendRecordatorioToUser
import kotlinx.coroutines.delay
import model.Solicitud
import viewmodel.UserViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun LOAD_CHARGE_SOLICITUD_ABOGADO_BY_INSERT(viewModel: UserViewModel, isLoading: MutableState<Boolean>,
                                            selectedDate: MutableState<Solicitud?>, allSolicitudes: Map<String, List<Solicitud>>?
) {


    LaunchedEffect(Unit) {
        delay(3000L)
        while (true) {
            viewModel.getAllSolicitudes()
            viewModel.getUsersNotificationData()
            delay(3000L)
            isLoading.value = false;
            Log.d("UserEnviarSolicitudLoadView", "Agenda Antes de enviar solicitud")
        }
    }


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

fun allSolicitudesCaducadas(
    viewModel: UserViewModel,
    allSolicitudes: Map<String, List<Solicitud>>?,
    formatterTime: DateTimeFormatter,
    isLoading: MutableState<Boolean>
) {
    Log.d("SolicitudesCaducadas", "En la funcion, antes del primer if ")

    if (!allSolicitudes.isNullOrEmpty()) {
        val currentDate = LocalDate.now()
        val currentTime = LocalTime.now()
        var contador: Int = 0
        Log.d("SolicitudesCaducadas", "En la funcion, antes de hacer for each: ")

        // Iterar sobre el mapa
        allSolicitudes.forEach { (_, solicitudes) -> // Desestructuramos el mapa en clave y lista de solicitudes
            solicitudes.forEach { solicitud -> // Iteramos sobre cada solicitud en la lista
                if (solicitud != null) {
                    val solicitudFecha = LocalDate.of(
                        solicitud.anio?.toIntOrNull() ?: 0,
                        solicitud.mes?.toIntOrNull() ?: 1,
                        solicitud.dia?.toIntOrNull() ?: 1
                    )

                    val solicitudHora = solicitud.hora?.let {
                        LocalTime.parse(it, formatterTime)
                    } ?: LocalTime.MIDNIGHT

                    if (currentDate.isAfter(solicitudFecha) ||
                        (currentDate.isEqual(solicitudFecha) && currentTime.isAfter(solicitudHora))) {
                        solicitud.id?.let { id ->
                            viewModel.updateEstadoSolicitud(id, "2")
                            viewModel.usersNotificationData.value[solicitud.id_cliente]?.let {
                                viewModel.insertNotifications(solicitud.id_cliente, "Su solicitud con fecha ${solicitud.anio}/${solicitud.mes}/${solicitud.dia} a las ${solicitud.hora} ha caducado, ahora cuenta con otra solicitud para hacer")
                                sendNotificationToUser(
                                    it, "Su solicitud con fecha ${solicitud.anio}/${solicitud.mes}/${solicitud.dia} a las ${solicitud.hora} ha caducado"
                                )
                            }
                            contador++
                        }
                    }
                }
            }
        }
        if (contador > 0) isLoading.value = true
    }
}


fun buscarIdsConFechaYHora(
    allSolicitudes: Map<String, List<Solicitud>>?,
    selectedDate: Solicitud
): List<String> {
    // Asegurarse de que allSolicitudes no sea nulo
    if (allSolicitudes.isNullOrEmpty()) return emptyList()

    return allSolicitudes.flatMap { (_, solicitudes) ->
        solicitudes.filter {
            it.dia == selectedDate.dia &&
                    it.mes == selectedDate.mes &&
                    it.anio == selectedDate.anio &&
                    it.hora == selectedDate.hora ||
                    it.id_cliente == selectedDate.id_cliente
        }.map { it.id }
    }
}

// NOTIFICACIONES ESTUDIANTE--------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreenEstudiante(
    navController: NavHostController,
    viewModel: UserViewModel,

    ) {

    val isLoading = remember { mutableStateOf(false) }
    val allSolicitudes = remember { viewModel.allSolicitudes }
    val selectedDate = remember { mutableStateOf<Solicitud?>(null) }
    val formatterTime =  DateTimeFormatter.ofPattern("H:mm")
    // Llamar a getHoras() cuando la pantalla se inicializa
    LaunchedEffect(Unit) {
        viewModel.getAllSolicitudes()
        viewModel.getUsersNotificationData()
    }

    com.example.nortech_app.Abogados.allSolicitudesCaducadas(
        viewModel,
        allSolicitudes.value,
        formatterTime,
        isLoading
    )

    Log.d("getUsersNotificationData", "${viewModel.usersNotificationData.value}")



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
                BottomNavigationBarEstudiante(navController, 3)
            }
        }
    ) { paddingValues ->


        if(isLoading.value)
        {
            com.example.nortech_app.Abogados.LOAD_CHARGE_SOLICITUD_ABOGADO_BY_INSERT(
                viewModel,
                isLoading,
                selectedDate,
                allSolicitudes.value
            )
        }
        else{

            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {


                if(allSolicitudes.value.isNullOrEmpty()) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        verticalArrangement = Arrangement.spacedBy(8.dp), // Espacio entre elementos
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        item{
                            Text(
                                text = "No hay solicitudes",
                                fontWeight = FontWeight.Bold, // Negrita
                                fontSize = 26.sp, // Tamaño de fuente
                                modifier = Modifier.fillMaxWidth(), // Asegura que el texto ocupe todo el ancho disponible
                                textAlign = TextAlign.Center, // Centra el texto
                                color = Color.Black // Color del texto, puedes cambiarlo según tus necesidades
                            )
                        }
                    }
                }
                else {

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp, bottom = 20.dp)
                        //.background(MaterialTheme.colorScheme.primary),
                    ){

                        allSolicitudes.value?.forEach(){(idCliente,solicitudes) ->

                            item {
                                SolicitudCard(idCliente = idCliente, solicitudes = solicitudes,
                                    viewModel = viewModel, isLoading = isLoading,
                                    allSolicitudes = allSolicitudes.value,
                                    selectedDate = selectedDate)
                            }


                        }

                    }

                }

            }
        }
    }
}


@Composable
fun SolicitudCard(
    idCliente: String,
    solicitudes: List<Solicitud>,
    viewModel: UserViewModel,
    isLoading: MutableState<Boolean>,
    allSolicitudes: Map<String, List<Solicitud>>?,
    selectedDate: MutableState<Solicitud?>

) {
    var expanded by remember { mutableStateOf(false) } // Controls whether to show additional information
    var selectedDateCard by remember { mutableStateOf<Solicitud?>(null) } // Track the selected date
    val formatterTime =  DateTimeFormatter.ofPattern("H:mm")

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
            // Show summary (title, relationship, truncated description)

            if(selectedDateCard != selectedDate.value)
            {
                selectedDateCard = null
            }

            Text(
                text = "Cliente: ${solicitudes.firstOrNull()?.nombre ?: "Sin nombre"} ",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = solicitudes.firstOrNull()?.motivo ?: "Sin motivo",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = solicitudes.firstOrNull()?.descripcion ?: "Sin descripción",
                maxLines = if (expanded) Int.MAX_VALUE else 1, // Show full text when expanded
                overflow = if (!expanded) TextOverflow.Ellipsis else TextOverflow.Visible, // Add "..." if the text is too long when not expanded
                fontSize = if (expanded) 14.sp else 12.sp, // Increase font size when expanded
                color = if (expanded) Color.Black else Color.Gray // Change color to black when expanded
            )



            Spacer(modifier = Modifier.height(8.dp))

            // Show details if expanded
            if (expanded) {
                Log.d("UserViewModel InsertSolicitud", "Before rendering date buttons")
                Column {
                    solicitudes.forEach { solicitud ->
                        // Button for each date
                        Button(
                            onClick = {
                                selectedDateCard = solicitud // Update selected date
                                selectedDate.value = selectedDateCard
                            },
                            modifier = Modifier.padding(vertical = 4.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedDateCard == solicitud && selectedDateCard != null) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.primary,
                                disabledContentColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Text(
                                text = "Fecha: ${solicitud.dia}/${solicitud.mes}/${solicitud.anio} ${solicitud.hora}",
                                color = if (selectedDateCard == solicitud && selectedDateCard != null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onPrimary
                            )
                        }

                    }
                }
            }


            // Row for the buttons to select appointment and toggle details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                // Toggle button to expand/collapse details
                Button(
                    onClick = { expanded = !expanded

                        if(!expanded){
                            selectedDateCard = null
                            if(selectedDate.value == selectedDateCard) selectedDate.value = null
                        }

                    },
                    modifier = Modifier
                        .padding(vertical = 8.dp)


                ) {
                    Text(if (expanded) "Ocultar" else "Mostrar más")
                }

            }

        }
    }
}
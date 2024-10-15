package com.example.nortech_app

import HumanRightsView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.nortech_app.Abogados.AddEstudiante
import com.example.nortech_app.Abogados.AgendaView
import com.example.nortech_app.Abogados.CasoForm
import com.example.nortech_app.Abogados.CasosView
import com.example.nortech_app.Abogados.EstudiantesView
import com.example.nortech_app.Abogados.MainView
import com.example.nortech_app.Abogados.PerfilView
import com.example.nortech_app.Abogados.SolicitudesView
import com.example.nortech_app.Abogados.VerCasoView
import com.example.nortech_app.Abogados.VerEstudiantesView
import com.example.nortech_app.Abogados.VerSolicitudView
import com.example.nortech_app.Students.AgendaScreen
import com.example.nortech_app.Students.CasosScreen
import com.example.nortech_app.Students.MainEstudianteScreen
import com.example.nortech_app.Students.NotificationsScreenEstudiante
import com.example.nortech_app.Students.ProfileEstudianteScreen
import com.example.nortech_app.Students.VerCasoViewEstudiante
import com.example.nortech_app.Students.VerNotificacion
import com.example.nortech_app.Abogados.ErrorScreen
import com.example.nortech_app.Visits.LawCategoriesGrid
import com.example.nortech_app.Visits.LegalSolutionsScreen
import com.example.nortech_app.Abogados.LoadingScreen
import com.example.nortech_app.Visits.MainScreen
import com.example.nortech_app.Abogados.NotFormScreen
import com.example.nortech_app.Abogados.NoticiaUPFormScreen
import com.example.nortech_app.Abogados.UPFormCaso
import com.example.nortech_app.Visits.DetalleNoticiaScreen
import com.example.nortech_app.Visits.LeyesAmenazas
import com.example.nortech_app.Visits.LeyesAsalto
import com.example.nortech_app.Visits.LeyesRobo
import com.example.nortech_app.Visits.NotificationsScreen
import com.example.nortech_app.Visits.ProfileScreen
import com.example.nortech_app.Visits.SolicitudesScreen
//import com.example.nortech_app.Visits.VerNotGeneral
import io.github.jan.supabase.gotrue.SessionStatus
import kotlinx.coroutines.delay
import viewmodel.UserViewModel

@Composable
fun UserAuthScreen(viewModel: UserViewModel) {
    val sessionState by viewModel.sessionState.collectAsState()
    val navController = rememberNavController()
    NavHost(navController, startDestination = "home") {
        composable("home") { LegalSolutionsScreen(navController) }
        composable("login") { LoginScreen(viewModel, navController, viewModel.rol.value) }
        composable("Register") { RegisterScreen(viewModel, navController) }
        composable("Main") {
            if (viewModel.rol.value ==""){
                LOAD_VIEW(viewModel)
            }
            if (viewModel.rol.value =="1"){
                MainScreen(viewModel,navController)
            }
            if (viewModel.rol.value =="3"){
                MainEstudianteScreen(viewModel, navController)
            }
            if (viewModel.rol.value =="2"){
                MainView(viewModel, navController)
            }
        }
        composable("Notification") { NotificationsScreen(navController, viewModel) }
        composable("Profile") { ProfileScreen(viewModel,navController) }
        composable("Derechos") { LawCategoriesGrid(navController) }
        composable("Solicitudes") { SolicitudesScreen(navController, viewModel) }
        composable("VerDerechos") { HumanRightsView(navController) }
        composable("LeyesRobo") { LeyesRobo(navController) }
        composable("LeyesAsalto") { LeyesAsalto(navController) }
        composable("LeyesAmenazas") { LeyesAmenazas(navController) }

        // Estudiantes
        composable("AgendaEstudiante") { AgendaScreen(navController,viewModel) }
        composable("CasosEstudiantes") { CasosScreen(navController, viewModel) }
        composable("NotificacionEstudiantes") { NotificationsScreenEstudiante(navController, viewModel) }
        composable("PerfilEstudiantes") { ProfileEstudianteScreen(viewModel,navController) }
        composable("VerNotificacionesEstudiantes") { VerNotificacion(navController) }
        composable("VerCasoEstudiantes") { VerCasoViewEstudiante(navController, viewModel) }

        // Abogados
        composable("PerfilAbogado") { PerfilView(viewModel,navController) }
        composable("NoticiaForm") { NotFormScreen(viewModel,navController) }
        composable("AgendaAbogado") { AgendaView(navController, viewModel) }
        composable("CasosAbogado") { CasosView(navController, viewModel) }
        composable("EstudiantesAbogado") { EstudiantesView(navController, viewModel) }
        composable("SolicitudesAbogado") { SolicitudesView(navController, viewModel) }
        composable("VerCasoAbogado") { VerCasoView(navController, viewModel) }
        composable("VerEstudianteAbogado") { VerEstudiantesView(navController, viewModel) }
        composable("VerSolicitudAbogado") { VerSolicitudView(navController) }

        composable("AddEstudiante") { AddEstudiante(navController, viewModel)}

        composable("CrearCaso") { CasoForm(viewModel,navController) }
        composable("detalle_noticia") { DetalleNoticiaScreen(navController, viewModel.id_not.value ,viewModel.TituloNot.value, viewModel.DescripcionNot.value, viewModel.URL_Not.value, viewModel) }

        composable("chatbot") { Chatbot(viewModel, navController)}
        // Extras
        composable("loading") { LoadingScreen() }
        composable("error") { ErrorScreen("Network error") }

        composable ("UPForm") {NoticiaUPFormScreen(viewModel,navController)}
        composable ("UPFormCaso") {UPFormCaso(navController, viewModel)}
    }


    LaunchedEffect(sessionState) {
        viewModel.getRol()
        when (sessionState) {
            is SessionStatus.Authenticated -> {
                viewModel.getRol()
                navController.navigate("main") {
                    popUpTo("login") { inclusive = true } // Evita volver a la pantalla de login
                }
            }
            SessionStatus.LoadingFromStorage -> {
                navController.navigate("loading") {
                    popUpTo("main") { inclusive = true } // Evita volver a la pantalla principal
                }
            }
            SessionStatus.NetworkError -> {
                navController.navigate("error") {
                    popUpTo("main") { inclusive = true } // Evita volver a la pantalla principal
                }
            }
            is SessionStatus.NotAuthenticated -> {
                navController.navigate("home") {
                    popUpTo("main") { inclusive = true } // Evita volver a la pantalla principal
                }
            }
        }
    }
}

@Composable
fun LOAD_VIEW(viewModel: UserViewModel) { var isLoading by remember { mutableStateOf(true) }
    val cargas = remember { mutableStateOf(1)}
    LaunchedEffect(Unit) {
        while (true) {
            isLoading = true
            viewModel.getRol()
            delay(3000L) // Espera 3 segundos antes de volver a ejecutar la solicitud
            isLoading = false
            cargas.value += 1
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator() // Ruedita de carga
            } else {
                // Aquí puedes poner el contenido cuando no está cargando
                Text("Contenido cargado")
            }

            // Logout Button
            if(cargas.value>=5) {
                Button(
                    onClick = { viewModel.signOut() },
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 16.dp) // Agrega un padding superior para separar el botón del contenido
                ) {
                    Text(text = "Volver al Inicio")
                }
            }
        }
    }

}






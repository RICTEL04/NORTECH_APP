package com.example.nortech_app

import HumanRightsView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.nortech_app.Abogados.AgendaView
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
import com.example.nortech_app.Visits.ErrorScreen
import com.example.nortech_app.Visits.LawCategoriesGrid
import com.example.nortech_app.Visits.LegalSolutionsScreen
import com.example.nortech_app.Visits.LoadingScreen
import com.example.nortech_app.Visits.MainScreen
import com.example.nortech_app.Visits.NotificationsScreen
import com.example.nortech_app.Visits.ProfileScreen
import com.example.nortech_app.Visits.SolicitudesScreen
import com.example.nortech_app.Visits.VerNotGeneral
import io.github.jan.supabase.gotrue.SessionStatus
import viewmodel.UserViewModel

@Composable
fun UserAuthScreen(viewModel: UserViewModel) {
    val sessionState by viewModel.sessionState.collectAsState()
    val navController = rememberNavController()
    val globalVariable = remember { mutableStateOf("1") }
    NavHost(navController, startDestination = "home") {
        composable("home") { LegalSolutionsScreen(navController) }
        composable("login") { LoginScreen(viewModel, navController, globalVariable) }
        composable("Register") { RegisterScreen(viewModel, navController, globalVariable) }
        composable("Main") {
            if (globalVariable.value =="1"){
                MainScreen(viewModel, navController)
            }
            if (globalVariable.value =="2"){
                MainView(navController)
            }
            if (globalVariable.value =="3"){
                MainEstudianteScreen(navController)
            }
        }
        composable("Notification") { NotificationsScreen(navController) }
        composable("Profile") { ProfileScreen(viewModel,navController) }
        composable("Derechos") { LawCategoriesGrid(navController) }
        composable("Solicitudes") { SolicitudesScreen(navController, viewModel) }
        composable("VerNot") { VerNotGeneral(navController) }
        composable("VerDerechos") { HumanRightsView(navController) }

        // Estudiantes
        composable("AgendaEstudiante") { AgendaScreen(navController) }
        composable("CasosEstudiantes") { CasosScreen(navController) }
        composable("NotificacionEstudiantes") { NotificationsScreenEstudiante(navController) }
        composable("PerfilEstudiantes") { ProfileEstudianteScreen(navController) }
        composable("VerNotificacionesEstudiantes") { VerNotificacion(navController) }
        composable("VerCasoEstudiantes") { VerCasoViewEstudiante(navController) }

        // Abogados
        composable("PerfilAbogado") { PerfilView(navController) }
        composable("AgendaAbogado") { AgendaView(navController) }
        composable("CasosAbogado") { CasosView(navController) }
        composable("EstudiantesAbogado") { EstudiantesView(navController) }
        composable("SolicitudesAbogado") { SolicitudesView(navController) }
        composable("VerCasoAbogado") { VerCasoView(navController) }
        composable("VerEstudianteAbogado") { VerEstudiantesView(navController) }
        composable("VerSolicitudAbogado") { VerSolicitudView(navController) }

        // Extras
        composable("loading") { LoadingScreen() }
        composable("error") { ErrorScreen("Network error") }
    }


    LaunchedEffect(sessionState) {
        when (sessionState) {
            is SessionStatus.Authenticated -> {
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



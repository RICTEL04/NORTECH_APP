package com.example.nortech_app

import HumanRightsView
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
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
import com.example.nortech_app.Visits.DateAndTimePicker
import com.example.nortech_app.Visits.LawCategoriesGrid
import com.example.nortech_app.Visits.LegalSolutionsScreen
import com.example.nortech_app.Visits.MainScreen
import com.example.nortech_app.Visits.NotificationsScreen
import com.example.nortech_app.Visits.ProfileScreen
import com.example.nortech_app.Visits.SolicitudesScreen
import com.example.nortech_app.Visits.VerNotGeneral
import com.example.nortech_app.Visits.agendaAvailabilityMap
import com.example.nortech_app.Visits.agendaFullMap

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LegalSolutionsApp()
        }
    }
}

@Composable
fun LegalSolutionsApp() {
    val navController = rememberNavController()
    val globalVariable = remember { mutableStateOf("visitante") }
    NavHost(navController = navController, startDestination = "home") {
        // Inicio
        composable("home") { LegalSolutionsScreen(navController, globalVariable) }
        composable("login") { LoginScreen(navController) }
        composable("Register") { RegisterScreen(navController) }
        composable("Main") {
            if (globalVariable.value =="visitante"){
                MainScreen(navController)
            }
            if (globalVariable.value =="abogado"){
                MainView(navController)
            }
            if (globalVariable.value =="estudiante"){
                MainEstudianteScreen(navController)
            }
        }
        composable("Notification") { NotificationsScreen(navController) }
        composable("Profile") { ProfileScreen(navController) }
        composable("Derechos") { LawCategoriesGrid(navController) }
        composable("Solicitudes") { SolicitudesScreen(navController, agendaFullMap = agendaFullMap, agendaAvailabilityMap = agendaAvailabilityMap) }
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
    }
}

@Preview(showBackground = true)
@Composable
fun LegalSolutionsScreenPreview() {
    LegalSolutionsApp()
}

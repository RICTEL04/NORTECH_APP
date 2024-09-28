package model

import android.util.Log
import androidx.compose.runtime.MutableState
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.SessionStatus
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class UserRepository(private val supabase: SupabaseClient, scope: CoroutineScope) {
    private val _sessionState = MutableStateFlow<SessionStatus>(SessionStatus.LoadingFromStorage)
    val sessionState: StateFlow<SessionStatus> get() = _sessionState

    init {
        scope.launch {
            // Listener para cambios de sesión
            supabase.auth.sessionStatus.collect { sessionStatus ->
                _sessionState.value = sessionStatus
            }
        }
    }


    suspend fun signIn(userEmail: String, userPassword: String, opcion: MutableState<String>) {
        supabase.auth.signInWith(Email){
            email = userEmail
            password = userPassword
        }
        delay(1000)
        getRol(opcion)
    }

    suspend fun signUp(userEmail: String, userPassword: String, name: String, rol: String, opcion: MutableState<String>) {
        supabase.auth.signUpWith(Email) {
            email = userEmail
            password = userPassword
        }
        insertUser(name, rol)
        delay(1000)
        getRol(opcion)
    }

    suspend fun signOut() {
        supabase.auth.signOut()
    }

    suspend fun insertUser(Name: String,Role: String) {
        // Se pueden agregar mas variables pero hay que actualizar UserViewModel.kt
        var id = supabase.auth.retrieveUserForCurrentSession(updateSession = true).id
        val user = User(Nombre = Name, Rol = Role,id=id.toString())
        supabase.from("usuarios_").insert(user) // Use the ViewModel to add user
        Log.d("UserRepository3", "User insert: $user")

    }

    // Obtiene el ID de la sesion.
    suspend fun getUUID() {
        val result = supabase.auth.retrieveUserForCurrentSession(updateSession = true)?.id
        Log.d("UserRepository", "UUID fetched: $result")
    }

    // Obtiene el ID de la sesion.
    suspend fun getRol(opcion: MutableState<String>) {
        val id = supabase.auth.retrieveUserForCurrentSession(updateSession = true)?.id
        val result = supabase.from("usuarios_").select(columns = Columns.list("Rol")){
            filter {
                User::id eq id
            }
        }
        opcion.value  = result.data.replace(Regex("[^0-9]"), "")
        Log.d("UserRepository2", "Rol fetched: ${opcion.value}")
    }

    // Get name
    suspend fun getName(): String {
        val id = supabase.auth.retrieveUserForCurrentSession(updateSession = true)?.id
        val result = supabase.from("usuarios_").select(columns = Columns.list("Nombre")){
            filter {
                User::id eq id
            }
        }
        var finalResult = result.data
        Log.d("UserRepository2", "Name fetched: ${finalResult}")
        return finalResult

    }

    // Get Phone
    suspend fun getPhone(): String {
        val id = supabase.auth.retrieveUserForCurrentSession(updateSession = true)?.id
        val result = supabase.from("usuarios_").select(columns = Columns.list("Telefono")){
            filter {
                User::id eq id
            }
        }
        var finalResult = result.data.replace(Regex("[^0-9]"), "")
        Log.d("UserRepository2", "Phone fetched: ${finalResult}")
        return finalResult

    }

    // Obtiene las horas disponibles
    suspend fun getHoras(): Map<LocalDate, List<LocalTime>>? {
        Log.d("UsedrRepository3", "Buenos días desde UserRepository")
        val id = supabase.auth.retrieveUserForCurrentSession(updateSession = true).id
        Log.d("UsedrRepository3", "Ya jalamos el ID del supabase chavales")

        val result = supabase.from("Citas").select(columns = Columns.list("anio", "mes", "dia", "hora")) {
            // Filtros adicionales si los necesitas
        }

        // Procesamos el JSON obtenido
        val jsonString = result.data
        Log.d("UsedrRepository3", "Citas raw JSON: $jsonString")

        val timeFormatter = DateTimeFormatter.ofPattern("H:mm")

        return try {
            Log.d("UsedrRepository3", "Antes de deserializar lista")
            // Deserializar el JSON en una lista de citas
            val citas: List<Cita> = Json.decodeFromString(jsonString)
            Log.d("UsedrRepository3", "Lista Deserializada")

            // Crear el mapa de fechas programadas
            val scheduledDates = citas.groupBy { cita ->
                LocalDate.of(cita.anio.toInt(), cita.mes.toInt(), cita.dia.toInt())
            }.mapValues { entry ->
                entry.value.map { cita ->
                    // Usar el formateador personalizado para parsear las horas
                    LocalTime.parse(cita.hora, timeFormatter)
                }
            }

            Log.d("UsedrRepository3", "Valores obtenidos")
            Log.d("UsedrRepository3", "Scheduled dates map: $scheduledDates")

            // Retornar el mapa
            scheduledDates
        } catch (e: Exception) {
            Log.e("UsedrRepository3", "Error procesando el JSON: ${e.message}")
            null // Retornar null si ocurre un error
            }
        }
}
package viewmodel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jan.supabase.gotrue.SessionStatus
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import model.UserRepository
import java.time.LocalDate
import java.time.LocalTime

class UserViewModel( private val userRepository: UserRepository) : ViewModel(){
    val userName = mutableStateOf("")

    // Estado de sesión observable por la UI
    val sessionState: StateFlow<SessionStatus> = userRepository.sessionState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SessionStatus.LoadingFromStorage
    )

    // Estados adicionales para controlar la UI durante el proceso de autenticación
    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf("")

    fun signIn(email: String, password: String, opcion: MutableState<String>) {
        isLoading.value = true
        errorMessage.value = ""
        viewModelScope.launch {
            try {
                userRepository.signIn(email, password, opcion)
            } catch (e: Exception) {
                errorMessage.value = e.message ?: "Unknown error"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun signUp(email: String, password: String, name: String, rol: String, opcion: MutableState<String>) {
        isLoading.value = true
        errorMessage.value = ""
        viewModelScope.launch {
            try {
                userRepository.signUp(email, password, name, rol, opcion)
            }catch (e: Exception) {
                errorMessage.value = e.message ?: "Unknown error"
                Log.d("UserRepository4", "User insert: $e")

            } finally {
                isLoading.value = false
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            userRepository.signOut()
        }
    }

    fun insertUser(Name:String,Role:String) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                userRepository.insertUser(Name,Role)
                isLoading.value = false
            } catch (e: Exception) {
                errorMessage.value = e.message ?: "Unknown error"
                isLoading.value = false
            }
        }
    }


    var uuid = mutableStateOf<String?>(null)
        private set

    fun getUUID() {
        viewModelScope.launch {
            try {
                isLoading.value = true
                userRepository.getUUID()
                isLoading.value = false
            } catch (e: Exception) {
                errorMessage.value = e.message ?: "Unknown error"
                isLoading.value = false
            }
        }
    }

    fun getRol(opcion: MutableState<String>) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                userRepository.getRol(opcion)
                isLoading.value = false
            } catch (e: Exception) {
                errorMessage.value = e.message ?: "Unknown error"
                isLoading.value = false
            }

        }
    }

    fun getName() {
        viewModelScope.launch {
            try {
                isLoading.value = true
                userName.value = userRepository.getName()
                isLoading.value = false
            } catch (e: Exception) {
                errorMessage.value = e.message ?: "Unknown error"
                isLoading.value = false
            }

        }
    }

    fun getPhone() {
        viewModelScope.launch {
            try {
                isLoading.value = true
                userRepository.getPhone()
                isLoading.value = false
            } catch (e: Exception) {
                errorMessage.value = e.message ?: "Unknown error"
                isLoading.value = false
            }

        }
    }

    // Estado mutable que almacena las horas programadas
    var scheduledDates = mutableStateOf<Map<LocalDate, List<LocalTime>>?>(null)
        private set

    fun getHoras() {
        Log.d("UsedrRepository3", "Buenos dias desde UserViewModel")

        viewModelScope.launch {
            try {
                isLoading.value = true
                Log.d("UsedrRepository4", "Vamos a calar el Get Horas, isLoading Value = ${isLoading.value}")

                // Llamada a la función getHoras del repositorio
                val horasResult = userRepository.getHoras()

                // Actualizar el estado con los resultados obtenidos
                scheduledDates.value = horasResult

                Log.d("UsedrRepository4", "Ya se calo el get horas, horas: $horasResult")
                isLoading.value = false
            } catch (e: Exception) {
                errorMessage.value = e.message ?: "Unknown error"
                isLoading.value = false
            }
            }
        }

}
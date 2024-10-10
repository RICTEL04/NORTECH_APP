package viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jan.supabase.gotrue.SessionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import model.Caso
import model.Noticias
import model.Solicitud
import model.UserRepository
import java.time.LocalDate
import java.time.LocalTime

class UserViewModel( private val userRepository: UserRepository) : ViewModel(){
    val userName = mutableStateOf("")
    var TituloNot = mutableStateOf("")
    val DescripcionNot = mutableStateOf("")
    val imagenfilename = mutableStateOf("")
    val URL_Not = mutableStateOf("")
    val id_not = mutableStateOf("")

    val rol = mutableStateOf("")
    val email = mutableStateOf("")

    // Estado de sesión observable por la UI
    val sessionState: StateFlow<SessionStatus> = userRepository.sessionState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SessionStatus.LoadingFromStorage
    )

    // Estados adicionales para controlar la UI durante el proceso de autenticación
    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf("")

    fun signIn(email: String, password: String) {
        isLoading.value = true
        errorMessage.value = ""
        viewModelScope.launch {
            try {
                userRepository.signIn(email, password)
            } catch (e: Exception) {
                errorMessage.value = e.message ?: "Unknown error"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun signUp(email: String, password: String, name: String, role: String) {
        isLoading.value = true
        errorMessage.value = ""
        viewModelScope.launch {
            try {
                userRepository.signUp(email, password, name, role)
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


    fun getRol() {
        viewModelScope.launch {
            try {
                isLoading.value = true
                rol.value= userRepository.getRol().toString()
                isLoading.value = false
            } catch (e: Exception) {
                errorMessage.value = e.message ?: "Unknown error"
                isLoading.value = false
            }

        }
    }

    fun getEmail() {
        viewModelScope.launch {
            try {
                isLoading.value = true
                email.value= userRepository.getEmail().toString()
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
                userName.value = userRepository.getName().toString()
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

    val uploadSuccess = mutableStateOf(false)
    val uploadErrorMessage = mutableStateOf("")

        fun uploadFile(uri: Uri, fileName: String, context: Context, titulo: String, des: String) {
            viewModelScope.launch {
                try {
                    val success = userRepository.uploadFile(uri, fileName, context, titulo, des)
                    if (success) {
                        uploadSuccess.value = true
                    } else {
                        uploadErrorMessage.value = "Error al subir el archivo"
                    }
                } catch (e: Exception) {
                    uploadErrorMessage.value = e.message ?: "Error desconocido"
                }
            }
        }

    private val _noticias = MutableStateFlow<List<Noticias>>(emptyList())
    val noticias: StateFlow<List<Noticias>> = _noticias
    val isLoading2 = MutableStateFlow(false)
    val errorMessage2 = MutableStateFlow("")
    fun fetchNoticias() {
        viewModelScope.launch {
            try {
                isLoading2.value = true
                val fetchedNoticias = userRepository.getAllNoticias()
                if (fetchedNoticias != null) {
                    // Actualiza el flujo de noticias con los datos obtenidos
                    _noticias.value = fetchedNoticias
                    Log.d("UserViewModel", "Noticias fetched: $fetchedNoticias")
                }
            } catch (e: Exception) {
                errorMessage2.value = e.message ?: "Error desconocido"
            } finally {
                isLoading2.value = false
            }
        }
    }

    val isLoadingUpdate = mutableStateOf(false)
    val errorMessageUpdate = mutableStateOf("")

    fun updateimgNoticia(id: String, nombreImg: String) {
        viewModelScope.launch {
            try {
                isLoadingUpdate.value = true
                userRepository.UpdateImagenNoticia(id, nombreImg)
                Log.d("UserViewModel", "Noticia actualizada con ID: $id")
            } catch (e: Exception) {
                errorMessageUpdate.value = e.message ?: "Error desconocido al actualizar noticia"
            } finally {
                isLoadingUpdate.value = false
            }
        }
    }

    val isLoadingUpdate01 = mutableStateOf(false)
    val errorMessageUpdate01 = mutableStateOf("")

    fun updatetitNoticia(id: String, tit: String) {
        viewModelScope.launch {
            try {
                isLoadingUpdate01.value = true
                userRepository.UpdateTituloNoticia(id, tit)
                Log.d("UserViewModel", "Noticia actualizada con ID: $id")
            } catch (e: Exception) {
                errorMessageUpdate01.value = e.message ?: "Error desconocido al actualizar noticia"
            } finally {
                isLoadingUpdate01.value = false
            }
        }
    }

    val isLoadingUpdate02 = mutableStateOf(false)
    val errorMessageUpdate02 = mutableStateOf("")

    fun updatedesNoticia(id: String, des: String) {
        viewModelScope.launch {
            try {
                isLoadingUpdate02.value = true
                userRepository.UpdateDescipcionNoticia(id, des)
                Log.d("UserViewModel", "Noticia actualizada con ID: $id")
            } catch (e: Exception) {
                errorMessageUpdate02.value = e.message ?: "Error desconocido al actualizar noticia"
            } finally {
                isLoadingUpdate02.value = false
            }
        }
    }

    fun eliminateIMG(nombreImg: String) {
        viewModelScope.launch {
            try {
                isLoadingUpdate.value = true
                userRepository.EliminateImg(nombreImg)
            } catch (e: Exception) {
                errorMessageUpdate.value = e.message ?: "Error desconocido al eliminar Imagen"
            } finally {
                isLoadingUpdate.value = false
            }
        }
    }

    fun eliminateNoticia(id: String, nombreImg: String) {
        viewModelScope.launch {
            try {
                isLoadingUpdate.value = true
                userRepository.EliminateNoticia(id, nombreImg)
                Log.d("UserViewModel", "Noticia eliminada con ID: $id")
            } catch (e: Exception) {
                errorMessageUpdate.value = e.message ?: "Error desconocido al eliminar noticia"
            } finally {
                isLoadingUpdate.value = false
            }
        }
    }

    val uploadSuccess3 = mutableStateOf(false)
    val uploadErrorMessage3 = mutableStateOf("")

    fun JUSTuploadFile(uri: Uri, fileName: String, context: Context) {
        viewModelScope.launch {
            try {
                val success = userRepository.JUSTuploadFile(uri, fileName, context)
                if (success) {
                    uploadSuccess3.value = true
                } else {
                    uploadErrorMessage3.value = "Error al subir el archivo"
                }
            } catch (e: Exception) {
                uploadErrorMessage3.value = e.message ?: "Error desconocido"
            }
        }
    }

    // Estado mutable que almacena las horas programadas
    var allSolicitudes = mutableStateOf< Map<String, List<Solicitud>>?>(null)
        private set

    fun getAllSolicitudes(){

        Log.d("UserViewModelGetAllSolicitudes", "getAllSolicitudes desde viewModel")

        viewModelScope.launch {
            try {
                isLoading.value = true
                Log.d("UserViewModelGetAllSolicitudes", "Vamos a calar el GetAllSolicitudes, isLoading Value = ${isLoading.value}")

                // Llamada a la función getHoras del repositorio
                val allSolicitudesResult = userRepository.getAllSolicitudes()

                // Actualizar el estado con los resultados obtenidos
                allSolicitudes.value = allSolicitudesResult

                Log.d("UsedrRepository4", "Ya se calo el get horas, horas: $allSolicitudesResult")
                isLoading.value = false
            } catch (e: Exception) {
                errorMessage.value = e.message ?: "Unknown error"
                isLoading.value = false
            }
        }
    }

    // Estado para almacenar las solicitudes filtradas por id_Cliente
    var solicitudesByCliente = mutableStateOf<List<Solicitud>?>(null)
        private set

    // Función para obtener solicitudes por cliente ID
    fun getSolicitudesByClienteId() {

        Log.d("UserViewModelGetSolicitudesByClienteId", "Obteniendo solicitudes para cliente con ID desde viewModel")

        viewModelScope.launch {
            try {
                isLoading.value = true
                Log.d("UserViewModelGetSolicitudesByClienteId", "Vamos a intentar obtener las solicitudes, isLoading Value = ${isLoading.value}")

                // Llamada a la función getSolicitudesByClienteId del repositorio
                val solicitudesResult = userRepository.getSolicitudesByClienteId()

                // Actualizar el estado con los resultados obtenidos
                solicitudesByCliente.value = solicitudesResult

                Log.d("UserViewModelGetSolicitudesByClienteId", "Solicitudes obtenidas: $solicitudesResult")
                isLoading.value = false
            } catch (e: Exception) {
                errorMessage.value = e.message ?: "Unknown error"
                isLoading.value = false
                Log.e("UserViewModelGetSolicitudesByClienteId", "Error obteniendo solicitudes: ${e.message}")
                isLoading.value = false
            }
        }
    }


    fun insertSolicitud(anio: String, mes: String, dia: String, hora: String, descripcion: String, motivo: String) {
        viewModelScope.launch {
            try {
                Log.d("UserViewModel InsertSolicitud", "Vamos a insertar solicitud desde UserViewModel")
                isLoading.value = true
                userRepository.insertSolicitud(anio,mes,dia,hora,descripcion,motivo)
                isLoading.value = false
                Log.d("UserViewModel InsertSolicitud", "Usuario Insertado Desde View Model")
            } catch (e: Exception) {
                errorMessage.value = e.message ?: "Unknown error"
                isLoading.value = false
            }
            }
        }

    var nameByID = mutableStateOf<String?>(null)
    fun getNameById(id : String) {
        Log.d("UserRepository2", "Antes de viewModelgetNameID")
        viewModelScope.launch {
            try {
                isLoading.value = true
                Log.d("UserRepository2", "Antes de getName viewModel")
                nameByID.value = userRepository.getNameById(id).toString()
                isLoading.value = false
            } catch (e: Exception) {
                errorMessage.value = e.message ?: "Unknown error"
                isLoading.value = false
            }

            }
        }

    // Estado mutable que almacena las horas programadas
    private val _casos = MutableStateFlow<List<Caso>>(emptyList())
    val casos: StateFlow<List<Caso>> = _casos
    val idcaso = mutableStateOf("")
    val tipocaso = mutableStateOf("")
    val fechaCreadoCaso = mutableStateOf("")
    val nucCaso = mutableStateOf("")
    val descCaso = mutableStateOf("")
    val caroJudicialCaso = mutableStateOf("")
    val carpInvestCaso = mutableStateOf("")
    val accFVCaso = mutableStateOf("")
    val passFVCaso = mutableStateOf("")
    val fiscalTituCaso = mutableStateOf("")
    val unidadInvestCaso = mutableStateOf("")
    val carpDriveCaso = mutableStateOf("")
    val dirUIcaso = mutableStateOf("")
    val ALIAScaso = mutableStateOf("")
    val namecaso = mutableStateOf("")

    // Añade esta función en tu UserRepository
    suspend fun getAllCasos(){
        viewModelScope.launch {
            try {
                isLoading2.value = true
                val fetchedcasos = userRepository.getAllCasos()
                if (fetchedcasos != null) {
                    // Actualiza el flujo de noticias con los datos obtenidos
                    _casos.value = fetchedcasos
                    Log.d("UserViewModelCasos", "Noticias fetched: $fetchedcasos")
                }
            } catch (e: Exception) {
                errorMessage2.value = e.message ?: "Error desconocido"
            } finally {
                isLoading2.value = false
            }
        }
    }

    val uploadSuccessaddCaso = mutableStateOf(false)
    val uploadErrorMessageaddCaso = mutableStateOf("")

    fun InsertCaso(description: String,
                   tipo: String,
                   nuc: String,
                   activo: Boolean,
                   carp_Judicial: String,
                   carp_Investigacion: String,
                   acc_FV: String,
                   pass_FV: String,
                   fiscal_Titular: String,
                   unidad_Invest: String,
                   dir_UI: String,
                   carpeta_drive: String,
                   ALIAS: String,
                   name: String) {
        viewModelScope.launch {
            try {
                val success = userRepository.insertCaso(description, tipo, nuc, activo, carp_Judicial, carp_Investigacion, acc_FV, pass_FV, fiscal_Titular, unidad_Invest, dir_UI, carpeta_drive, ALIAS, name)
                if (success) {
                    uploadSuccessaddCaso.value = true
                } else {
                    uploadErrorMessageaddCaso.value = "Error al subir el archivo"
                }
            } catch (e: Exception) {
                uploadErrorMessageaddCaso.value = e.message ?: "Error desconocido"
            }
        }
    }
}
package viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jan.supabase.gotrue.SessionStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import model.Caso
import model.Noticias
import model.Solicitud
import model.Cita
import model.FileResponse
import model.Notification
import model.UserRepository
import model.estudianteabogado
import java.time.LocalDate
import java.time.LocalTime

class UserViewModel( private val userRepository: UserRepository) : ViewModel() {
    val userName = mutableStateOf("")
    var TituloNot = mutableStateOf("")
    val DescripcionNot = mutableStateOf("")
    val imagenfilename = mutableStateOf("")
    val URL_Not = mutableStateOf("")
    val id_not = mutableStateOf("")
    val Genero = mutableStateOf("")
    val FechaNacimiento = mutableStateOf("")
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

    fun signUp(email: String, password: String, name: String, role: String, fechanac: kotlinx.datetime.LocalDate, Genero: String) {
        isLoading.value = true
        errorMessage.value = ""
        viewModelScope.launch {
            try {
                userRepository.signUp(email, password, name, role, fechanac, Genero)
            } catch (e: Exception) {
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

    fun insertNotifications(user_id: String, body: String){
        Log.d("UserViewModel insertNotifications", "Antes del launch")
        viewModelScope.launch {
            try {
                isLoading.value = true
                Log.d("UserViewModel insertNotifications", "Antes del insert")
                userRepository.insertNotificacion(user_id,body)
                Log.d("UserViewModel insertNotifications", "YA se hizo el insert")
                isLoading.value = false
            } catch (e: Exception) {
                errorMessage.value = e.message ?: "Unknown error"
                isLoading.value = false
            }
        }
    }

    var userNotifications = mutableStateOf<List<Notification>>(emptyList())
        private set

    fun getUserNotifications(){
        Log.d("UserViewModel getUserNotifications", "Antes del launch")
        viewModelScope.launch {
            try {
                isLoading.value = true
                Log.d("UserViewModel getUserNotifications", "Antes de Antes de getUsersNot")

                // Llamada a la función getHoras del repositorio
                userNotifications.value = userRepository.getUserNotifications()
                Log.d("UserViewModel getUserNotifications", "Despues de getUsersNot")

                isLoading.value = false
            } catch (e: Exception) {
                errorMessage.value = e.message ?: "Unknown error"
                isLoading.value = false
            }
        }
    }

    var usersNotificationData = mutableStateOf<Map<String, String?>>(emptyMap())
        private set

    fun getUsersNotificationData(){
        Log.d("UserViewModel getUsersNotificationData", "Antes del launch")
        viewModelScope.launch {
            try {
                isLoading.value = true
                Log.d("UserViewModel getUsersNotificationData", "Antes de Antes de getUsersNot")

                // Llamada a la función getHoras del repositorio
                usersNotificationData.value = userRepository.getUsersNotificationData()
                Log.d("UserViewModel getUsersNotificationData", "Despues de getUsersNot")

                isLoading.value = false
            } catch (e: Exception) {
                errorMessage.value = e.message ?: "Unknown error"
                isLoading.value = false
            }
        }
    }

    fun insertCita(
        anio: String,
        mes: String,
        dia: String,
        hora: String,
        descripcion: String,
        motivo: String,
        nombreUsuario: String,
        user_id: String,
        estado: String,
        abogado: String
    ) {
        Log.d("UserViewModel insertCita", "Antes del launch")
        viewModelScope.launch {
            try {
                isLoading.value = true
                Log.d("UserViewModel insertCita", "Antes del insert")
                userRepository.insertCita(
                    anio,
                    mes,
                    dia,
                    hora,
                    descripcion,
                    motivo,
                    nombreUsuario,
                    user_id,
                    estado,
                    abogado
                )
                Log.d("UserViewModel insertCita", "YA se hizo el insert")
                isLoading.value = false
            } catch (e: Exception) {
                errorMessage.value = e.message ?: "Unknown error"
                isLoading.value = false
            }
        }
    }

    var citasUsuario = mutableStateOf<List<Cita>?>(emptyList())
        private set

    suspend fun getCitasUsuario(){
        Log.d("UserViewModel getCitasUsuario", "Antes de viewModelScope")
        viewModelScope.launch {
            try {
                isLoading.value = true
                Log.d("UserViewModel getCitasUsuario", "Antes de Antes de getCitasUsuario")

                // Llamada a la función getHoras del repositorio
                citasUsuario.value = userRepository.getCitasUsuario()
                Log.d("UserViewModel getCitasUsuario", "Despues de getCitasUsuario")

                isLoading.value = false
            } catch (e: Exception) {
                errorMessage.value = e.message ?: "Unknown error"
                isLoading.value = false
            }
        }
    }

    var allCitasPendientes = mutableStateOf<List<Cita>?>(null)
        private set

    suspend fun getAllCitas() {
        Log.d("ViewModelGetAllCitasPendiente", "GetAllCitas desde ViewModel antes del scope")
        viewModelScope.launch {
            try {
                isLoading.value = true
                Log.d("ViewModelGetAllCitasPendiente", "Antes de llamar al repository")

                // Llamada a la función getHoras del repositorio
                val allCitasResult = userRepository.getAllCitasPendientes()
                Log.d("ViewModelGetAllCitasPendiente", "Despues de llamar al repository")
                // Actualizar el estado con los resultados obtenidos
                allCitasPendientes.value = allCitasResult

                Log.d(
                    "ViewModelGetAllCitasPendiente",
                    "Ya se calo el getCitas ${allCitasPendientes.value}"
                )
                isLoading.value = false
            } catch (e: Exception) {
                errorMessage.value = e.message ?: "Unknown error"
                isLoading.value = false
            }
        }

    }

    fun updateEstadoCita(id_cita: String, estado_cita: String, motivo_cancelar: String) {
        Log.d("ViewModelUpdateEstadoCita", "updateEstadoCita desde ViewModel antes del scope")
        viewModelScope.launch {
            try {
                isLoading.value = true
                Log.d("ViewModelUpdateEstadoCita", "Antes de llamar al repository")

                // Llamada a la función getHoras del repositorio
                userRepository.updateEstadoCita(id_cita, estado_cita, motivo_cancelar)
                Log.d("ViewModelUpdateEstadoCita", "Despues de llamar al repository")

                Log.d("ViewModelUpdateEstadoCita", "Ya se calo el updateEstadoCitas ")
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
                rol.value = userRepository.getRol().toString()
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
                email.value = userRepository.getEmail().toString()
                isLoading.value = false
            } catch (e: Exception) {
                errorMessage.value = e.message ?: "Unknown error"
                isLoading.value = false
            }

        }
    }

    fun getEmailbyID() {
        viewModelScope.launch {
            try {
                isLoading.value = true
                email.value = userRepository.getEmailbyID().toString()
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

    fun getGenero() {
        viewModelScope.launch {
            try {
                isLoading.value = true
                Genero.value = userRepository.getGenero().toString()
                isLoading.value = false
            } catch (e: Exception) {
                errorMessage.value = e.message ?: "Unknown error"
                isLoading.value = false
            }

        }
    }

    fun getFechaNacimiento() {
        viewModelScope.launch {
            try {
                isLoading.value = true
                FechaNacimiento.value = userRepository.getFechaNacimiento().toString()
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
        Log.d("UserGetHoras", "Buenos dias desde UserViewModel")

        viewModelScope.launch {
            try {
                isLoading.value = true
                Log.d(
                    "UserGetHoras",
                    "Vamos a calar el Get Horas, isLoading Value = ${isLoading.value}"
                )

                // Llamada a la función getHoras del repositorio
                val horasResult = userRepository.getHoras()

                // Actualizar el estado con los resultados obtenidos
                scheduledDates.value = horasResult

                Log.d("UserGetHoras", "Ya se calo el get horas, horas: $horasResult")
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
    var allSolicitudes = mutableStateOf<Map<String, List<Solicitud>>?>(null)
        private set

    fun getAllSolicitudes() {

        Log.d("UserViewModelGetAllSolicitudes", "getAllSolicitudes desde viewModel")

        viewModelScope.launch {
            try {
                isLoading.value = true
                Log.d(
                    "UserViewModelGetAllSolicitudes",
                    "Vamos a calar el GetAllSolicitudes, isLoading Value = ${isLoading.value}"
                )

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
    private val _solicitudesByCliente = MutableStateFlow<List<Solicitud>?>(null)
    val solicitudesByCliente: StateFlow<List<Solicitud>?> = _solicitudesByCliente
    val isLoadingGetSolById = MutableStateFlow(false)

    // Función para obtener solicitudes por cliente ID
    fun getSolicitudesByClienteId() {
        Log.d("UserGetSolById", "Obteniendo solicitudes para cliente con ID desde viewModel")

        viewModelScope.launch {
            try {
                isLoadingGetSolById.value = true
                Log.d(
                    "UserGetSolById",
                    "Vamos a intentar obtener las solicitudes, isLoading Value = ${isLoadingGetSolById.value}"
                )

                // Llamada a la función getSolicitudesByClienteId del repositorio
                val solicitudesResult = userRepository.getSolicitudesByClienteId()

                // Actualizar el estado con los resultados obtenidos
                _solicitudesByCliente.value = solicitudesResult
                Log.d("UserGetSolById", "SolicitudesByCliente: ${_solicitudesByCliente.value}")
                Log.d("UserGetSolById", "Solicitudes obtenidas: $solicitudesResult")
            } catch (e: Exception) {
                // Manejo del error y actualización del mensaje de error
                errorMessage.value = e.message ?: "Unknown error"
                Log.e("UserGetSolById", "Error obteniendo solicitudes: ${e.message}")
            } finally {
                // Esto se ejecuta tanto si ocurre un error como si no
                isLoadingGetSolById.value = false
                Log.d(
                    "UserGetSolById",
                    "Finalizó la obtención de solicitudes, isLoadingGetSolById = false"
                )
            }
        }
    }


    fun insertSolicitud(
        anio: String,
        mes: String,
        dia: String,
        hora: String,
        descripcion: String,
        motivo: String
    ) {
        viewModelScope.launch {
            try {
                Log.d(
                    "UserViewModel InsertSolicitud",
                    "Vamos a insertar solicitud desde UserViewModel"
                )
                isLoading.value = true
                userRepository.insertSolicitud(anio, mes, dia, hora, descripcion, motivo)
                isLoading.value = false
                Log.d("UserViewModel InsertSolicitud", "Usuario Insertado Desde View Model")
            } catch (e: Exception) {
                errorMessage.value = e.message ?: "Unknown error"
                isLoading.value = false
            }
        }
    }

    fun updateSolicitud(id_solicitud: String, descripcion: String, motivo: String) {
        Log.d("UserUpdateSol", "Actualizar solicitud, desde viewModel")

        viewModelScope.launch {
            try {
                isLoadingGetSolById.value = true
                Log.d("UserUpdateSol", "Antes de actualizar solicitud desde viewModel")

                userRepository.updateSolicitud(id_solicitud, descripcion, motivo)
                Log.d("UserUpdateSol", "Despues de hacer el update desde viewModel")

            } catch (e: Exception) {
                // Manejo del error y actualización del mensaje de error
                errorMessage.value = e.message ?: "Unknown error"
                Log.e("UserUpdateSol", "Error actualizando solicitudes: ${e.message}")
            } finally {
                // Esto se ejecuta tanto si ocurre un error como si no
                isLoadingGetSolById.value = false
                Log.d(
                    "UserUpdateSol",
                    "Finalizó la actualozacion de solicitudes, isLoadingGetSolById = false"
                )
            }
        }
    }

    fun updateEstadoSolicitud(id_solicitud: String, estado_solicitud: String) {
        viewModelScope.launch {
            try {
                isLoadingGetSolById.value = true
                Log.d("UserDeleteEstSol", "Antes de cambiar estado de solicitud desde viewModel")

                userRepository.updateEstadoSolicitud(id_solicitud, estado_solicitud)
                Log.d("UserDeleteEstSol", "Despues de cambiar estado de solicitud desde viewModel")

            } catch (e: Exception) {
                // Manejo del error y actualización del mensaje de error
                errorMessage.value = e.message ?: "Unknown error"
                Log.e("UserDeleteEstSol", "Error cambiar est solicitud: ${e.message}")
            } finally {
                // Esto se ejecuta tanto si ocurre un error como si no
                isLoadingGetSolById.value = false
                Log.d("UserDeleteEstSol", "Finalizó cambiar est de solicitudes")
            }
        }
    }

    fun deleteSolicitud(id: String) {

        viewModelScope.launch {
            try {
                isLoadingGetSolById.value = true
                Log.d("UserDeleteSol", "Antes de borrar solicitud desde viewModel")

                userRepository.deleteSolicitud(id)
                Log.d("UserDeleteSol", "Despues de hacer el borrar solicitud desde viewModel")

            } catch (e: Exception) {
                // Manejo del error y actualización del mensaje de error
                errorMessage.value = e.message ?: "Unknown error"
                Log.e("UserDeleteSol", "Error borrar solicitudes: ${e.message}")
            } finally {
                // Esto se ejecuta tanto si ocurre un error como si no
                isLoadingGetSolById.value = false
                Log.d(
                    "UserDeleteSol",
                    "Finalizó la borrar de solicitudes, isLoadingGetSolById = false"
                )
            }
        }

    }

    var nameByID = mutableStateOf<String?>(null)
    fun getNameById(id: String) {
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

    val estadocaso = mutableStateOf(true)

    // Añade esta función en tu UserRepository
    suspend fun getAllCasos() {
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

    fun InsertCaso(
        description: String,
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
        name: String
    ) {
        viewModelScope.launch {
            try {
                val success = userRepository.insertCaso(
                    description,
                    tipo,
                    nuc,
                    activo,
                    carp_Judicial,
                    carp_Investigacion,
                    acc_FV,
                    pass_FV,
                    fiscal_Titular,
                    unidad_Invest,
                    dir_UI,
                    carpeta_drive,
                    ALIAS,
                    name
                )
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

    val uploadSuccessfile = mutableStateOf(false)
    val uploadErrorMessagefile = mutableStateOf("")

    fun filecaso(uri: Uri, fileName: String, context: Context) {
        viewModelScope.launch {
            try {
                val success = userRepository.uploadFileToSupabase(uri, fileName, context)
                if (success) {
                    uploadSuccessfile.value = true
                } else {
                    uploadErrorMessagefile.value = "Error al subir el archivo"
                }
            } catch (e: Exception) {
                uploadErrorMessagefile.value = e.message ?: "Error desconocido"
            }
        }
    }

    val uploadSuccessaddFileCaso = mutableStateOf(false)
    val uploadErrorMessageaddFileCaso = mutableStateOf("")

    fun InsertFileCaso(
        id: String,
        filename: String
    ) {
        viewModelScope.launch {
            try {
                val success = userRepository.insertFileCaso(id, filename)
                if (success) {
                    uploadSuccessaddFileCaso.value = true
                } else {
                    uploadErrorMessageaddFileCaso.value = "Error al subir el archivo"
                }
            } catch (e: Exception) {
                uploadErrorMessageaddFileCaso.value = e.message ?: "Error desconocido"
            }
        }
    }

    private val _filecaso = MutableStateFlow<List<String>>(emptyList())
    val filecaso: StateFlow<List<String>> = _filecaso
    val isLoadingshowfilecaso = MutableStateFlow(false)
    val errorMessageshowfilecaso = MutableStateFlow("")
    fun fetchFileNamesByCasoId(id: String) {
        viewModelScope.launch {
            try {
                isLoadingshowfilecaso.value = true
                val fetchedFilecaso = userRepository.getFileNamesByCasoId(id)
                if (fetchedFilecaso != null) {
                    // Actualiza el flujo de noticias con los datos obtenidos
                    _filecaso.value = fetchedFilecaso
                    Log.d("UserViewModel", "Noticias fetched: $fetchedFilecaso")
                }
            } catch (e: Exception) {
                errorMessageshowfilecaso.value = e.message ?: "Error desconocido"
            } finally {
                isLoadingshowfilecaso.value = false
            }
        }
    }

    val isLoadingUpdatecaso = mutableStateOf(false)
    val errorMessageUpdatecaso = mutableStateOf("")

    fun updateCaso(
        id: String, description: String,
        tipo: String,
        nuc: String,
        carp_Judicial: String,
        carp_Investigacion: String,
        acc_FV: String,
        pass_FV: String,
        fiscal_Titular: String,
        unidad_Invest: String,
        dir_UI: String,
        carpeta_drive: String,
        ALIAS: String,
        name: String
    ) {
        viewModelScope.launch {
            try {
                isLoadingUpdatecaso.value = true
                userRepository.UpdateCaso(
                    id,
                    description,
                    tipo,
                    nuc,
                    carp_Judicial,
                    carp_Investigacion,
                    acc_FV,
                    pass_FV,
                    fiscal_Titular,
                    unidad_Invest,
                    dir_UI,
                    carpeta_drive,
                    ALIAS,
                    name
                )
                Log.d("UserViewModel", "Caso actualizado con ID: $id")
            } catch (e: Exception) {
                errorMessageUpdatecaso.value = e.message ?: "Error desconocido al actualizar caso"
            } finally {
                isLoadingUpdatecaso.value = false
            }
        }
    }

    val isLoadingUpdateEstadocaso = mutableStateOf(false)
    val errorMessageUpdateEstadocaso = mutableStateOf("")

    fun updateEstadoCaso(id: String, Estado: Boolean) {
        viewModelScope.launch {
            try {
                isLoadingUpdateEstadocaso.value = true
                userRepository.UpdateEstadoCaso(id, Estado)
                Log.d("UserViewModel", "Estado actualizado con ID Caso: $id")
            } catch (e: Exception) {
                errorMessageUpdateEstadocaso.value =
                    e.message ?: "Error desconocido al actualizar estado caso"
            } finally {
                isLoadingUpdateEstadocaso.value = false
            }
        }
    }

    fun eliminateCaso(id: String, filenames: List<String>) {
        viewModelScope.launch {
            try {
                isLoadingUpdate.value = true
                userRepository.EliminateCaso(id, filenames)
                Log.d("UserViewModel", "Caso eliminado con ID: $id")
            } catch (e: Exception) {
                errorMessageUpdate.value = e.message ?: "Error desconocido al eliminar caso"
            } finally {
                isLoadingUpdate.value = false
            }
        }
    }

    fun eliminateFile(id: String, fileName: String) {
        viewModelScope.launch {
            try {
                isLoadingUpdate.value = true
                userRepository.EliminateFile(id, fileName)
                _filecaso.value = _filecaso.value.filter { it != fileName }
                Log.d("UserViewModel", "Caso eliminado con ID: $id")
            } catch (e: Exception) {
                errorMessageUpdate.value = e.message ?: "Error desconocido al eliminar caso"
            } finally {
                isLoadingUpdate.value = false
            }
        }
    }

    val nameEstudiante = mutableStateOf("")
    fun InsertEstudiante(
        Matricula : String, Semestre : String, Correo_alumno : String) {
        viewModelScope.launch {
            try {
                nameEstudiante.value = userRepository.getNameByMail(Correo_alumno).toString()
                Log.d("UserViewModelID", "ID email: ${nameEstudiante.value}")
                val success = userRepository.insertEstudiante(Matricula, Semestre, Correo_alumno, nameEstudiante.value)
                delay(500L)
                fetchEstudiantes()
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

    val Matricula = mutableStateOf("")
    val Semestre = mutableStateOf("")
    val CorreoAlumno = mutableStateOf("")
    val nameAlumno = mutableStateOf("")

    private val _estudiantes = MutableStateFlow<List<estudianteabogado>>(emptyList())
    val estudiantes: StateFlow<List<estudianteabogado>> = _estudiantes
    val isLoadingshowestudiantes = MutableStateFlow(false)
    val errorMessageshowestudiantes = MutableStateFlow("")
    fun fetchEstudiantes() {
        viewModelScope.launch {
            try {
                isLoadingshowestudiantes.value = true
                val fetchedEstudiantes = userRepository.getAllEstudiantes()
                if (fetchedEstudiantes != null) {
                    // Actualiza el flujo de noticias con los datos obtenidos
                    _estudiantes.value = fetchedEstudiantes
                    Log.d("UserViewModel", "Noticias fetched: $fetchedEstudiantes")
                }
            } catch (e: Exception) {
                errorMessageshowestudiantes.value = e.message ?: "Error desconocido"
            } finally {
                isLoadingshowestudiantes.value = false
            }
        }
    }

    val FechaNacimientoEstudiante = mutableStateOf("")
    val FilepicEstudiante = mutableStateOf("")
    val GeneroEstudiante = mutableStateOf("")
    fun GetEstudianteFFpicG( Correo_alumno : String) {
        viewModelScope.launch {
            try {
                FechaNacimientoEstudiante.value = userRepository.getFechaNacimientoByMail(Correo_alumno).toString()
                FilepicEstudiante.value = userRepository.getprofpicByMail(Correo_alumno).toString()
                GeneroEstudiante.value = userRepository.getGeneroByMail(Correo_alumno).toString()
            } catch (e: Exception) {
                uploadErrorMessageaddCaso.value = e.message ?: "Error desconocido"
            }
        }
    }
    fun EliminateStudent( Correo_alumno : String) {
        viewModelScope.launch {
            try {
                userRepository.EliminateEstudiante(Correo_alumno)
            } catch (e: Exception) {
                uploadErrorMessageaddCaso.value = e.message ?: "Error desconocido"
            }
        }
    }

    val PicActual = mutableStateOf("")
    fun uploadProfilePicture( uri: Uri, fileName: String, context: Context) {
        viewModelScope.launch {
            try {
                PicActual.value = userRepository.getprofpicByID().toString()
                userRepository.uploadProfilePicture(uri, fileName, context, PicActual.value)
                delay(3000L)
                PicActual.value = userRepository.getprofpicByID().toString()
            } catch (e: Exception) {
                uploadErrorMessageaddCaso.value = e.message ?: "Error desconocido"
            }
        }
    }

    fun GetActualProfilePicture() {
        viewModelScope.launch {
            try {
                PicActual.value = userRepository.getprofpicByID().toString()
            } catch (e: Exception) {
                uploadErrorMessageaddCaso.value = e.message ?: "Error desconocido"
            }
        }
    }

    val MatriculaCuenta = mutableStateOf("")
    val SemestreCuenta = mutableStateOf("")
    val IDAbogado = mutableStateOf("")
    val NombreAbogadoCuenta = mutableStateOf("")
    fun GetMatSemAbogado() {
        viewModelScope.launch {
            try {
                email.value = userRepository.getEmailbyID().toString()
                MatriculaCuenta.value = userRepository.getMatriculaByMail(email.value).toString()
                SemestreCuenta.value = userRepository.getSemestreByMail(email.value).toString()
                IDAbogado.value = userRepository.getIDAbogadoByMail(email.value).toString()
                NombreAbogadoCuenta.value = userRepository.getNameById(IDAbogado.value).toString()
            } catch (e: Exception) {
                uploadErrorMessageaddCaso.value = e.message ?: "Error desconocido"
            }
        }
    }


}
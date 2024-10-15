package model

import android.content.Context
import android.net.Uri
import android.util.Log
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.SessionStatus
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

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


    suspend fun signIn(userEmail: String, userPassword: String) {
        supabase.auth.signInWith(Email) {
            email = userEmail
            password = userPassword
        }
        Log.d("Gei", "ante")
        val id = supabase.auth.retrieveUserForCurrentSession(updateSession = true).id
        Log.d("Gei", "depue")
        insertIdDispositivo(id)

    }

    suspend fun signUp(userEmail: String, userPassword: String, name: String, rol: String, fechanac: kotlinx.datetime.LocalDate, Genero: String) {
        supabase.auth.signUpWith(Email) {
            email = userEmail
            password = userPassword
        }
        Log.d("UserRepository3", "InsertUser: $name $rol")
        insertUser(name, rol, userEmail, fechanac, Genero)
    }

    suspend fun signOut() {
        Log.d("Gei2", "ante")
        var id = supabase.auth.retrieveUserForCurrentSession(updateSession = true).id
        Log.d("Gei2", "depue")
        supabase.auth.signOut()
        deleteIDDispositivo(id)
    }

    suspend fun insertUser(Name: String, Role: String, usermail: String, fechanac: kotlinx.datetime.LocalDate, Genero: String) {
        Log.d("UserRepository3", "InsertUser: entrada")
        // Se pueden agregar mas variables pero hay que actualizar UserViewModel.kt
        delay(3000)
        var id = supabase.auth.retrieveUserForCurrentSession(updateSession = true).id
        val user = User(
            Nombre = Name, Rol = Role,
            id =id, Correo = usermail, FechaNacimiento = fechanac, Genero = Genero, profpic = "", INE = "")
        Log.d("UserRepository3", "User insert 1: ${user.Rol}")
        supabase.from("usuarios2").insert(user) // Use the ViewModel to add user
        Log.d("UserRepository3", "User insert: $user")
        insertIdDispositivo(id)
    }

    suspend fun insertIdDispositivo(id: String){

        val a: ApplicationClass = ApplicationClass()
        var currentUserId: String? = ApplicationClass.userId
        Log.d("updateIdDispositivo", "User insert 1: ${currentUserId}")

        supabase.from("usuarios2").update(
            {
                set("id_dispositivo", currentUserId)
            }
        ) {
            filter {
                User::id eq id
            }
        }

    }

    suspend fun deleteIDDispositivo(id: String){
        supabase.from("usuarios2").update(
            {
                set("id_dispositivo", "")
            }
        ) {
            filter {
                User::id eq id
            }
        }
    }

    suspend fun insertNotificacion(user_id: String, body: String){

        Log.d("UserRepository insertNotificacion", "Apunto de crear nptificacion")
        val notification = Notification(
            id = UUID.randomUUID().toString(),
            user_id = user_id,
            created_at = ZonedDateTime.now(java.time.ZoneOffset.UTC).toString(),
            body = body
        )

        Log.d("UserRepository insertNotificacion", "Antes de insertar notificacion")
        supabase.from("notifications").insert(notification) // Use the ViewModel to add user
        delay(1000)
        Log.d("UserRepository insertNotification", "notificacion ya insertada")
    }

    suspend fun getUserNotifications(): List<Notification> {
        try {
            // 1. Obtener el ID del usuario actual
            Log.e("UserRepository getUserNotifications", "Antes de obtener el id")
            val id = supabase.auth.retrieveUserForCurrentSession(updateSession = true).id

            // 2. Hacer la consulta en la tabla "notifications"
            Log.e("UserRepository getUserNotifications", "Antes de obtener result")
            val result = supabase.from("notifications")
                .select(columns = Columns.list("id", "user_id", "created_at", "body")) {
                    filter { Notification::user_id eq id }
                }

            // 3. Obtener el JSON de la respuesta

            val jsonString = result.data

            // 4. Deserializar el JSON en una lista de notificaciones
            Log.e("UserRepository getUserNotifications", "Antes de deserializar")
            val notificationList: List<Notification> = Json.decodeFromString(jsonString ?: "[]")

            // 5. Ordenar las notificaciones por fecha (del más antiguo al más reciente)
            Log.e("UserRepository getUserNotifications", "Antes del ordenamiento")
            val sortedNotifications = notificationList.sortedBy { notification ->
                // Convertir el campo 'created_at' a ZonedDateTime para comparar
                ZonedDateTime.parse(notification.created_at, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            }

            Log.d("UserRepository getUserNotifications", "Sorted Notifications: $sortedNotifications")

            return sortedNotifications
        } catch (e: Exception) {
            Log.e("UserRepository getUserNotifications", "Error processing JSON: ${e.message}")
            return emptyList() // Devolver lista vacía en caso de error
        }
    }


    suspend fun getUsersNotificationData(): Map<String, String?> {

        Log.d("UserRepository getUsersNotificationData", "Antes del result")
        val result = supabase.from("usuarios2").select(columns = Columns.list("id", "id_dispositivo"))

        // Verificamos si hay datos disponibles
        val jsonString = result.data

        Log.d("UserRepository getUsersNotificationData", "Raw JSON: $jsonString")
        return try {
            // Deserializamos el JSON en una lista de pares (id, id_dispositivo)
            val usersList: List<UserNotificationData> = Json.decodeFromString(jsonString ?: "[]")

            // Convertimos la lista a un Map usando `associate`
            val usersMap = usersList.associate { it.id to it.id_dispositivo }

            Log.d("UserRepository getUsersNotificationData", "Processed Map: $usersMap")
            usersMap // Retornamos el Map con los datos procesados
        } catch (e: Exception) {
            Log.d("UserRepository getUsersNotificationData", "Error processing JSON: ${e.message}")
            emptyMap() // Retornamos un Map vacío en caso de error
        }
    }

    suspend fun insertCita(anio: String, mes: String, dia: String, hora: String, descripcion: String, motivo: String, nombreUsuario: String, user_id: String, estado: String, abogado: String)
    {

        Log.d("UserRepository insertCita", "Antes de obtener el ID")
        var id = supabase.auth.retrieveUserForCurrentSession(updateSession = true).id
        //Log.d("UserRepository InsertSolicitud", "Vamos a insertar solicitud desde UserRepository")
        //var id = supabase.auth.retrieveUserForCurrentSession(updateSession = true).id
        Log.d("UserRepository insertCita", "Apunto de crear cita")
        val cita = Cita(
            id = UUID.randomUUID().toString(),
            anio = anio,
            mes = mes,
            dia = dia,
            hora = hora,
            descripcion = descripcion,
            motivo = motivo,
            nombre_usuario = nombreUsuario,
            user_id = user_id,
            estado = estado,
            abogado = abogado
        )

        Log.d("UserRepository insertCita", "Antes de insertar la cita")
        supabase.from("Citas").insert(cita) // Use the ViewModel to add user
        delay(1000)
        Log.d("UserRepository insertCita", "De huevos ya se inserto la cita")

    }

    suspend fun getCitasUsuario(): List<Cita>
    {
        Log.d("UserRepository getCitasUsuario", "Antes de obtener el ID")
        var id = supabase.auth.retrieveUserForCurrentSession(updateSession = true).id

        val result = supabase.from("Citas").select(columns = Columns.list("id", "anio", "mes", "dia", "hora", "descripcion", "motivo", "user_id", "nombre_usuario", "estado", "abogado"))
        {
            filter {
                or {
                    Cita::estado eq "pendiente"
                    Cita::estado eq "confirmada"
                }
                Cita::user_id eq id
            }
        }
        val jsonString = result.data
        return try {
            // Deserializamos el JSON en una lista de Cita
            val citasList: List<Cita> = Json.decodeFromString(jsonString ?: "[]")
            Log.d("UserRepository getCitasUsuario", "Citas processed: $citasList")
            citasList
        } catch (e: Exception) {
            Log.e("UserRepository getCitasUsuario", "Error processing JSON: ${e.message}")
            emptyList() // Retornar lista vacía en caso de error
        }
    }

    suspend fun getAllCitasPendientes(): List<Cita> {
        Log.d("UserRepositoryGetAllCitasPendientes", "GetAllCitas desde repository")

        val result = supabase.from("Citas").select(columns = Columns.list("id", "anio", "mes", "dia", "hora", "descripcion", "motivo", "user_id", "nombre_usuario", "estado", "abogado"))
        {
            filter {
                or {
                    Cita::estado eq "pendiente"
                    Cita::estado eq "confirmada"
                }
            }
        }

        // Procesamos el JSON obtenido
        val jsonString = result.data
        Log.d("UserRepositoryGetAllCitasPendientes", "Citas raw JSON: $jsonString")

        return try {
            // Deserializamos el JSON en una lista de Cita
            val citasList: List<Cita> = Json.decodeFromString(jsonString ?: "[]")
            Log.d("UserRepositoryGetAllCitasPendientes", "Citas processed: $citasList")
            citasList
        } catch (e: Exception) {
            Log.e("UserRepositoryGetAllCitasPendientes", "Error processing JSON: ${e.message}")
            emptyList() // Retornar lista vacía en caso de error
        }
    }

    suspend fun updateEstadoCita(id_cita: String,  estado_cita: String, motivo_cancelar: String)
    {
        Log.d("UserUpdateEstCita", "Antes de hacer estado de la cita desde USerRepository")
        supabase.from("Citas").update(
            {
                Cita::estado setTo estado_cita
                Cita::motivo_cancelar setTo motivo_cancelar
            }
        ) {
            filter {
                Cita::id eq id_cita
            }
        }
        Log.d("UserUpdateEstCita", "Despues de hacer estado de la cita" )
    }

    // Obtiene el ID de la sesion.
    suspend fun getUUID() {
        val result = supabase.auth.retrieveUserForCurrentSession(updateSession = true)?.id
        Log.d("UserRepository", "UUID fetched: $result")
    }

    // Obtiene el ID de la sesion.
    suspend fun getRol(): String {
        val id = supabase.auth.retrieveUserForCurrentSession(updateSession = true)?.id
        val result = supabase.from("usuarios2").select(columns = Columns.list("Rol")){
            filter {
                User::id eq id
            }
        }
        return result.data.replace(Regex("[^0-9]"), "")
        Log.d("UserRepository2", "Rol fetched: ${result.data}")
    }

    suspend fun getEmail(): String? {
        val mail = supabase.auth.retrieveUserForCurrentSession(updateSession = true).email
        Log.d("UserRepository7", "User insert: $mail")
        return mail
    }

    suspend fun getEmailbyID(): String? {
        var id = supabase.auth.retrieveUserForCurrentSession(updateSession = true).id
        Log.d("UserRepository2", "Id: $id")
        // Realiza la consulta a la tabla 'usuarios_' filtrando por el ID del usuario
        val result = supabase.from("usuarios2").select(columns = Columns.list("Correo")) {
            filter {
                eq("id",id)
            }
        }

        val finalResult = result.data.toString()  // Convertir el resultado a String si no lo es
        Log.d("UserRepository2", "Raw data fetched: $finalResult")

        // Usar regex para extraer el campo "Nombre"
        finalResult?.let {
            val regex = """"Correo":"(.*?)"""".toRegex()  // Expresión regular para capturar el valor de "Nombre"
            val matchResult = regex.find(finalResult)     // Busca coincidencia

            // Si se encuentra el valor, retorna el nombre
            val nombre = matchResult?.groupValues?.get(1)
            Log.d("UserRepository2", "Name fetched: $nombre")
            return nombre
        }

        // Si no se encuentra nada, retorna null
        Log.d("UserRepository2", "No Name found")
        return null
    }


    // Get name
    suspend fun getName(): String? {
        val id = supabase.auth.retrieveUserForCurrentSession(updateSession = true)?.id

        // Realiza la consulta a la tabla 'usuarios_' filtrando por el ID del usuario
        val result = supabase.from("usuarios2").select(columns = Columns.list("Nombre")) {
            filter {
                User::id eq id
            }
        }

        val finalResult = result.data.toString()  // Convertir el resultado a String si no lo es
        Log.d("UserRepository2", "Raw data fetched: $finalResult")

        // Usar regex para extraer el campo "Nombre"
        finalResult?.let {
            val regex = """"Nombre":"(.*?)"""".toRegex()  // Expresión regular para capturar el valor de "Nombre"
            val matchResult = regex.find(finalResult)     // Busca coincidencia

            // Si se encuentra el valor, retorna el nombre
            val nombre = matchResult?.groupValues?.get(1)
            Log.d("UserRepository2", "Name fetched: $nombre")
            return nombre
        }

        // Si no se encuentra nada, retorna null
        Log.d("UserRepository2", "No Name found")
        return null
    }

    suspend fun getGenero(): String? {
        val id = supabase.auth.retrieveUserForCurrentSession(updateSession = true).id

        // Realiza la consulta a la tabla 'usuarios_' filtrando por el ID del usuario
        val result = supabase.from("usuarios2").select(columns = Columns.list("Genero")) {
            filter {
                eq("id",id)
            }
        }

        val finalResult = result.data.toString()  // Convertir el resultado a String si no lo es
        Log.d("UserRepository2", "Raw data fetched: $finalResult")

        // Usar regex para extraer el campo "Nombre"
        finalResult?.let {
            val regex = """"Genero":"(.*?)"""".toRegex()  // Expresión regular para capturar el valor de "Nombre"
            val matchResult = regex.find(finalResult)     // Busca coincidencia

            // Si se encuentra el valor, retorna el nombre
            val genero = matchResult?.groupValues?.get(1)
            Log.d("UserRepository2", "Name fetched: $genero")
            return genero
        }

        // Si no se encuentra nada, retorna null
        Log.d("UserRepository2", "No Genero found")
        return null
    }

    suspend fun getFechaNacimiento(): String? {
        val id = supabase.auth.retrieveUserForCurrentSession(updateSession = true).id

        // Realiza la consulta a la tabla 'usuarios_' filtrando por el ID del usuario
        val result = supabase.from("usuarios2").select(columns = Columns.list("FechaNacimiento")) {
            filter {
                eq("id",id)
            }
        }

        val finalResult = result.data.toString()  // Convertir el resultado a String si no lo es
        Log.d("UserRepository2", "Raw data fetched: $finalResult")

        // Usar regex para extraer el campo "Nombre"
        finalResult?.let {
            val regex = """"FechaNacimiento":"(.*?)"""".toRegex()  // Expresión regular para capturar el valor de "Nombre"
            val matchResult = regex.find(finalResult)     // Busca coincidencia

            // Si se encuentra el valor, retorna el nombre
            val FechaNacimiento = matchResult?.groupValues?.get(1)
            Log.d("UserRepository2", "Name fetched: $FechaNacimiento")
            return FechaNacimiento
        }

        // Si no se encuentra nada, retorna null
        Log.d("UserRepository2", "No FechaNacimiento found")
        return null
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
        Log.d("UserGetHoras", "Buenos días desde UserRepository")
        val id = supabase.auth.retrieveUserForCurrentSession(updateSession = true).id
        Log.d("UserGetHoras", "Ya jalamos el ID del supabase chavales")

        val result = supabase.from("Citas").select(columns = Columns.list("anio", "mes", "dia", "hora")) {
            filter {
                or {
                    Cita::estado eq "pendiente"
                    Cita::estado eq "confirmada"
                }
            }
        }

        // Procesamos el JSON obtenido
        val jsonString = result.data
        Log.d("UserGetHoras", "Citas raw JSON: $jsonString")

        val timeFormatter = DateTimeFormatter.ofPattern("H:mm")
        val currentDate = LocalDate.now()

        return try {
            Log.d("UserGetHoras", "Antes de deserializar lista")
            // Deserializar el JSON en una lista de citas
            val citas: List<Cita> = Json.decodeFromString(jsonString)
            Log.d("UserGetHoras", "Lista Deserializada")

            // Crear el mapa de fechas programadas
            val scheduledDates = citas.mapNotNull { cita ->
                // Verificar si los campos no son nulos
                val anio = cita.anio?.toIntOrNull()
                val mes = cita.mes?.toIntOrNull()
                val dia = cita.dia?.toIntOrNull()
                val hora = cita.hora

                if (anio != null && mes != null && dia != null && hora != null) {
                    // Crear la fecha y parsear la hora
                    val date = LocalDate.of(anio, mes, dia)
                    val time = LocalTime.parse(hora, timeFormatter)
                    date to time
                } else {
                    null // Omitir las citas con campos nulos o inválidos
                }
            }.groupBy({ it.first }, { it.second })
                .filterKeys { it >= currentDate } // Filtrar solo fechas a partir del día actual

            Log.d("UserGetHoras", "Valores obtenidos")
            Log.d("UserGetHoras", "Scheduled dates map: $scheduledDates")

            // Retornar el mapa
            scheduledDates
        } catch (e: Exception) {
            Log.e("UserGetHoras", "Error procesando el JSON: ${e.message}")
            null // Retornar null si ocurre un error
        }
    }

    suspend fun uploadFile(uri: Uri, fileName: String, context: Context, titulo: String, Des: String): Boolean {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            inputStream?.let {
                supabase.storage.from("imagenes")
                    .upload(fileName, it.readBytes())
                it.close()
                insertNoticia(uri.toString(), titulo, Des, fileName)
            }
            true
        } catch (e: Exception) {
            Log.e("UserRepository", "File upload error: ${e.message}")
            false
        }
    }

    suspend fun JUSTuploadFile(uri: Uri, fileName: String, context: Context): Boolean {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            inputStream?.let {
                supabase.storage.from("imagenes")
                    .upload(fileName, it.readBytes())
                it.close()
            }
            true
        } catch (e: Exception) {
            Log.e("UserRepository", "File upload error: ${e.message}")
            false
        }
    }

    suspend fun insertNoticia(imageUrl: String, titulo: String, des: String, nombreimg: String) {
        try {
            // Genera un UUID para el id
            val id = UUID.randomUUID().toString()
            // Crea la noticia con el id generado
            val noticia = Noticias(
                id = id, // Asigna el id generado
                Image_URL = "https://wlrjyngxgupgafqzyxvs.supabase.co/storage/v1/object/public/imagenes/$nombreimg",
                Titulo = titulo,
                Descripcion = des,
                NombreImg = nombreimg
            )
            // Inserta la noticia en la base de datos
            supabase.from("Noticias").insert(noticia)
        } catch (e: Exception) {
            Log.e("UserRepository", "Error al insertar noticia: ${e.message}")
        }
    }


    // Añade esta función en tu UserRepository
    suspend fun getAllNoticias(): List<Noticias>? {
        return try {
            // Realiza la consulta a la tabla "Noticias" incluyendo el id
            val result = supabase.from("Noticias")
                .select(columns = Columns.list("id", "Image_URL", "Titulo", "Descripcion", "NombreImg"))

            // Deserializa el resultado en una lista de objetos Noticias
            val noticiasList: List<Noticias> = Json.decodeFromString(result.data)

            Log.d("UserRepository", "Noticias fetched: $noticiasList")
            noticiasList
        } catch (e: Exception) {
            Log.e("UserRepository", "Error fetching noticias: ${e.message}")
            null // Retorna null si ocurre algún error
        }
    }


    suspend fun UpdateImagenNoticia(id: String, nombreimg: String) {
        try {
            val result = supabase.from("Noticias").update(
                {
                    set("Image_URL", "https://wlrjyngxgupgafqzyxvs.supabase.co/storage/v1/object/public/imagenes/$nombreimg")
                    set("NombreImg", nombreimg)
                }
            ) {
                filter {
                    eq("id",id)
                }
            }
            Log.d("UserRepository", "Noticia actualizada: $result")
        } catch (e: Exception) {
            Log.e("UserRepository", "Error al actualizar noticia: ${e.message}")
        }
    }

    suspend fun UpdateTituloNoticia(id: String, titulo: String) {
        try {
            val result = supabase.from("Noticias").update(
                {
                    set("Titulo", titulo)
                }
            ) {
                filter {
                    eq("id",id)
                }
            }
            Log.d("UserRepository", "Noticia actualizada: $result")
        } catch (e: Exception) {
            Log.e("UserRepository", "Error al actualizar noticia: ${e.message}")
        }
    }

    suspend fun UpdateDescipcionNoticia(id: String, des: String) {
        try {
            val result = supabase.from("Noticias").update(
                {
                    set("Descripcion", des)
                }
            ) {
                filter {
                    eq("id",id)
                }
            }
            Log.d("UserRepository", "Noticia actualizada: $result")
        } catch (e: Exception) {
            Log.e("UserRepository", "Error al actualizar noticia: ${e.message}")
        }
    }

    suspend fun EliminateImg(nombreimg: String) {
        try {
            val bucket = supabase.storage.from("imagenes")
            bucket.delete(nombreimg)
        } catch (e: Exception) {
            Log.e("UserRepository", "Error al eliminar noticia: ${e.message}")
        }
    }


    suspend fun EliminateNoticia(id: String, nombreimg: String) {
        try {
            // Intenta eliminar la noticia directamente por su ID
            val result = supabase.from("Noticias").delete{
                filter{
                    eq("id",id)
                }
            }
            val bucket = supabase.storage.from("imagenes")
            bucket.delete(nombreimg)
        } catch (e: Exception) {
            Log.e("UserRepository", "Error al eliminar noticia: ${e.message}")
        }
    }

    suspend fun getImageUrl(noticiaId: String): String? {
        return try {
            val result = supabase.from("Noticias").select(columns = Columns.list("Image_URL")) {
                filter {
                    Noticias::NombreImg eq noticiaId
                }
            }

            // Deserializamos el resultado
            val imageUrl = result.data.toString()

            // Extraemos el valor de "Image_URL" usando Regex
            val regex = """"Image_URL":"(.*?)"""".toRegex()
            val matchResult = regex.find(imageUrl)
            val imageUrlValue = matchResult?.groupValues?.get(1)

            imageUrlValue
        } catch (e: Exception) {
            Log.e("UserRepository", "Error fetching image URL: ${e.message}")
            null
        }
    }

    //Nuevos elementos de UserRepository:
    //Obtiene todas las solicitudes
    suspend fun getAllSolicitudes(): Map<String, List<Solicitud>>? {
        Log.d("UserRepository", "Obteniendo solicitudes")

        // Obtener el ID del usuario actual
        val id = supabase.auth.retrieveUserForCurrentSession(updateSession = true)?.id

        // Obtener los datos de la tabla Solicitud
        val result = supabase.from("Solicitud").select(columns = Columns.list("id", "id_cliente", "anio", "mes", "dia", "hora", "descripcion", "motivo", "nombre", "estado_solicitud")){
            filter {
                Solicitud::estado_solicitud eq "1"  // Make sure id_Cliente matches the column name in your database
            }
        }


        // Procesar el JSON obtenido
        val jsonString = result.data
        Log.d("UserRepository", "Solicitudes raw JSON: $jsonString")

        return try {
            Log.d("UserRepository", "Antes de deserializar lista")

            // Configurar el deserializador para ignorar claves desconocidas
            val json = Json { ignoreUnknownKeys = true }

            // Deserializar el JSON en una lista de Solicitudes
            val solicitudes: List<Solicitud> = json.decodeFromString(jsonString)
            Log.d("UserRepository", "Lista de solicitudes deserializada")
            Log.d("UserRepository", "Lista de solicitudes deserializada: $solicitudes")

            // Ordenar las solicitudes por fecha
            val solicitudesOrdenadas = solicitudes.sortedWith(compareBy<Solicitud> {
                it.anio.toInt()  // Convertir a Int para comparación correcta
            }.thenBy {
                it.mes.toInt()   // Convertir a Int para comparación correcta
            }.thenBy {
                it.dia.toInt()   // Convertir a Int para comparación correcta
            }.thenBy {
                it.hora          // Asumiendo que hora es en formato HH:mm
            })

            // Agrupar las solicitudes por id_Cliente
            val solicitudesAgrupadas = solicitudesOrdenadas.groupBy { it.id_cliente }

            Log.d("UserRepository", "Solicitudes agrupadas: $solicitudesAgrupadas")

            // Retornar el mapa con las solicitudes agrupadas
            solicitudesAgrupadas
        } catch (e: Exception) {
            Log.e("UserRepository", "Error procesando el JSON: ${e.message}")
            null // Retornar null si ocurre un error
        }
    }

    suspend fun getSolicitudesByClienteId(): List<Solicitud>? {
        val id = supabase.auth.retrieveUserForCurrentSession(updateSession = true)?.id
        Log.d("UserGetSolById", "USERREP Obteniendo solicitudes para el cliente con ID: $id")

        return try {
            // Obtener los datos de la tabla Solicitud filtrados por id_Cliente usando el filtro
            val result = supabase.from("Solicitud")
                .select(columns = Columns.list("id", "id_cliente", "anio", "mes", "dia", "hora", "descripcion", "motivo", "nombre", "estado_solicitud")) {
                    filter {
                        Solicitud::id_cliente eq id  // Make sure id_Cliente matches the column name in your database
                        Solicitud::estado_solicitud eq "1"
                    }
                }

            Log.d("UserGetSolById", "Result by id: $result")

            // Procesar el JSON obtenido
            val jsonString = result.data
            Log.d("UserGetSolById", "Solicitudes raw JSON by id: $jsonString")

            // Configurar el deserializador para ignorar claves desconocidas
            val json = Json { ignoreUnknownKeys = true }

            // Deserializar el JSON en una lista de Solicitudes
            val solicitudes: List<Solicitud> = json.decodeFromString(jsonString)
            Log.d("UserGetSolById", "Lista de solicitudes deserializada para id_cliente $id: $solicitudes")

            // Ordenar las solicitudes por fecha
            val solicitudesOrdenadas = solicitudes.sortedWith(compareBy<Solicitud> {
                it.anio.toInt()
            }.thenBy {
                it.mes.toInt()
            }.thenBy {
                it.dia.toInt()
            }.thenBy {
                it.hora })

            // Retornar la lista de solicitudes ordenadas para ese cliente
            solicitudesOrdenadas
        } catch (e: Exception) {
            Log.e("UserGetSolById", "Error procesando el JSON: ${e.message}")
            null // Retornar null si ocurre un error
        }
    }

    suspend fun insertSolicitud(anio: String, mes: String, dia: String, hora: String, descripcion: String, motivo: String) {
        // Se pueden agregar mas variables pero hay que actualizar UserViewModel.kt
        var id = supabase.auth.retrieveUserForCurrentSession(updateSession = true).id
        //Log.d("UserRepository InsertSolicitud", "Vamos a insertar solicitud desde UserRepository")
        //var id = supabase.auth.retrieveUserForCurrentSession(updateSession = true).id
        Log.d("UserRepository InsertSolicitud", "Vamos a crear solicitud desde UserRepository")
        val solicitud = Solicitud(
            id = UUID.randomUUID().toString(),
            id_cliente = id,
            nombre = getName().toString(),
            anio = anio,
            mes = mes,
            dia = dia,
            hora = hora,
            descripcion = descripcion,
            motivo = motivo
        )
        Log.d("UserRepository InsertSolicitud", "Vamos a insertar solicitud desde UserRepository")
        supabase.from("Solicitud").insert(solicitud) // Use the ViewModel to add user
        Log.d("UserRepository InsertSolicitud", "De huevos ya se inserto la solicitud")

    }

    suspend fun updateSolicitud(id_solicitud: String, descripcion: String, motivo: String)
    {
        Log.d("UserUpdateSol", "Antes de hacer la solicitud desde USerRepository")
        var id = supabase.auth.retrieveUserForCurrentSession(updateSession = true).id
        supabase.from("Solicitud").update(
            {
                Solicitud::descripcion setTo descripcion
                Solicitud::motivo setTo motivo

            }
        ) {
            filter {
                Solicitud::id eq id_solicitud
            }
        }
        Log.d("UserUpdateSol", "Despues de hacer la solicitud desde UserRepository")
    }

    suspend fun updateEstadoSolicitud(id_solicitud: String,  estado_solicitud: String)
    {
        Log.d("UserUpdateEstSol", "Antes de hacer estado de la solicitud desde USerRepository")
        supabase.from("Solicitud").update(
            {
                Solicitud::estado_solicitud setTo estado_solicitud
            }
        ) {
            filter {
                Solicitud::id eq id_solicitud
            }
        }
        Log.d("UserUpdateEstSol", "Despues de hacer estado de la solicitud desde UserRepository")
    }

    // Funcion para borrar la solicitud
    suspend fun deleteSolicitud(id : String)
    {
        Log.d("UserDeleteSol", "Usar")
        supabase.from("Solicitud").delete {
            filter {
                Solicitud::id eq id;
            }
        }

    }

    // Get name
    suspend fun getNameById(user_id: String): String? {

        // Realiza la consulta a la tabla 'usuarios_' filtrando por el ID del usuario
        Log.d("UserRepository2", "Antes de result: ")
        val result = supabase.from("usuarios2").select(columns = Columns.list("Nombre")) {
            filter {
                eq("id",user_id)
            }
        }

        Log.d("UserRepository2", "Result Obtenido")
        val finalResult = result.data.toString()  // Convertir el resultado a String si no lo es
        Log.d("UserRepository2", "Final Result Obtenido")

        // Usar regex para extraer el campo "Nombre"
        finalResult?.let {
            val regex = """"Nombre":"(.*?)"""".toRegex()  // Expresión regular para capturar el valor de "Nombre"
            val matchResult = regex.find(finalResult)     // Busca coincidencia

            // Si se encuentra el valor, retorna el nombre
            val nombre = matchResult?.groupValues?.get(1)
            Log.d("UserRepository2", "Name fetched: $nombre")
            return nombre
        }

        // Si no se encuentra nada, retorna null
        Log.d("UserRepository2", "No Name found")
        return null
        }

    //Obtiene todas los Casos
    suspend fun getAllCasos(): List<Caso>? {
        return try {
            // Realiza la consulta a la tabla "Noticias"
            val result = supabase.from("Caso")
                .select(columns = Columns.list("id", "description", "tipo", "nuc", "activo", "fecha_Creado", "carp_Judicial", "carp_Investigacion", "acc_FV", "pass_FV", "fiscal_Titular", "unidad_Invest", "dir_UI", "carpeta_drive", "ALIAS", "nombre"))

            // Deserializa el resultado en una lista de objetos Noticias
            val noticiasList: List<Caso> = Json.decodeFromString(result.data)

            Log.d("UserRepositoryCasos", "casos fetched: $noticiasList")
            noticiasList
        } catch (e: Exception) {
            Log.e("UserRepository", "Error fetching casos: ${e.message}")
            null // Retorna null si ocurre algún error
        }
    }

    suspend fun insertCaso(description: String,
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
                           name: String): Boolean {
        return try {
            // Genera un UUID para el id
            val id = UUID.randomUUID().toString()
            // fecha
            val fecha_Creado = Clock.System.now()
            // Crea la noticia con el id generado
            val addcaso = Caso(
                id, description, tipo, nuc, activo, fecha_Creado, carp_Judicial, carp_Investigacion, acc_FV, pass_FV, fiscal_Titular, unidad_Invest, dir_UI, carpeta_drive, ALIAS, name
            )
            // Inserta la noticia en la base de datos
            supabase.from("Caso").insert(addcaso)
            true
        } catch (e: Exception) {
            Log.e("UserRepository", "Error al insertar caso: ${e.message}")
            false
        }
    }

    suspend fun uploadFileToSupabase(uri: Uri, fileName: String, context: Context, bucketName: String = "files"): Boolean {
        return try {
            // Abre el InputStream del archivo seleccionado
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                // Lee los bytes del archivo
                val fileBytes = inputStream.readBytes()

                // Intenta subir el archivo al bucket de Supabase
                val response = supabase.storage.from(bucketName)
                    .upload(fileName, fileBytes)

                // Si la subida fue exitosa, no hay error y devolvemos true
                Log.d("Supabase", "File uploaded successfully")
                true
            } ?: throw IllegalArgumentException("No file found at URI: $uri")
        } catch (e: Exception) {
            // Captura cualquier excepción y muestra el error en los logs
            Log.e("UserRepository", "File upload error: ${e.message}")
            false
        }
    }

    suspend fun insertFileCaso(idcaso: String, filename:String): Boolean {
        return try {
            // Crea la noticia con el id generado
            val addfilecaso = casofile(
                filename, idcaso
            )
            // Inserta la noticia en la base de datos
            supabase.from("casofile").insert(addfilecaso)
            true
        } catch (e: Exception) {
            Log.e("UserRepository", "Error al insertar file del caso: ${e.message}")
            false
        }
    }

    suspend fun getFileNamesByCasoId(idcaso: String): List<String> {
        return try {
            // Realiza la consulta en la tabla "casofile" para obtener los filenames relacionados al "IDCASO"
            val response = supabase.from("casofile").select(columns = Columns.list("filename")){
                filter { eq("IDCASO", idcaso) }
            }
            Log.d("UserRepository5", "Noticias fetched2: ${response.data}")
            val fileList: List<FileResponse> = Json.decodeFromString(response.data)
            Log.d("UserRepository5", "Noticias fetched2: ${fileList}")
            val filenames = fileList.map { it.filename }
            Log.d("UserRepository5", "Noticias fetched2: ${filenames}")
            filenames
        } catch (e: Exception) {
            Log.e("UserRepository", "Error al ejecutar la consulta: ${e.message}")
            emptyList()
        }
    }

    suspend fun UpdateCaso(id: String, description: String,
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
                           name: String) {
        try {
            val result = supabase.from("Caso").update(
                {
                    set("description", description)
                    set("tipo", tipo)
                    set("nuc", nuc)
                    set("carp_Judicial", carp_Judicial)
                    set("carp_Investigacion", carp_Investigacion)
                    set("acc_FV", acc_FV)
                    set("pass_FV", pass_FV)
                    set("fiscal_Titular", fiscal_Titular)
                    set("unidad_Invest", unidad_Invest)
                    set("dir_UI", dir_UI)
                    set("carpeta_drive", carpeta_drive)
                    set("ALIAS", ALIAS)
                    set("nombre", name)

                }
            ) {
                filter {
                    eq("id",id)
                }
            }
            Log.d("UserRepository", "Caso actualizado: $result")
        } catch (e: Exception) {
            Log.e("UserRepository", "Error al actualizar Caso: ${e.message}")
        }
    }

    suspend fun UpdateEstadoCaso(id: String, Estado: Boolean) {
        try {
            val result = supabase.from("Caso").update(
                {
                    set("activo", Estado)
                }
            ) {
                filter {
                    eq("id",id)
                }
            }
            Log.d("UserRepository", "Caso actualizado: $result")
        } catch (e: Exception) {
            Log.e("UserRepository", "Error al actualizar Caso: ${e.message}")
        }
    }

    suspend fun EliminateCaso(id: String, filenames: List<String>) {
        try {
            // Intenta eliminar la noticia directamente por su ID
            val result = supabase.from("Caso").delete{
                filter{
                    eq("id",id)
                }
            }
            val result2 = supabase.from("casofile").delete{
                filter{
                    eq("IDCASO",id)
                }
            }
            filenames.forEach { filename ->
                val bucket = supabase.storage.from("files")
                bucket.delete(filename)
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error al eliminar noticia: ${e.message}")
        }
    }

    suspend fun EliminateFile(id: String,nombreimg: String) {
        try {
            val result = supabase.from("casofile").delete{
                filter{
                    eq("IDCASO",id)
                    eq("filename", nombreimg)
                }
            }
            val bucket = supabase.storage.from("files")
            bucket.delete(nombreimg)
        } catch (e: Exception) {
            Log.e("UserRepository", "Error al eliminar noticia: ${e.message}")
        }
    }

    suspend fun getUserIdByEmail(email: String): String {
        val result = supabase.from("usuarios2").select(columns = Columns.list("id")) {
            filter {
                eq("Correo", email)
            }
        }
        return result.data
    }

    // Get name
    suspend fun getNameByMail(mail: String): String? {

        // Realiza la consulta a la tabla 'usuarios_' filtrando por el ID del usuario
        val result = supabase.from("usuarios2").select(columns = Columns.list("Nombre")) {
            filter {
                eq("Correo",mail)
            }
        }

        val finalResult = result.data.toString()  // Convertir el resultado a String si no lo es
        Log.d("UserRepository2", "Raw data fetched: $finalResult")

        // Usar regex para extraer el campo "Nombre"
        finalResult?.let {
            val regex = """"Nombre":"(.*?)"""".toRegex()  // Expresión regular para capturar el valor de "Nombre"
            val matchResult = regex.find(finalResult)     // Busca coincidencia

            // Si se encuentra el valor, retorna el nombre
            val nombre = matchResult?.groupValues?.get(1)
            Log.d("UserRepository2", "Name fetched: $nombre")
            return nombre
        }

        // Si no se encuentra nada, retorna null
        Log.d("UserRepository2", "No Name found")
        return null
    }

    suspend fun insertEstudiante(Matricula : String, Semestre : String, Correo_alumno : String, name : String): Boolean {
        return try {
            val id = supabase.auth.retrieveUserForCurrentSession(updateSession = true).id
            val addestudiante = estudianteabogado(id, Matricula, Semestre, Correo_alumno, name)
            supabase.from("estudianteabogado").insert(addestudiante)
            Log.d("UserViewModelID", "ID email: ${addestudiante}")
            val result = supabase.from("usuarios2").update(
                {
                    set("Rol", "3")
                }
            ) {
                filter {
                    eq("Correo",Correo_alumno)
                }
            }
            true
        } catch (e: Exception) {
            Log.e("UserRepository", "Error al insertar caso: ${e.message}")
            false
        }
    }

    //Obtiene todas los Casos
    suspend fun getAllEstudiantes(): List<estudianteabogado>? {
        return try {
            // Realiza la consulta a la tabla "Noticias"
            val result = supabase.from("estudianteabogado")
                .select(columns = Columns.list("Id_Abogado", "Matricula", "Semestre", "Correo_alumno", "name"))

            // Deserializa el resultado en una lista de objetos Noticias
            val EstudiantesList: List<estudianteabogado> = Json.decodeFromString(result.data)

            Log.d("UserRepositoryCasos", "casos fetched: $EstudiantesList")
            EstudiantesList
        } catch (e: Exception) {
            Log.e("UserRepository", "Error fetching casos: ${e.message}")
            null // Retorna null si ocurre algún error
        }
    }

    suspend fun getGeneroByMail(mail: String): String? {

        // Realiza la consulta a la tabla 'usuarios_' filtrando por el ID del usuario
        val result = supabase.from("usuarios2").select(columns = Columns.list("Genero")) {
            filter {
                eq("Correo",mail)
            }
        }

        val finalResult = result.data.toString()  // Convertir el resultado a String si no lo es
        Log.d("UserRepository2", "Raw data fetched: $finalResult")

        // Usar regex para extraer el campo "Nombre"
        finalResult?.let {
            val regex = """"Genero":"(.*?)"""".toRegex()  // Expresión regular para capturar el valor de "Nombre"
            val matchResult = regex.find(finalResult)     // Busca coincidencia

            // Si se encuentra el valor, retorna el nombre
            val genero = matchResult?.groupValues?.get(1)
            Log.d("UserRepository2", "Name fetched: $genero")
            return genero
        }

        // Si no se encuentra nada, retorna null
        Log.d("UserRepository2", "No Genero found")
        return null
    }

    suspend fun getFechaNacimientoByMail(mail: String): String? {

        // Realiza la consulta a la tabla 'usuarios_' filtrando por el ID del usuario
        val result = supabase.from("usuarios2").select(columns = Columns.list("FechaNacimiento")) {
            filter {
                eq("Correo",mail)
            }
        }

        val finalResult = result.data.toString()  // Convertir el resultado a String si no lo es
        Log.d("UserRepository2", "Raw data fetched: $finalResult")

        // Usar regex para extraer el campo "Nombre"
        finalResult?.let {
            val regex = """"FechaNacimiento":"(.*?)"""".toRegex()  // Expresión regular para capturar el valor de "Nombre"
            val matchResult = regex.find(finalResult)     // Busca coincidencia

            // Si se encuentra el valor, retorna el nombre
            val FechaNacimiento = matchResult?.groupValues?.get(1)
            Log.d("UserRepository2", "Name fetched: $FechaNacimiento")
            return FechaNacimiento
        }

        // Si no se encuentra nada, retorna null
        Log.d("UserRepository2", "No FechaNacimiento found")
        return null
    }

    suspend fun getprofpicByMail(mail: String): String? {

        // Realiza la consulta a la tabla 'usuarios_' filtrando por el ID del usuario
        val result = supabase.from("usuarios2").select(columns = Columns.list("profpic")) {
            filter {
                eq("Correo",mail)
            }
        }

        val finalResult = result.data.toString()  // Convertir el resultado a String si no lo es
        Log.d("UserRepository2", "Raw data fetched: $finalResult")

        // Usar regex para extraer el campo "Nombre"
        finalResult?.let {
            val regex = """"profpic":"(.*?)"""".toRegex()  // Expresión regular para capturar el valor de "Nombre"
            val matchResult = regex.find(finalResult)     // Busca coincidencia

            // Si se encuentra el valor, retorna el nombre
            val profilepic = matchResult?.groupValues?.get(1)
            Log.d("UserRepository2", "Name fetched: $profilepic")
            return profilepic
        }

        // Si no se encuentra nada, retorna null
        Log.d("UserRepository2", "No FechaNacimiento found")
        return null
    }

    suspend fun EliminateEstudiante(Correo_alumno: String) {
        try {
            val result = supabase.from("estudianteabogado").delete{
                filter{
                    eq("Correo_alumno",Correo_alumno)
                }
            }
            val result2 = supabase.from("usuarios2").update(
                {
                    set("Rol", "1")
                }
            ) {
                filter {
                    eq("Correo",Correo_alumno)
                }
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error al eliminar noticia: ${e.message}")
        }
    }

    suspend fun uploadProfilePicture(uri: Uri, fileName: String, context: Context, fileNameActual: String): Boolean {
        return try {
            if (fileNameActual!=""){
                val bucket = supabase.storage.from("profile")
                bucket.delete(fileNameActual)
            }
            val id = supabase.auth.retrieveUserForCurrentSession(updateSession = true).id
            val inputStream = context.contentResolver.openInputStream(uri)
            inputStream?.let {
                supabase.storage.from("profile")
                    .upload(fileName, it.readBytes())
                it.close()
            }
            val result2 = supabase.from("usuarios2").update(
                {
                    set("profpic", fileName)
                }
            ) {
                filter {
                    eq("id",id)
                }
            }
            true
        } catch (e: Exception) {
            Log.e("UserRepository", "File upload error: ${e.message}")
            false
        }
    }

    suspend fun getprofpicByID(): String? {
        val id = supabase.auth.retrieveUserForCurrentSession(updateSession = true).id
        // Realiza la consulta a la tabla 'usuarios_' filtrando por el ID del usuario
        val result = supabase.from("usuarios2").select(columns = Columns.list("profpic")) {
            filter {
                eq("id",id)
            }
        }

        val finalResult = result.data.toString()  // Convertir el resultado a String si no lo es
        Log.d("UserRepository2", "Raw data fetched: $finalResult")

        // Usar regex para extraer el campo "Nombre"
        finalResult?.let {
            val regex = """"profpic":"(.*?)"""".toRegex()  // Expresión regular para capturar el valor de "Nombre"
            val matchResult = regex.find(finalResult)     // Busca coincidencia

            // Si se encuentra el valor, retorna el nombre
            val profilepic = matchResult?.groupValues?.get(1)
            Log.d("UserRepository2", "Name fetched: $profilepic")
            return profilepic
        }

        // Si no se encuentra nada, retorna null
        Log.d("UserRepository2", "No FechaNacimiento found")
        return null
    }

    suspend fun getMatriculaByMail(mail: String): String? {

        // Realiza la consulta a la tabla 'usuarios_' filtrando por el ID del usuario
        val result = supabase.from("estudianteabogado").select(columns = Columns.list("Matricula")) {
            filter {
                eq("Correo_alumno",mail)
            }
        }

        val finalResult = result.data.toString()  // Convertir el resultado a String si no lo es
        Log.d("UserRepository2", "Raw data fetched: $finalResult")

        // Usar regex para extraer el campo "Nombre"
        finalResult?.let {
            val regex = """"Matricula":"(.*?)"""".toRegex()  // Expresión regular para capturar el valor de "Nombre"
            val matchResult = regex.find(finalResult)     // Busca coincidencia

            // Si se encuentra el valor, retorna el nombre
            val genero = matchResult?.groupValues?.get(1)
            Log.d("UserRepository2", "Name fetched: $genero")
            return genero
        }

        // Si no se encuentra nada, retorna null
        Log.d("UserRepository2", "No Genero found")
        return null
    }

    suspend fun getSemestreByMail(mail: String): String? {

        // Realiza la consulta a la tabla 'usuarios_' filtrando por el ID del usuario
        val result = supabase.from("estudianteabogado").select(columns = Columns.list("Semestre")) {
            filter {
                eq("Correo_alumno",mail)
            }
        }

        val finalResult = result.data.toString()  // Convertir el resultado a String si no lo es
        Log.d("UserRepository2", "Raw data fetched: $finalResult")

        // Usar regex para extraer el campo "Nombre"
        finalResult?.let {
            val regex = """"Semestre":"(.*?)"""".toRegex()  // Expresión regular para capturar el valor de "Nombre"
            val matchResult = regex.find(finalResult)     // Busca coincidencia

            // Si se encuentra el valor, retorna el nombre
            val genero = matchResult?.groupValues?.get(1)
            Log.d("UserRepository2", "Name fetched: $genero")
            return genero
        }

        // Si no se encuentra nada, retorna null
        Log.d("UserRepository2", "No Genero found")
        return null
    }

    suspend fun getIDAbogadoByMail(mail: String): String? {

        // Realiza la consulta a la tabla 'usuarios_' filtrando por el ID del usuario
        val result = supabase.from("estudianteabogado").select(columns = Columns.list("Id_Abogado")) {
            filter {
                eq("Correo_alumno",mail)
            }
        }

        val finalResult = result.data.toString()  // Convertir el resultado a String si no lo es
        Log.d("UserRepository2", "Raw data fetched: $finalResult")

        // Usar regex para extraer el campo "Nombre"
        finalResult?.let {
            val regex = """"Id_Abogado":"(.*?)"""".toRegex()  // Expresión regular para capturar el valor de "Nombre"
            val matchResult = regex.find(finalResult)     // Busca coincidencia

            // Si se encuentra el valor, retorna el nombre
            val genero = matchResult?.groupValues?.get(1)
            Log.d("UserRepository2", "Name fetched: $genero")
            return genero
        }

        // Si no se encuentra nada, retorna null
        Log.d("UserRepository2", "No Genero found")
        return null
    }

    suspend fun getMailbyID(mail: String): String? {
        val id = supabase.auth.retrieveUserForCurrentSession(updateSession = true).id
        // Realiza la consulta a la tabla 'usuarios_' filtrando por el ID del usuario
        val result = supabase.from("usuarios2").select(columns = Columns.list("Correo")) {
            filter {
                eq("id",id)
            }
        }

        val finalResult = result.data.toString()  // Convertir el resultado a String si no lo es
        Log.d("UserRepository2", "Raw data fetched: $finalResult")

        // Usar regex para extraer el campo "Nombre"
        finalResult?.let {
            val regex = """"Id_Abogado":"(.*?)"""".toRegex()  // Expresión regular para capturar el valor de "Nombre"
            val matchResult = regex.find(finalResult)     // Busca coincidencia

            // Si se encuentra el valor, retorna el nombre
            val genero = matchResult?.groupValues?.get(1)
            Log.d("UserRepository2", "Name fetched: $genero")
            return genero
        }

        // Si no se encuentra nada, retorna null
        Log.d("UserRepository2", "No Genero found")
        return null
    }
}
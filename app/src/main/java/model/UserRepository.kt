package model

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
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
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import okhttp3.internal.wait
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.Duration
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
    }

    suspend fun signUp(userEmail: String, userPassword: String, name: String, rol: String) {
        supabase.auth.signUpWith(Email) {
            email = userEmail
            password = userPassword
        }
        Log.d("UserRepository3", "InsertUser: $name $rol")
        insertUser(name, rol)
    }

    suspend fun signOut() {
        supabase.auth.signOut()
    }

    suspend fun insertUser(Name: String,Role: String) {
        Log.d("UserRepository3", "InsertUser: entrada")
        // Se pueden agregar mas variables pero hay que actualizar UserViewModel.kt
        delay(3000)
        var id = supabase.auth.retrieveUserForCurrentSession(updateSession = true).id
        val user = User(Nombre = Name, Rol = Role,id=id)
        Log.d("UserRepository3", "User insert 1: ${user.Rol}")
        supabase.from("usuarios2").insert(user) // Use the ViewModel to add user
        Log.d("UserRepository3", "User insert: $user")
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
        val result = supabase.from("Solicitud").select(columns = Columns.list("id", "id_cliente", "anio", "mes", "dia", "hora", "descripcion", "motivo", "nombre"))

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
        Log.d("UserRepository", "USERREP Obteniendo solicitudes para el cliente con ID: $id")

        return try {
            // Obtener los datos de la tabla Solicitud filtrados por id_Cliente usando el filtro
            val result = supabase.from("Solicitud")
                .select(columns = Columns.list("id", "id_cliente", "anio", "mes", "dia", "hora", "descripcion", "motivo", "nombre")) {
                    filter {
                        Solicitud::id_cliente eq id  // Make sure id_Cliente matches the column name in your database
                    }
                }

            Log.d("UserRepository2", "Result by id: $result")

            // Procesar el JSON obtenido
            val jsonString = result.data
            Log.d("UserRepository2", "Solicitudes raw JSON by id: $jsonString")

            // Configurar el deserializador para ignorar claves desconocidas
            val json = Json { ignoreUnknownKeys = true }

            // Deserializar el JSON en una lista de Solicitudes
            val solicitudes: List<Solicitud> = json.decodeFromString(jsonString)
            Log.d("UserRepository", "Lista de solicitudes deserializada para id_cliente $id: $solicitudes")

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
            Log.e("UserRepository", "Error procesando el JSON: ${e.message}")
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


    // Get name
    suspend fun getNameById(user_id: String): String? {

        // Realiza la consulta a la tabla 'usuarios_' filtrando por el ID del usuario
        Log.d("UserRepository2", "Antes de result: ")
        val result = supabase.from("usuarios_").select(columns = Columns.list("Nombre")) {
            filter {
                User::id eq user_id
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
}
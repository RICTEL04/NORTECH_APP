package model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Cita(
    val id: String? = null, // Ahora es opcional
    val anio: String? = null,
    val mes: String? = null,
    val dia: String? = null,
    val hora: String? = null,
    val descripcion: String? = null, // Ahora es opcional
    val motivo: String? = null, // Ahora es opcional
    val nombre_usuario: String? = null, // Ahora es opcional
    val user_id: String? = null, // Ahora es opcional
    val estado: String? = null,
    val abogado: String? = null,
    val motivo_cancelar: String?=null
)
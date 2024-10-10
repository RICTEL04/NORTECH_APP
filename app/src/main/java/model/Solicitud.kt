package model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Solicitud(
    val id: String,
    val id_cliente: String,
    val nombre: String,
    val anio: String,
    val mes: String,
    val dia: String,
    val hora: String,
    val descripcion: String,
    val motivo: String
)
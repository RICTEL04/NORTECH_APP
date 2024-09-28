package model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Cita(
    val anio: String,
    val mes: String,
    val dia: String,
    val hora: String
)
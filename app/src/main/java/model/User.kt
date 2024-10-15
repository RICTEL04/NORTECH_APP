package model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val Nombre: String,
    val Rol: String,
    val id: String,
    val Correo: String,
    val FechaNacimiento: LocalDate,
    val Genero: String,
    val profpic: String,
    val INE: String
)
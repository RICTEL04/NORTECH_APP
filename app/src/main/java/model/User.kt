package model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class User(
    val Nombre: String,
    val Rol: String,
    val id: String
)
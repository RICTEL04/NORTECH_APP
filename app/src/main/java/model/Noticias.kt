package model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Noticias(
    val id: String,
    val Image_URL: String,
    val Titulo: String,
    val Descripcion: String,
    val NombreImg: String
)
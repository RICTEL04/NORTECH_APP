package model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class estudianteabogado(
    val Id_Abogado : String,
    val Matricula : String,
    val Semestre : String,
    val Correo_alumno : String,
    val name : String,
)
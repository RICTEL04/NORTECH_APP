package model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable


@Serializable
data class Caso(
    val id: String,
    val description: String,
    val tipo: String,
    val nuc: String,
    val activo: Boolean,
    val fecha_Creado: Instant,
    val carp_Judicial: String,
    val carp_Investigacion: String,
    val acc_FV: String,
    val pass_FV: String,
    val fiscal_Titular: String,
    val unidad_Invest: String,
    val dir_UI: String,
    val carpeta_drive: String,
    val ALIAS: String,
    val nombre: String,
)
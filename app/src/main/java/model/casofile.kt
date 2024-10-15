package model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable


@Serializable
data class casofile(
    val filename: String,
    val IDCASO: String
)
package model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable


@Serializable
data class FileResponse(val filename: String)
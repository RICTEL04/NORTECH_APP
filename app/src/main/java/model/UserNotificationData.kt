package model

import kotlinx.serialization.Serializable
import androidx.compose.runtime.MutableState
import java.util.UUID

@Serializable
data class UserNotificationData(
    val id: String,
    val id_dispositivo: String?
)
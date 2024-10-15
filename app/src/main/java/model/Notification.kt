package model

import kotlinx.serialization.Serializable
import androidx.compose.runtime.MutableState
import java.util.UUID

@Serializable
data class Notification(
    val id: String,
    val user_id : String,
    val created_at : String,
    val body : String
)

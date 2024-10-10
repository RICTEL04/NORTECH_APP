package com.example.nortech_app

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import com.example.nortech_app.Visits.DateAndTimePicker
import com.example.nortech_app.Visits.SelectTimeByDate
import model.ApplicationClass
import okhttp3.internal.format
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

fun sendNotification(mensaje: String) {
    val a: ApplicationClass = ApplicationClass()

    // Now you can access the userId variable
    var currentUserId: String? = ApplicationClass.userId

    Log.d("Prueba", "$currentUserId")
    // Replace with your actual REST API key and App ID
    val apiKey = "NWNhOTI5OGItMzI1OC00Njg2LTkxYWQtM2I3MzNkZjVhOGU1"
    val appId = "f2624cd6-dc6a-45c8-a899-9d44839dd448"

    // Log the start of the function
    Log.d("Notification", "Attempting to send notification")



    // Use coroutine to handle network request in the background
    CoroutineScope(Dispatchers.IO).launch {
        try {
            // The URL for the OneSignal API
            val url = URL("https://api.onesignal.com/notifications")

            // Create the JSON payload
            val payload = JSONObject().apply {
                put("app_id", appId)
                put("contents", JSONObject().put("en", mensaje))
                put("include_player_ids", JSONArray().put(currentUserId))
            }

            Log.d("Notification", "JSON payload created") // Log after payload creation

            // Open the connection
            with(url.openConnection() as HttpURLConnection) {
                // Set up the request headers
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
                setRequestProperty("Authorization", "Basic $apiKey")
                doOutput = true

                // Write the request body (JSON payload)
                OutputStreamWriter(outputStream).apply {
                    write(payload.toString())
                    flush()
                    close()
                }
                Log.d("Notification", "Request sent")

                // Check the response code
                val responseCode = responseCode
                Log.d("Notification", "Response Code: $responseCode")

                // Read the response
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = inputStream.bufferedReader().use { it.readText() }
                    Log.d("Notification", "Response: $response")
                } else {
                    val errorResponse = errorStream.bufferedReader().use { it.readText() }
                    Log.e("Notification", "Error Response: $errorResponse")
                }
            }
        } catch (e: Exception) {
            // Catch and log any exceptions
            withContext(Dispatchers.Main) {
                Log.e("Notification", "Failed to send notification: ${e.message}")
            }
        }
    }
}

fun sendRecordatorio(date: LocalDate, time: LocalTime) {
    val a: ApplicationClass = ApplicationClass()

    // Now you can access the userId variable
    var currentUserId: String? = ApplicationClass.userId


    Log.d("Prueba", "$currentUserId")
    // Replace with your actual REST API key and App ID
    val apiKey = "NWNhOTI5OGItMzI1OC00Njg2LTkxYWQtM2I3MzNkZjVhOGU1"
    val appId = "f2624cd6-dc6a-45c8-a899-9d44839dd448"

    // Log the start of the function
    Log.d("Notification", "Attempting to send notification")



    // Use coroutine to handle network request in the background
    CoroutineScope(Dispatchers.IO).launch {
        try {
            // The URL for the OneSignal API
            val url = URL("https://api.onesignal.com/notifications")

            // VALORES PARA PRUEBAS CAMBIAR POR VARIABLES DATE AND TIME
            val selectedDate = date  // Example selected date
            val selectedTime = time  // Example selected time

// Combine date and time into a LocalDateTime object
            val selectedDateTime = LocalDateTime.of(selectedDate, selectedTime)

// Subtract 30 minutes
            val sendAfterTime = selectedDateTime.minusMinutes(30)

// Format the time to the required format
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val formattedSendAfter = sendAfterTime.format(formatter) + " CST-0600" // Adjust as needed
            Log.d("Fecha", formattedSendAfter)
// Create the JSON payload
            val payload = JSONObject().apply {
                put("app_id", appId)
                put("contents", JSONObject().put("en", "Recordatorio: asesoría en 30 min"))
                put("include_player_ids", JSONArray().put(currentUserId))
                put("send_after", formattedSendAfter)
            }


            Log.d("Notification", "JSON payload created") // Log after payload creation

            // Open the connection
            with(url.openConnection() as HttpURLConnection) {
                // Set up the request headers
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
                setRequestProperty("Authorization", "Basic $apiKey")
                doOutput = true

                // Write the request body (JSON payload)
                OutputStreamWriter(outputStream).apply {
                    write(payload.toString())
                    flush()
                    close()
                }
                Log.d("Notification", "Request sent")

                // Check the response code
                val responseCode = responseCode
                Log.d("Notification", "Response Code: $responseCode")

                // Read the response
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = inputStream.bufferedReader().use { it.readText() }
                    Log.d("Notification", "Response: $response")
                } else {
                    val errorResponse = errorStream.bufferedReader().use { it.readText() }
                    Log.e("Notification", "Error Response: $errorResponse")
                }
            }
        } catch (e: Exception) {
            // Catch and log any exceptions
            withContext(Dispatchers.Main) {
                Log.e("Notification", "Failed to send notification: ${e.message}")
            }
        }
    }
}



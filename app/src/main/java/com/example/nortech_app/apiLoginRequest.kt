package com.example.nortech_app

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

suspend fun fetchApiData(usuario:String, password:String): String {
    val apiUrl = "https://periodic-lumpy-tracker.glitch.me/login"
    val postData = "email=$usuario&password=$password"

    return try {
        val url = URL(apiUrl)
        val connection = withContext(Dispatchers.IO) {
            url.openConnection()
        } as HttpURLConnection

        connection.requestMethod = "POST"
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
        connection.doOutput = true

        // Enviar los datos en el cuerpo de la solicitud POST
        OutputStreamWriter(connection.outputStream).use {
            it.write(postData)
            it.flush()
        }

        val responseCode: Int = connection.responseCode
        Log.d("API_CALL", "Response Code: $responseCode")

        if (responseCode == HttpURLConnection.HTTP_OK) {
            val response = connection.inputStream.bufferedReader().use { it.readText() }
            Log.d("API_CALL", "Response Data: $response")
            response
        } else {
            Log.e("API_CALL", "Error: Unable to fetch data from the API, Response Code: $responseCode")
            "Error: $responseCode"
        }.also {
            connection.disconnect()
        }
    } catch (e: Exception) {
        Log.e("API_CALL", "Exception: ${e.message}", e)
        "Exception: ${e.message}"
    }
}

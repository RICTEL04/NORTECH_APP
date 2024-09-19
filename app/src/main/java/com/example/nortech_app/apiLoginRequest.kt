package com.example.nortech_app

import android.util.Log
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.serializer.KotlinXSerializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

suspend fun register(nombre: String, correo: String, contraseña: String, contraseña2: String): String {
    println("Start")  // Log the result to inspect its structure
    val supabaseUrl = "https://wlrjyngxgupgafqzyxvs.supabase.co"
    val supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Indscmp5bmd4Z3VwZ2FmcXp5eHZzIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MjU0MDI2NzIsImV4cCI6MjA0MDk3ODY3Mn0.atQpLuA-KA50fO3gScLqdRUefhTfv7x8FlVWdjOHAIg"

    return try {
        val supabase = createSupabaseClient(supabaseUrl, supabaseKey) {
            install(Auth)
            install(Postgrest)
            defaultSerializer = KotlinXSerializer()
        }
        // verificar que las contraseñas sean iguales
        if (contraseña == contraseña2){
            val user = supabase.auth.signUpWith(Email) {
                email = correo
                password = contraseña
            }
            return "1"
        }



        return "Error al registrarse"

    } catch (e: Exception) {
        e.printStackTrace()  // Log the error to the console
        return "Error: ${e.message}"
    }
}

suspend fun login(correo: String, contraseña: String): String {
    println("Start")  // Log the result to inspect its structure
    val supabaseUrl = "https://wlrjyngxgupgafqzyxvs.supabase.co"
    val supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Indscmp5bmd4Z3VwZ2FmcXp5eHZzIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MjU0MDI2NzIsImV4cCI6MjA0MDk3ODY3Mn0.atQpLuA-KA50fO3gScLqdRUefhTfv7x8FlVWdjOHAIg"

    return try {
        val supabase = createSupabaseClient(supabaseUrl, supabaseKey) {
            install(Auth)
            install(Postgrest)
            defaultSerializer = KotlinXSerializer()
        }
        // Call the Supabase signIn function
        val result = supabase.auth.signInWith(Email) {
            email = correo
            password = contraseña
        }
        val session = supabase.auth.currentSessionOrNull()?.user

        if (session != null) {
            return session.role.toString() //authenticated
        }

        return "El usuario no existe"

    } catch (e: Exception) {
        e.printStackTrace()  // Log the error to the console
        return "Error: ${e.message}"
    }
}
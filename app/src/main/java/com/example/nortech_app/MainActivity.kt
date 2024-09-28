package com.example.nortech_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.nortech_app.ui.theme.NORTECH_APPTheme
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import model.UserRepository
import viewmodel.UserViewModel

class MainActivity : ComponentActivity() {
    val supabase: SupabaseClient = createSupabaseClient(
        supabaseUrl = "https://wlrjyngxgupgafqzyxvs.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Indscmp5bmd4Z3VwZ2FmcXp5eHZzIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MjU0MDI2NzIsImV4cCI6MjA0MDk3ODY3Mn0.atQpLuA-KA50fO3gScLqdRUefhTfv7x8FlVWdjOHAIg"
    ) {
        install(Auth)
        install(Postgrest)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NORTECH_APPTheme{
                UserAuthScreen(UserViewModel(UserRepository(supabase, CoroutineScope(Dispatchers.IO))))
            }
        }
    }
}
package com.example.app_room

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.app_room.ui.theme.ApproomTheme

// activity principal, es el punto de entrada de la app
class MainActivity : ComponentActivity() {

    // crea el viewmodel la primera vez y lo reutiliza al rotar la pantalla
    private val viewModel: TareaViewModel by viewModels()

    // se ejecuta cuando se abre la activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // la app usa toda la pantalla, incluso detras de las barras del sistema
        enableEdgeToEdge()
        // setcontent define la interfaz con compose, sin xml
        setContent {
            // aplica los colores y textos del tema
            ApproomTheme {
                // muestra la pantalla de tareas
                TareaScreen(viewModel)
            }
        }
    }
}

package com.example.app_room

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// viewmodel: conecta la pantalla con la base de datos
// sobrevive a la rotacion, asi los datos no se pierden al girar el telefono
// androidviewmodel recibe application, que se necesita para abrir la base de datos
class TareaViewModel(application: Application) : AndroidViewModel(application) {

    // dao para hablar con la base de datos
    private val dao = AppDatabase.obtener(application).tareaDao()

    // lista de tareas que observa la pantalla
    // statein convierte el flow en un stateflow, que siempre tiene un valor actual
    // whilesubscribed(5_000) espera 5 segundos antes de dejar de escuchar a room
    // emptylist es el valor inicial mientras llegan los datos
    val tareas: StateFlow<List<Tarea>> = dao.obtenerTodas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // create: crea una tarea nueva
    fun agregar(titulo: String) {
        // quita los espacios del inicio y del final
        val limpio = titulo.trim()
        // si queda vacio no se guarda nada
        if (limpio.isEmpty()) return
        // viewmodelscope lanza la corrutina y la cancela cuando el viewmodel se destruye
        viewModelScope.launch { dao.insertar(Tarea(titulo = limpio)) }
    }

    // update: cambia el titulo de una tarea
    fun editarTitulo(tarea: Tarea, nuevoTitulo: String) {
        val limpio = nuevoTitulo.trim()
        if (limpio.isEmpty()) return
        // copy crea una copia con el titulo nuevo y deja igual lo demas
        viewModelScope.launch { dao.actualizar(tarea.copy(titulo = limpio)) }
    }

    // update: marca o desmarca una tarea como hecha
    fun cambiarHecha(tarea: Tarea, hecha: Boolean) {
        viewModelScope.launch { dao.actualizar(tarea.copy(hecha = hecha)) }
    }

    // delete: borra una tarea
    fun eliminar(tarea: Tarea) {
        viewModelScope.launch { dao.eliminar(tarea) }
    }
}

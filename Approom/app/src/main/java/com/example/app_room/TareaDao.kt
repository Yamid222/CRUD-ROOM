package com.example.app_room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

// dao: aqui se definen las operaciones del crud
// solo se declaran las funciones, room crea el codigo real al compilar usando ksp
@Dao
interface TareaDao {

    // read: trae todas las tareas, las mas nuevas primero
    // el flow avisa solo cada vez que la tabla cambia, por eso la lista se actualiza sola
    // no es suspend porque el flow ya trabaja fuera del hilo principal
    @Query("SELECT * FROM tareas ORDER BY id DESC")
    fun obtenerTodas(): Flow<List<Tarea>>

    // create: agrega una tarea nueva a la tabla
    // suspend hace que corra sin congelar la pantalla
    @Insert
    suspend fun insertar(tarea: Tarea)

    // update: guarda los cambios de una tarea, room la busca por su id
    @Update
    suspend fun actualizar(tarea: Tarea)

    // delete: borra una tarea, room la busca por su id
    @Delete
    suspend fun eliminar(tarea: Tarea)
}

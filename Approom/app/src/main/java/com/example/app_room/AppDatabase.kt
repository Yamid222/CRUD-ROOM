package com.example.app_room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// database: define la base de datos, sus tablas y su version
// si cambia la estructura de tarea hay que subir la version
// exportschema = false evita guardar el esquema en un archivo json
@Database(entities = [Tarea::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // da acceso al dao, room crea la implementacion al compilar
    abstract fun tareaDao(): TareaDao

    // companion object: lo que esta aqui se usa sin crear un objeto de la clase
    companion object {
        // guarda la unica instancia de la base de datos
        // volatile hace que todos los hilos vean siempre el valor actual
        @Volatile
        private var instancia: AppDatabase? = null

        // singleton: abrir la base de datos es costoso, asi que se crea una sola vez
        fun obtener(context: Context): AppDatabase =
            // si ya existe se devuelve, si no se crea
            // synchronized evita que dos hilos la creen al mismo tiempo
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    // se usa el contexto de la app para no retener una activity
                    context.applicationContext,
                    AppDatabase::class.java,
                    // nombre del archivo sqlite en el telefono
                    "tareas.db"
                ).build().also { instancia = it }
            }
    }
}

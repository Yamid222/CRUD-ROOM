package com.example.app_room

import androidx.room.Entity
import androidx.room.PrimaryKey

// entity: esta clase representa una tabla de la base de datos
// cada objeto tarea es una fila de la tabla "tareas"
@Entity(tableName = "tareas")
data class Tarea(
    // llave primaria: identifica cada tarea
    // con id = 0 y autogenerate, sqlite asigna el numero al insertar
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    // texto de la tarea
    val titulo: String,
    // indica si la tarea ya esta hecha, al crearla empieza en false
    val hecha: Boolean = false
)

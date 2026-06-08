package com.example.proyectobryanlaurrabaquioramirez.data

data class Tarea(
    val id: Long = 0,
    val titulo: String,
    val materia: String,
    val descripcion: String,
    val fecha: String,
    val prioridad: String,
    val completada: Boolean = false
)
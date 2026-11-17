package com.example.mistareas

import androidx.lifecycle.ViewModel

/** Guarda la lista mientras la actividad se recrea (por ejemplo, al girar la pantalla). */
class TareasViewModel : ViewModel() {
    val tareas = mutableListOf<Tarea>()
    var siguienteId = 1L
    var vistaCuadricula = false

    fun nuevoId(): Long = siguienteId++
}

package com.example.noticiasfp

import androidx.lifecycle.ViewModel

/** Mantiene las noticias y la sección elegida al girar la pantalla. */
class NoticiasViewModel : ViewModel() {
    val noticias = mutableListOf<Noticia>()
    var seccionActual: Int = R.id.nav_portada
    var siguienteId = 1L

    fun nuevoId(): Long = siguienteId++
}

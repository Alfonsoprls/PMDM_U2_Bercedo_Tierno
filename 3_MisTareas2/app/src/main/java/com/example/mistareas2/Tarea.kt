package com.example.mistareas2

import androidx.annotation.ColorRes
import androidx.annotation.StringRes

enum class Prioridad(@StringRes val nombre: Int, @ColorRes val color: Int) {
    ALTA(R.string.prioridad_alta, R.color.prioridad_alta),
    MEDIA(R.string.prioridad_media, R.color.prioridad_media),
    BAJA(R.string.prioridad_baja, R.color.prioridad_baja)
}

data class Tarea(
    val id: Long,
    var titulo: String,
    var prioridad: Prioridad,
    var completada: Boolean = false,
    var fechaLimite: Long? = null
)

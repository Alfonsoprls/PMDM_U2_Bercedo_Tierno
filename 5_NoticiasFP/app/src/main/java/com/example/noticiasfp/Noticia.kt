package com.example.noticiasfp

import androidx.annotation.ColorRes
import androidx.annotation.StringRes

enum class Categoria(@StringRes val nombre: Int, @ColorRes val color: Int) {
    TECNOLOGIA(R.string.categoria_tecnologia, R.color.chip_tecnologia),
    EMPLEO(R.string.categoria_empleo, R.color.chip_empleo),
    DEPORTES(R.string.categoria_deportes, R.color.chip_deportes),
    CULTURA(R.string.categoria_cultura, R.color.chip_cultura)
}

data class Noticia(
    val id: Long,
    val titulo: String,
    val resumen: String,
    val categoria: Categoria,
    val favorita: Boolean = false
)

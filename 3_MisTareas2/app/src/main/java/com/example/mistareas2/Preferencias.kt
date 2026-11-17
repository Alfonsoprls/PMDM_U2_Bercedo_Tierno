package com.example.mistareas2

import android.content.Context
import androidx.core.content.edit

/** Envoltorio de SharedPreferences: se guarda en shared_prefs/ajustes_mis_tareas.xml */
class Preferencias(contexto: Context) {

    private val prefs = contexto.getSharedPreferences(NOMBRE_FICHERO, Context.MODE_PRIVATE)

    var nombreUsuario: String?
        get() = prefs.getString(CLAVE_NOMBRE, null)
        set(valor) = prefs.edit { putString(CLAVE_NOMBRE, valor) }

    var vistaCuadricula: Boolean
        get() = prefs.getBoolean(CLAVE_CUADRICULA, false)
        set(valor) = prefs.edit { putBoolean(CLAVE_CUADRICULA, valor) }

    companion object {
        const val NOMBRE_FICHERO = "ajustes_mis_tareas"
        private const val CLAVE_NOMBRE = "nombre_usuario"
        private const val CLAVE_CUADRICULA = "vista_cuadricula"
    }
}

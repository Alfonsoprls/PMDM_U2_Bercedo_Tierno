package com.example.mibiblioteca.datos

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Libro::class], version = 1, exportSchema = false)
abstract class BibliotecaDatabase : RoomDatabase() {

    abstract fun libroDao(): LibroDao

    companion object {
        @Volatile
        private var instancia: BibliotecaDatabase? = null

        /** Patrón singleton: una sola instancia de la base de datos para toda la app. */
        fun obtener(contexto: Context): BibliotecaDatabase {
            return instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    contexto.applicationContext,
                    BibliotecaDatabase::class.java,
                    "biblioteca.db"
                ).build().also { instancia = it }
            }
        }
    }
}

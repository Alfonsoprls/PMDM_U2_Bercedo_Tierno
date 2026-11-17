package com.example.mibiblioteca.datos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

/**
 * DAO: las operaciones con la base de datos.
 * Son funciones normales (no suspend), así que hay que llamarlas desde otro hilo:
 * Room lanza una excepción si se usan en el hilo principal.
 */
@Dao
interface LibroDao {

    @Query("SELECT * FROM libros ORDER BY titulo COLLATE NOCASE ASC")
    fun obtenerTodos(): List<Libro>

    @Insert
    fun insertar(libro: Libro): Long

    @Update
    fun actualizar(libro: Libro)

    @Delete
    fun borrar(libro: Libro)
}

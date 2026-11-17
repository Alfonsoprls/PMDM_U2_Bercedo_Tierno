package com.example.mibiblioteca.datos

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/** Entidad: cada objeto Libro es una fila de la tabla «libros». */
@Entity(tableName = "libros")
data class Libro(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "titulo") val titulo: String,
    @ColumnInfo(name = "autor") val autor: String,
    @ColumnInfo(name = "leido") val leido: Boolean = false
)

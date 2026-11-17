package com.example.mibiblioteca

import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.example.mibiblioteca.databinding.ActivityMainBinding
import com.example.mibiblioteca.datos.BibliotecaDatabase
import com.example.mibiblioteca.datos.Libro
import com.example.mibiblioteca.datos.LibroDao
import com.google.android.material.snackbar.Snackbar
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dao: LibroDao
    private lateinit var adapter: LibroAdapter

    // Hilo secundario para todas las operaciones con Room
    private val hiloBD: ExecutorService = Executors.newSingleThreadExecutor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dao = BibliotecaDatabase.obtener(this).libroDao()

        adapter = LibroAdapter(
            alCambiarLeido = { libro, leido -> cambiarLeido(libro, leido) },
            alBorrar = { libro -> borrarLibro(libro) }
        )
        binding.rvLibros.adapter = adapter

        binding.btnAnadir.setOnClickListener { anadirLibro() }
        binding.etTitulo.doAfterTextChanged { binding.tilTitulo.error = null }
        binding.etAutor.doAfterTextChanged { binding.tilAutor.error = null }

        cargarLibros()
    }

    override fun onDestroy() {
        super.onDestroy()
        hiloBD.shutdown()
    }

    /** Ejecuta [trabajo] en el hilo de la BD y después recarga la lista en el hilo principal. */
    private fun enSegundoPlano(trabajo: () -> Unit) {
        hiloBD.execute {
            trabajo()
            val libros = dao.obtenerTodos()
            runOnUiThread { mostrarLibros(libros) }
        }
    }

    private fun cargarLibros() = enSegundoPlano { }

    private fun mostrarLibros(libros: List<Libro>) {
        if (isFinishing || isDestroyed) return
        adapter.submitList(libros)

        val leidos = libros.count { it.leido }
        binding.tvContador.text = getString(
            R.string.contador,
            resources.getQuantityString(R.plurals.n_libros, libros.size, libros.size),
            resources.getQuantityString(R.plurals.n_leidos, leidos, leidos)
        )
        binding.tvVacio.visibility = if (libros.isEmpty()) View.VISIBLE else View.GONE
        binding.rvLibros.visibility = if (libros.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun anadirLibro() {
        val titulo = binding.etTitulo.text.toString().trim()
        val autor = binding.etAutor.text.toString().trim()

        var correcto = true
        if (titulo.isEmpty()) {
            binding.tilTitulo.error = getString(R.string.error_titulo)
            correcto = false
        }
        if (autor.isEmpty()) {
            binding.tilAutor.error = getString(R.string.error_autor)
            correcto = false
        }
        if (!correcto) return

        enSegundoPlano { dao.insertar(Libro(titulo = titulo, autor = autor)) }

        binding.etTitulo.text?.clear()
        binding.etAutor.text?.clear()
        binding.etTitulo.requestFocus()
        ocultarTeclado()
        Snackbar.make(binding.root, R.string.libro_anadido, Snackbar.LENGTH_SHORT).show()
    }

    private fun cambiarLeido(libro: Libro, leido: Boolean) {
        enSegundoPlano { dao.actualizar(libro.copy(leido = leido)) }
    }

    private fun borrarLibro(libro: Libro) {
        enSegundoPlano { dao.borrar(libro) }

        Snackbar.make(
            binding.root,
            getString(R.string.libro_borrado, libro.titulo),
            Snackbar.LENGTH_LONG
        ).setAction(R.string.deshacer) {
            // Se vuelve a insertar con el mismo id
            enSegundoPlano { dao.insertar(libro) }
        }.show()
    }

    private fun ocultarTeclado() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }
}

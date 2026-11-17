package com.example.mistareas

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mistareas.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), TareaAdapter.Listener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: TareaAdapter
    private val viewModel: TareasViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (viewModel.tareas.isEmpty()) cargarTareasDeEjemplo()

        adapter = TareaAdapter(viewModel.tareas, this)
        binding.rvTareas.adapter = adapter
        aplicarVista()
    }

    private fun cargarTareasDeEjemplo() {
        val titulos = resources.getStringArray(R.array.tareas_ejemplo)
        val prioridades = Prioridad.entries
        titulos.forEachIndexed { i, titulo ->
            viewModel.tareas.add(
                Tarea(viewModel.nuevoId(), titulo, prioridades[i % prioridades.size])
            )
        }
    }

    private fun aplicarVista() {
        binding.rvTareas.layoutManager =
            if (viewModel.vistaCuadricula) GridLayoutManager(this, 2)
            else LinearLayoutManager(this)
    }

    // ---------- Menú de opciones ----------

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_principal, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val item = menu.findItem(R.id.accion_cambiar_vista)
        if (viewModel.vistaCuadricula) {
            item.setTitle(R.string.menu_vista_lista)
            item.setIcon(R.drawable.ic_view_list)
        } else {
            item.setTitle(R.string.menu_vista_cuadricula)
            item.setIcon(R.drawable.ic_view_grid)
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.accion_cambiar_vista -> {
                viewModel.vistaCuadricula = !viewModel.vistaCuadricula
                aplicarVista()
                invalidateOptionsMenu()
                true
            }
            R.id.accion_borrar_completadas -> {
                borrarCompletadas()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun borrarCompletadas() {
        val antes = viewModel.tareas.size
        viewModel.tareas.removeAll { it.completada }
        val borradas = antes - viewModel.tareas.size

        if (borradas == 0) {
            Toast.makeText(this, R.string.sin_completadas, Toast.LENGTH_SHORT).show()
        } else {
            @Suppress("NotifyDataSetChanged")
            adapter.notifyDataSetChanged()
            val mensaje = resources.getQuantityString(R.plurals.tareas_borradas, borradas, borradas)
            Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
        }
    }

    // ---------- Eventos de las tarjetas ----------

    override fun onCompletadaCambiada(posicion: Int, completada: Boolean) {
        viewModel.tareas[posicion].completada = completada
        binding.rvTareas.post { adapter.notifyItemChanged(posicion) }
    }

    override fun onPrioridadElegida(posicion: Int, prioridad: Prioridad) {
        viewModel.tareas[posicion].prioridad = prioridad
        adapter.notifyItemChanged(posicion)
        val mensaje = getString(R.string.prioridad_cambiada, getString(prioridad.nombre))
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }

    override fun onDuplicar(posicion: Int) {
        val original = viewModel.tareas[posicion]
        val copia = original.copy(
            id = viewModel.nuevoId(),
            titulo = getString(R.string.titulo_copia, original.titulo)
        )
        viewModel.tareas.add(posicion + 1, copia)
        adapter.notifyItemInserted(posicion + 1)
        binding.rvTareas.scrollToPosition(posicion + 1)
        Toast.makeText(this, R.string.tarea_duplicada, Toast.LENGTH_SHORT).show()
    }

    override fun onEliminar(posicion: Int) {
        viewModel.tareas.removeAt(posicion)
        adapter.notifyItemRemoved(posicion)
        Toast.makeText(this, R.string.tarea_eliminada, Toast.LENGTH_SHORT).show()
    }
}

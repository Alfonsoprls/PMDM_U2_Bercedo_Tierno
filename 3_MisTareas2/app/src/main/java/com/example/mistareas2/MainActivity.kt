package com.example.mistareas2

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mistareas2.databinding.ActivityMainBinding
import com.example.mistareas2.databinding.DialogoNombreBinding
import com.example.mistareas2.databinding.DialogoNuevaTareaBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import java.util.Calendar
import java.util.Date

class MainActivity : AppCompatActivity(), TareaAdapter.Listener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: TareaAdapter
    private lateinit var preferencias: Preferencias
    private val viewModel: TareasViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferencias = Preferencias(this)

        if (viewModel.tareas.isEmpty()) cargarTareasDeEjemplo()

        adapter = TareaAdapter(viewModel.tareas, this)
        binding.rvTareas.adapter = adapter
        aplicarVista()

        binding.fabNuevaTarea.setOnClickListener { mostrarDialogoNuevaTarea() }

        // Primer arranque: todavía no sabemos cómo se llama el usuario
        val nombre = preferencias.nombreUsuario
        if (nombre.isNullOrBlank()) {
            pedirNombre(obligatorio = true)
        } else {
            actualizarTitulo(nombre)
        }
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
            if (preferencias.vistaCuadricula) GridLayoutManager(this, 2)
            else LinearLayoutManager(this)
    }

    private fun actualizarTitulo(nombre: String) {
        supportActionBar?.title = getString(R.string.titulo_tareas_de, nombre)
    }

    // ---------- Diálogo del nombre (AlertDialog con vista propia) ----------

    private fun pedirNombre(obligatorio: Boolean) {
        val vista = DialogoNombreBinding.inflate(layoutInflater)
        vista.etNombre.setText(preferencias.nombreUsuario.orEmpty())

        val constructor = MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialogo_nombre_titulo)
            .setMessage(R.string.dialogo_nombre_mensaje)
            .setView(vista.root)
            .setCancelable(!obligatorio)
            .setPositiveButton(R.string.aceptar, null)
        if (!obligatorio) constructor.setNegativeButton(R.string.cancelar, null)
        val dialogo = constructor.create()

        // Sobrescribimos el botón para que el diálogo no se cierre si el nombre está vacío
        dialogo.setOnShowListener {
            dialogo.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val nombre = vista.etNombre.text.toString().trim()
                if (nombre.isEmpty()) {
                    vista.tilNombre.error = getString(R.string.error_nombre_vacio)
                } else {
                    preferencias.nombreUsuario = nombre
                    actualizarTitulo(nombre)
                    Toast.makeText(this, getString(R.string.bienvenida, nombre), Toast.LENGTH_SHORT).show()
                    dialogo.dismiss()
                }
            }
        }
        dialogo.show()
    }

    // ---------- Diálogo de nueva tarea con DatePicker ----------

    private fun mostrarDialogoNuevaTarea() {
        val vista = DialogoNuevaTareaBinding.inflate(layoutInflater)
        var fechaElegida: Long? = null

        vista.btnElegirFecha.setOnClickListener {
            val calendario = Calendar.getInstance()
            fechaElegida?.let { calendario.timeInMillis = it }
            DatePickerDialog(
                this,
                { _, anio, mes, dia ->
                    val elegido = Calendar.getInstance()
                    elegido.set(anio, mes, dia, 0, 0, 0)
                    fechaElegida = elegido.timeInMillis
                    vista.tvFechaElegida.text = getString(
                        R.string.fecha_limite,
                        TareaAdapter.FORMATO_FECHA.format(Date(elegido.timeInMillis))
                    )
                },
                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        val dialogo = MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialogo_nueva_tarea_titulo)
            .setView(vista.root)
            .setPositiveButton(R.string.crear, null)
            .setNegativeButton(R.string.cancelar, null)
            .create()

        dialogo.setOnShowListener {
            dialogo.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val titulo = vista.etTitulo.text.toString().trim()
                if (titulo.isEmpty()) {
                    vista.tilTitulo.error = getString(R.string.error_titulo_vacio)
                    return@setOnClickListener
                }
                val prioridad = when (vista.rgPrioridad.checkedRadioButtonId) {
                    R.id.rbAlta -> Prioridad.ALTA
                    R.id.rbBaja -> Prioridad.BAJA
                    else -> Prioridad.MEDIA
                }
                val tarea = Tarea(viewModel.nuevoId(), titulo, prioridad, fechaLimite = fechaElegida)
                viewModel.tareas.add(0, tarea)
                adapter.notifyItemInserted(0)
                binding.rvTareas.scrollToPosition(0)
                Toast.makeText(this, R.string.tarea_creada, Toast.LENGTH_SHORT).show()
                dialogo.dismiss()
            }
        }
        dialogo.show()
    }

    // ---------- Menú de opciones ----------

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_principal, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val item = menu.findItem(R.id.accion_cambiar_vista)
        if (preferencias.vistaCuadricula) {
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
                preferencias.vistaCuadricula = !preferencias.vistaCuadricula
                aplicarVista()
                invalidateOptionsMenu()
                true
            }
            R.id.accion_borrar_completadas -> {
                borrarCompletadas()
                true
            }
            R.id.accion_cambiar_nombre -> {
                pedirNombre(obligatorio = false)
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

    /** Pide confirmación y, al borrar, ofrece Deshacer en un Snackbar. */
    override fun onEliminar(posicion: Int) {
        val tarea = viewModel.tareas[posicion]
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.confirmar_eliminar_titulo)
            .setMessage(getString(R.string.confirmar_eliminar_mensaje, tarea.titulo))
            .setPositiveButton(R.string.menu_eliminar) { _, _ ->
                val indice = viewModel.tareas.indexOf(tarea)
                if (indice == -1) return@setPositiveButton
                viewModel.tareas.removeAt(indice)
                adapter.notifyItemRemoved(indice)

                Snackbar.make(binding.root, R.string.tarea_eliminada, Snackbar.LENGTH_LONG)
                    .setAction(R.string.deshacer) {
                        val destino = indice.coerceAtMost(viewModel.tareas.size)
                        viewModel.tareas.add(destino, tarea)
                        adapter.notifyItemInserted(destino)
                        binding.rvTareas.scrollToPosition(destino)
                    }
                    .show()
            }
            .setNegativeButton(R.string.cancelar, null)
            .show()
    }
}

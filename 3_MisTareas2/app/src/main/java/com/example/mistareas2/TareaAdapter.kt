package com.example.mistareas2

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.mistareas2.databinding.ItemTareaBinding

class TareaAdapter(
    private val tareas: List<Tarea>,
    private val listener: Listener
) : RecyclerView.Adapter<TareaAdapter.TareaViewHolder>() {

    interface Listener {
        fun onCompletadaCambiada(posicion: Int, completada: Boolean)
        fun onPrioridadElegida(posicion: Int, prioridad: Prioridad)
        fun onDuplicar(posicion: Int)
        fun onEliminar(posicion: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TareaViewHolder {
        val binding = ItemTareaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TareaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TareaViewHolder, position: Int) {
        holder.bind(tareas[position])
    }

    override fun getItemCount(): Int = tareas.size

    companion object {
        val FORMATO_FECHA = SimpleDateFormat("dd/MM/yyyy", Locale("es", "ES"))
    }

    inner class TareaViewHolder(private val binding: ItemTareaBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(tarea: Tarea) {
            val contexto = binding.root.context
            binding.tvTitulo.text = tarea.titulo

            // Quitamos el listener antes de cambiar la casilla: el ViewHolder se recicla
            binding.cbCompletada.setOnCheckedChangeListener(null)
            binding.cbCompletada.isChecked = tarea.completada
            aplicarTachado(tarea.completada)
            binding.cbCompletada.setOnCheckedChangeListener { _, marcada ->
                val posicion = bindingAdapterPosition
                if (posicion != RecyclerView.NO_POSITION) {
                    listener.onCompletadaCambiada(posicion, marcada)
                }
            }

            // Fecha límite (solo si la tarea tiene)
            val fecha = tarea.fechaLimite
            if (fecha == null) {
                binding.tvFechaLimite.visibility = View.GONE
            } else {
                binding.tvFechaLimite.visibility = View.VISIBLE
                binding.tvFechaLimite.text =
                    contexto.getString(R.string.fecha_limite, FORMATO_FECHA.format(Date(fecha)))
            }

            // Color de la prioridad
            val color = ContextCompat.getColor(contexto, tarea.prioridad.color)
            binding.vistaPrioridad.setBackgroundColor(color)
            binding.tvPrioridad.text = contexto.getString(
                R.string.prioridad_formato, contexto.getString(tarea.prioridad.nombre)
            )
            binding.tvPrioridad.setTextColor(color)

            // Menú popup: cambiar la prioridad
            binding.btnPrioridad.setOnClickListener { mostrarMenuPrioridad(it) }

            // Menú contextual: pulsación larga sobre la tarjeta
            binding.root.setOnCreateContextMenuListener { menu, vista, _ ->
                MenuInflater(vista.context).inflate(R.menu.menu_contextual_tarea, menu)
                menu.setHeaderTitle(binding.tvTitulo.text)
                for (i in 0 until menu.size()) {
                    menu.getItem(i).setOnMenuItemClickListener { item ->
                        val posicion = bindingAdapterPosition
                        if (posicion == RecyclerView.NO_POSITION) return@setOnMenuItemClickListener false
                        when (item.itemId) {
                            R.id.accion_duplicar -> listener.onDuplicar(posicion)
                            R.id.accion_eliminar -> listener.onEliminar(posicion)
                        }
                        true
                    }
                }
            }
        }

        private fun aplicarTachado(completada: Boolean) {
            val titulo = binding.tvTitulo
            if (completada) {
                titulo.paintFlags = titulo.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                titulo.alpha = 0.6f
            } else {
                titulo.paintFlags = titulo.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                titulo.alpha = 1f
            }
        }

        private fun mostrarMenuPrioridad(ancla: View) {
            val popup = PopupMenu(ancla.context, ancla)
            popup.menuInflater.inflate(R.menu.menu_prioridad, popup.menu)
            popup.setOnMenuItemClickListener { item ->
                val posicion = bindingAdapterPosition
                if (posicion == RecyclerView.NO_POSITION) return@setOnMenuItemClickListener false
                val prioridad = when (item.itemId) {
                    R.id.prioridad_alta -> Prioridad.ALTA
                    R.id.prioridad_media -> Prioridad.MEDIA
                    else -> Prioridad.BAJA
                }
                listener.onPrioridadElegida(posicion, prioridad)
                true
            }
            popup.show()
        }
    }
}

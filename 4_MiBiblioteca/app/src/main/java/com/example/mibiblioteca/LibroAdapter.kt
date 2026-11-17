package com.example.mibiblioteca

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mibiblioteca.databinding.ItemLibroBinding
import com.example.mibiblioteca.datos.Libro

class LibroAdapter(
    private val alCambiarLeido: (Libro, Boolean) -> Unit,
    private val alBorrar: (Libro) -> Unit
) : ListAdapter<Libro, LibroAdapter.LibroViewHolder>(Comparador) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibroViewHolder {
        val binding = ItemLibroBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LibroViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LibroViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class LibroViewHolder(private val binding: ItemLibroBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(libro: Libro) {
            binding.tvTitulo.text = libro.titulo
            binding.tvAutor.text = libro.autor

            binding.cbLeido.setOnCheckedChangeListener(null)
            binding.cbLeido.isChecked = libro.leido
            binding.cbLeido.setOnCheckedChangeListener { _, marcado ->
                alCambiarLeido(libro, marcado)
            }

            binding.btnBorrar.setOnClickListener { alBorrar(libro) }
        }
    }

    object Comparador : DiffUtil.ItemCallback<Libro>() {
        override fun areItemsTheSame(antiguo: Libro, nuevo: Libro) = antiguo.id == nuevo.id
        override fun areContentsTheSame(antiguo: Libro, nuevo: Libro) = antiguo == nuevo
    }
}

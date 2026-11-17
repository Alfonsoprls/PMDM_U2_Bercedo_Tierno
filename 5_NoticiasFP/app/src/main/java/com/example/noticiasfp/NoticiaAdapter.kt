package com.example.noticiasfp

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.noticiasfp.databinding.ItemNoticiaBinding

class NoticiaAdapter(
    private val alPulsarFavorita: (Noticia) -> Unit
) : ListAdapter<Noticia, NoticiaAdapter.NoticiaViewHolder>(Comparador) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticiaViewHolder {
        val binding = ItemNoticiaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoticiaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoticiaViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class NoticiaViewHolder(private val binding: ItemNoticiaBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(noticia: Noticia) {
            val contexto = binding.root.context
            binding.tvTitulo.text = noticia.titulo
            binding.tvResumen.text = noticia.resumen

            // Chip con la categoría y su color
            binding.chipCategoria.setText(noticia.categoria.nombre)
            binding.chipCategoria.chipBackgroundColor = ColorStateList.valueOf(
                ContextCompat.getColor(contexto, noticia.categoria.color)
            )

            // Estrella de favorita
            binding.btnFavorita.setImageResource(
                if (noticia.favorita) R.drawable.ic_star else R.drawable.ic_star_border
            )
            binding.btnFavorita.contentDescription = contexto.getString(
                if (noticia.favorita) R.string.cd_quitar_favorita else R.string.cd_marcar_favorita
            )
            binding.btnFavorita.setOnClickListener { alPulsarFavorita(noticia) }
        }
    }

    object Comparador : DiffUtil.ItemCallback<Noticia>() {
        override fun areItemsTheSame(antigua: Noticia, nueva: Noticia) = antigua.id == nueva.id
        override fun areContentsTheSame(antigua: Noticia, nueva: Noticia) = antigua == nueva
    }
}

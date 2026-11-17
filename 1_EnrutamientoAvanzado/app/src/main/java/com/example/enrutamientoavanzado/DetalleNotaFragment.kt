package com.example.enrutamientoavanzado

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.enrutamientoavanzado.databinding.FragmentDetalleNotaBinding

class DetalleNotaFragment : Fragment() {

    private var _binding: FragmentDetalleNotaBinding? = null
    private val binding get() = _binding!!

    // SafeArgs: los argumentos llegan ya tipados
    private val args: DetalleNotaFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetalleNotaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvTituloNota.text = args.tituloNota

        // El contenido es opcional: por deep link solo llega el título
        if (args.contenidoNota.isEmpty()) {
            binding.tvContenidoNota.setText(R.string.sin_contenido)
        } else {
            binding.tvContenidoNota.text = args.contenidoNota
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

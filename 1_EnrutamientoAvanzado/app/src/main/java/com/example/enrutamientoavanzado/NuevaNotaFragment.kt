package com.example.enrutamientoavanzado

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.enrutamientoavanzado.databinding.FragmentNuevaNotaBinding

class NuevaNotaFragment : Fragment() {

    private var _binding: FragmentNuevaNotaBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNuevaNotaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnEnviar.setOnClickListener {
            val titulo = binding.etTitulo.text.toString().trim()
            val contenido = binding.etContenido.text.toString().trim()

            if (titulo.isEmpty()) {
                Toast.makeText(requireContext(), R.string.aviso_titulo_vacio, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // SafeArgs: clase Directions generada a partir de la acción del grafo
            val accion = NuevaNotaFragmentDirections
                .actionNuevaNotaFragmentToDetalleNotaFragment(titulo, contenido)
            findNavController().navigate(accion)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

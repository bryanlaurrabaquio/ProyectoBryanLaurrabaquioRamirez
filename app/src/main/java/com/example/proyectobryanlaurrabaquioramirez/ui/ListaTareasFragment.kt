package com.example.proyectobryanlaurrabaquioramirez.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectobryanlaurrabaquioramirez.MainActivity
import com.example.proyectobryanlaurrabaquioramirez.data.TareaDbHelper
import com.example.proyectobryanlaurrabaquioramirez.databinding.FragmentListaTareasBinding

class ListaTareasFragment : Fragment() {

    private var _binding: FragmentListaTareasBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: TareaAdapter
    private lateinit var dbHelper: TareaDbHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListaTareasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = TareaDbHelper.obtener(requireContext())

        adapter = TareaAdapter(
            alHacerClic = { tarea -> (activity as MainActivity).mostrarFormulario(tarea.id) },
            alMarcar = { tarea ->
                dbHelper.cambiarCompletada(tarea.id, !tarea.completada)
                cargarTareas()
            },
            alEliminar = { tarea ->
                dbHelper.eliminar(tarea.id)
                cargarTareas()
            }
        )

        binding.recyclerTareas.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerTareas.adapter = adapter

        binding.fabAgregar.setOnClickListener {
            (activity as MainActivity).mostrarFormulario(null)
        }
    }

    override fun onResume() {
        super.onResume()
        cargarTareas()
    }

    private fun cargarTareas() {
        val lista = dbHelper.obtenerTodas()
        adapter.submitList(lista)
        binding.txtVacio.visibility = if (lista.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
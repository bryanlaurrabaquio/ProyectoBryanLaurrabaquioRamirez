package com.example.proyectobryanlaurrabaquioramirez.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.proyectobryanlaurrabaquioramirez.R
import com.example.proyectobryanlaurrabaquioramirez.data.Tarea
import com.example.proyectobryanlaurrabaquioramirez.data.TareaDbHelper
import com.example.proyectobryanlaurrabaquioramirez.databinding.FragmentAgregarEditarTareaBinding
import java.util.Calendar

class AgregarEditarTareaFragment : Fragment() {

    private var _binding: FragmentAgregarEditarTareaBinding? = null
    private val binding get() = _binding!!

    private lateinit var dbHelper: TareaDbHelper

    private var tareaId: Long = -1L
    private var completadaActual: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAgregarEditarTareaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = TareaDbHelper.obtener(requireContext())
        tareaId = arguments?.getLong(ARG_ID, -1L) ?: -1L

        configurarMenuPrioridad()
        configurarSelectorFecha()

        if (tareaId != -1L) {
            cargarTarea(tareaId)
            binding.btnEliminar.visibility = View.VISIBLE
            binding.txtTituloPantalla.text = getString(R.string.editar_tarea)
        } else {
            binding.btnEliminar.visibility = View.GONE
            binding.txtTituloPantalla.text = getString(R.string.nueva_tarea)
        }

        binding.btnGuardar.setOnClickListener { guardarTarea() }

        binding.btnEliminar.setOnClickListener {
            if (tareaId != -1L) dbHelper.eliminar(tareaId)
            parentFragmentManager.popBackStack()   // regresar a la lista
        }
    }

    private fun configurarMenuPrioridad() {
        val prioridades = listOf(
            getString(R.string.prioridad_alta),
            getString(R.string.prioridad_media),
            getString(R.string.prioridad_baja)
        )
        val adapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_list_item_1, prioridades
        )
        binding.dropdownPrioridad.setAdapter(adapter)
    }

    private fun configurarSelectorFecha() {
        binding.editFecha.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                { _, anio, mes, dia ->
                    val texto = "%04d-%02d-%02d".format(anio, mes + 1, dia) // AAAA-MM-DD
                    binding.editFecha.setText(texto)
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun cargarTarea(id: Long) {
        val tarea = dbHelper.obtenerPorId(id) ?: return
        completadaActual = tarea.completada
        binding.editTitulo.setText(tarea.titulo)
        binding.editMateria.setText(tarea.materia)
        binding.editDescripcion.setText(tarea.descripcion)
        binding.editFecha.setText(tarea.fecha)
        binding.dropdownPrioridad.setText(tarea.prioridad, false)
    }

    private fun guardarTarea() {
        val titulo = binding.editTitulo.text.toString().trim()
        val materia = binding.editMateria.text.toString().trim()
        val descripcion = binding.editDescripcion.text.toString().trim()
        val fecha = binding.editFecha.text.toString().trim()
        val prioridad = binding.dropdownPrioridad.text.toString().trim()

        if (titulo.isEmpty()) {
            binding.layoutTitulo.error = getString(R.string.error_titulo)
            return
        }
        binding.layoutTitulo.error = null

        val tarea = Tarea(
            id = if (tareaId == -1L) 0L else tareaId,
            titulo = titulo,
            materia = materia,
            descripcion = descripcion,
            fecha = fecha,
            prioridad = if (prioridad.isEmpty()) getString(R.string.prioridad_media) else prioridad,
            completada = completadaActual
        )

        if (tareaId == -1L) {
            dbHelper.insertar(tarea)
        } else {
            dbHelper.actualizar(tarea)
        }

        Toast.makeText(requireContext(), R.string.guardado_ok, Toast.LENGTH_SHORT).show()
        parentFragmentManager.popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_ID = "arg_id"

        fun nuevaInstancia(id: Long?): AgregarEditarTareaFragment {
            val fragment = AgregarEditarTareaFragment()
            val args = Bundle()
            args.putLong(ARG_ID, id ?: -1L)
            fragment.arguments = args
            return fragment
        }
    }
}
package com.example.proyectobryanlaurrabaquioramirez.ui

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectobryanlaurrabaquioramirez.data.Tarea
import com.example.proyectobryanlaurrabaquioramirez.databinding.ItemTareaBinding

class TareaAdapter(
    private val alHacerClic: (Tarea) -> Unit,
    private val alMarcar: (Tarea) -> Unit,
    private val alEliminar: (Tarea) -> Unit
) : ListAdapter<Tarea, TareaAdapter.TareaViewHolder>(DIFERENCIAS) {

    inner class TareaViewHolder(
        private val binding: ItemTareaBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun enlazar(tarea: Tarea) {
            binding.txtTitulo.text = tarea.titulo
            binding.txtMateria.text = tarea.materia
            binding.txtFecha.text = tarea.fecha
            binding.txtPrioridad.text = tarea.prioridad
            binding.checkCompletada.isChecked = tarea.completada

            if (tarea.completada) {
                binding.txtTitulo.paintFlags =
                    binding.txtTitulo.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                binding.txtTitulo.paintFlags =
                    binding.txtTitulo.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }

            binding.root.setOnClickListener { alHacerClic(tarea) }
            binding.checkCompletada.setOnClickListener { alMarcar(tarea) }
            binding.btnEliminar.setOnClickListener { alEliminar(tarea) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TareaViewHolder {
        val binding = ItemTareaBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TareaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TareaViewHolder, position: Int) {
        holder.enlazar(getItem(position))
    }

    companion object {
        private val DIFERENCIAS = object : DiffUtil.ItemCallback<Tarea>() {
            override fun areItemsTheSame(a: Tarea, b: Tarea) = a.id == b.id
            override fun areContentsTheSame(a: Tarea, b: Tarea) = a == b
        }
    }
}
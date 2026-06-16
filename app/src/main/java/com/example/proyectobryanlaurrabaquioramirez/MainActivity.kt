package com.example.proyectobryanlaurrabaquioramirez

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectobryanlaurrabaquioramirez.databinding.ActivityMainBinding
import com.example.proyectobryanlaurrabaquioramirez.ui.AgregarEditarTareaFragment
import com.example.proyectobryanlaurrabaquioramirez.ui.ListaTareasFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.contenedor, ListaTareasFragment())
                .commit()
        }
    }

    fun mostrarFormulario(id: Long?) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.contenedor, AgregarEditarTareaFragment.nuevaInstancia(id))
            .addToBackStack(null)
            .commit()
    }
}
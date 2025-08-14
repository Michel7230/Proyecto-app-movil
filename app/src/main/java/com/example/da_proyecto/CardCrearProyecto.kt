package com.example.da_proyecto

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.da_proyecto.databinding.ActivityCardCrearProyectoBinding

class CardCrearProyecto : AppCompatActivity() {
    private lateinit var binding: ActivityCardCrearProyectoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardCrearProyectoBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
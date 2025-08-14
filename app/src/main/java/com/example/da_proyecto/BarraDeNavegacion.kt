package com.example.da_proyecto

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.da_proyecto.databinding.ActivityBarraDeNavegacionBinding

class BarraDeNavegacion : AppCompatActivity() {
    private lateinit var binding: ActivityBarraDeNavegacionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBarraDeNavegacionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val destinationFragmentId = intent.getIntExtra("destination_fragment", -1)
        if (destinationFragmentId != -1) {
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
            val navController = navHostFragment.navController
            navController.navigate(destinationFragmentId)
        }

        val bottomNavigationView = binding.brNavegacion
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment


        val navController = navHostFragment.navController

        bottomNavigationView.setupWithNavController(navController)

    }
}
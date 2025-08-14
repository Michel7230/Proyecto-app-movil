package com.example.da_proyecto

data class Planta(
    val nombre: String,
    val temp_min: Float,
    val temp_max: Float,
    val humedad_min: Float ,
    val humedad_max: Float,
    val ph_min: Float,
    val ph_max: Float
)

object PlantasData{
    val ListaPlantas = listOf(
        Planta(
            nombre = "Lechuga",
            temp_min = 15f,
            temp_max = 20f,
            humedad_min = 60f,
            humedad_max =80f,
            ph_min = 6.7f,
            ph_max = 7.4f
        ),
        Planta(
            nombre = "Rabano",
            temp_min = 20f,
            temp_max = 25f,
            humedad_min = 60f,
            humedad_max =80f,
            ph_min = 5.5f,
            ph_max = 6.8f
        ),
        Planta(
            nombre = "Cilantro",
            temp_min = 10f,
            temp_max = 30f,
            humedad_min = 60f,
            humedad_max =80f,
            ph_min = 6.2f,
            ph_max = 6.8f
        )
    )
}
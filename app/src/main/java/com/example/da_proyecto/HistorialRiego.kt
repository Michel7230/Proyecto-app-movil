package com.example.da_proyecto

data class HistorialRiego(
    val fecha: String = "",
    val horaRiego: String = "",
    val humedad: Double = 0.0,
    val plantaRegada: Boolean = false,
    val temperatura: Double = 0.0
)

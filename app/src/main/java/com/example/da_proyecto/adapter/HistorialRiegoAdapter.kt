package com.example.da_proyecto.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.da_proyecto.HistorialRiego
import com.example.da_proyecto.databinding.HistorialRiegoBinding

class HistorialRiegoAdapter(private val listaHistorial: List<HistorialRiego>): RecyclerView.Adapter<HistorialRiegoAdapter.HistorialRiegoViewHolder>() {

    class HistorialRiegoViewHolder(val binding: HistorialRiegoBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistorialRiegoViewHolder {
        val binding = HistorialRiegoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistorialRiegoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistorialRiegoViewHolder, position: Int) {
        with(holder.binding){
            val historialRiego = listaHistorial[position]
            fecha.text = historialRiego.fecha
            temperaturaPromedio.text = historialRiego.temperatura.toString()
            humedadSueloTextView.text = historialRiego.humedad.toString()
            estadoRiego.text = historialRiego.plantaRegada.toString()
            horaRiegoTextView.text = historialRiego.horaRiego.toString()
        }
    }

    override fun getItemCount(): Int {
        return listaHistorial.size
    }

}
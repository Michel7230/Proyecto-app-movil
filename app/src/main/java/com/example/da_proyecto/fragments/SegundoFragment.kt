package com.example.da_proyecto.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.da_proyecto.Card
import com.example.da_proyecto.HistorialRiego
import com.example.da_proyecto.LinkHelper
import com.example.da_proyecto.OnItemClickListener
import com.example.da_proyecto.R
import com.example.da_proyecto.databinding.FragmentSegundoBinding
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SegundoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SegundoFragment : Fragment(), OnItemClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentSegundoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSegundoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onItemClick(card: Card) {
        TODO("Not yet implemented")
    }

    override fun onDeleteClick(card: Card) {
        TODO("Not yet implemented")
    }

    override fun onGenerarURL(card: Card) {
        val lik = LinkHelper.generadorLink(card)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        val database = Firebase.database
        val myRef = database.getReference("HistorialRiego")

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val riegoList = mutableListOf<HistorialRiego>()
                for (cardSnapshot in dataSnapshot.children) {
                    val nodoRiego = cardSnapshot.child("riego")
                    for (riegoSnapshot in nodoRiego.children) {
                        val riego = riegoSnapshot.getValue(HistorialRiego::class.java)
                        if (riego != null) {
                            riegoList.add(riego)
                        }
                    }
                }

                if(riegoList.isNotEmpty()) {

                    val referenceTime = riegoList.minOf{ convertirFecha(it.fecha) }

                    val entriesTemperatura = riegoList.map { riego ->
                        Entry(
                            ((convertirFecha(riego.fecha) - referenceTime) / 1000f) ,
                            riego.temperatura.toFloat()
                        )
                    }
                    val dataSetTemperatura = LineDataSet(entriesTemperatura, "Temperatura Promedio")
                    val lineDataTemperatura = LineData(dataSetTemperatura)
                    binding.graficaTemp.data = lineDataTemperatura
                    binding.graficaTemp.xAxis.valueFormatter = DateValueFormatter(referenceTime)
                    binding.graficaTemp.invalidate()

                    val entriesHumedad = riegoList.map { riego ->
                        Entry(
                            ((convertirFecha(riego.fecha) - referenceTime) / 1000f),
                            riego.humedad.toFloat()
                        )
                    }
                    val dataSetHumedad = LineDataSet(entriesHumedad, "Humedad del Suelo")
                    val lineDataHumedad = LineData(dataSetHumedad)
                    binding.graficaHum.data = lineDataHumedad
                    binding.graficaHum.xAxis.valueFormatter = DateValueFormatter(referenceTime)
                    binding.graficaHum.invalidate()

                    val entriesHora = riegoList.map { riego ->
                        Entry(
                            ((convertirFecha(riego.fecha) - referenceTime) / 1000f),
                            convertirHoraAMinutos(riego.horaRiego).toFloat()
                        )
                    }
                    val dataSetHora = LineDataSet(entriesHora, "Hora de Riego")
                    val lineDataHora = LineData(dataSetHora)
                    binding.graficaRiego.data = lineDataHora
                    binding.graficaRiego.xAxis.valueFormatter = DateValueFormatter(referenceTime)
                    binding.graficaRiego.invalidate()
                }

            }

            fun convertirHoraAMinutos(hora: String): Int {
                val formato = SimpleDateFormat("hh:mm a", Locale.getDefault())
                try {
                    val date = formato.parse(hora)
                    val calendar = Calendar.getInstance().apply { time = date }
                    val horas = calendar.get(Calendar.HOUR_OF_DAY)
                    val minutos = calendar.get(Calendar.MINUTE)
                    return horas * 60 + minutos
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
                return 0
            }

            fun convertirFecha(fecha: String): Long {
                val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val date = formato.parse(fecha)
                return date?.time ?: 0L
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejo de errores
                Log.w("Firebase", "Error al leer los datos.", error.toException())
            }
        })

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SegundoFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SegundoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
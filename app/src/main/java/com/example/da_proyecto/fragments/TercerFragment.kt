package com.example.da_proyecto.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.da_proyecto.HistorialRiego
import com.example.da_proyecto.R
import com.example.da_proyecto.adapter.HistorialRiegoAdapter
import com.example.da_proyecto.databinding.FragmentTercerBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TercerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TercerFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentTercerBinding
    private lateinit var historialRiegoAdapter: HistorialRiegoAdapter

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
        binding = FragmentTercerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        historialRiegoAdapter = HistorialRiegoAdapter(listOf())
        binding.recyclerView.adapter = historialRiegoAdapter
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 4)


        val database = FirebaseDatabase.getInstance().getReference("HistorialRiego")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val listaHistorial = mutableListOf<HistorialRiego>()
                dataSnapshot.children.forEach { cardSnapshot ->
                    val riegoSnapshot = cardSnapshot.child("riego")
                    riegoSnapshot.children.forEach{itemSnapshot ->
                        val historial = itemSnapshot.getValue(HistorialRiego::class.java)
                        if (historial != null) {
                            listaHistorial.add(historial)
                        }
                    }
                }
                historialRiegoAdapter = HistorialRiegoAdapter(listaHistorial)
                binding.recyclerView.adapter = historialRiegoAdapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Manejar error
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
         * @return A new instance of fragment TercerFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TercerFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
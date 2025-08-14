package com.example.da_proyecto.fragments

import android.content.ContentValues.TAG
import android.content.Intent
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.da_proyecto.DBManager
import com.example.da_proyecto.MainActivity
import com.example.da_proyecto.R
import com.example.da_proyecto.databinding.FragmentCuartoBinding
import com.example.da_proyecto.nombre
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

enum class ProviderType{
    BASIC
}


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CuartoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CuartoFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentCuartoBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private var db = FirebaseFirestore.getInstance()

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
        _binding = FragmentCuartoBinding.inflate(inflater, container, false)
        auth = Firebase.auth
        val view = binding.root

        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            val nombre = user.displayName

            if (nombre != null) {
                val docRef = db.collection("usuarios").document(nombre)
                docRef.get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            val name = document.getString("nombre")
                            val phoneNumber = document.getString("telefono")
                            val email = document.getString("email")

                            binding.nombreEditText.setText(name)
                            binding.telefono.setText(phoneNumber)
                            binding.correoElectronico.setText(email)
                        } else {
                            Toast.makeText(context, "EL documento no existe", Toast.LENGTH_LONG).show()
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d(TAG, "get failed with ", exception)
                    }
            } else{
                Toast.makeText(context, "Error al obtener los datos", Toast.LENGTH_LONG).show()
            }



        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.buttonActualisar.setOnClickListener {
            actualizarDatos()
        }

        binding.buttonCerrar.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(activity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            activity?.finish()
        }

    }


    private fun actualizarDatos() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val nombre = user.displayName
            val nombreNuevo = binding.nombreEditText.text.toString()
            val telefono = binding.telefono.text.toString()
            val docRef = db.collection("usuarios").document(nombre!!)

            val DatosActualizados = hashMapOf<String, Any>(
                "nombre" to nombreNuevo,
                "telefono" to telefono
            )
            docRef.update(DatosActualizados)
                .addOnSuccessListener { Toast.makeText(context, "Datos actualizados correctamente", Toast.LENGTH_LONG).show() }
                .addOnFailureListener { e -> Log.w(TAG, "Error en la actualizacion de tus datos", e) }
        } else {
            Log.w(TAG, "La actualizacion tuvo un error")
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CuartoFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CuartoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
package com.example.da_proyecto

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.da_proyecto.databinding.ActivityRegistroBinding
import com.example.da_proyecto.fragments.ProviderType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Registro : AppCompatActivity() {

    private lateinit var binding: ActivityRegistroBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        binding.btnIngresar.setOnClickListener {
            createAccount()
        }

    }

    private fun createAccount() {
        val nombre = binding.nombre.text.toString()
        val telefono = binding.telefono.text.toString()
        val correoElectronico = binding.correoElectronico.text.toString()
        val Password = binding.Password.text.toString()
        auth.createUserWithEmailAndPassword(correoElectronico, Password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(nombre)
                            .build()

                        it.updateProfile(profileUpdates)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.d(TAG, "User profile updated.")
                                }
                            }

                        db.collection("usuarios").document(nombre).set(hashMapOf(
                            "nombre" to nombre,
                            "telefono" to telefono,
                            "cards" to "",
                            "email" to correoElectronico,
                            "uid" to auth.currentUser?.uid
                        ))
                    }
                    Toast.makeText(this, "Tienes que iniciar sesion", Toast.LENGTH_LONG).show()
                    val next = Intent(applicationContext, MainActivity::class.java)
                    startActivity(next)
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext,"El registro tu un fallo.", Toast.LENGTH_SHORT,).show()
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseAuth?) {}

}
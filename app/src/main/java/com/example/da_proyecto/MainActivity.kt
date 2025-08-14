package com.example.da_proyecto

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import com.example.da_proyecto.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        binding.btnIngresar.setOnClickListener {
            val nombre = binding.nombre.text.toString()
            val Password = binding.Password.text.toString()
            if (nombre.isNotEmpty() && Password.isNotEmpty()) {
                loginUser(nombre, Password)
            } else {
                Toast.makeText(baseContext, "Por favor, ingresa nombre y contraseña.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.bntRegistrar.setOnClickListener {
            registrar()
        }

    }

    fun registrar(){
        val next = Intent(this, Registro::class.java)
        startActivity(next)
    }

    private fun loginUser(nombre: String, Password: String) {
        auth = Firebase.auth
        db.collection("usuarios").whereEqualTo("nombre", nombre).get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty()) {
                    val usuario = documents.documents[0].data
                    val correoElectronico = usuario?.get("email") as String?
                    if (correoElectronico != null && android.util.Patterns.EMAIL_ADDRESS.matcher(correoElectronico).matches()) {
                        auth.signInWithEmailAndPassword(correoElectronico, Password)
                            .addOnCompleteListener(this) { task ->
                                if (task.isSuccessful) {
                                    Log.d(TAG, "signInWithEmail:success")
                                    val user = auth.currentUser

                                    val next = Intent(applicationContext, PantallaInicio::class.java)
                                    startActivity(next)
                                } else {
                                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                                    Toast.makeText(
                                        baseContext,
                                        "El inicio de sesión tuvo un fallo.",
                                        Toast.LENGTH_SHORT,
                                    ).show()
                                    updateUI(null)
                                }
                            }
                    } else {
                        Toast.makeText(baseContext,"El correo electrónico recuperado está mal formateado o es nulo.", Toast.LENGTH_SHORT,
                        ).show()
                    }
                } else {
                    Toast.makeText(baseContext,"El nombre de usuario proporcionado no existe en la base de datos.", Toast.LENGTH_SHORT,).show()
                }
            }
    }


    private fun updateUI(user: FirebaseAuth?) {}

}
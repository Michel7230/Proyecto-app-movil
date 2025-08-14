package com.example.da_proyecto

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User

class DBManager: ViewModel() {
    val user: MutableLiveData<nombre> = MutableLiveData()

    fun fetchUserData(nombre: String){
        val db = FirebaseFirestore.getInstance()
        db.collection("usuarios").whereEqualTo("nombre",nombre).get().addOnSuccessListener {
                document ->
            for (document in document) {
                val nombre = document.getString("nombre")
                val telefono = document.getString("telefono")
                val email = document.getString("email")
                user.value = nombre(nombre, telefono, email)
            }
        }
    }

}

data class nombre(
    val nombre: String?,
    val telefono: String?,
    val email: String?
)
package com.example.da_proyecto

import android.app.AlertDialog
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.da_proyecto.adapter.cardAdapter
import com.example.da_proyecto.databinding.ActivityPantallaInicioBinding
import com.example.da_proyecto.databinding.CardcrearpBinding
import com.example.da_proyecto.fragments.CuartoFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class PantallaInicio : AppCompatActivity(), OnItemClickListener {

    private lateinit var binding: ActivityPantallaInicioBinding
    private lateinit var auth: FirebaseAuth
    val db = FirebaseFirestore.getInstance()
    private lateinit var cardAdapter: cardAdapter
    private lateinit var cardList: MutableList<Card>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPantallaInicioBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        val nombre = FirebaseAuth.getInstance().currentUser
        nombre?.let {
            db.collection("usuarios").document(it.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val nombre = document.getString("nombre")
                        val telefono = document.getString("telefono")
                        val email = document.getString("email")
                    }
                }
        }

        cardList = mutableListOf()
        cardAdapter = cardAdapter(cardList, PlantasData.ListaPlantas,this)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = cardAdapter

        loadCards()

        binding.btnCrearPr.setOnClickListener(){
            showCustomDialog()
        }

        binding.MenuFlotante.setOnClickListener {
            MostrarMenu(it)
        }

    }

    private fun showCustomDialog(): Dialog {
        val dialogBinding = CardcrearpBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(this, R.style.MyAlertDialogTheme)
        builder.setView(dialogBinding.root)
        builder.setPositiveButton("Aceptar", null)
        builder.setNegativeButton("Cancelar", null)

        val adapter = ArrayAdapter(
            this,
            R.layout.item_spinner,
            PlantasData.ListaPlantas.map { it.nombre }
        )
        adapter.setDropDownViewResource(R.layout.item_spinner)

        dialogBinding.opcionesPlantas.adapter = adapter

        builder.setPositiveButton("Aceptar") { _, _->
            val newCardId = FirebaseDatabase.getInstance().getReference("cards").push().key ?: ""
            val newCardName = dialogBinding.nameProyect.text.toString()
            val newCardDescription = dialogBinding.descriptionProyect.text.toString()
            val seleccionarPosicion = dialogBinding.opcionesPlantas.selectedItemPosition
            val newPlanta = PlantasData.ListaPlantas[seleccionarPosicion].nombre

            if (newCardName.isNotEmpty() && newCardDescription.isNotEmpty() && newPlanta.isNotEmpty()) {
                addCard(newCardName, newCardDescription, newPlanta)
            }
        }
            .setNegativeButton("Cancelar",null)

        val dialog = builder.create()
        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(
            ContextCompat.getColor(this,android.R.color.black)
        )
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(
            ContextCompat.getColor(this, android.R.color.black)
        )

        val width = (resources.displayMetrics.widthPixels * 0.9).toInt()
        dialog.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)

        return dialog
    }

    private fun addCard(name:String, description:String, opciones: String) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null){
            val nombre = user.displayName
            val docRef = db.collection("usuarios").document(nombre!!)

            val plantaSeleccionada = PlantasData.ListaPlantas.find{ it.nombre == opciones }

            val plantaMap = plantaSeleccionada?.let {
                hashMapOf(
                    "nombre" to it.nombre,
                    "temp_min" to it.temp_min,
                    "temp_max" to it.temp_max,
                    "humedad_min" to it.humedad_min,
                    "humedad_max" to it.humedad_max,
                    "ph_min" to it.ph_min,
                    "ph_max" to it.ph_max
                )
            }  ?: "No hay informacion de la planta"

            val cardData = hashMapOf<String, Any>(
                "nombreProyecto" to name,
                "descripcion" to description,
                "opcionPlanta" to opciones
            )

            docRef.collection("cards").add(cardData).addOnSuccessListener { documentReference ->
                Toast.makeText(this, "Proyecto añadido exitosamente", Toast.LENGTH_LONG).show()
                val newCardId = documentReference.id

                val dataRealtime = hashMapOf<String, Any>(
                    "nombreProyecto" to name,
                    "planta" to plantaMap,
                    "riegoCounter" to 0,
                    "riego" to hashMapOf<String, Any>("dummy" to "init")
                )
                FirebaseDatabase.getInstance().getReference("HistorialRiego/$newCardId").setValue(dataRealtime)
                FirebaseDatabase.getInstance().getReference("CurrentCardId").setValue(newCardId)

                val newCard = Card(newCardId, name, description, opciones)
                cardList.add(newCard)
                cardAdapter.notifyItemInserted(cardList.size - 1)

            }.addOnFailureListener { e ->
                Toast.makeText(this, "Error al añadir el proyecto: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadCards() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null){
            val nombre = user.displayName
            db.collection("usuarios").document(nombre!!)
                .collection("cards")
                .get()
                .addOnSuccessListener { documents ->
                    cardList.clear()
                    for (document in documents) {
                        val cardId = document.id
                        val cardName = document.getString("nombreProyecto")
                        val cardDescription = document.getString("descripcion")
                        val cardPlanta = document.getString("opcionPlanta")?: ""
                        val card = Card(cardId, cardName!!, cardDescription!!, cardPlanta!!)
                        cardList.add(card)
                    }
                    cardAdapter.notifyDataSetChanged()
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error al cargar los proyectos: $exception")
                }
        }
    }

    override fun onItemClick(card: Card) {
        val intent = Intent(this, BarraDeNavegacion::class.java)
        intent.putExtra("PROYECTO_NOMBRE", card.name)
        intent.putExtra("PROYECTO_DESCRIPCION", card.description)
        intent.putExtra("PROYECTO_PLANTA", card.opciones)
        startActivity(intent)

    }

    private fun MostrarMenu(view: View) {
        val popup =  PopupMenu(this, view)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.menuopciones, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.CerrarSesion -> {
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.Perfil -> {
                    val intent = Intent(this, BarraDeNavegacion::class.java)
                    intent.putExtra("destination_fragment", R.id.cuartoFragment)
                    startActivity(intent)
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }
        }
        popup.show()
    }

    override fun onDeleteClick(card: Card) {
        val cardId = card.id
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null){
            val nombre = user.displayName
            val docRef = db.collection("usuarios").document(nombre!!)
            docRef.collection("cards").document(cardId)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(this, "Proyecto eliminado exitosamente", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al eliminar el proyecto: ${e.message}", Toast.LENGTH_SHORT).show()
                }

            FirebaseDatabase.getInstance().getReference("HistorialRiego/$cardId")
                .removeValue()
                .addOnSuccessListener {
                    Toast.makeText(this, "Historial de riego eliminado exitosamente", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al eliminar el historial de riego: ${e.message}", Toast.LENGTH_SHORT).show()
                }

            val position = cardList.indexOf(card)
            if (position != -1) {
                cardAdapter.deleteCard(position)
            }
        }
    }

    override fun onGenerarURL(card: Card) {
        val link = LinkHelper.generadorLink(card)

        val clipBoard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipBoard.setPrimaryClip(ClipData.newPlainText("Enlace del proyecto", link))
        Toast.makeText(this, "Enlace copiado: $link", Toast.LENGTH_LONG).show()
    }
}

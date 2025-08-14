package com.example.da_proyecto.adapter

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.da_proyecto.Card
import com.example.da_proyecto.OnItemClickListener
import com.example.da_proyecto.Planta
import com.example.da_proyecto.R
import com.example.da_proyecto.databinding.ActivityCardCrearProyectoBinding


class cardAdapter(private val cardList: MutableList<Card>, private  val plantas: List<Planta>,private val clickListener: OnItemClickListener): RecyclerView.Adapter<cardAdapter.CardViewHolder>() {

    inner class CardViewHolder(val binding: ActivityCardCrearProyectoBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ActivityCardCrearProyectoBinding.inflate(inflater, parent, false)
        return CardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        with(holder){
            with(cardList[position]){
                val planta = plantas.find { it.nombre == opciones }?.nombre?: opciones
                binding.nameProyect.text = name
                binding.descriptionProyect.text = description

                binding.nombrePlanta.text = planta

                binding.cardDeProyecto.setOnClickListener {
                    clickListener.onItemClick(this)
                }

                binding.FlotingMenu.setOnClickListener { view ->
                    val pop = PopupMenu(view.context, view)
                    pop.menuInflater.inflate(R.menu.card_menu, pop.menu)
                    pop.setOnMenuItemClickListener { menuItem ->
                        when(menuItem.itemId){
                            R.id.eliminarProyecto -> {
                                clickListener.onDeleteClick(cardList[position])
                                true
                            }
                            R.id.generarURL -> {
                                clickListener.onGenerarURL(cardList[position])
                                true
                            }
                            else -> false
                        }
                    }
                    pop.show()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return cardList.size
    }

    fun deleteCard(position: Int) {
        cardList.removeAt(position)
        notifyItemRemoved(position)
    }
}
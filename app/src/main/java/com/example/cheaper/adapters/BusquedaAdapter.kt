package com.example.cheaper.adapters

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.cheaper.R
import com.example.cheaper.fragments.BuscarFragment
import com.example.cheaper.fragments.InicioFragment
import com.example.cheaper.fragments.ProductoCategoriaFragment
import com.example.cheaper.model.Actualizacion
import com.example.cheaper.model.Busqueda
import com.example.cheaper.model.Categoria
import com.google.firebase.firestore.core.View

class BusquedaAdapter(private val busquedasList: List<Busqueda>):
 RecyclerView.Adapter<BusquedaAdapter.ViewHolder>(){

    private val items: MutableList<CardView>

    init {
        this.items = ArrayList()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusquedaAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.item_busquedas,
            parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val currentItem : Busqueda = busquedasList[position]
        holder.nombre.text = currentItem.nombre

        items.add(holder.card)

        val CatName = currentItem.nombre

        holder.card.setOnClickListener(object : android.view.View.OnClickListener{
            override fun onClick(v: android.view.View?) {
                val bundle = Bundle()
                if(CatName=="Codigo"){

                }else if(CatName=="Categorias"){
                    val activity=v!!.context as AppCompatActivity
                    val buscarFragment = BuscarFragment()
                    activity.supportFragmentManager.beginTransaction().replace(R.id.dashboard, buscarFragment).addToBackStack(null).commit()

                }else if(CatName=="Nombre"){
                    val activity=v!!.context as AppCompatActivity
                    val inicioFragment = InicioFragment()
                    activity.supportFragmentManager.beginTransaction().replace(R.id.dashboard, inicioFragment).addToBackStack(null).commit()

                }

            }
        })
    }

    override fun getItemCount(): Int {
        return busquedasList.size
    }


    class ViewHolder(itemView : android.view.View) : RecyclerView.ViewHolder(itemView){
        val nombre : TextView = itemView.findViewById(R.id.txtNombre)
        val card: CardView = itemView.findViewById(R.id.card)

    }
}
package com.example.cheaper.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cheaper.R
import com.example.cheaper.model.Actualizacion
import com.example.cheaper.model.ProductoDashboard
import com.squareup.picasso.Picasso

class ActualizacionAdapter(private val actualizaciononesList: ArrayList<Actualizacion>):

    RecyclerView.Adapter<ActualizacionAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActualizacionAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.item_actualizacion,
            parent, false)
        return ActualizacionAdapter.MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem : Actualizacion = actualizaciononesList[position]

        holder.usuarioNombre.text = currentItem.nombreUsuario
        holder.fecha.text = currentItem.fecha.toString()
        Picasso.get().load(currentItem.imagenProducto).into(holder.usuarioImage)
    }

    override fun getItemCount(): Int {
        return actualizaciononesList.size
    }

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val usuarioNombre : TextView = itemView.findViewById(R.id.txtUsuario)
        val fecha : TextView = itemView.findViewById(R.id.txtFecha)
        val usuarioImage : ImageView = itemView.findViewById(R.id.avatarUsuario)

    }
}
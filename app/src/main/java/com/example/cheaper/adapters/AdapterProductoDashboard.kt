package com.example.cheaper.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cheaper.ProductoAdapter
import com.example.cheaper.R
import com.example.cheaper.model.ProductoDashboard
import com.squareup.picasso.Picasso

class AdapterProductoDashboard(private val productosList: ArrayList<ProductoDashboard>):

    RecyclerView.Adapter<AdapterProductoDashboard.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterProductoDashboard.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.dashboard_item,
            parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem : ProductoDashboard = productosList[position]

        holder.productoNombre.text = currentItem.nombre
        holder.productoPrecio.text = currentItem.precio.toString()
        holder.productoUbicacion.text = currentItem.lugar
        holder.productoNegocio.text = currentItem.tienda
        Picasso.get().load(currentItem.foto).into(holder.productImage)

    }

    override fun getItemCount(): Int {
        return productosList.size
    }

    public class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val productoNombre : TextView = itemView.findViewById(R.id.txtdProducto)
        val productoPrecio : TextView = itemView.findViewById(R.id.textdPrecio)
        val productoUbicacion : TextView = itemView.findViewById(R.id.textdUbicacion)
        val productoNegocio : TextView = itemView.findViewById(R.id.textdNegocio)
        val productImage : ImageView = itemView.findViewById(R.id.imagedProducto)

    }
}
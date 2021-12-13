package com.example.cheaper.adapters

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.example.cheaper.PerfilProductoFragment
import com.example.cheaper.R
import com.example.cheaper.fragments.EditarProductoFragment
import com.example.cheaper.model.Product
import com.squareup.picasso.Picasso

class AdapterProduct(private val productsList: ArrayList<Product>) :

    RecyclerView.Adapter<AdapterProduct.MyViewHolder>(){

    private lateinit var listener : View.OnClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.item_producto_perfil,
            parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentItem : Product = productsList[position]
        holder.productName.text = currentItem.nombre
        holder.productBrand.text = currentItem.marca
        holder.productDescription.text = currentItem.descripcion
        Picasso.get().load(currentItem.foto).into(holder.productImage)

    }

    override fun getItemCount(): Int {
        return productsList.size
    }

    public class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val productName : TextView = itemView.findViewById(R.id.txtNombreProducto_perfil)
        val productBrand : TextView = itemView.findViewById(R.id.txtMarcaProducto_perfil)
        val productDescription : TextView = itemView.findViewById(R.id.txtDescripcionProducto_perfil)
        val productImage : ImageView = itemView.findViewById(R.id.imageViewProductoPerfil)
    }


}
package com.example.cheaper.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cheaper.Product
import com.example.cheaper.R
import com.squareup.picasso.Picasso

class ProductoAdapter(private val productsList: ArrayList<Product>) :

    RecyclerView.Adapter<ProductoAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.item_producto_buscar,
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

        val productName : TextView = itemView.findViewById(R.id.tvName)
        val productBrand : TextView = itemView.findViewById(R.id.tvBrand)
        val productDescription : TextView = itemView.findViewById(R.id.tvDescription)
        val productImage : ImageView = itemView.findViewById(R.id.imageViewProducto)

    }

}
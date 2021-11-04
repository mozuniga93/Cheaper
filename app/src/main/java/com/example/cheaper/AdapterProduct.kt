package com.example.cheaper

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdapterProduct(private val productsList: ArrayList<Product>) :
    RecyclerView.Adapter<AdapterProduct.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_product,
            parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentItem = productsList[position]

        holder.productName.text = currentItem.productName
        holder.productBrand.text = currentItem.productBrand
        holder.productDescription.text = currentItem.productDescription
    }

    override fun getItemCount(): Int {
        return productsList.size
    }

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val productName : TextView = itemView.findViewById(R.id.tvName)
        val productBrand : TextView = itemView.findViewById(R.id.tvBrand)
        val productDescription : TextView = itemView.findViewById(R.id.tvDescription)

    }
}
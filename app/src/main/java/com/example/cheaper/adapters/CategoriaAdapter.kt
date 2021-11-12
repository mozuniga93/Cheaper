package com.example.cheaper.adapters

import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.example.cheaper.R
import com.example.cheaper.fragments.InicioFragment
import com.example.cheaper.model.Categoria
import com.squareup.picasso.Picasso
import androidx.appcompat.app.AppCompatActivity




class CategoriaAdapter(private val categoriasList: ArrayList<Categoria>):
    RecyclerView.Adapter<CategoriaAdapter.MyViewCategorieHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewCategorieHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.grid_layout_categoria,
            parent, false)
        return MyViewCategorieHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewCategorieHolder, position: Int) {
       val currentItem : Categoria = categoriasList[position]

        holder.nombreCategoria.text = currentItem.nombre
        Picasso.get().load(currentItem.foto).into(holder.fotoCategoria)

        holder.itemView.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                val activity=v!!.context as AppCompatActivity
                val inicioFragment = InicioFragment()
                activity.supportFragmentManager.beginTransaction().replace(R.id.categoriasListBuscar, inicioFragment).addToBackStack(null).commit()
            }
        })

    }

    override fun getItemCount(): Int {
        return categoriasList.size
    }

    class MyViewCategorieHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val nombreCategoria : TextView = itemView.findViewById(R.id.categoria_nombre)
        val fotoCategoria : ImageView = itemView.findViewById(R.id.categoria_imagen)
    }

}
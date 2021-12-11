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
        val precio = currentItem.precio.toString()
        val precioSigno = "₡" + precio


        holder.productoNombre.text = currentItem.nombre
        holder.productoPrecio.text = precioSigno
        holder.productoUbicacion.text = currentItem.lugar
        holder.productoNegocio.text = currentItem.tienda
        Picasso.get().load(currentItem.foto).into(holder.productImage)

        holder.productImage.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                val activity = v!!.context as AppCompatActivity

                val perfilProductoFragment = PerfilProductoFragment()
                var bundle = Bundle()
                bundle.putString("id", currentItem.id.toString())
                bundle.putString("nombre", currentItem.nombre.toString())
                bundle.putString("marca", currentItem.marca.toString())
                bundle.putString("categoria", currentItem.categoria.toString())
                bundle.putString("descripcion", currentItem.descripcion.toString())
                bundle.putString("imagen", currentItem.foto.toString())
                bundle.putString("usuario", currentItem.usuario.toString())
                perfilProductoFragment.arguments = bundle
                Log.e("bundle", bundle.toString())
                val transaction: FragmentTransaction = activity.supportFragmentManager.beginTransaction()
                transaction.replace(R.id.dashboard, perfilProductoFragment).addToBackStack(null)
                transaction.commit()
            }
        })

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
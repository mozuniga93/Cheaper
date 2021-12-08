package com.example.cheaper


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
import com.example.cheaper.model.Product
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
        holder.btnIrAPerfilProducto.setOnClickListener(object : View.OnClickListener{
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
                transaction.replace(R.id.inicio, perfilProductoFragment).addToBackStack(null)
                transaction.commit()
            }
        })
    }

    override fun getItemCount(): Int {
        return productsList.size
    }

    public class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val productName : TextView = itemView.findViewById(R.id.txtNombreProducto_perfil)
        val productBrand : TextView = itemView.findViewById(R.id.txtMarcaProducto_perfil)
        val productDescription : TextView = itemView.findViewById(R.id.txtDescripcionProducto_perfil)
        val productImage : ImageView = itemView.findViewById(R.id.imageViewProductoPerfil)
        val btnIrAPerfilProducto = itemView.findViewById<Button>(R.id.buttonProduct_perfil)



    }

}
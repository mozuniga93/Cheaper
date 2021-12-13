package com.example.cheaper.fragments

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cheaper.ProductoAdapter
import com.example.cheaper.R
import com.example.cheaper.adapters.AdapterProductoDashboard
import com.example.cheaper.adapters.BusquedaAdapter
import com.example.cheaper.adapters.CategoriaAdapter
import com.example.cheaper.model.Busqueda
import com.example.cheaper.model.Product
import com.example.cheaper.model.ProductoDashboard
import com.example.cheaper.model.Resenna
import com.example.cheaper.repositorios.UsuarioRepositorio
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.fragment_dashboard.view.*
import kotlinx.android.synthetic.main.fragment_perfil.view.*
import kotlinx.android.synthetic.main.fragment_perfil.view.txtNombre
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult

class DashboardFragment: Fragment() {

    private lateinit var productRecyclerView : RecyclerView
    private lateinit var busquedaRecyclerView: RecyclerView
    private lateinit var productArrayList : ArrayList<ProductoDashboard>
    private lateinit var resennasArrayList : ArrayList<Resenna>
    private lateinit var myAdapter : AdapterProductoDashboard
    private lateinit var busquedaAdapter : BusquedaAdapter
    private lateinit var db : FirebaseFirestore
    private lateinit var viewOfLayout: View
    private lateinit var data: ArrayList<Busqueda>
    private lateinit var cardView : CardView
    private var sTextSearch:String=""
    private var cantUsuarios : Int = 0
    private var cantProductos : Int = 0
    private var cantResennas : Int = 0



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewOfLayout =  inflater.inflate(R.layout.fragment_dashboard, container, false)
        cantUsuarios = 0
        cantProductos = 0
        cantResennas = 0
        cardView = viewOfLayout.findViewById(R.id.cardHeader)
        cardView.setVisibility(View.VISIBLE)
        productRecyclerView = viewOfLayout.findViewById(R.id.productosListDashboard)
        productRecyclerView.layoutManager = LinearLayoutManager(this.context)
        productArrayList = arrayListOf()
        resennasArrayList = arrayListOf()
        myAdapter = AdapterProductoDashboard(productArrayList)
        productRecyclerView.adapter = myAdapter
        myAdapter.notifyDataSetChanged()
        resennasArrayList = getResennas()
        getProductos(resennasArrayList)
        getUsuarios()
        data = arrayListOf()
        data.add(Busqueda("CB","Código de barras"))
        data.add(Busqueda("CA","Categorias"))
        data.add(Busqueda("NB","Nombre"))
        busquedaRecyclerView = viewOfLayout.findViewById(R.id.listaBusquedas)
        busquedaRecyclerView.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
        busquedaAdapter = BusquedaAdapter(data)
        busquedaRecyclerView.adapter = busquedaAdapter
        return viewOfLayout
    }


    private fun getProductos(resennas: ArrayList<Resenna>){
        var productoEncontrado: Boolean = false
            db = FirebaseFirestore.getInstance()
            db.collection("productos").
            get().
            addOnSuccessListener { documents ->
                productArrayList.clear()
                var productos = ArrayList<ProductoDashboard>()
                productos = arrayListOf()
                for (document in documents) {
                    cantProductos = cantProductos + 1
                    var producto = document.toObject(Product::class.java)
                    var productodashboard = document.toObject(ProductoDashboard::class.java)
                    producto.id = document.id
                    for(resenna in resennas){
                        if(resenna.producto.equals(producto.id)){
                            productoEncontrado = true
                            productodashboard.precio = resenna.precio
                            productodashboard.lugar = resenna.lugar
                            productodashboard.tienda = resenna.tienda
                        }
                    }
                    if(productoEncontrado && productos.size < 4){
                        productodashboard.id = producto.id
                        productodashboard.nombre = producto.nombre
                        productodashboard.foto = producto.foto
                        productodashboard.marca = producto.marca
                        productodashboard.descripcion = producto.descripcion
                        productodashboard.categoria = producto.categoria
                        productodashboard.usuario = producto.usuario
                        productos.add(productodashboard)
                    }
                   productoEncontrado = false
                }
                productArrayList.addAll(productos)
                productRecyclerView.adapter = AdapterProductoDashboard(productArrayList)
                viewOfLayout.textCantProductos.setText(cantProductos.toString())
            }
                .addOnFailureListener{ exception ->
                    Log.w(ContentValues.TAG, "Error getting products: ", exception)
                }

    }

    private fun getResennas(): ArrayList<Resenna> {
        var duplicateResenna: Boolean = false
        db = FirebaseFirestore.getInstance()
        db.collection("resennas").
        orderBy("precio").
        get().
        addOnSuccessListener { documents ->
            resennasArrayList.clear()
            var resennas = ArrayList<Resenna>()
            resennas = arrayListOf()
            for (document in documents) {
                cantResennas = cantResennas + 1
                var resenna = document.toObject(Resenna::class.java)
                resenna.id = document.id
                for(r in resennas){
                    if(r.producto.equals(resenna.producto)){
                        duplicateResenna = true
                    }
                }

                if(!duplicateResenna){
                    resennas.add(resenna)
                }

                duplicateResenna = false
            }
            resennasArrayList.addAll(resennas)
            viewOfLayout.textCantResennas.setText(cantResennas.toString())
        }
            .addOnFailureListener{ exception ->
                Log.w(ContentValues.TAG, "Error getting products: ", exception)
            }

        return resennasArrayList
    }

    private fun getUsuarios() {
        db = FirebaseFirestore.getInstance()
        db.collection("usuarios").
        get().
        addOnSuccessListener { documents ->
            for (document in documents) {
                cantUsuarios = cantUsuarios + 1
            }
            viewOfLayout.textCantUsuarios.setText(cantUsuarios.toString())
        }
            .addOnFailureListener{ exception ->
                Log.w(ContentValues.TAG, "Error getting products: ", exception)
            }

    }


}
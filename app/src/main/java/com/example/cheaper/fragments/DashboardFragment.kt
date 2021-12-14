package com.example.cheaper.fragments

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cheaper.PerfilProductoFragment
import com.example.cheaper.R
import com.example.cheaper.adapters.AdapterProductoDashboard
import com.example.cheaper.model.Product
import com.example.cheaper.model.ProductoDashboard
import com.example.cheaper.model.Resenna
import com.example.cheaper.utilidades.CodigoBarrasDialog
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.fragment_dashboard.view.*
import kotlinx.android.synthetic.main.fragment_perfil.view.*
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import kotlinx.android.synthetic.main.fragment_registrar_producto.*

class DashboardFragment: Fragment() {

    private lateinit var productRecyclerView : RecyclerView
    private lateinit var productArrayList : ArrayList<ProductoDashboard>
    private lateinit var resennasArrayList : ArrayList<Resenna>
    private lateinit var myAdapter : AdapterProductoDashboard
    private lateinit var db : FirebaseFirestore
    private lateinit var viewOfLayout: View
    private lateinit var cardView : CardView
    private var cantUsuarios : Int = 0
    private var cantProductos : Int = 0
    private var cantResennas : Int = 0
    private var codigoDeBarras = ""


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


        val btnEscanearProducto = viewOfLayout.findViewById<Button>(R.id.buttonEscanear)
        btnEscanearProducto.setOnClickListener{
            initScanner()
        }
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
                    for(resenna in resennas){
                        if(document.id == resenna.producto){
                            var producto = document.toObject(Product::class.java)
                            var productodashboard = document.toObject(ProductoDashboard::class.java)
                            producto.id = document.id
                            productodashboard.precio = resenna.precio
                            productodashboard.lugar = resenna.lugar
                            productodashboard.tienda = resenna.tienda
                            productodashboard.id = producto.id
                            productodashboard.nombre = producto.nombre
                            productodashboard.foto = producto.foto
                            productodashboard.marca = producto.marca
                            productodashboard.descripcion = producto.descripcion
                            productodashboard.categoria = producto.categoria
                            productodashboard.usuario = producto.usuario
                            productos.add(productodashboard)
                        }
                    }
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
                if(resennas.size < 4){
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

    private fun initScanner(){
        val integrator = IntentIntegrator.forSupportFragment(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
        integrator.setPrompt("Escanea el código de un producto")
        integrator.setTorchEnabled(true)
        integrator.setBeepEnabled(true)
        integrator.initiateScan()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val intentResultCode : IntentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        if (intentResultCode != null){
            if (intentResultCode.contents == null){
                Log.d("PRODUCTO CANCELAR", "El producto no pudo ser escaneado.")
            }
            else{
                Log.d("PRODUCTO ESCANEADO", "Producto escaneado exitosamente:  ${intentResultCode.contents}")
                codigoDeBarras = intentResultCode.contents
                buscarProductoCodigo()
            }
        }
    }

    private fun buscarProductoCodigo(){

        db = FirebaseFirestore.getInstance()
        db.collection("productos").
        whereEqualTo("codigoBarras",codigoDeBarras).
        get().
        addOnSuccessListener { documents ->
            for (document in documents) {
                var producto = document.toObject(Product::class.java)
                producto.id = document.id
                if(producto.id != null) {
                    val perfilProductoFragment = PerfilProductoFragment()
                    var bundle = Bundle()
                    bundle.putString("id", producto.id.toString())
                    bundle.putString("nombre", producto.nombre.toString())
                    bundle.putString("marca", producto.marca.toString())
                    bundle.putString("categoria", producto.categoria.toString())
                    bundle.putString("descripcion", producto.descripcion.toString())
                    bundle.putString("imagen", producto.foto.toString())
                    bundle.putString("usuario", producto.usuario.toString())
                    perfilProductoFragment.arguments = bundle
                    var fr = getFragmentManager()?.beginTransaction()
                    fr?.replace(R.id.dashboard, perfilProductoFragment)?.addToBackStack(null)
                    fr?.commit()
                }
            }
        }
            .addOnFailureListener{ exception ->
                val dialogo = CodigoBarrasDialog()
                dialogo.show(childFragmentManager, "CodigoBarrasDialog")
            }
    }



}
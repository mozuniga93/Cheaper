package com.example.cheaper.fragments

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cheaper.R
import com.example.cheaper.adapters.ActualizacionAdapter
import com.example.cheaper.model.Actualizacion
import com.google.firebase.firestore.FirebaseFirestore

class ActualizacionesFragment: Fragment() {

    private lateinit var actualizacionRecyclerView : RecyclerView
    private lateinit var actualizacionArrayList : ArrayList<Actualizacion>
    private lateinit var myAdapter : ActualizacionAdapter
    private lateinit var db : FirebaseFirestore
    private lateinit var viewOfLayout: View
    private var idProducto : String? = ""
    private var nombreProducto : Any? = ""
    private var marcaProducto : Any? = ""
    private var descripcionProducto : Any? = ""
    private var categoria : Any? = ""
    private var imagenProducto : Any? = ""
    private var usuarioProducto : Any? = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        obtenerInfoProducto()
        idProducto = arguments?.getString("productoId")
        viewOfLayout =  inflater.inflate(R.layout.fragment_actualizaciones, container, false)

        // Para volver al perfil
        viewOfLayout?.findViewById<TextView>(R.id.volverFromEditarProducto)?.setOnClickListener {
            val editarProductoFragment = EditarProductoFragment()
            var bundle = Bundle()
            bundle.putString("id", idProducto.toString())
            bundle.putString("nombre", nombreProducto.toString())
            bundle.putString("marca", marcaProducto.toString())
            bundle.putString("categoria", categoria.toString())
            bundle.putString("descripcion", descripcionProducto.toString())
            bundle.putString("imagen", imagenProducto.toString())
            bundle.putString("usuario", usuarioProducto.toString())
            editarProductoFragment.arguments = bundle
            val transaction: FragmentTransaction = parentFragmentManager!!.beginTransaction()
            transaction.replace(R.id.fragment_logs, editarProductoFragment)
            transaction.commit()
        }

        actualizacionRecyclerView = viewOfLayout.findViewById(R.id.actualizacionesLista)
        actualizacionRecyclerView.layoutManager = LinearLayoutManager(this.context)
        actualizacionArrayList = arrayListOf()
        myAdapter = ActualizacionAdapter(actualizacionArrayList)
        actualizacionRecyclerView.adapter = myAdapter
        buscarActualizaciones()

        return viewOfLayout
    }

    private fun buscarActualizaciones(){

        db = FirebaseFirestore.getInstance()
        db.collection("productos").document(idProducto!!).collection("actualizacionesProducto").
        get().
        addOnSuccessListener { documents ->
            actualizacionArrayList.clear()
            var actualizaciones = ArrayList<Actualizacion>()
            actualizaciones = arrayListOf()
            for (document in documents) {
                var actualizacion = document.toObject(Actualizacion::class.java)
                actualizacion.id = document.id
                actualizacion.nombreUsuario = actualizacion.nombreUsuario + " " + actualizacion.apellidoUsuario + " ha actualizado el item."
                actualizacion.fecha = "Fecha: " + actualizacion.fecha
                actualizaciones.add(actualizacion)
            }
            actualizacionArrayList.addAll(actualizaciones)
            actualizacionRecyclerView.adapter = ActualizacionAdapter(actualizacionArrayList)
        }
            .addOnFailureListener{ exception ->
                Log.w(ContentValues.TAG, "Error getting updates: ", exception)
            }
    }

    private fun obtenerInfoProducto() {
        val args = this.arguments
        idProducto = arguments?.getString("productoId")
        nombreProducto = args?.get("nombre")
        marcaProducto = args?.get("marca")
        descripcionProducto = args?.get("descripcion")
        categoria = args?.get("categoria")
        imagenProducto = args?.get("imagen")
        usuarioProducto = args?.get("usuario")
    }

}

package com.example.cheaper.fragments

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cheaper.ProductoAdapter
import com.example.cheaper.R
import com.example.cheaper.adapters.ActualizacionAdapter
import com.example.cheaper.adapters.CategoriaAdapter
import com.example.cheaper.model.Actualizacion
import com.example.cheaper.model.Product
import com.example.cheaper.repositorios.RepositorioConstantes
import com.example.cheaper.repositorios.UsuarioRepositorio
import com.google.firebase.firestore.*

class ActualizacionesFragment: Fragment() {

    private lateinit var actualizacionRecyclerView : RecyclerView
    private lateinit var actualizacionArrayList : ArrayList<Actualizacion>
    private lateinit var myAdapter : ActualizacionAdapter
    private lateinit var db : FirebaseFirestore
    private lateinit var viewOfLayout: View
    private var idProducto : String? = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        idProducto = arguments?.getString("productoId")
        viewOfLayout =  inflater.inflate(R.layout.fragment_actualizaciones, container, false)
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

}

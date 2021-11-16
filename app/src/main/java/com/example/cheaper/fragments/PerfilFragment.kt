package com.example.cheaper.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cheaper.MainActivity
import com.example.cheaper.R
import com.example.cheaper.adapters.AdapterProduct
import com.example.cheaper.adapters.AdapterResennasPerfil
import com.example.cheaper.model.Product
import com.example.cheaper.model.Resenna
import com.example.cheaper.repositorios.UsuarioRepositorio
import com.google.firebase.firestore.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_perfil.view.*





class PerfilFragment : Fragment() {

    private lateinit var productRecyclerView : RecyclerView
    private lateinit var myProductArrayList : ArrayList<Product>
    private lateinit var resennaRecyclerView : RecyclerView
    private lateinit var myResennaArrayList : ArrayList<Resenna>
    private lateinit var myAdapter : AdapterProduct
    private lateinit var myResennaAdapter: AdapterResennasPerfil
    private lateinit var db : FirebaseFirestore
    private lateinit var viewOfLayout: View


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewOfLayout = inflater.inflate(R.layout.fragment_perfil, container, false)

        // Me lleva al editar perfil
        viewOfLayout?.findViewById<TextView>(R.id.textViewEditarPerfil)?.setOnClickListener {
            val secondFragment = editarUsuarioFragment()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.perfil_container, secondFragment)?.commit()
        }


        cargarPerfil(viewOfLayout)
        mostrarListaMisProducos()
        mostrarListaMisResennas()

        val btnRegistrarProducto = viewOfLayout.findViewById<Button>(R.id.button_crear_prod)
        btnRegistrarProducto.setOnClickListener {
            val secondFragment = RegistrarProducto()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.perfil_container, secondFragment)?.commit()
        }

        return viewOfLayout
    }

    private fun mostrarListaMisProducos(){
        productRecyclerView = viewOfLayout.findViewById(R.id.recyclerProductPerfil)
        productRecyclerView.layoutManager = LinearLayoutManager(this.context)
        myProductArrayList = arrayListOf()
        myAdapter = AdapterProduct(myProductArrayList)
        productRecyclerView.adapter = myAdapter
        EventChangeProductosListener()
    }

    private fun mostrarListaMisResennas(){
        resennaRecyclerView = viewOfLayout.findViewById(R.id.recyclerResennaPerfil)
        resennaRecyclerView.layoutManager = LinearLayoutManager(this.context)
        myResennaArrayList = arrayListOf()
        myResennaAdapter = AdapterResennasPerfil(myResennaArrayList)
        resennaRecyclerView.adapter = myResennaAdapter
        EventChangeResennasListener()
    }

    private fun EventChangeProductosListener(){

        val usuario = UsuarioRepositorio.usuarioLogueado
        val myUserId = usuario.id.toString()

        // Crear una referencia a la base de datos y la colección que quiero consultar
        db = FirebaseFirestore.getInstance()
        val productosRef = db.collection("productos")

       // Crear un query a esa colección para buscar con datos de un campo específico
        val query = productosRef.whereEqualTo("usuario", myUserId)

       query.
        addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(
                value: QuerySnapshot?, error: FirebaseFirestoreException?
            ) {

                if (error != null){
                    Log.e("Firestore Error", error.message.toString())
                    return
                }

                for (dc : DocumentChange in value?.documentChanges!!){

                    if (dc.type == DocumentChange.Type.ADDED){
                        myProductArrayList.add(dc.document.toObject(Product::class.java))
                    }
                }
                myAdapter.notifyDataSetChanged()
            }
        })
    }

    private fun EventChangeResennasListener(){

        val usuario = UsuarioRepositorio.usuarioLogueado
        val myUserId = usuario.id.toString()

        // Crear una referencia a la base de datos y la colección que quiero consultar
        db = FirebaseFirestore.getInstance()
        val resennasRef = db.collection("resennas")

        // Crear un query a esa colección para buscar con datos de un campo específico
        val query = resennasRef.whereEqualTo("usuario", myUserId)

        query.
        addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(
                value: QuerySnapshot?, error: FirebaseFirestoreException?
            ) {

                if (error != null){
                    Log.e("Firestore Error", error.message.toString())
                    return
                }

                for (dc : DocumentChange in value?.documentChanges!!){

                    if (dc.type == DocumentChange.Type.ADDED){
                        var resenna = dc.document.toObject(Resenna::class.java)
                        resenna.id = dc.document.id
                        myResennaArrayList.add(resenna)
                    }
                }
                myResennaAdapter.notifyDataSetChanged()
            }
        })
    }


    fun cargarPerfil(viewOfLayout: View){

        val usuario = UsuarioRepositorio.usuarioLogueado

        viewOfLayout.txtNombre.setText(usuario.nombre.toString())
        viewOfLayout.txtApellido.setText(usuario.apellido.toString())

        val imageUri = usuario.foto.toString()
        val ivBasicImage = viewOfLayout.imageViewPerfil
        Picasso.get().load(imageUri).into(ivBasicImage)
    }

}

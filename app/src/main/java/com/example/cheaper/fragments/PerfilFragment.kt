package com.example.cheaper.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cheaper.*
import com.example.cheaper.R
import com.example.cheaper.adapters.AdapterProduct
import com.example.cheaper.model.Product
import com.example.cheaper.repositorios.UsuarioRepositorio
import com.google.firebase.firestore.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_perfil.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class PerfilFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    private lateinit var productRecyclerView : RecyclerView
    private lateinit var myProductArrayList : ArrayList<Product>
    private lateinit var myAdapter : AdapterProduct
    private lateinit var db : FirebaseFirestore
    private lateinit var viewOfLayout: View

   // private val usuario = UsuarioRepositorio.usuarioLogueado



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewOfLayout = inflater.inflate(R.layout.fragment_perfil, container, false)
        viewOfLayout?.findViewById<TextView>(R.id.txtCerrarSesion)?.setOnClickListener {
            cerrarSesion()
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


    private fun EventChangeProductosListener(){

        db = FirebaseFirestore.getInstance()
        db.collection("productos").
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

    private fun mostrarListaMisResennas(){

    }

    private fun EventChangeResennasListener(){

    }


    fun cerrarSesion(){
        Log.d("[Manati] PerfilFragment","Cerrando Sesion.")
        UsuarioRepositorio.cerrarSesion(this?.requireActivity())
        val intent = Intent(context, MainActivity::class.java)
        startActivity(intent)
        this?.requireActivity().finish()
    }

    fun cargarPerfil(viewOfLayout: View){

        val usuario = UsuarioRepositorio.usuarioLogueado

        viewOfLayout.txtNombre.setText(usuario.nombre.toString())
        viewOfLayout.txtApellido.setText(usuario.apellido.toString())

        val imageUri = usuario.foto.toString()
        val ivBasicImage = viewOfLayout.imageViewPerfil
        Picasso.get().load(imageUri).into(ivBasicImage)
    }



    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PerfilFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
package com.example.cheaper.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cheaper.*
import com.example.cheaper.R
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.fragment_perfil.*

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



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        cargarPerfil()
        viewOfLayout = inflater.inflate(R.layout.fragment_perfil, container, false)

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

    fun cargarPerfil(){

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
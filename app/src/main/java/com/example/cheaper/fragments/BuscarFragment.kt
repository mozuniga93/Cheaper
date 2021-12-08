package com.example.cheaper.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cheaper.R
import com.example.cheaper.adapters.CategoriaAdapter
import com.example.cheaper.model.Categoria
import com.example.cheaper.utilidades.Communicator
import com.google.firebase.firestore.*

class BuscarFragment : Fragment(),Communicator {

    private lateinit var categoriaRecyclerView : RecyclerView
    private lateinit var categoriaArrayList : ArrayList<Categoria>
    private lateinit var myAdapter : CategoriaAdapter
    private lateinit var cardCategoria : CardView
    private lateinit var db : FirebaseFirestore
    private lateinit var viewOfLayout: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewOfLayout =  inflater.inflate(R.layout.fragment_buscar, container, false)
        categoriaRecyclerView = viewOfLayout.findViewById(R.id.categoriasListBuscar)
        categoriaRecyclerView.layoutManager = GridLayoutManager(this.context, 2)
        categoriaArrayList = arrayListOf()
        myAdapter = CategoriaAdapter(categoriaArrayList)
        categoriaRecyclerView.adapter = myAdapter
        EventChangeListener()

        return viewOfLayout
    }

    private fun EventChangeListener(){

        db = FirebaseFirestore.getInstance()
        db.collection("categorias").
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
                        categoriaArrayList.add(dc.document.toObject(Categoria::class.java))
                    }
                }
                myAdapter.notifyDataSetChanged()
            }
        })
    }

    override fun passDataCom(pCategoria: String) {
       val bundle = Bundle()
        bundle.putString("categoria",pCategoria)
    }


}
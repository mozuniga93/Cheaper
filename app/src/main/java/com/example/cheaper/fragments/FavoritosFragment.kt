package com.example.cheaper.fragments

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cheaper.ProductoAdapter
import com.example.cheaper.R
import com.example.cheaper.model.Product
import com.example.cheaper.repositorios.RepositorioConstantes
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


private lateinit var productRecyclerView : RecyclerView
private lateinit var productArrayList : ArrayList<Product>
private lateinit var myAdapter : ProductoAdapter
private lateinit var db : FirebaseFirestore
private lateinit var viewOfLayout: View

/**
 * A simple [Fragment] subclass.
 * Use the [FavoritosFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FavoritosFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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
        viewOfLayout = inflater.inflate(R.layout.fragment_favoritos, container, false)
        productRecyclerView = viewOfLayout.findViewById(R.id.productsListFavoritos)
        productRecyclerView.layoutManager = LinearLayoutManager(this.context)
        productArrayList = arrayListOf()
        myAdapter = ProductoAdapter(productArrayList)
        productRecyclerView.adapter = myAdapter
        myAdapter.notifyDataSetChanged()
        getProductos()
        return viewOfLayout
    }


    private fun getProductos(){
        db = FirebaseFirestore.getInstance()
        db.collection(RepositorioConstantes.productosCollection).
        orderBy("nombre").
        get().
        addOnSuccessListener { documents ->
            productArrayList.clear()
            var productos = ArrayList<Product>()
            productos = arrayListOf()
            for (document in documents) {
                var producto = document.toObject(Product::class.java)
                producto.id = document.id
                productos.add(producto)
            }
            productArrayList.addAll(productos)
            productRecyclerView.adapter = ProductoAdapter(productArrayList)
        }
        .addOnFailureListener{ exception ->
            Log.w(ContentValues.TAG, "Error getting products: ", exception)
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FavoritosFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FavoritosFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
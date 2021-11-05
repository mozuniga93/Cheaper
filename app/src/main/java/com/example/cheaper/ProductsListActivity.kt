package com.example.cheaper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.google.firebase.firestore.*

class ProductsListActivity : AppCompatActivity() {

    private lateinit var dbref : DatabaseReference
    private lateinit var productRecyclerView : RecyclerView
    private lateinit var productArrayList : ArrayList<Product>
    private lateinit var myAdapter : AdapterProduct
    private lateinit var db : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_products_list)

        productRecyclerView = findViewById(R.id.productsList)
        productRecyclerView.layoutManager = LinearLayoutManager(this)
        productRecyclerView.setHasFixedSize(true)

        productArrayList = arrayListOf()

        myAdapter = AdapterProduct(productArrayList)

        EventChangeListener()
    }

    private fun EventChangeListener(){
        db = FirebaseFirestore.getInstance()
        db.collection("productos").
        addSnapshotListener(object : EventListener<QuerySnapshot>{
            override fun onEvent(
                value: QuerySnapshot?, error: FirebaseFirestoreException?
            ) {

                if (error != null){
                    Log.e("Firestore Error", error.message.toString())
                    return
                }

                for (dc : DocumentChange in value?.documentChanges!!){

                    if (dc.type == DocumentChange.Type.ADDED){
                        productArrayList.add(dc.document.toObject(Product::class.java))
                    }
                }
                myAdapter.notifyDataSetChanged()
            }
        })
    }
}
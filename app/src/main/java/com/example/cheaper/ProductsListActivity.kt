package com.example.cheaper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class ProductsListActivity : AppCompatActivity() {

    private lateinit var dbref : DatabaseReference
    private lateinit var productRecyclerView : RecyclerView
    private lateinit var productArrayList : ArrayList<Product>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_products_list)

        productRecyclerView = findViewById(R.id.productsList)
        productRecyclerView.layoutManager = LinearLayoutManager(this)
        productRecyclerView.setHasFixedSize(true)

        productArrayList = arrayListOf<Product>()
        getProductData()
    }

    private fun getProductData(){

        dbref = FirebaseDatabase.getInstance().getReference("productos")

        dbref.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()){

                    for(productSnapshot in snapshot.children){

                        val product = productSnapshot.getValue(Product::class.java)
                        productArrayList.add(product!!)
                    }

                    productRecyclerView.adapter = AdapterProduct(productArrayList)
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}
package com.example.cheaper.repositorios

import android.util.Log
import com.example.cheaper.model.Product
import com.example.cheaper.model.Resenna
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object ProductoRepositorio {

    fun crearNuevoProducto(nuevaProducto: Product){
        val db = Firebase.firestore
        db.collection(RepositorioConstantes.productosCollection).document()
            .set(nuevaProducto)
            .addOnSuccessListener { documentReference ->
                Log.d(UsuarioRepositorio.tag, "Producto creado exitosamente.")
            }
            .addOnFailureListener { e ->
                Log.w(UsuarioRepositorio.tag, "Error al crear el nuevo producto.", e)
            }
    }
}
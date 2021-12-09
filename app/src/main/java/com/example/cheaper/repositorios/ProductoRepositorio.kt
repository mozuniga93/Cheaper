package com.example.cheaper.repositorios

import android.util.Log
import com.example.cheaper.model.Product
import com.example.cheaper.model.Resenna
import com.example.cheaper.model.Usuario
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging

object ProductoRepositorio {

    val tag = "[Manati] ProductoRep"

    fun crearNuevoProducto(nuevaProducto: Product){
        val db = Firebase.firestore
        db.collection(RepositorioConstantes.productosCollection).document()
            .set(nuevaProducto)
            .addOnSuccessListener { documentReference ->
                Log.d(tag,"Producto creado exitosamente.")
            }
            .addOnFailureListener { e ->
                Log.w(tag, "Error al crear el nuevo producto.", e)
            }
    }

    fun registrarTopic(producto: Product){
        Firebase.messaging.subscribeToTopic(producto?.id!!)
            .addOnCompleteListener { task ->
                var msg = "Subscripción exitosa"
                if (!task.isSuccessful) {
                    msg = "Subscripción no exitosa"
                }
                Log.d(tag, msg)
            }
    }

    fun removerTopic(producto: Product){
        Firebase.messaging.unsubscribeFromTopic(producto?.id!!)
            .addOnCompleteListener { task ->
                var msg = "Desinscripción exitosa"
                if (!task.isSuccessful) {
                    msg = "Desinscripción no exitosa"
                }
                Log.d(tag, msg)
            }
    }

    fun registrarSeguidorProducto(producto: Product, usuario:Usuario){
        val db = Firebase.firestore
        db.collection(RepositorioConstantes.productosCollection).document(producto?.id!!)
            .collection(RepositorioConstantes.productosSeguidores)
            .document(usuario?.id!!)
            .set(usuario?.id!!)
            .addOnSuccessListener { documentReference ->
                Log.d(UsuarioRepositorio.tag, "Usuario agregado a seguidores del producto.")
            }
            .addOnFailureListener { e ->
                Log.w(UsuarioRepositorio.tag, "Error al agregar seguidor.", e)
            }
    }

    fun removerSeguidorProducto(producto: Product, usuario:Usuario){
        val db = Firebase.firestore
        db.collection(RepositorioConstantes.productosCollection).document(producto?.id!!)
            .collection(RepositorioConstantes.productosSeguidores)
            .document(usuario?.id!!)
            .set(usuario?.id!!)
            .addOnSuccessListener { documentReference ->
                Log.d(UsuarioRepositorio.tag, "Usuario agregado a seguidores del producto.")
            }
            .addOnFailureListener { e ->
                Log.w(UsuarioRepositorio.tag, "Error al agregar seguidor.", e)
            }
    }
    fun actualizarProcuto(producto: Product){

        val db = Firebase.firestore
        val docRef = db.collection(RepositorioConstantes.productosCollection).
        document(producto.id.toString()).set(producto).addOnSuccessListener {
            Log.d(UsuarioRepositorio.tag, "Producto actualizado exitosamente.")
        }.addOnFailureListener {e ->
            Log.w(UsuarioRepositorio.tag, "Error al actualizar el producto.", e)
        }
    }

}
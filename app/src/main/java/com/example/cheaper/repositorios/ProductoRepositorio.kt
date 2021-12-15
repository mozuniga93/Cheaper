package com.example.cheaper.repositorios

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.cheaper.model.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
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


    @RequiresApi(Build.VERSION_CODES.O)
    fun actualizarProcuto(producto: Product, usuario: Usuario){

        val db = Firebase.firestore
        db.collection(RepositorioConstantes.productosCollection).
        document(producto.id.toString()).set(producto).addOnSuccessListener {
            Log.d(UsuarioRepositorio.tag, "Producto actualizado exitosamente.")
            registrarActualizacionProducto(usuario, producto)
        }.addOnFailureListener {e ->
            Log.w(UsuarioRepositorio.tag, "Error al actualizar el producto.", e)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun registrarActualizacionProducto(usuario: Usuario, product: Product){
        val db = Firebase.firestore

        val nuevaActualizacionProducto = Actualizacion(
            "",
            usuario.nombre.toString(),
            usuario.apellido.toString(),
            product.nombre,
            product.marca,
            product.descripcion,
            product.categoria,
            usuario.foto.toString(),
            LocalDate.now().toString()
        )

        db.collection(RepositorioConstantes.productosCollection).document(product.id!!)
            .collection(RepositorioConstantes.productosCollectionActualizacionProductos)
            .document()
            .set(nuevaActualizacionProducto)
            .addOnSuccessListener { documentReference ->
                Log.d(tag, "Atualización agregada exitosamente.")
            }
            .addOnFailureListener { e ->
                Log.w(tag, "Error al agregar actualizaciónroducto favorito.", e)
            }
    }

}
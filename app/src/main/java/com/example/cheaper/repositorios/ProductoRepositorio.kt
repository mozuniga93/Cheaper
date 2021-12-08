package com.example.cheaper.repositorios

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.cheaper.model.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.time.LocalDate

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
            product.foto,
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
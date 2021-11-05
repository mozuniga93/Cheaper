package com.example.cheaper.repositorios

import android.util.Log
import com.example.cheaper.model.Resenna
import com.example.cheaper.model.Usuario
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object ResennaRepositorio {

    val tag = "[Manati] UsuarioRep"

    lateinit var usuarioLogueado: Usuario
    lateinit var authUsuario: FirebaseUser

    fun crearNuevaResenna(nuevaResenna:Resenna){
        val db = Firebase.firestore
        db.collection(RepositorioConstantes.resennasCollection).document(nuevaResenna?.id!!)
            .set(nuevaResenna)
            .addOnSuccessListener { documentReference ->
                Log.d(UsuarioRepositorio.tag, "Usuario creado exitosamente.")
            }
            .addOnFailureListener { e ->
                Log.w(UsuarioRepositorio.tag, "Error al crear el nuevo usuario.", e)
            }
    }


}
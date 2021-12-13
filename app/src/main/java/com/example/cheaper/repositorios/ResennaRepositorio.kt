package com.example.cheaper.repositorios

import android.util.Log
import com.example.cheaper.model.Resenna
import com.example.cheaper.model.ResennaVotada
import com.example.cheaper.model.Usuario
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


object ResennaRepositorio {

    val tag = "[Manati] ResennaRep"

    fun crearNuevaResenna(nuevaResenna:Resenna){
        val db = Firebase.firestore
        db.collection(RepositorioConstantes.resennasCollection).document()
            .set(nuevaResenna)
            .addOnSuccessListener { documentReference ->
                Log.d(tag, "Resenna creada exitosamente.")
            }
            .addOnFailureListener { e ->
                Log.w(tag, "Error al crear nueva resenna.", e)
            }
    }

    fun registrarVotoResenna(usuario: Usuario, resenna: Resenna){
        val db = Firebase.firestore
        var nuevaResennaVotada = ResennaVotada(
            resenna.id,
            usuario?.id!!,
            true
        )

        Log.d(tag, nuevaResennaVotada.toString())
        Log.d("Resenna id", resenna.id!!)

        db.collection(RepositorioConstantes.resennasCollection).document(resenna.id!!)
            .collection(RepositorioConstantes.votoResennaCollection)
            .document(usuario?.id!!)
            .set(nuevaResennaVotada)
            .addOnSuccessListener { documentReference ->
                Log.d(UsuarioRepositorio.tag, "Voto en resenna agregado exitosamente.")
            }
            .addOnFailureListener { e ->
                Log.w(UsuarioRepositorio.tag, "Error al agregar voto resenna.", e)
            }
    }
}
package com.example.cheaper.repositorios

import android.util.Log
import com.example.cheaper.model.Resenna
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
}
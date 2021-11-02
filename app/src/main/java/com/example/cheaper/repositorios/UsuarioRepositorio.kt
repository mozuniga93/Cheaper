package com.example.cheaper.repositorios

import android.util.Log
import com.example.cheaper.model.Usuario
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import javax.security.auth.callback.Callback

object UsuarioRepositorio {

    val tag = "[Manati] UsuarioRep"

    lateinit var usuarioLogueado: Usuario
    lateinit var authUsuario: FirebaseUser

    fun obtenerUsuarioPorTelefono(telefono:String){

    }

    fun crearNuevoUsuario(usuarioNuevo:Usuario){
        val db = Firebase.firestore
        db.collection(RepositorioConstantes.usuariosCollection).document(usuarioNuevo?.id!!)
            .set(usuarioNuevo)
            .addOnSuccessListener { documentReference ->
                Log.d(tag, "Usuario creado exitosamente.")
            }
            .addOnFailureListener { e ->
                Log.w(tag, "Error al crear el nuevo usuario.", e)
            }
    }

    fun buscarUsuarioPorId(uid: String){
        val db = Firebase.firestore
        val docRef = db.collection(RepositorioConstantes.usuariosCollection).document(uid)
        var usuario = docRef.get().addOnSuccessListener {
            Log.d(tag,"Usuario obtenido. ${it.toObject<Usuario>()}")
        }
    }

    suspend fun buscarUsuarioPorIdSus(uid: String):Usuario?{
        val db = Firebase.firestore
        val docRef = db.collection(RepositorioConstantes.usuariosCollection).document(uid)
        var usuario = docRef.get().await().toObject<Usuario>()
        return usuario
    }

    suspend fun cargarUsuarioLogueado(){
        authUsuario = Firebase.auth.currentUser!!
        val db = Firebase.firestore
        val docRef = db.collection(RepositorioConstantes.usuariosCollection).document(authUsuario.uid)
        val res = docRef.get().await()
        if(res != null){
            usuarioLogueado = res.toObject<Usuario>()!!
            Log.d(tag,"Usuario $usuarioLogueado")
        }else{
            Log.d(tag,"Usuario nulo.")
        }
    }
}
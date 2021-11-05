package com.example.cheaper

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cheaper.model.Usuario
import com.example.cheaper.repositorios.RepositorioConstantes
import com.example.cheaper.repositorios.UsuarioRepositorio
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class VerificacionViewModel : ViewModel(){
    var usuario = MutableLiveData<Usuario>()


    // create instance of firebase auth
    lateinit var auth: FirebaseAuth

    fun  doWork(){
        viewModelScope.launch {
            usuario.value = cargarUsuario()
        }
    }

    private suspend fun cargarUsuario(): Usuario {

        auth=FirebaseAuth.getInstance()
        return withContext(Dispatchers.IO){

            Log.d(UsuarioRepositorio.tag,"firestore.")
            UsuarioRepositorio.authUsuario = Firebase.auth.currentUser!!
            val db = Firebase.firestore
            val docRef = db.collection(RepositorioConstantes.usuariosCollection).document(
                UsuarioRepositorio.authUsuario.uid)
            val res = docRef.get().await()
            if(res != null){
                UsuarioRepositorio.usuarioLogueado = res.toObject<Usuario>()!!
                Log.d(UsuarioRepositorio.tag,"Usuario ${UsuarioRepositorio.usuarioLogueado}")
            }else{
                Log.d(UsuarioRepositorio.tag,"Usuario nulo.")
        }
            return@withContext UsuarioRepositorio.usuarioLogueado
        }
    }
}
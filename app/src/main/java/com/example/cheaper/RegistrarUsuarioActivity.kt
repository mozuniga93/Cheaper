package com.example.cheaper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.cheaper.model.Usuario
import com.example.cheaper.repositorios.UsuarioRepositorio
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegistrarUsuarioActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_usuario)
    }

    fun registrar(){
        //TODO
        Log.d("Login log", "Enviando a registrar")
        var authUsuario = Firebase.auth.currentUser!!
        var usuario = Usuario(
            authUsuario?.phoneNumber!!,
            "Monica",
            "Zuniga",
            authUsuario?.uid!!
        )
        UsuarioRepositorio.crearNuevoUsuario(usuario)
    }

}
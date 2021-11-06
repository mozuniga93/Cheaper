package com.example.cheaper

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.example.cheaper.model.Usuario
import com.example.cheaper.repositorios.UsuarioRepositorio
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegistrarUsuarioActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_usuario)
        findViewById<Button>(R.id.btn_registrarme).setOnClickListener {
            registrar()
        }

    }

    fun registrar(){
        Log.d("Login log", "Enviando a registrar")
        var authUsuario = Firebase.auth.currentUser!!
        var usuario = Usuario(
            authUsuario?.uid!!,
            findViewById<EditText>(R.id.txt_Nombre).text.toString(),
            findViewById<EditText>(R.id.txt_apellido).text.toString(),
            authUsuario?.phoneNumber!!,
            "https://firebasestorage.googleapis.com/v0/b/cheaper-manati4.appspot.com/o/hot-girl-on-phone.png?alt=media&token=84a32359-b91f-43a8-a51d-2caeddc34565",
            true
        )
        UsuarioRepositorio.crearNuevoUsuario(usuario, this)
        val intent = Intent(this , MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}
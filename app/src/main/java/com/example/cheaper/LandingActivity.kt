package com.example.cheaper

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.cheaper.model.Usuario
import com.example.cheaper.repositorios.UsuarioRepositorio
import com.google.firebase.FirebaseException
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class LandingActivity : AppCompatActivity() {

    val tag = "[Manati] Landing"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)

        revisarPrimerInicio()
    }

    fun revisarPrimerInicio() {
        val sharedPref = this?.getPreferences(Context.MODE_PRIVATE) ?: return
        val primerInicio = false //sharedPref.getBoolean(getString(R.string.app_name)+"-login-inicio", false)

        Log.d(tag , "Es primer uso: $primerInicio")
        if(primerInicio){
            primerInicio()
        }else{
            //enviarVerificacionActivity()
            revisarSesion()
        }
    }

    fun primerInicio() {

        //TODO Mostrar mensajes o guia de primer instalacion

        val sharedPref = this?.getPreferences(Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putBoolean(getString(R.string.app_name)+"-login-inicio", false)
            apply()
        }

        enviarVerificacionActivity()
    }

    fun enviarVerificacionActivity(){
        val intent = Intent(this , VerificacionActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun revisarSesion() {
        Log.d(tag,"revisando sesion...")
        val sharedPref = this?.getPreferences(Context.MODE_PRIVATE) ?: return

        sharedPref.all.forEach{
            Log.d(tag,"Preference: ${it.toString()}")
        }


        val usuarioId = sharedPref.getString(getString(R.string.app_name)+"-login-id", "")
        Log.d(tag,"Shared preference de usuario ID: $usuarioId")
        if(usuarioId!=""){
            UsuarioRepositorio.usuarioLogueado = Usuario(
                usuarioId,
                sharedPref.getString(getString(R.string.app_name)+"-login-nombre", ""),
                sharedPref.getString(getString(R.string.app_name)+"-login-apellido", ""),
                sharedPref.getString(getString(R.string.app_name)+"-login-telefono", ""),
                sharedPref.getString(getString(R.string.app_name)+"-login-foto", "")
            )
            Log.d(tag, "Usuario logueado")
            Log.d(tag, UsuarioRepositorio.usuarioLogueado.toString())
        }

        enviarMain()
    }

    private fun enviarMain() {
        val intent = Intent(this , MainActivity::class.java)
        startActivity(intent)
        finish()
    }


}
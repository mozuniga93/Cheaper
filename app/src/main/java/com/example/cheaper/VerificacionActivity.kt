package com.example.cheaper

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.cheaper.model.Usuario
import com.example.cheaper.repositorios.UsuarioRepositorio
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VerificacionActivity : AppCompatActivity() {

    // get reference of the firebase auth
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verficacion)

        auth=FirebaseAuth.getInstance()

        // get storedVerificationId from the intent
        val storedVerificationId= intent.getStringExtra("storedVerificationId")

        // fill otp and call the on click on button
        findViewById<Button>(R.id.login).setOnClickListener {
            val otp = findViewById<EditText>(R.id.et_otp).text.trim().toString()
            if(otp.isNotEmpty()){
                val credential : PhoneAuthCredential = PhoneAuthProvider.getCredential(
                    storedVerificationId.toString(), otp)
                signInWithPhoneAuthCredential(credential)
            }else{
                Toast.makeText(this,"Ingresar código de verificación", Toast.LENGTH_SHORT).show()
            }
        }
    }
    // verifies if the code matches sent by firebase
    // if success start the new activity in our case it is main Activity
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    siguienteActivity()
                } else {
                    // Sign in failed, display a message and update the UI
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Log.d("Login log", "Invalid OTP")
                        Toast.makeText(this,"Código de verificación inválido", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    fun enviarMain(){
        val intent = Intent(this , MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun enviarRegistrar(){
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
        //enviarMain()
    }

    fun siguienteActivity(){
        GlobalScope.launch(Dispatchers.IO) {
            UsuarioRepositorio.cargarUsuarioLogueado()
            if(UsuarioRepositorio.usuarioLogueado ==null){
//                withContext(Dispatchers.Main){
//
//                    val intent = Intent(this@VerificacionActivity , MainActivity::class.java)
//                    startActivity(intent)
//                    finish()
//                }
            }else{
//                withContext(Dispatchers.Main){
//
//                    val intent = Intent(this@VerificacionActivity , MainActivity::class.java)
//                    startActivity(intent)
//                    finish()
//                }
            }
        }
        enviarMain()

    }

}
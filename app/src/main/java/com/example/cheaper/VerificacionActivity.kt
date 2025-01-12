package com.example.cheaper

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.example.cheaper.model.Usuario
import com.example.cheaper.repositorios.RepositorioConstantes
import com.example.cheaper.repositorios.UsuarioRepositorio
import com.example.cheaper.repositorios.UsuarioRepositorio.guardarSesion
import com.example.cheaper.utilidades.TelefonoDialog
import com.google.firebase.FirebaseException
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class VerificacionActivity : FragmentActivity() {

    // this stores the phone number of the user
    var number : String =""

    // create instance of firebase auth
    lateinit var auth: FirebaseAuth


    private lateinit var firebaseAnalytics: FirebaseAnalytics

    // we will use this to match the sent otp from firebase
    lateinit var storedVerificationId:String
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    val tag = "[Manati] Login"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verficacion)

        auth=FirebaseAuth.getInstance()

        firebaseAnalytics = Firebase.analytics

        findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.login).setOnClickListener {
            ingresarNumeroTelefono()
        }

        findViewById<TextView>(R.id.verificacion_numero_requerido).setOnClickListener {
            val dialogo = TelefonoDialog()
            dialogo.show(supportFragmentManager, "TelefonoDialog")
        }

        findViewById<TextView>(R.id.verificacion_volver_texto).setOnClickListener {
            finish()
        }

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d(tag , "Verficación exitosa.")
                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.d(tag , "Problema con verificación:  $e")
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d(tag,"Código enviado. ID de verificación: $verificationId")
                storedVerificationId = verificationId
                resendToken = token
                //this@LandingActivity.enableUserManuallyInputCode()


                findViewById<EditText>(R.id.et_otp).setText("")
                findViewById<EditText>(R.id.et_otp).setHint("")
                findViewById<TextView>(R.id.tv_otp).setText("Ingresar código SMS")
                findViewById<Button>(R.id.login).setText("Verificar")



                iniciarVerificacion()
            }

            override fun onCodeAutoRetrievalTimeOut(verificationId: String) {
                //Log.d(tag,"Código no se puedo obtener automaticamente, verification ID: $verificationId")
            }
        }

    }

    private fun ingresarNumeroTelefono() {
        number = findViewById<EditText>(R.id.et_otp).text.trim().toString()

        if (number.isNotEmpty()){
            number = "+506$number"


            mostrarCargando()
            sendVerificationCode(number)

        }else{
            Toast.makeText(this,"Ingresar número telefónico", Toast.LENGTH_SHORT).show()
        }
    }

    fun mostrarCargando(){
        var progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Cargando...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        Handler().postDelayed({
            progressDialog.dismiss()
        }, 3000)
    }

    private fun sendVerificationCode(number: String) {
        Log.d(tag , "Phone number $number")
        auth.getFirebaseAuthSettings().setAppVerificationDisabledForTesting(false)

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(number) // Phone number to verify
            .setTimeout(3, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        Log.d(tag, "Autenticación iniciada...")
    }

    fun iniciarVerificacion(){

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
    // if success start the new activity
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

    fun siguienteActivity(){
        Log.d(tag, "Cargando usuario")
        UsuarioRepositorio.authUsuario = Firebase.auth.currentUser!!
        val db = Firebase.firestore
        val docRef = db.collection(RepositorioConstantes.usuariosCollection)
            .document(UsuarioRepositorio.authUsuario.uid)
        docRef.get().addOnSuccessListener { document ->

            val tag = "[Manati] Login"
            if (document?.getData() != null) {
                var usuario = document.toObject<Usuario>()!!
                if(usuario?.habilitado!!){
                    UsuarioRepositorio.usuarioLogueado = usuario
                    Log.d(tag, "Usuario logueado")
                    Log.d(tag, UsuarioRepositorio.usuarioLogueado.toString())
                    guardarSesion(this)
                    UsuarioRepositorio.cargarProductosFavoritos()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }else{
                    UsuarioRepositorio.cerrarSesion(this)
                    Toast.makeText(this,"No se puedo iniciar sesión, el número telefónico ha sido desactivado.", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            } else {
                Log.d(tag, "No existe usuario con este numero.")
                val intent = Intent(this, RegistrarUsuarioActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }



}
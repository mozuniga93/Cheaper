package com.example.cheaper

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.cheaper.databinding.ActivityRegistrarUsuarioBinding
import com.example.cheaper.model.Usuario
import com.example.cheaper.repositorios.UsuarioRepositorio
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_registrar_usuario.*
import kotlinx.android.synthetic.main.fragment_registrar_producto.*
import java.text.SimpleDateFormat
import java.util.*

class RegistrarUsuarioActivity : AppCompatActivity() {

    lateinit var binding : ActivityRegistrarUsuarioBinding
    lateinit var ImageUri : Uri
    var imagenUrlFinal = "https://firebasestorage.googleapis.com/v0/b/cheaper-manati4.appspot.com/o/user.png?alt=media&token=98ae4512-acf3-4254-a780-e893db9b19b7"

    val tag = "[Manati] RegistrarUsua"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrarUsuarioBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_registrar_usuario)
        cargarImagenPorDefecto()

        findViewById<de.hdodenhof.circleimageview.CircleImageView>(R.id.usuarioImagen).setOnClickListener {
            selectImage()
        }

        findViewById<Button>(R.id.btn_registrarme).setOnClickListener {
            if(::ImageUri.isInitialized)
                subirImagenYRegistrarUsuario()
            else
                registrar()
        }
    }

    private fun selectImage() {
        val intent = Intent()
        intent.type= "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent,100)
    }

    fun cargarImagenPorDefecto(){
        Picasso.get().load(imagenUrlFinal).into(this.imagenIdProducto)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==100 && resultCode == RESULT_OK){
            ImageUri = data?.data!!
            Picasso.get().load(ImageUri).into(this.usuarioImagen)
        }
    }

    fun registrar(){
        Log.d("Login log", "Enviando a registrar")
        var authUsuario = Firebase.auth.currentUser!!
        var nuevoUsuario = Usuario(
            authUsuario?.uid!!,
            findViewById<EditText>(R.id.txt_Nombre).text.toString(),
            findViewById<EditText>(R.id.txt_apellido).text.toString(),
            authUsuario?.phoneNumber!!,
            imagenUrlFinal,
            true
        )
        UsuarioRepositorio.crearNuevoUsuario(nuevoUsuario, this)
        UsuarioRepositorio.usuarioLogueado = nuevoUsuario
        UsuarioRepositorio.guardarSesion(this)
        val intent = Intent(this , MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun subirImagenYRegistrarUsuario() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Subiendo imagen...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        //*GUID*
        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val now = Date()
        val fileName = formatter.format(now)
        val path = "Usuarios/"

        val storageReference = FirebaseStorage.getInstance().getReference("$path$fileName")

        storageReference.putFile(ImageUri).addOnSuccessListener {
            it.metadata?.reference?.downloadUrl?.addOnSuccessListener {
                imagenUrlFinal = it.toString()
                registrar()
            }
            if(progressDialog.isShowing) progressDialog.dismiss()
        }.addOnFailureListener{
            if(progressDialog.isShowing) progressDialog.dismiss()
            Toast.makeText(this@RegistrarUsuarioActivity, "Error al subir imagen",Toast.LENGTH_SHORT).show()
        }
    }

}
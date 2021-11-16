package com.example.cheaper.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import com.example.cheaper.MainActivity
import com.example.cheaper.R
import com.example.cheaper.model.Product
import com.example.cheaper.model.Usuario
import com.example.cheaper.repositorios.ProductoRepositorio
import com.example.cheaper.repositorios.UsuarioRepositorio
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_editar_usuario.*
import kotlinx.android.synthetic.main.fragment_editar_usuario.view.*
import kotlinx.android.synthetic.main.fragment_perfil.view.*
import kotlinx.android.synthetic.main.fragment_registrar_producto.*
import java.text.SimpleDateFormat
import java.util.*


class editarUsuarioFragment : Fragment() {


    private lateinit var viewOfLayout: View
    lateinit var ImageUri : Uri
    var imagenUrlFinal = "https://firebasestorage.googleapis.com/v0/b/cheaper-manati4.appspot.com/o/user.png?alt=media&token=98ae4512-acf3-4254-a780-e893db9b19b7"


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewOfLayout = inflater.inflate(R.layout.fragment_editar_usuario, container, false)
        val empty = "empty"
        ImageUri = empty.toUri()


        viewOfLayout?.findViewById<TextView>(R.id.txtCerrarSesion)?.setOnClickListener {
            cerrarSesion()
        }
        // Para volver al perfil
        viewOfLayout?.findViewById<TextView>(R.id.volverFromEditarUsuario)?.setOnClickListener {
            val perfilFragment = PerfilFragment()
            (activity as MainActivity?)?.makeCurrentFragment(perfilFragment)
        }

        //Funciones de botones
        val btnActualizarImagen = viewOfLayout.btnActualizarImgUsuario
        btnActualizarImagen.setOnClickListener {
            fileManager()
        }

        val btnActualizarUsuario = viewOfLayout.btn_actualizar_usuario
        btnActualizarUsuario.setOnClickListener {
            val nombre = viewOfLayout.txtNombreUEditar
            fileUpload(nombre)
        }


        // Cargar los datos del usuario
        cargarPerfil(viewOfLayout)

        return viewOfLayout
    }

    private fun fileManager(){
        val intent = Intent()
        intent.type= "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent,100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==100 && resultCode == Activity.RESULT_OK){
            ImageUri = data?.data!!
            Picasso.get().load(ImageUri).into(this.fotoUsuarioEditar)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fileUpload(nombre: EditText) {

        val usuario = UsuarioRepositorio.usuarioLogueado

        if (ImageUri.equals("empty".toUri())){
            actualizarUsuario(nombre, usuario)
        }else{
            //*GUID*
            val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
            val now = Date()
            val fileName = formatter.format(now)
            val path = "Imagenes/"

            val storageReference = FirebaseStorage.getInstance().getReference("$path$fileName")

            storageReference.putFile(ImageUri).addOnSuccessListener {
                it.metadata?.reference?.downloadUrl?.addOnSuccessListener {
                    imagenUrlFinal = it.toString()
                    usuario.foto = imagenUrlFinal
                    actualizarUsuario(nombre, usuario)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun actualizarUsuario(nombre: EditText, usuario : Usuario) {


        usuario.nombre = nombre.text.toString()
        UsuarioRepositorio.actualizarUsuario(usuario)

        // Volver al perfil luego de actualizar
        val perfilFragment = PerfilFragment()
        (activity as MainActivity?)?.makeCurrentFragment(perfilFragment)

    }



    fun cargarPerfil(viewOfLayout: View){

        val usuario = UsuarioRepositorio.usuarioLogueado

        viewOfLayout.txtTelefonoUsuarioEditar.setText(usuario.telefono.toString())
        viewOfLayout.txtTelefonoUsuarioEditar.isEnabled = false;
        viewOfLayout.txtNombreUEditar.setText(usuario.nombre.toString())

        val imageUri = usuario.foto.toString()
        val ivBasicImage = viewOfLayout.fotoUsuarioEditar
        Picasso.get().load(imageUri).into(ivBasicImage)
    }


    fun cerrarSesion(){
        Log.d("[Manati] EditarUsua","Cerrando Sesion.")
        UsuarioRepositorio.cerrarSesion(this?.requireActivity())
        val intent = Intent(context, MainActivity::class.java)
        startActivity(intent)
        this?.requireActivity().finish()
    }

} // FIN
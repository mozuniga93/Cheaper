package com.example.cheaper.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import com.example.cheaper.MainActivity
import com.example.cheaper.R
import com.example.cheaper.databinding.FragmentRegistrarProductoBinding
import com.example.cheaper.model.Product
import com.example.cheaper.repositorios.ProductoRepositorio
import com.example.cheaper.repositorios.UsuarioRepositorio
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_registrar_usuario.*
import kotlinx.android.synthetic.main.fragment_registrar_producto.*
import java.text.SimpleDateFormat
import java.util.*


class RegistrarProducto : Fragment() {

    lateinit var ImageUri : Uri
    var imagenUrlFinal = "https://firebasestorage.googleapis.com/v0/b/cheaper-manati4.appspot.com/o/user.png?alt=media&token=98ae4512-acf3-4254-a780-e893db9b19b7"


    private var _binding: FragmentRegistrarProductoBinding? = null
    private val binding get() = _binding!!


    override fun onResume() {
        super.onResume()
        val categorias = resources.getStringArray(R.array.categotias)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, categorias)
        binding.autoCompleteTextView.setAdapter(arrayAdapter)
    }



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentRegistrarProductoBinding.inflate(inflater, container, false)

        _binding!!.root.findViewById<TextView>(R.id.volverFromRegistrarProducto).setOnClickListener {
            val perfilFragment = PerfilFragment()
            (activity as MainActivity?)?.makeCurrentFragment(perfilFragment)
        }

        val btnRegistrarProducto = _binding!!.root.findViewById<Button>(R.id.btn_actualizar_usuario)
        btnRegistrarProducto.setOnClickListener {
            val nombre = binding.txtNombreProducto
            val marca = binding.txtMarcaProducto
            val descripcion = binding.txtDescripcionProducto
            val categria = binding.autoCompleteTextView
            verificarCampostxt(nombre, marca, descripcion, categria)
        }

        val btnCargarImagen = _binding!!.root.findViewById<Button>(R.id.btnCargarImgProducto)
        btnCargarImagen.setOnClickListener {
            fileManager()
        }

        return binding.root
    }

    private fun fileManager(){
        val intent = Intent()
        intent.type= "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent,100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==100 && resultCode == RESULT_OK){
            ImageUri = data?.data!!
            Picasso.get().load(ImageUri).into(this.imagenIdProducto)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fileUpload(nombre: EditText, marca: EditText, descripcion: EditText, categria: EditText) {

        //*GUID*
        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val now = Date()
        val fileName = formatter.format(now)
        val path = "Imagenes/"

        val storageReference = FirebaseStorage.getInstance().getReference("$path$fileName")

        storageReference.putFile(ImageUri).addOnSuccessListener {
            it.metadata?.reference?.downloadUrl?.addOnSuccessListener {
                imagenUrlFinal = it.toString()
                crearNuevoProducto(nombre, marca, descripcion, categria)
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun verificarCampostxt(nombre: EditText, marca: EditText, descripcion: EditText, categria: EditText) {

        if (!nombre.text.isNullOrBlank() && !marca.text.isNullOrBlank()
            && !descripcion.text.isNullOrBlank() && !categria.equals("Categoría")) {
            fileUpload(nombre, marca, descripcion, categria)
        } else {
            Log.d("Registro de producto", "Registro fallido")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun crearNuevoProducto(nombre: EditText, marca: EditText, descripcion: EditText, categria: EditText) {

        val usuario = UsuarioRepositorio.usuarioLogueado
        val idUsuario = usuario.id.toString()
        val nombretxt = nombre.text.toString()
        val marcatxt = marca.text.toString()
        val descripciontxt = descripcion.text.toString()
        val categoriatxt = categria.text.toString()
        var nuevoProducto = Product(
            "",
            nombretxt,
            marcatxt,
            descripciontxt,
            "",
            categoriatxt,
            imagenUrlFinal,
            idUsuario
        )

        Log.d("Nuevo producto", nuevoProducto.toString())
        ProductoRepositorio.crearNuevoProducto(nuevoProducto)

        // Volver al perfil luego de registrar
        val perfilFragment = PerfilFragment()
        (activity as MainActivity?)?.makeCurrentFragment(perfilFragment)
    }
}
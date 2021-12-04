package com.example.cheaper.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.cheaper.MainActivity
import com.example.cheaper.R
import com.example.cheaper.adapters.CategoriaAdapter
import com.example.cheaper.databinding.FragmentRegistrarProductoBinding
import com.example.cheaper.model.Categoria
import com.example.cheaper.model.Product
import com.example.cheaper.repositorios.ProductoRepositorio
import com.example.cheaper.repositorios.UsuarioRepositorio
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_registrar_producto.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class RegistrarProducto : Fragment() {

    lateinit var ImageUri : Uri
    var imagenUrlFinal = "https://firebasestorage.googleapis.com/v0/b/cheaper-manati4.appspot.com/o/user.png?alt=media&token=98ae4512-acf3-4254-a780-e893db9b19b7"
    private lateinit var categoriaArrayList : ArrayList<Categoria>
    private lateinit var myCategoryAdapter : CategoriaAdapter
    private lateinit var lstAdapter: ArrayList<String>
    private lateinit var db : FirebaseFirestore

    private var _binding: FragmentRegistrarProductoBinding? = null
    private val binding get() = _binding!!


    override fun onResume() {
        super.onResume()
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

        // Me lleva al lector de códigos
        _binding!!.root.findViewById<TextView>(R.id.escanearQR).setOnClickListener {
            initScanner()
        }

        val btnRegistrarProducto = _binding!!.root.findViewById<Button>(R.id.btn_registrarProducto)
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

        dropCategorias()

        return binding.root
    }

    private fun dropCategorias(){
        categoriaArrayList = arrayListOf()
        lstAdapter = arrayListOf()
        myCategoryAdapter = CategoriaAdapter(categoriaArrayList)
        EventCategoryChangeListener()
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, lstAdapter)
        binding.autoCompleteTextView.setAdapter(arrayAdapter)
    }

    private fun initScanner(){
        val integrator = IntentIntegrator(this.activity)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
        integrator.setPrompt("Escanea un código de producto")
        integrator.setTorchEnabled(true)
        integrator.setBeepEnabled(true)
        integrator.initiateScan()
    }

    private fun EventCategoryChangeListener(){

        db = FirebaseFirestore.getInstance()
        db.collection("categorias").
        addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(
                value: QuerySnapshot?, error: FirebaseFirestoreException?
            ) {

                if (error != null){
                    Log.e("Firestore Error", error.message.toString())
                    return
                }

                for (dc : DocumentChange in value?.documentChanges!!){

                    if (dc.type == DocumentChange.Type.ADDED){
                        categoriaArrayList.add(dc.document.toObject(Categoria::class.java))
                        lstAdapter.add(dc.document.toObject(Categoria::class.java).nombre.toString())
                    }
                }
                myCategoryAdapter.notifyDataSetChanged()
            }
        })
    }

    private fun fileManager(){
        val intent = Intent()
        intent.type= "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(intent,100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val intentResultCode : IntentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        if (intentResultCode != null){
            if (intentResultCode.contents == null){
               // Toast.makeText(this, "", Toast.LENGTH_SHORT).show()
                Toast.makeText(this.requireContext(), "Cancelado", Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(this.requireContext(), "El valor escaneado es: ${intentResultCode.contents}", Toast.LENGTH_SHORT).show()
        }

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
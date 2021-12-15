package com.example.cheaper.fragments

import android.annotation.SuppressLint
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
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cheaper.*
import com.example.cheaper.R
import com.example.cheaper.adapters.AdapterProduct
import com.example.cheaper.adapters.AdapterResennas
import com.example.cheaper.adapters.CategoriaAdapter
import com.example.cheaper.databinding.FragmentEditarProductoBinding
import com.example.cheaper.model.Categoria
import com.example.cheaper.model.Product
import com.example.cheaper.model.Usuario
import com.example.cheaper.repositorios.ProductoRepositorio
import com.example.cheaper.repositorios.UsuarioRepositorio
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_editar_producto.*
import kotlinx.android.synthetic.main.fragment_editar_producto.view.*
import kotlinx.android.synthetic.main.fragment_editar_usuario.*
import kotlinx.android.synthetic.main.fragment_editar_usuario.view.*
import kotlinx.android.synthetic.main.fragment_perfil.*
import kotlinx.android.synthetic.main.fragment_registrar_producto.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class EditarProductoFragment : Fragment() {


    private lateinit var myAdapter: AdapterProduct
    lateinit var db: FirebaseFirestore
    private lateinit var viewOfLayout: View
    private var idProducto : Any? = ""
    private var nombreProducto : Any? = ""
    private var marcaProducto : Any? = ""
    private var descripcionProducto : Any? = ""
    private var categoria : Any? = ""
    private var imagenProducto : Any? = ""
    private var usuarioProducto : Any? = ""

    private lateinit var categoriaArrayList : ArrayList<Categoria>
    private lateinit var myCategoryAdapter : CategoriaAdapter
    private lateinit var lstAdapter: ArrayList<String>

    private var _binding: FragmentEditarProductoBinding? = null
    private val binding get() = _binding!!

    lateinit var ImageUri : Uri
    var imagenUrlFinal = "https://firebasestorage.googleapis.com/v0/b/cheaper-manati4.appspot.com/o/user.png?alt=media&token=98ae4512-acf3-4254-a780-e893db9b19b7"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        obtenerInfoProducto()
        // Inflate the layout for this fragment
        viewOfLayout = inflater.inflate(R.layout.fragment_editar_producto, container, false)
        val empty = "empty"
        ImageUri = empty.toUri()


        // Para volver al perfil
        viewOfLayout?.findViewById<TextView>(R.id.volverFromEditarProducto)?.setOnClickListener {
            val perfilFragment = PerfilFragment()
            (activity as MainActivity?)?.makeCurrentFragment(perfilFragment)
        }


        // Para ir a Logs
        viewOfLayout?.findViewById<TextView>(R.id.buttonLogs)?.setOnClickListener {
            val actualizacionesFragment = ActualizacionesFragment()
            var bundle = Bundle()
            bundle.putString("productoId", idProducto.toString())
            bundle.putString("nombre", nombreProducto.toString())
            bundle.putString("marca", marcaProducto.toString())
            bundle.putString("categoria", categoria.toString())
            bundle.putString("descripcion", descripcionProducto.toString())
            bundle.putString("imagen", imagenProducto.toString())
            bundle.putString("usuario", usuarioProducto.toString())
            actualizacionesFragment.arguments = bundle
            (activity as MainActivity?)?.makeCurrentFragment(actualizacionesFragment)
        }

        //Relleno de información
        mostrarInfoProducto(viewOfLayout)
        dropCategorias(viewOfLayout)


        //Funciones de botones
        val btnActualizarImagen = viewOfLayout.btnCargarImgProductoEditar
        btnActualizarImagen.setOnClickListener {
            fileManager()
        }

        val btnActualizarProducto = viewOfLayout.btn_actualizar_producto
        btnActualizarProducto.setOnClickListener {
            val nombreUpdate = viewOfLayout.txtNombreProductoEditar.text.toString()
            val marcaUpdate = viewOfLayout.txtMarcaProductoEditar.text.toString()
            val descripcionUpdate = viewOfLayout.txtDescripcionProductoEditar.text.toString()
            val categriaUpdate = viewOfLayout.autoCompleteTextViewEditarCat.text.toString()
            fileUpload(nombreUpdate, marcaUpdate, descripcionUpdate, categriaUpdate)
        }

        return viewOfLayout
    }

    private fun dropCategorias(vista: View){
        categoriaArrayList = arrayListOf()
        lstAdapter = arrayListOf()
        myCategoryAdapter = CategoriaAdapter(categoriaArrayList)
        EventCategoryChangeListener()
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, lstAdapter)
        vista.autoCompleteTextViewEditarCat.setAdapter(arrayAdapter)
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

    private fun obtenerInfoProducto() {
        val args = this.arguments
        idProducto = args?.get("id")
        nombreProducto = args?.get("nombre")
        marcaProducto = args?.get("marca")
        descripcionProducto = args?.get("descripcion")
        categoria = args?.get("categoria")
        imagenProducto = args?.get("imagen")
        usuarioProducto = args?.get("usuario")
    }

    private fun mostrarInfoProducto(vista: View) {
        val nombreProd: TextView = vista.findViewById(R.id.txtNombreProductoEditar)
        nombreProd.text = nombreProducto.toString()
        val marcaProd: TextView = vista.findViewById(R.id.txtMarcaProductoEditar)
        marcaProd.text = marcaProducto.toString()
        val descripcionProd: TextView = vista.findViewById(R.id.txtDescripcionProductoEditar)
        descripcionProd.text = descripcionProducto.toString()
        val categoriaProd: TextView = vista.findViewById(R.id.autoCompleteTextViewEditarCat)
        categoriaProd.text = categoria.toString()
        val fotoProducto: ImageView = vista.findViewById(R.id.imagenIdProductoEditar)
        Picasso.get().load(imagenProducto.toString()).into(fotoProducto)
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
            Picasso.get().load(ImageUri).into(this.imagenIdProductoEditar)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fileUpload(nombre: String, marca: String, descripcion: String, categoria: String) {

        var nuevoProducto = Product(
            idProducto.toString(),
            nombre,
            marca,
            descripcion,
            "",
            categoria,
            imagenProducto.toString(),
            usuarioProducto.toString()
        )

        if (ImageUri.equals("empty".toUri())){
            actualizarProducto(nuevoProducto)
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
                    nuevoProducto.foto = imagenUrlFinal
                    actualizarProducto(nuevoProducto)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun actualizarProducto(productoActualizar: Product) {

        val usuarioEditor = UsuarioRepositorio.usuarioLogueado
        ProductoRepositorio.actualizarProcuto(productoActualizar, usuarioEditor)

        // Volver al perfil luego de actualizar
        val perfilFragment = PerfilFragment()
        (activity as MainActivity?)?.makeCurrentFragment(perfilFragment)

    }
}
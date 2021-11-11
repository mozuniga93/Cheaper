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
import com.example.cheaper.R
import com.example.cheaper.databinding.FragmentRegistrarProductoBinding
import com.example.cheaper.model.Product
import com.example.cheaper.repositorios.ProductoRepositorio
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class RegistrarProducto : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    private lateinit var db : FirebaseFirestore
    private val fileResult = 1

    private var _binding: FragmentRegistrarProductoBinding? = null
    private val binding get() = _binding!!


    override fun onResume() {
        super.onResume()

        val categorias = resources.getStringArray(R.array.categotias)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, categorias)
        binding.autoCompleteTextView.setAdapter(arrayAdapter)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentRegistrarProductoBinding.inflate(inflater, container, false)

        val btnRegistrarProducto = _binding!!.root.findViewById<Button>(R.id.btn_guardar_producto)
        btnRegistrarProducto.setOnClickListener {
            val nombre = binding.txtNombreProducto
            val marca = binding.txtMarcaProducto
            val descripcion = binding.txtDescripcionProducto
            val categria = binding.autoCompleteTextView
            verificarCampostxt(nombre, marca, descripcion, categria)
        }

        return binding.root
    }


        @RequiresApi(Build.VERSION_CODES.O)
        private fun verificarCampostxt(nombre: EditText, marca: EditText, descripcion: EditText, categria: EditText) {

            if (!nombre.text.isNullOrBlank() && !marca.text.isNullOrBlank()
                && !descripcion.text.isNullOrBlank() && !categria.equals("Categoría")) {
                crearNuevoProducto(nombre, marca, descripcion, categria)
            } else {
                Log.d("Registro de producto", "Registro fallido")
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        private fun crearNuevoProducto(nombre: EditText, marca: EditText, descripcion: EditText, categria: EditText) {

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
                ""
            )

            Log.d("Nuevo producto", nuevoProducto.toString())
            ProductoRepositorio.crearNuevoProducto(nuevoProducto)

        }

        companion object {
            /**
             * Use this factory method to create a new instance of
             * this fragment using the provided parameters.
             *
             * @param param1 Parameter 1.
             * @param param2 Parameter 2.
             * @return A new instance of fragment RegistrarProducto.
             */
            // TODO: Rename and change types and number of parameters
            @JvmStatic
            fun newInstance(param1: String, param2: String) =
                RegistrarProducto().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }

        }
    }
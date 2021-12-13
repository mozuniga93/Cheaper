package com.example.cheaper.fragments

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.cheaper.PerfilProductoFragment
import com.example.cheaper.ProductoAdapter
import com.example.cheaper.R
import com.example.cheaper.databinding.FragmentEscanearBinding
import com.example.cheaper.model.Product
import com.example.cheaper.utilidades.CodigoBarrasDialog
import com.example.cheaper.utilidades.QRDialog
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_registrar_producto.*

class EscanearFragment: Fragment() {

    private var _binding: FragmentEscanearBinding? = null
    private val binding get() = _binding!!
    private var codigoDeBarras = ""
    private lateinit var productoEncontrado : Product
    private lateinit var db : FirebaseFirestore


    override fun onResume() {
        super.onResume()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEscanearBinding.inflate(inflater, container, false)
        initScanner()
        return binding.root
    }

    private fun initScanner(){
        val integrator = IntentIntegrator.forSupportFragment(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
        integrator.setPrompt("Escanea el código de un producto")
        integrator.setTorchEnabled(true)
        integrator.setBeepEnabled(true)
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        val intentResultCode : IntentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        if (intentResultCode != null){
            if (intentResultCode.contents == null){
                Log.d("PRODUCTO CANCELAR", "El producto no pudo ser escaneado.")
            }
            else{
                Log.d("PRODUCTO ESCANEADO", "Producto escaneado exitosamente:  ${intentResultCode.contents}")
                codigoDeBarras = intentResultCode.contents
                productoEncontrado = buscarProducto(codigoDeBarras)
                if(productoEncontrado.id != null){
                    val perfilProductoFragment = PerfilProductoFragment()
                    var bundle = Bundle()
                    bundle.putString("id", productoEncontrado.id.toString())
                    bundle.putString("nombre", productoEncontrado.nombre.toString())
                    bundle.putString("marca", productoEncontrado.marca.toString())
                    bundle.putString("categoria", productoEncontrado.categoria.toString())
                    bundle.putString("descripcion", productoEncontrado.descripcion.toString())
                    bundle.putString("imagen", productoEncontrado.foto.toString())
                    bundle.putString("usuario", productoEncontrado.usuario.toString())
                    perfilProductoFragment.arguments = bundle
                    var fr = getFragmentManager()?.beginTransaction()
                    fr?.replace(R.id.inicio, perfilProductoFragment)?.addToBackStack(null)
                    fr?.commit()
                }else{
                    val dialogo = CodigoBarrasDialog()
                    dialogo.show(childFragmentManager, "CodigoBarrasDialog")
                }
                // dialogo.show(supportFupragmentManager, "TelefonoDialog")
            }
        }
    }

    private fun buscarProducto(codigoBarras: String): Product {

        productoEncontrado.id = null
        db = FirebaseFirestore.getInstance()
        db.collection("productos").
        whereEqualTo("codigoBarras",codigoBarras).
        get().
        addOnSuccessListener { documents ->
            for (document in documents) {
                var producto = document.toObject(Product::class.java)
                producto.id = document.id
                productoEncontrado = producto
            }
        }
            .addOnFailureListener{ exception ->
                Log.w(ContentValues.TAG, "Error getting product: ", exception)
            }
        return productoEncontrado
    }

}
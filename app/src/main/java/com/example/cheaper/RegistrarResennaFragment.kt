package com.example.cheaper

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.cheaper.fragments.InicioFragment
import com.example.cheaper.model.Resenna
import com.example.cheaper.repositorios.ResennaRepositorio
import com.example.cheaper.utilidades.RegistrarDialog
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_registrar_resenna.view.*
import java.time.LocalDate




// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BlankFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ResennaFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var idProducto : Any? = ""
    private var nombreProducto : Any? = ""
    private var marcaProducto : Any? = ""
    private var descripcionProducto : Any? = ""
    private var imagenProducto : Any? = ""


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

        val vista =  inflater.inflate(R.layout.fragment_registrar_resenna, container, false)
        obtenerIdProducto()
        verificarSiTiendaEsVirtual(vista)
        registrar(vista)

        // Para volver al perfil
        vista?.findViewById<TextView>(R.id.tv_volver)?.setOnClickListener {
            val inicioFragment = InicioFragment()
            (activity as MainActivity?)?.makeCurrentFragment(inicioFragment)
        }

        return vista
        // Inflate the layout for this fragment
    }

    private fun obtenerIdProducto() {
       val args = this.arguments
        idProducto = args?.get("productoId")
        nombreProducto = args?.get("productoNombre")
        marcaProducto = args?.get("productoMarca")
        descripcionProducto = args?.get("productoDescripcion")
        imagenProducto = args?.get("productoImagen")
    }


    private fun verificarSiTiendaEsVirtual(vista: View){
        val esVirtual: Switch = vista.findViewById(R.id.switchEsVirtual)
        esVirtual.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                with(vista) {
                    txt_provincia.setText("")
                    txt_provincia.setText("")
                    txt_provincia.setEnabled(false)
                    txt_canton.setEnabled(false)
                    txt_provincia.setHintTextColor(Color.parseColor("#B7BABA"))
                    txt_canton.setHintTextColor(Color.parseColor("#B7BABA"))
                }
            }else{
                vista.txt_provincia.setEnabled(true)
                vista.txt_canton.setEnabled(true)
                vista.txt_provincia.setHintTextColor(Color.parseColor("#4B5963"))
                vista.txt_canton.setHintTextColor(Color.parseColor("#4B5963"))
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun registrar(vista: View) {
        val btnRegistrarResenna = vista.findViewById<Button>(R.id.btn_agregar_resenna)
        btnRegistrarResenna.setOnClickListener {
            val precio = vista.findViewById<EditText>(R.id.txt_precio)
            val tienda = vista.findViewById<EditText>(R.id.txt_tienda)
            val provincia = vista.findViewById<EditText>(R.id.txt_provincia)
            val canton = vista.findViewById<EditText>(R.id.txt_canton)
            val esVirtual = vista.findViewById<Switch>(R.id.switchEsVirtual)
            verificarCampostxt(precio, tienda, provincia, canton, esVirtual)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun verificarCampostxt(precio: EditText, tienda: EditText, provincia: EditText, canton: EditText, esVirtual: Switch) {

        if(!esVirtual.isChecked){
            if (!precio.text.isNullOrBlank() && !tienda.text.isNullOrBlank() && !provincia.text.isNullOrBlank() && !canton.text.isNullOrBlank()) {
                crearNuevaResenna(precio, tienda, provincia, canton, esVirtual)
            } else {
                Log.d("Registro resenna", "Registro fallido")
                val dialogo = RegistrarDialog()
                dialogo.show(parentFragmentManager, "RegistroDialog")
            }
        }else{
            if (!precio.text.isNullOrBlank() && !tienda.text.isNullOrBlank()) {
                crearNuevaResenna(precio, tienda, provincia, canton, esVirtual)
            } else {
                Log.d("Registro resenna", "Registro fallido")
                val dialogo = RegistrarDialog()
                dialogo.show(parentFragmentManager, "RegistroDialog")
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun crearNuevaResenna(precio: EditText, tienda: EditText, provincia: EditText, canton: EditText, esVirtual: Switch) {
        var authUsuario = Firebase.auth.currentUser!!
        val preciotxt = precio.text.toString().toInt()
        val tiendatxt = tienda.text.toString()
        val provinciatxt = provincia.text.toString()
        val cantontxt = canton.text.toString()
        var nuevaResenna = Resenna(
            "",
            authUsuario?.uid!!,
            idProducto.toString(),
            preciotxt,
            tiendatxt,
            provinciatxt,
            cantontxt,
            esVirtual.isChecked,
            0,
            LocalDate.now().toString()
        )
        ResennaRepositorio.crearNuevaResenna(nuevaResenna)
        limpiartxt(precio, tienda, provincia, canton, esVirtual)
    }

    private fun limpiartxt(precio: EditText, tienda: EditText, provincia: EditText, canton: EditText, esVirtual: Switch){
        precio.setText("")
        tienda.setText("")
        provincia.setText("")
        canton.setText("")

        val perfilProductoFragment = PerfilProductoFragment()
        var bundle = Bundle()
        bundle.putString("id", idProducto.toString())
        bundle.putString("nombre", nombreProducto.toString())
        bundle.putString("marca", marcaProducto.toString())
        bundle.putString("descripcion", descripcionProducto.toString())
        bundle.putString("imagen", imagenProducto.toString())
        perfilProductoFragment.arguments = bundle
        val transaction: FragmentTransaction = parentFragmentManager!!.beginTransaction()
        transaction.replace(R.id.fl_registrar_resenna, perfilProductoFragment)
        transaction.commit()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BlankFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ResennaFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
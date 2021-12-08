package com.example.cheaper

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cheaper.adapters.AdapterResennas
import com.example.cheaper.fragments.EditarProductoFragment
import com.example.cheaper.fragments.InicioFragment
import com.example.cheaper.model.Product
import com.example.cheaper.model.Resenna
import com.example.cheaper.model.ResennaVotada
import com.example.cheaper.model.Usuario
import com.example.cheaper.repositorios.RepositorioConstantes
import com.example.cheaper.repositorios.UsuarioRepositorio
import com.example.cheaper.utilidades.SesionDialog
import com.google.firebase.firestore.*
import com.squareup.picasso.Picasso
import java.time.LocalDate
import java.time.Period

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private lateinit var resennaRecyclerView: RecyclerView
private lateinit var resennaArrayList: ArrayList<Resenna>
private lateinit var usuarioArrayList:  ArrayList<Usuario>
private lateinit var votosResennausuarioArrayList: ArrayList<ResennaVotada>
private lateinit var myAdapter: AdapterResennas
lateinit var db: FirebaseFirestore
private lateinit var viewOfLayout: View
private var idProducto : Any? = ""
private var nombreProducto : Any? = ""
private var marcaProducto : Any? = ""
private var categoriaProducto : Any? = ""
private var descripcionProducto : Any? = ""
private var imagenProducto : Any? = ""
private var usuarioProducto : Any? = ""
private var esFavorito = false

/**
 * A simple [Fragment] subclass.
 * Use the [PerfilProductoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PerfilProductoFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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
        obtenerInfoProducto()
        obtenerFotoUsuario()
        usuarioArrayList = arrayListOf()
        // Inflate the layout for this fragment
        viewOfLayout = inflater.inflate(R.layout.fragment_perfil_producto, container, false)
        resennaRecyclerView = viewOfLayout.findViewById(R.id.listaResennas)
        resennaRecyclerView.layoutManager = LinearLayoutManager(this.context)
        resennaArrayList = arrayListOf()
        votosResennausuarioArrayList = arrayListOf()
        myAdapter = AdapterResennas(resennaArrayList)
        resennaRecyclerView.adapter = myAdapter

        // Para volver al inicio
        viewOfLayout?.findViewById<TextView>(R.id.tvVolver)?.setOnClickListener {
            val inicioFragment = InicioFragment()
            (activity as MainActivity?)?.makeCurrentFragment(inicioFragment)
        }

        cargarEstadoFavorito()
        viewOfLayout?.findViewById<TextView>(R.id.btnAgregarFavoritos)?.setOnClickListener {
            cambiarEstadoFavorito()
        }



        irARegistrar(viewOfLayout)
        irAEditar(viewOfLayout)
        mostrarInfoProducto(viewOfLayout)
        return viewOfLayout
    }

    private fun cambiarEstadoFavorito() {
        if(!UsuarioRepositorio.usuarioEstaLogueado()){
            val dialogo = SesionDialog()
            dialogo.show(childFragmentManager, "SesionDialog")
        }else {
            var product = Product(
                idProducto?.toString(),
                nombreProducto?.toString()
            )

            if (!esFavorito) {
                cambiarIconoFavorito(R.drawable.ic_favorito_relleno)
                UsuarioRepositorio.registrarProductoFavorito(
                    UsuarioRepositorio.usuarioLogueado,
                    product
                )
            } else {
                UsuarioRepositorio.removerProductoFavorito(
                    UsuarioRepositorio.usuarioLogueado,
                    product
                )
                cambiarIconoFavorito(R.drawable.ic_favorito_vacio)
            }
            esFavorito = !esFavorito
        }
    }

    private fun cargarEstadoFavorito(){
        if(UsuarioRepositorio.usuarioEstaLogueado()){
            val productoFavorito = UsuarioRepositorio.usuarioLogueado.productosFavoritos!!.
                getOrDefault(idProducto, null)
            esFavorito = productoFavorito != null && productoFavorito.habilitado!!
        }
        else
            esFavorito = false

        if(esFavorito){
            cambiarIconoFavorito(R.drawable.ic_favorito_relleno)
        }else {
            cambiarIconoFavorito(R.drawable.ic_favorito_vacio)
        }
    }

    private fun cambiarIconoFavorito(iconoId: Int){
        val button = viewOfLayout?.findViewById<TextView>(R.id.btnAgregarFavoritos)
        button.setCompoundDrawables(null,null,null,null)

        var iconoDrawable = resources.getDrawable(iconoId,this.context?.theme)
        iconoDrawable = DrawableCompat.wrap(iconoDrawable)
        DrawableCompat.setTint(iconoDrawable,resources.getColor(R.color.white))
        iconoDrawable.setBounds(0,0,iconoDrawable.intrinsicWidth, iconoDrawable.intrinsicHeight)

        button.setCompoundDrawablesRelativeWithIntrinsicBounds(null,null,iconoDrawable,null)
    }

    private fun obtenerInfoProducto() {
        val args = this.arguments
        idProducto = args?.get("id")
        nombreProducto = args?.get("nombre")
        marcaProducto = args?.get("marca")
        categoriaProducto = args?.get("categoria")
        descripcionProducto = args?.get("descripcion")
        imagenProducto = args?.get("imagen")
        usuarioProducto = args?.get("usuario")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun obtenerResennaDestacada(itemView: View, listaResennas: ArrayList<Resenna>) {
        var resennaDestacada = Resenna()
            for (resenna in listaResennas) {
                if (resennaDestacada.producto == null) {
                    resennaDestacada = resenna
                } else {
                    if (resennaDestacada.precio!! > resenna.precio!!) {
                        resennaDestacada = resenna
                    }
                }
            }
        if(resennaDestacada.producto != null) {
            mostrarResennaDestacada(resennaDestacada, itemView)
        }else{
            var imagenUrlFinal = "https://firebasestorage.googleapis.com/v0/b/cheaper-manati4.appspot.com/o/user.png?alt=media&token=98ae4512-acf3-4254-a780-e893db9b19b7"
            val fotoUsuarioDestacado: ImageView = itemView.findViewById(R.id.IvUsuarioDestacado)
            Picasso.get().load(imagenUrlFinal).into(fotoUsuarioDestacado)
            val signoColones : TextView = itemView.findViewById(R.id.tvSignoColones)
            signoColones.text = ""

            val precioDestacado: TextView = itemView.findViewById(R.id.tvPrecio_Destacado)
            precioDestacado.text = ""
        }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun mostrarResennaDestacada(resennaDestacada: Resenna, itemView: View) {
        val precioDestacado: TextView = itemView.findViewById(R.id.tvPrecio_Destacado)
        precioDestacado.text = resennaDestacada.precio.toString()

        val nombreDestacado: TextView = itemView.findViewById(R.id.lblNombreComercioDestacado)
        nombreDestacado.text = resennaDestacada.tienda.toString()

        val ubicacion = obtenerUbicacion(resennaDestacada.provincia, resennaDestacada.lugar, resennaDestacada.virtual)
        val ubicacionDestacado: TextView = itemView.findViewById(R.id.lblUbicacionDestacado)
        ubicacionDestacado.text = ubicacion

        val fotoUsuarioDestacado: ImageView = itemView.findViewById(R.id.IvUsuarioDestacado)
        Picasso.get().load(resennaDestacada.usuario).into(fotoUsuarioDestacado)

        val tiempo = transformarFecha(resennaDestacada.fecha)
        val tiempoDestacado: TextView = itemView.findViewById(R.id.tvTiempoDestacado)
        tiempoDestacado.text = tiempo

        val signoColones : TextView = itemView.findViewById(R.id.tvSignoColones)
        signoColones.text = "₡"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun transformarFecha(fecha: String?): String {
        val fechaI = LocalDate.parse(fecha)
        val fechaf = LocalDate.now()

        val period: Period = Period.between(fechaI, fechaf)
        val diff: Int = period.getDays()
        validarDias(diff)

        val cuantoTiempo = validarDias(diff)
        return cuantoTiempo
    }

    private fun validarDias(diff: Int): String {
        var cuantoTiempo = ""

        if (diff == 0) {
            cuantoTiempo = "Hoy"
        } else if (diff == 1) {
            cuantoTiempo = "Hace 1 día"
        } else if (diff > 1 && diff < 31) {
            cuantoTiempo = "Hace " + diff + " días"
        } else if (diff > 30 && diff < 61) {
            cuantoTiempo = "Hace 1 mes"
        } else if (diff > 60 && diff < 91) {
            cuantoTiempo = "Hace 2 mes"
        } else if (diff > 90 && diff < 121) {
            cuantoTiempo = "Hace 3 mes"
        } else if (diff > 120 && diff < 151) {
            cuantoTiempo = "Hace 4 mes"
        } else if (diff > 150 && diff < 181) {
            cuantoTiempo = "Hace 5 mes"
        } else if (diff > 180 && diff < 211) {
            cuantoTiempo = "Hace 6 mes"
        } else if (diff > 210 && diff < 241) {
            cuantoTiempo = "Hace 7 mes"
        } else if (diff > 240 && diff < 271) {
            cuantoTiempo = "Hace 8 mes"
        } else if (diff > 270 && diff < 301) {
            cuantoTiempo = "Hace 9 mes"
        } else if (diff > 300 && diff < 331) {
            cuantoTiempo = "Hace 10 mes"
        } else if (diff > 330 && diff < 361) {
            cuantoTiempo = "Hace 11 mes"
        } else if (diff > 360) {
            cuantoTiempo = "Hace 1 año"
        }

        return cuantoTiempo
    }

    private fun obtenerUbicacion(provincia: String?, lugar: String?, virtual: Boolean?): String {
        var ubicacion = ""
        if (virtual == true) {
            ubicacion = "Tienda virtual"
        } else {
            ubicacion = provincia + ", " + lugar
        }
        return ubicacion
    }

    private fun mostrarInfoProducto(vista: View) {
        val nombreProd: TextView = vista.findViewById(R.id.lblNombreProducto)
        nombreProd.text = nombreProducto.toString()
        val marcaProd: TextView = vista.findViewById(R.id.lblMarcaProducto)
        marcaProd.text = marcaProducto.toString()
        val descripcionProd: TextView = vista.findViewById(R.id.lblDescripcionProducto)
        descripcionProd.text = descripcionProducto.toString()
        val fotoProducto: ImageView = vista.findViewById(R.id.IvProducto)
        Picasso.get().load(imagenProducto.toString()).into(fotoProducto)
    }

    private fun irARegistrar(vista: View) {
        val btnAgregarResenna = vista.findViewById<Button>(R.id.btnNuevaResenna)

        btnAgregarResenna.setOnClickListener {
            val resennaFragment = ResennaFragment()
            var bundle = Bundle()
            bundle.putString("productoId", idProducto.toString())
            bundle.putString("productoNombre", nombreProducto.toString())
            bundle.putString("productoMarca", marcaProducto.toString())
            bundle.putString("productoCategoria", categoriaProducto.toString())
            bundle.putString("productoDescripcion", descripcionProducto.toString())
            bundle.putString("productoImagen", imagenProducto.toString())
            resennaFragment.arguments = bundle
            val transaction: FragmentTransaction = parentFragmentManager!!.beginTransaction()
            transaction.replace(R.id.fl_perfil_producto, resennaFragment)
            transaction.commit()
        }

    }

    private fun irAEditar(vista: View) {
        val btnEditarPoducto = vista.findViewById<Button>(R.id.btnEditarProducto)

        btnEditarPoducto.setOnClickListener {
            val editarProductoFragment = EditarProductoFragment()
            var bundle = Bundle()
            bundle.putString("id", idProducto.toString())
            bundle.putString("nombre", nombreProducto.toString())
            bundle.putString("marca", marcaProducto.toString())
            bundle.putString("categoria", categoriaProducto.toString())
            bundle.putString("descripcion", descripcionProducto.toString())
            bundle.putString("imagen", imagenProducto.toString())
            bundle.putString("usuario", usuarioProducto.toString())
            editarProductoFragment.arguments = bundle
            val transaction: FragmentTransaction = parentFragmentManager!!.beginTransaction()
            transaction.replace(R.id.fl_perfil_producto, editarProductoFragment)
            transaction.commit()
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun obtenerFotoUsuario() {
        val tag = "[Manati] Usuario"
        db = FirebaseFirestore.getInstance()
        db.collection(RepositorioConstantes.usuariosCollection)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    usuarioArrayList.add(document.toObject(Usuario::class.java))
                    Log.d(tag, usuarioArrayList.toString())
                }
                EventChangeListener()
            }
            .addOnFailureListener { exception ->
                Log.d(tag, "Error getting documents: ", exception)
            }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PerfilProductoFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PerfilProductoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun EventChangeListener() {
        if(resennaArrayList.size != 0){
            resennaArrayList.clear()
        }
        db = FirebaseFirestore.getInstance()
        db.collection("resennas").addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(
                value: QuerySnapshot?, error: FirebaseFirestoreException?
            ) {
                if (error != null) {
                    Log.e("Firestore Error", error.message.toString())
                    return
                }
                for (dc: DocumentChange in value?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        var resenna = dc.document.toObject(Resenna::class.java)
                        resenna.id = dc.document.id
/*
                        obtenerColeccionDeVotos(resenna)
*/
                        val resennaFoto = cambiarIdPorFoto(resenna)
                        if(resennaFoto.producto.equals(idProducto.toString())) {
                            resennaArrayList.add(resennaFoto)

                        }
                    }
                }
                myAdapter.notifyDataSetChanged()
                obtenerResennaDestacada(viewOfLayout, resennaArrayList)

            }
        })
    }

   /* private fun obtenerColeccionDeVotos(resenna: Resenna) {
        db.collection(RepositorioConstantes.resennasCollection).document(resenna.id!!)
            .collection(RepositorioConstantes.votoResennaCollection)
            .get()
            .addOnSuccessListener { documentReference->
                for (document in documentReference) {
                    var resennaVotos = document.toObject(ResennaVotada::class.java)
                    votosResennausuarioArrayList.add(resennaVotos)
                    *//*Log.d("Colleccion de votos", resennaVotos.toString())*//*
                }

            }
            .addOnFailureListener { e ->
                Log.w(UsuarioRepositorio.tag, "Error al cargar los votos de resenna.", e)
            }
    }*/

    private fun cambiarIdPorFoto(resennaFoto: Resenna) : Resenna{
        for (document in usuarioArrayList) {
            if(resennaFoto.usuario.equals(document.id)){
                resennaFoto.usuario = document.foto
            }
        }
        return resennaFoto
    }
}
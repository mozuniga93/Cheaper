package com.example.cheaper

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cheaper.model.Resenna
import com.example.cheaper.model.Usuario
import com.example.cheaper.repositorios.RepositorioConstantes
import com.google.firebase.firestore.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private lateinit var resennaRecyclerView: RecyclerView
private lateinit var resennaArrayList: ArrayList<Resenna>
private lateinit var usuarioArrayList:  ArrayList<Usuario>
private lateinit var myAdapter: AdapterResennas
lateinit var db: FirebaseFirestore
private lateinit var viewOfLayout: View

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        obtenerFotoUsuario()
        usuarioArrayList = arrayListOf()
        // Inflate the layout for this fragment
        viewOfLayout = inflater.inflate(R.layout.fragment_perfil_producto, container, false)
        resennaRecyclerView = viewOfLayout.findViewById(R.id.listaResennas)
        resennaRecyclerView.layoutManager = LinearLayoutManager(this.context)
        resennaArrayList = arrayListOf()
        myAdapter = AdapterResennas(resennaArrayList)
        resennaRecyclerView.adapter = myAdapter
        irARegistrar(viewOfLayout)
        return viewOfLayout
    }

    private fun irARegistrar(vista: View) {
        val btnAgregarResenna = vista.findViewById<Button>(R.id.btnNuevaResenna)

        btnAgregarResenna.setOnClickListener {
            val resennaFragment = ResennaFragment()
            val transaction: FragmentTransaction = parentFragmentManager!!.beginTransaction()
            transaction.replace(R.id.fl_perfil_producto, resennaFragment)
            transaction.commit()
        }

    }

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

    private fun EventChangeListener() {
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
                        val resennaFoto = cambiarIdPorFoto(resenna)
                        Log.d("[Manati] resenna foto",resennaFoto.toString())
                        resennaArrayList.add(resennaFoto)
                        val docRef = db.collection(RepositorioConstantes.usuariosCollection)
                            .document(dc.document.toObject(Resenna::class.java).usuario!!)
                    }
                }
                myAdapter.notifyDataSetChanged()
            }
        })
    }

    private fun cambiarIdPorFoto(resennaFoto: Resenna) : Resenna{
        for (document in usuarioArrayList) {
            if(resennaFoto.usuario.equals(document.id)){
                resennaFoto.usuario = document.foto
            }
        }
        return resennaFoto
    }
}
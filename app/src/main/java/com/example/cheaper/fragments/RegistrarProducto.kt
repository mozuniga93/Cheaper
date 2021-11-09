package com.example.cheaper.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.get
import androidx.core.view.isNotEmpty
import androidx.fragment.app.FragmentTransaction
import com.example.cheaper.Product
import com.example.cheaper.R
import com.example.cheaper.databinding.FragmentRegistrarProductoBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_registrar_producto.*
import kotlinx.android.synthetic.main.fragment_registrar_producto.view.*
import java.lang.reflect.Field

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class RegistrarProducto : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    private lateinit var db : FirebaseFirestore

    private var _binding: com.example.cheaper.databinding.FragmentRegistrarProductoBinding? = null
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



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentRegistrarProductoBinding.inflate(inflater, container, false)


        return binding.root
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
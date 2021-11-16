package com.example.cheaper.utilidades

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import com.example.cheaper.db
import com.example.cheaper.model.Resenna
import com.google.firebase.firestore.FirebaseFirestore

class EliminarDialog(currentItem: Resenna) : DialogFragment() {
    private val currentItem = currentItem
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setMessage("¿Esta seguro(a) que desea eliminar la reseña?")
                .setPositiveButton("Eliminar",
                    DialogInterface.OnClickListener { dialog, id ->
                        db = FirebaseFirestore.getInstance()
                        db.collection("resennas").document(currentItem.id!!)
                            .delete()
                            .addOnSuccessListener { Log.d("Eliminar resenna", "DocumentSnapshot successfully deleted!" +
                                    " Resenna eliminada: " + currentItem.toString())
                            }
                            .addOnFailureListener { e -> Log.w("Eliminar resenna", "Error deleting document", e) }
                    })
                .setNegativeButton("Cancelar",
                    DialogInterface.OnClickListener { dialog, id ->
                        // User cancelled the dialog
                    })
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        return activity?.let {
//            // Use the Builder class for convenient dialog construction
//            val builder = AlertDialog.Builder(it)
//            builder.setMessage("Por favor rellene todos los campos requeridos")
//                .setPositiveButton("Aceptar",
//                    DialogInterface.OnClickListener { dialog, id ->
//
//                    })
//            builder.create()
//        } ?: throw IllegalStateException("Activity cannot be null")
//    }
}
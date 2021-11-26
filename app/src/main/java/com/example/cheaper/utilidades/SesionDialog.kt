package com.example.cheaper.utilidades

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class SesionDialog: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage("Es necesario iniciar sesión para realizar esta acción.")
                .setPositiveButton("OK",
                    DialogInterface.OnClickListener { dialog, id ->

                    })
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
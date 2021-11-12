package com.example.cheaper.utilidades

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class TelefonoDialog : DialogFragment(){
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setMessage("Requerimos el número de teléfono únicamente para validar que eres una personal real (y no un robot)")
                .setPositiveButton("OK",
                    DialogInterface.OnClickListener { dialog, id ->

                    })
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
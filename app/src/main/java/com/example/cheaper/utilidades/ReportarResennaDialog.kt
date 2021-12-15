package com.example.cheaper.utilidades

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.ImageButton
import androidx.fragment.app.DialogFragment
import com.example.cheaper.adapters.AdapterResennas
import com.example.cheaper.model.Resenna
import com.example.cheaper.model.ResennaVotada
import com.example.cheaper.repositorios.ResennaRepositorio
import com.example.cheaper.repositorios.UsuarioRepositorio

class ReportarResennaDialog(
    currentItem: Resenna,
    var btnReportar: ImageButton,
    var listaResennas: ArrayList<Resenna>,
    var posicion: Int,
    var votosResennausuarioArrayList: ArrayList<ResennaVotada>
) :
    DialogFragment() {

    private val currentItem: Resenna = currentItem
    private val adResenna = AdapterResennas(listaResennas, votosResennausuarioArrayList)
    private var mensaje : String = "¿Esta seguro(a) que desea reportar la reseña?"



    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        if (currentItem.cantReportes == 4){
            mensaje = "¡Usted está por realizar el 5to reporte, la reseña sera eliminada!\n"
            mensaje += "\n¿Esta seguro(a) que desea reportar la reseña?"
        }

        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setMessage(mensaje)
                .setPositiveButton("Reportar",
                    DialogInterface.OnClickListener { dialog, id ->
                        ResennaRepositorio.registrarReporteResenna(
                            UsuarioRepositorio.usuarioLogueado,
                            currentItem
                        )
                        ResennaRepositorio.actualizarCantidadReportes(currentItem, listaResennas, posicion, votosResennausuarioArrayList, btnReportar)
                    })
                .setNegativeButton("Cancelar",
                    DialogInterface.OnClickListener { dialog, id ->
                        dismiss()
                    })
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

}
package com.example.cheaper.repositorios

import android.util.Log
import android.widget.ImageButton
import com.example.cheaper.R
import com.example.cheaper.adapters.AdapterResennas
import com.example.cheaper.db
import com.example.cheaper.model.ReporteResenna
import com.example.cheaper.model.Resenna
import com.example.cheaper.model.ResennaVotada
import com.example.cheaper.model.Usuario
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


object ResennaRepositorio {

    val tag = "[Manati] ResennaRep"

    fun crearNuevaResenna(nuevaResenna:Resenna){
        val db = Firebase.firestore
        db.collection(RepositorioConstantes.resennasCollection).document()
            .set(nuevaResenna)
            .addOnSuccessListener { documentReference ->
                Log.d(tag, "Resenna creada exitosamente.")
            }
            .addOnFailureListener { e ->
                Log.w(tag, "Error al crear nueva resenna.", e)
            }
    }

    fun registrarVotoResenna(usuario: Usuario, resenna: Resenna){
        val db = Firebase.firestore
        var nuevaResennaVotada = ResennaVotada(
            resenna.id,
            usuario?.id!!,
            true
        )

        Log.d(tag, nuevaResennaVotada.toString())
        Log.d("Resenna id", resenna.id!!)

        db.collection(RepositorioConstantes.resennasCollection).document(resenna.id!!)
            .collection(RepositorioConstantes.votoResennaCollection)
            .document(usuario?.id!!)
            .set(nuevaResennaVotada)
            .addOnSuccessListener { documentReference ->
                Log.d(UsuarioRepositorio.tag, "Voto en resenna agregado exitosamente.")
            }
            .addOnFailureListener { e ->
                Log.w(UsuarioRepositorio.tag, "Error al agregar voto resenna.", e)
            }
    }

    fun registrarReporteResenna(usuario: Usuario, resenna: Resenna){
        val db = Firebase.firestore
        var nuevoReporte = ReporteResenna(
            resenna.id,
            usuario?.id!!
        )

        Log.d(tag, nuevoReporte.toString())

        db.collection(RepositorioConstantes.resennasCollection).document(resenna.id!!)
            .collection(RepositorioConstantes.reporteResennaCollection)
            .document(usuario?.id!!)
            .set(nuevoReporte)
            .addOnSuccessListener { documentReference ->
                Log.d(UsuarioRepositorio.tag, "Reporte en resenna agregado exitosamente.")
            }
            .addOnFailureListener { e ->
                Log.w(UsuarioRepositorio.tag, "Error al agregar el reporte en la resenna.", e)
            }
    }

    fun actualizarCantidadReportes(
        item: Resenna,
        listaResennas: ArrayList<Resenna>,
        posicion: Int,
        votosResennausuarioArrayList: ArrayList<ResennaVotada>,
        btnReportar: ImageButton
    ){
        val adResenna = AdapterResennas(listaResennas, votosResennausuarioArrayList)
        Log.d("Votos", item.votos.toString())
        if(item.cantReportes?.plus(1) == 5){
            eliminarResennaPorReportes(item.id, listaResennas, posicion, votosResennausuarioArrayList)
        }else{
        if (item.cantReportes != null) {
            item.cantReportes = item.cantReportes?.plus(1)
            adResenna.cambiarIcono(btnReportar, R.drawable.ic_baseline_flag_24)
        }else{
            item.cantReportes = 1
            adResenna.cambiarIcono(btnReportar, R.drawable.ic_baseline_flag_24)
        }

        Log.d("Reporte actualizado", item.cantReportes.toString())

        db.collection(RepositorioConstantes.resennasCollection).
        document(item.id!!).
        set(item).
        addOnSuccessListener { documentReference ->
            Log.d(UsuarioRepositorio.tag, "Cant reporte en resenna actualizado exitosamente.")
        }
            .addOnFailureListener { e ->
                Log.w(UsuarioRepositorio.tag, "Error al actualizar cant reporte de resenna.", e)
            }
        }
    }

    private fun eliminarResennaPorReportes(
        id: String?,
        listaResennas: ArrayList<Resenna>,
        posicion: Int,
        votosResennausuarioArrayList: ArrayList<ResennaVotada>
    ) {
        var adResennas = AdapterResennas(listaResennas, votosResennausuarioArrayList)

        db.collection("resennas").document(id!!)
            .delete()
            .addOnSuccessListener {
                Log.d(
                    "Eliminar resenna", "DocumentSnapshot successfully deleted!" +
                            " Resenna eliminada: " + id
                )
            }
            .addOnFailureListener { e ->
                Log.w(
                    "Eliminar resenna",
                    "Error deleting document",
                    e
                )
            }

        adResennas.actualizarLista(posicion)

    }
}
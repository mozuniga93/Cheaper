package com.example.cheaper.adapters

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.cheaper.R
import com.example.cheaper.db
import com.example.cheaper.model.Resenna
import com.example.cheaper.model.ResennaVotada
import com.example.cheaper.repositorios.RepositorioConstantes
import com.example.cheaper.repositorios.ResennaRepositorio
import com.example.cheaper.repositorios.UsuarioRepositorio
import com.squareup.picasso.Picasso
import java.time.LocalDate
import java.time.Period

class AdapterResennas(
    private val listaResennas: ArrayList<Resenna>) :
    RecyclerView.Adapter<AdapterResennas.MyViewHolder>() {

    private lateinit var votosResennausuarioArrayList: ArrayList<ResennaVotada>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        votosResennausuarioArrayList = arrayListOf()
        obtenerColeccionDeVotos()

        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.item,
            parent, false
        )
        return MyViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentItem: Resenna = listaResennas[position]
        ajustarBotonLike(currentItem, holder)
        holder.resennaPrecio.text = currentItem.precio.toString()
        holder.resennaTienda.text = currentItem.tienda
        val ubicacion =
            obtenerUbicacion(currentItem.provincia, currentItem.lugar, currentItem.virtual)
        holder.resennaDireccion.text = ubicacion
        val tiempo = transformarFecha(currentItem.fecha)
        holder.resennaTiempo.text = tiempo
        Picasso.get().load(currentItem.usuario).into(holder.fotoUsuario)
        holder.cantVotos.text = currentItem.votos.toString()
        holder.likeResenna.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                actualizarCantidadVotos(currentItem)

                ResennaRepositorio.registrarVotoResenna(
                    UsuarioRepositorio.usuarioLogueado,
                    currentItem
                )
                cambiarIconoLike(holder.likeResenna, R.drawable.ic_baseline_thumb_up_alt_24_gris)
            }
        })
    }

    private fun obtenerColeccionDeVotos() {
        for (resenna in listaResennas) {
            db.collection(RepositorioConstantes.resennasCollection).document(resenna.id!!)
                .collection(RepositorioConstantes.votoResennaCollection)
                .get()
                .addOnSuccessListener { documentReference ->
                    for (document in documentReference) {
                        var resennaVotos = document.toObject(ResennaVotada::class.java)
                        votosResennausuarioArrayList.add(resennaVotos)
                        Log.d("Colleccion de votos", votosResennausuarioArrayList.toString())
                    }

                }
                .addOnFailureListener { e ->
                    Log.w(UsuarioRepositorio.tag, "Error al cargar los votos de resenna.", e)
                }
        }
    }

    private fun ajustarBotonLike(currentItem: Resenna, holder: MyViewHolder) {
        for (voto in votosResennausuarioArrayList) {
            if(voto.idUsuario.equals(UsuarioRepositorio.usuarioLogueado.id) && voto.id.equals(currentItem.id)){
                cambiarIconoLike(holder.likeResenna, R.drawable.ic_baseline_thumb_up_alt_24_gris)
                holder.cantVotos.setEnabled(false)
            }
        }
    }

        private fun actualizarCantidadVotos(item : Resenna){
        Log.d("Votos", item.votos.toString())
        item.votos = item.votos?.plus(1)

        Log.d("Votos actualizado", item.votos.toString())

        db.collection(RepositorioConstantes.resennasCollection).
        document(item.id!!).
        set(item).
        addOnSuccessListener { documentReference ->
            Log.d(UsuarioRepositorio.tag, "Voto en resenna actualizado exitosamente.")
        }
            .addOnFailureListener { e ->
                Log.w(UsuarioRepositorio.tag, "Error al actualizar voto resenna.", e)
            }
    }

    private fun cambiarIconoLike(holder: ImageButton, iconoLikeNegro: Int) {
        holder.setBackgroundResource(iconoLikeNegro)
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

    override fun getItemCount(): Int {
        return listaResennas.size
    }

    public class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val resennaPrecio: TextView = itemView.findViewById(R.id.tvPrecio)
        val resennaTienda: TextView = itemView.findViewById(R.id.tvNombreComercio)
        val resennaDireccion: TextView = itemView.findViewById(R.id.tvUbicacion)
        val resennaTiempo: TextView = itemView.findViewById(R.id.tvTiempo)
        val fotoUsuario: ImageView = itemView.findViewById(R.id.ivFoto)
        val likeResenna = itemView.findViewById<ImageButton>(R.id.btnLike)
        val cantVotos: TextView = itemView.findViewById(R.id.tvCantVotos)
    }
}

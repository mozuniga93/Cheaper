package com.example.cheaper

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.cheaper.model.Resenna
import com.squareup.picasso.Picasso
import java.time.LocalDate
import java.time.Period

class AdapterResennas(private val listaResennas: ArrayList<Resenna>) :
    RecyclerView.Adapter<AdapterResennas.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.item,
            parent, false
        )
        return MyViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentItem: Resenna = listaResennas[position]

        holder.resennaPrecio.text = currentItem.precio.toString()
        holder.resennaTienda.text = currentItem.tienda
        val ubicacion =
            obtenerUbicacion(currentItem.provincia, currentItem.lugar, currentItem.virtual)
        holder.resennaDireccion.text = ubicacion
        val tiempo = transformarFecha(currentItem.fecha)
        holder.resennaTiempo.text = tiempo
        Picasso.get().load(currentItem.usuario).into(holder.fotoUsuario)


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

        if (diff <= 1) {
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

    }
}

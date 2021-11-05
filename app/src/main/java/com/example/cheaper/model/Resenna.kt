package com.example.cheaper.model

import java.time.LocalDate

data class Resenna (var id:String? = null, var usuario:String ? = null, var precio:Double? = null, var tienda:String ? = null,
                    var provincia:String? = null, var distrito:String ? = null, var virtual:Boolean? = null, var votos:Int ? = null,
                    var fecha:LocalDate? = null) {
}
package com.limaa.proyectofinal

import com.google.gson.annotations.SerializedName

data class RutaResponse(
    val ok: Boolean? = null,
    val mensaje: String? = null,
    val error: String? = null,
    @SerializedName("productos")
    val pedidos: List<Pedido>? = null
)

data class Pedido(
    @SerializedName("pedido")
    val id: String? = null,

    @SerializedName("nombre")
    val cliente: String? = null,

    val telefono: String? = null,
    val condominio: String? = null,
    val direccion: String? = null,

    @SerializedName("acceso")
    val informacionAcceso: String? = null,

    @SerializedName("horaentrega")
    val horaEntrega: String? = null,

    val estado: String? = null,

    @SerializedName("fechaentrega")
    val fechaEntrega: String? = null,

    @SerializedName("fecharecoger")
    val fechaRecoger: String? = null,

    @SerializedName("horarecoger")
    val horaRecoger: String? = null,

    val color: String? = null,
    val colortexto: String? = null,
    val coordenadas: String? = null,

    @SerializedName("vive_ahi")
    val viveAhi: String? = null,

    @SerializedName("lugar_montaje")
    val lugarMontaje: String? = null,

    @SerializedName("restriccion_entrega")
    val restriccionEntrega: String? = null,

    @SerializedName("restriccion_recoger")
    val restriccionRecoger: String? = null
)


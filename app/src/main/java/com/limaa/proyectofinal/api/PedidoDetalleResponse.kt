package com.limaa.proyectofinal


data class PedidoDetalleResponse(
    val ok: Boolean? = null,
    val mensaje: String? = null,
    val error: String? = null,
    val detalle: List<DetallePedido>? = null
)

data class DetallePedido(
    val parte: String? = null,
    val articulo: String? = null,
    val nombre: String? = null,
    val cantidad: String? = null,
    val precio: String? = null,
    val subtotal: String? = null,
    val extras: String? = null
)
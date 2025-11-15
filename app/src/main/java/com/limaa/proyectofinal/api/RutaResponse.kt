package com.limaa.proyectofinal

data class RutaResponse(
    val ok: Boolean? = null,
    val mensaje: String? = null,
    val error: String? = null,
    val ruta: Ruta? = null
)

data class Ruta(
    val id: String? = null,
    val fecha: String? = null,
    val conductor: String? = null,
    val vehiculo: String? = null,
    val pedidos: List<Pedido>? = null
)

data class Pedido(
    val id: String? = null,
    val cliente: String? = null,
    val direccion: String? = null,
    val estado: String? = null,
    val productos: List<Producto>? = null
)

data class Producto(
    val nombre: String? = null,
    val cantidad: Int? = null,
    val precio: Double? = null
)
package com.limaa.proyectofinal.NavigationRoutes

import kotlinx.serialization.Serializable
// rutas tipadas con @Serializable para evitar errores de string
@Serializable
object PantallaInicio

@Serializable
object Login

@Serializable
object OlvideContrasena

@Serializable
object LocationAccess

@Serializable
object Home

@Serializable
object Inventario

@Serializable
object RutaDelDia

@Serializable
data class DetallePedido(val pedidoId: String)

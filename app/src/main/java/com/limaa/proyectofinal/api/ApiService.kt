package com.limaa.proyectofinal

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

data class LoginRequest(
    val accion: String = "login",
    val usuario: String,
    val password: String
)

data class RutaRequest(
    val accion: String = "ruta",
    val token: String
)

data class PedidoDetalleRequest(
    val accion: String = "pedido",
    val token: String,
    val pedido: String
)

// NUEVO: Request para registrar ubicación
data class RegistrarUbicacionRequest(
    val accion: String = "coordenadas",
    val token: String,
    val pedido: String,
    val coordenadas: String,
    val tipo: String, // "llegada" o "salida"
    val timestamp: String
)

data class UbicacionResponse(
    val ok: Boolean?,
    val mensaje: String?,
    val error: String?
)

interface ApiService {
    @Headers("Content-Type: application/json")
    @POST("apilot.php")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("apilot.php")
    suspend fun obtenerRuta(@Body request: RutaRequest): Response<RutaResponse>

    @POST("apilot.php")
    suspend fun obtenerDetallePedido(@Body request: PedidoDetalleRequest): Response<PedidoDetalleResponse>

    @Headers("Content-Type: application/json")
    @POST("apilot.php")
    suspend fun registrarUbicacion(@Body request: RegistrarUbicacionRequest): Response<UbicacionResponse>
}
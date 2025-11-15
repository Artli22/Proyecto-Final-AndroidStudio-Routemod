package com.limaa.proyectofinal

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

data class LoginRequest(
    val accion: String = "login",  //
    val usuario: String,
    val password: String
)

data class RutaRequest(
    val accion: String = "ruta",
    val token: String  // El token se enviará en el body también
)

interface ApiService {
    @Headers("Content-Type: application/json")
    @POST("apilot.php")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("apilot.php")
    suspend fun obtenerRuta(@Body request: RutaRequest): Response<RutaResponse>
}
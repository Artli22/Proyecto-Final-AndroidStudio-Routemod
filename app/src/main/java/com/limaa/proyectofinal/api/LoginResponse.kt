package com.limaa.proyectofinal

data class LoginResponse(
    val ok: Boolean? = null,
    val token: String? = null,
    val mensaje: String? = null,
    val usuario: String? = null,
    val error: String? = null
)
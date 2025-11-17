package com.limaa.proyectofinal

import android.content.Context
import android.content.SharedPreferences

object TokenManager {
    private const val PREFS_NAME = "RoutemodPrefs"
    private const val KEY_TOKEN = "auth_token"
    private const val KEY_USUARIO = "usuario"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // guarda y lee token/usuario en SharedPreferences
    fun saveToken(context: Context, token: String, usuario: String) {
        getPrefs(context).edit().apply {
            putString(KEY_TOKEN, token)
            putString(KEY_USUARIO, usuario)
            apply()
        }
    }

    // Obtener token
    fun getToken(context: Context): String? {
        return getPrefs(context).getString(KEY_TOKEN, null)
    }

    // Obtener usuario
    fun getUsuario(context: Context): String? {
        return getPrefs(context).getString(KEY_USUARIO, null)
    }

    // Verificar si hay sesión activa
    fun isLoggedIn(context: Context): Boolean {
        return getToken(context) != null
    }

    // Cerrar sesión (borrar token)
    fun clearToken(context: Context) {
        getPrefs(context).edit().clear().apply()
    }
}
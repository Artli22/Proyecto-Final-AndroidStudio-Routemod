package com.limaa.proyectofinal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import android.util.Log

class LoginViewModel : ViewModel() {

    private val _loginState = MutableLiveData<Result<LoginResponse>>()
    val loginState: LiveData<Result<LoginResponse>> = _loginState

    private val api = ApiClient.retrofit.create(ApiService::class.java)


    // maneja login, loading y errores con Result
    fun login(usuario: String, contrasena: String) {
        viewModelScope.launch {
            try {
                Log.d("LoginViewModel", "=== INICIO LOGIN ===")
                Log.d("LoginViewModel", "Usuario: $usuario")

                val request = LoginRequest(
                    accion = "login",
                    usuario = usuario,
                    password = contrasena
                )

                val response = api.login(request)

                Log.d("LoginViewModel", "Código HTTP: ${response.code()}")
                Log.d("LoginViewModel", "Body: ${response.body()}")

                if (!response.isSuccessful) {
                    val errorBody = response.errorBody()?.string()
                    Log.e("LoginViewModel", "Error Body: $errorBody")
                }

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    Log.d("LoginViewModel", "Login exitoso - ok: ${body.ok}, token: ${body.token}")
                    _loginState.value = Result.success(body)
                } else {
                    val errorMsg = "Error en credenciales o servidor. Código: ${response.code()}"
                    Log.e("LoginViewModel", errorMsg)
                    _loginState.value = Result.failure(Exception(errorMsg))
                }
            } catch (e: IOException) {
                Log.e("LoginViewModel", "Error de conexión: ${e.message}", e)
                _loginState.value = Result.failure(Exception("Error de conexión"))
            } catch (e: HttpException) {
                Log.e("LoginViewModel", "Error HTTP: ${e.code()}", e)
                _loginState.value = Result.failure(Exception("Error HTTP"))
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Error inesperado: ${e.message}", e)
                _loginState.value = Result.failure(Exception("Error inesperado"))
            }
        }
    }
}
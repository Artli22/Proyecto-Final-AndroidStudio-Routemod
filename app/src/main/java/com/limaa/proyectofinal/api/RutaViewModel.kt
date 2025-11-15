package com.limaa.proyectofinal

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class RutaViewModel : ViewModel() {

    private val _rutaState = MutableLiveData<Result<RutaResponse>?>()
    val rutaState: LiveData<Result<RutaResponse>?> = _rutaState

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val api = ApiClient.retrofit.create(ApiService::class.java)

    fun obtenerRutaDelDia(context: Context) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                Log.d("RutaViewModel", "=== INICIO OBTENER RUTA ===")

                val token = TokenManager.getToken(context)

                if (token == null) {
                    Log.e("RutaViewModel", "No hay token guardado")
                    _rutaState.value = Result.failure(Exception("No hay sesión activa"))
                    _isLoading.value = false
                    return@launch
                }

                Log.d("RutaViewModel", "Token: $token")

                val request = RutaRequest(
                    accion = "ruta",
                    token = token
                )

                val response = api.obtenerRuta(request)

                Log.d("RutaViewModel", "Código HTTP: ${response.code()}")
                Log.d("RutaViewModel", "Body: ${response.body()}")

                if (!response.isSuccessful) {
                    val errorBody = response.errorBody()?.string()
                    Log.e("RutaViewModel", "Error Body: $errorBody")
                }

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!

                    Log.d("RutaViewModel", "==========================================")
                    Log.d("RutaViewModel", "RESPUESTA COMPLETA DE LA RUTA:")
                    Log.d("RutaViewModel", "==========================================")
                    Log.d("RutaViewModel", "Body completo: $body")
                    Log.d("RutaViewModel", "OK: ${body.ok}")
                    Log.d("RutaViewModel", "Mensaje: ${body.mensaje}")
                    Log.d("RutaViewModel", "Error: ${body.error}")
                    Log.d("RutaViewModel", "Cantidad pedidos: ${body.pedidos?.size ?: 0}")

                    body.pedidos?.forEachIndexed { index, pedido ->
                        Log.d("RutaViewModel", "  Pedido #${index + 1}:")
                        Log.d("RutaViewModel", "    ID: ${pedido.id}")
                        Log.d("RutaViewModel", "    Cliente: ${pedido.cliente}")
                        Log.d("RutaViewModel", "    Condominio: ${pedido.condominio}")
                        Log.d("RutaViewModel", "    Dirección: ${pedido.direccion}")
                        Log.d("RutaViewModel", "    Teléfono: ${pedido.telefono}")
                        Log.d("RutaViewModel", "    Hora Entrega: ${pedido.horaEntrega}")
                        Log.d("RutaViewModel", "    Estado: ${pedido.estado}")
                    }

                    if (body.pedidos.isNullOrEmpty()) {
                        Log.w("RutaViewModel", "La lista de pedidos está vacía")
                    }

                    Log.d("RutaViewModel", "==========================================")

                    if (body.error != null) {
                        Log.e("RutaViewModel", "Error del servidor: ${body.error}")
                        _rutaState.value = Result.failure(Exception(body.error))
                    } else {
                        Log.d("RutaViewModel", "Pedidos obtenidos exitosamente")
                        _rutaState.value = Result.success(body)
                    }
                } else {
                    val errorMsg = "Error en servidor. Código: ${response.code()}"
                    Log.e("RutaViewModel", errorMsg)
                    _rutaState.value = Result.failure(Exception(errorMsg))
                }

            } catch (e: IOException) {
                Log.e("RutaViewModel", "Error de conexión: ${e.message}", e)
                _rutaState.value = Result.failure(Exception("Error de conexión"))
            } catch (e: HttpException) {
                Log.e("RutaViewModel", "Error HTTP: ${e.code()}", e)
                _rutaState.value = Result.failure(Exception("Error HTTP"))
            } catch (e: Exception) {
                Log.e("RutaViewModel", "Error inesperado: ${e.message}", e)
                e.printStackTrace()
                _rutaState.value = Result.failure(Exception("Error inesperado: ${e.message}"))
            } finally {
                _isLoading.value = false
                Log.d("RutaViewModel", "=== FIN OBTENER RUTA ===")
            }
        }
    }

    fun limpiarEstado() {
        _rutaState.value = null
        Log.d("RutaViewModel", "Estado limpiado")
    }
}
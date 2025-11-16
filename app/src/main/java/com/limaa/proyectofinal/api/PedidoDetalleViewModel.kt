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
class PedidoDetalleViewModel : ViewModel() {

    private val _detalleState = MutableLiveData<Result<PedidoDetalleResponse>?>()
    val detalleState: LiveData<Result<PedidoDetalleResponse>?> = _detalleState

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val api = ApiClient.retrofit.create(ApiService::class.java)

    fun obtenerDetallePedido(context: Context, pedidoId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                Log.d("PedidoDetalleVM", "=== INICIO OBTENER DETALLE ===")
                Log.d("PedidoDetalleVM", "Pedido ID: $pedidoId")

                val token = TokenManager.getToken(context)

                if (token == null) {
                    Log.e("PedidoDetalleVM", "No hay token guardado")
                    _detalleState.value = Result.failure(Exception("No hay sesión activa"))
                    _isLoading.value = false
                    return@launch
                }

                Log.d("PedidoDetalleVM", "Token: $token")

                val request = PedidoDetalleRequest(
                    accion = "pedido",
                    token = token,
                    pedido = pedidoId
                )

                val response = api.obtenerDetallePedido(request)

                Log.d("PedidoDetalleVM", "Código HTTP: ${response.code()}")
                Log.d("PedidoDetalleVM", "Body: ${response.body()}")

                if (!response.isSuccessful) {
                    val errorBody = response.errorBody()?.string()
                    Log.e("PedidoDetalleVM", "Error Body: $errorBody")
                }

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!

                    // ========== LOGS PARA DEBUG ==========
                    Log.d("PedidoDetalleVM", "==========================================")
                    Log.d("PedidoDetalleVM", "DETALLE DEL PEDIDO:")
                    Log.d("PedidoDetalleVM", "==========================================")
                    Log.d("PedidoDetalleVM", "OK: ${body.ok}")
                    Log.d("PedidoDetalleVM", "Total items: ${body.detalle?.size ?: 0}")

                    body.detalle?.forEachIndexed { index, item ->
                        Log.d("PedidoDetalleVM", "")
                        Log.d("PedidoDetalleVM", "    Item #${index + 1}:")
                        Log.d("PedidoDetalleVM", "    Parte: ${item.parte}")
                        Log.d("PedidoDetalleVM", "    Artículo: ${item.articulo}")
                        Log.d("PedidoDetalleVM", "    Nombre: ${item.nombre}")
                        Log.d("PedidoDetalleVM", "    Cantidad: ${item.cantidad}")
                        Log.d("PedidoDetalleVM", "    Precio: ${item.precio}")
                        Log.d("PedidoDetalleVM", "    Subtotal: ${item.subtotal}")
                        Log.d("PedidoDetalleVM", "    Extras: ${item.extras}")
                    }

                    Log.d("PedidoDetalleVM", "==========================================")

                    if (body.error != null) {
                        Log.e("PedidoDetalleVM", "Error del servidor: ${body.error}")
                        _detalleState.value = Result.failure(Exception(body.error))
                    } else {
                        Log.d("PedidoDetalleVM", "✅ Detalle obtenido exitosamente")
                        _detalleState.value = Result.success(body)
                    }
                } else {
                    val errorMsg = "Error en servidor. Código: ${response.code()}"
                    Log.e("PedidoDetalleVM", errorMsg)
                    _detalleState.value = Result.failure(Exception(errorMsg))
                }

            } catch (e: IOException) {
                Log.e("PedidoDetalleVM", "Error de conexión: ${e.message}", e)
                _detalleState.value = Result.failure(Exception("Error de conexión"))
            } catch (e: HttpException) {
                Log.e("PedidoDetalleVM", "Error HTTP: ${e.code()}", e)
                _detalleState.value = Result.failure(Exception("Error HTTP"))
            } catch (e: Exception) {
                Log.e("PedidoDetalleVM", "Error inesperado: ${e.message}", e)
                e.printStackTrace()
                _detalleState.value = Result.failure(Exception("Error inesperado: ${e.message}"))
            } finally {
                _isLoading.value = false
                Log.d("PedidoDetalleVM", "=== FIN OBTENER DETALLE ===")
            }
        }
    }

    fun limpiarEstado() {
        _detalleState.value = null
        Log.d("PedidoDetalleVM", "Estado limpiado")
    }
}
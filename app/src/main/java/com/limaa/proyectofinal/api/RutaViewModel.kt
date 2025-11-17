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
    // obtiene ruta + inventario, con logs detallados para debug
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

                    //Historial completos para debug
                    Log.d("RutaViewModel", "==========================================")
                    Log.d("RutaViewModel", "RESPUESTA COMPLETA DE LA RUTA:")
                    Log.d("RutaViewModel", "==========================================")
                    Log.d("RutaViewModel", "Body completo: $body")
                    Log.d("RutaViewModel", "OK: ${body.ok}")
                    Log.d("RutaViewModel", "Mensaje: ${body.mensaje}")
                    Log.d("RutaViewModel", "Error: ${body.error}")

                    // Pedidos
                    Log.d("RutaViewModel", "")
                    Log.d("RutaViewModel", "--- PEDIDOS (PRODUCTOS) ---")
                    Log.d("RutaViewModel", "Total pedidos: ${body.pedidos?.size ?: 0}")

                    if (body.pedidos.isNullOrEmpty()) {
                        Log.w("RutaViewModel", "La lista de pedidos está vacía")
                    } else {
                        body.pedidos.forEachIndexed { index, pedido ->
                            Log.d("RutaViewModel", "")
                            Log.d("RutaViewModel", "    Pedido #${index + 1}:")
                            Log.d("RutaViewModel", "    ID: ${pedido.id}")
                            Log.d("RutaViewModel", "    Cliente: ${pedido.cliente}")
                            Log.d("RutaViewModel", "    Teléfono: ${pedido.telefono}")
                            Log.d("RutaViewModel", "    Dirección: ${pedido.direccion}")
                            Log.d("RutaViewModel", "    Condominio: ${pedido.condominio}")
                            Log.d("RutaViewModel", "    Info acceso: ${pedido.informacionAcceso}")
                            Log.d("RutaViewModel", "    Estado: ${pedido.estado}")
                            Log.d("RutaViewModel", "    --- Fechas y Horas ---")
                            Log.d("RutaViewModel", "    Fecha entrega: ${pedido.fechaEntrega}")
                            Log.d("RutaViewModel", "    Hora entrega: ${pedido.horaEntrega}")
                            Log.d("RutaViewModel", "    Fecha recoger: ${pedido.fechaRecoger}")
                            Log.d("RutaViewModel", "    Hora recoger: ${pedido.horaRecoger}")
                            Log.d("RutaViewModel", "    --- Colores ---")
                            Log.d("RutaViewModel", "    Color fondo: ${pedido.color}")
                            Log.d("RutaViewModel", "    Color texto: ${pedido.colortexto}")
                            Log.d("RutaViewModel", "    --- Detalles ---")
                            Log.d("RutaViewModel", "    Coordenadas: ${pedido.coordenadas}")
                            Log.d("RutaViewModel", "    Vive ahí: ${pedido.viveAhi}")
                            Log.d("RutaViewModel", "    Lugar montaje: ${pedido.lugarMontaje}")
                            Log.d("RutaViewModel", "    Restricción entrega: ${pedido.restriccionEntrega}")
                            Log.d("RutaViewModel", "    Restricción recoger: ${pedido.restriccionRecoger}")
                        }
                    }

                    // Inventario/ carga
                    Log.d("RutaViewModel", "")
                    Log.d("RutaViewModel", "--- INVENTARIO/CARGA ---")
                    Log.d("RutaViewModel", "Total items carga: ${body.carga?.size ?: 0}")

                    if (body.carga.isNullOrEmpty()) {
                        Log.w("RutaViewModel", "La lista de carga está vacía")
                    } else {
                        body.carga.forEachIndexed { index, item ->
                            Log.d("RutaViewModel", "")
                            Log.d("RutaViewModel", "    Item #${index + 1}:")
                            Log.d("RutaViewModel", "    Tipo producto: ${item.tipoProducto}")
                            Log.d("RutaViewModel", "    Nombre artículo: ${item.nombreArticulo}")
                            Log.d("RutaViewModel", "    Suma entrega: ${item.sumaEntrega}")
                            Log.d("RutaViewModel", "    Suma recoge: ${item.sumaRecoge}")
                            Log.d("RutaViewModel", "    CANTIDAD A CARGAR: ${item.cantidadCarga}")
                        }

                        val totalCargar = body.carga.sumOf {
                            it.cantidadCarga?.toDoubleOrNull()?.toInt() ?: 0
                        }
                        Log.d("RutaViewModel", "")
                        Log.d("RutaViewModel", "    RESUMEN INVENTARIO:")
                        Log.d("RutaViewModel", "    Total artículos diferentes: ${body.carga.size}")
                        Log.d("RutaViewModel", "    Total unidades a cargar: $totalCargar")
                    }

                    Log.d("RutaViewModel", "")
                    Log.d("RutaViewModel", "==========================================")
                    //  Fin historial

                    if (body.error != null) {
                        Log.e("RutaViewModel", "Error del servidor: ${body.error}")
                        _rutaState.value = Result.failure(Exception(body.error))
                    } else {
                        Log.d("RutaViewModel", "Ruta obtenida exitosamente")
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
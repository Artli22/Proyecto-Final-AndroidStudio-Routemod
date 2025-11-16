package com.limaa.proyectofinal.ui.Pantallas

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.limaa.proyectofinal.RutaViewModel
import com.limaa.proyectofinal.ItemCarga

object ColoresInventario {
    val naranja = Color(0xFFFF7A3D)
    val naranjaVivo = Color(0xFFFF5722)
    val verdeFosforescente = Color(0xFF76FF03)
    val fondoOscuro = Color(0xFF1A1A2E)
    val fondoTarjeta = Color(0xFFF5F5F5)
    val grisClaro = Color(0xFF9E9E9E)
    val blanco = Color.White
    val verde = Color(0xFF4CAF50)
    val gris = Color(0xFF757575)
}

// Estados del botón
enum class EstadoJornada {
    SALIDA_BODEGA,
    LLEGADA_BODEGA,
    JORNADA_TERMINADA
}

// SharedPreferences para persistencia
object InventarioPrefs {
    private const val PREFS_NAME = "InventarioPrefs"
    private const val KEY_ESTADO_JORNADA = "estado_jornada"
    private const val KEY_ITEMS_CHECKEADOS = "items_checkeados"

    fun guardarEstadoJornada(context: Context, estado: EstadoJornada) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_ESTADO_JORNADA, estado.name)
            .apply()
    }

    fun obtenerEstadoJornada(context: Context): EstadoJornada {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val estadoStr = prefs.getString(KEY_ESTADO_JORNADA, EstadoJornada.SALIDA_BODEGA.name)
        return try {
            EstadoJornada.valueOf(estadoStr ?: EstadoJornada.SALIDA_BODEGA.name)
        } catch (e: Exception) {
            EstadoJornada.SALIDA_BODEGA
        }
    }

    fun guardarItemsCheckeados(context: Context, items: Set<String>) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putStringSet(KEY_ITEMS_CHECKEADOS, items)
            .apply()
    }

    fun obtenerItemsCheckeados(context: Context): Set<String> {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getStringSet(KEY_ITEMS_CHECKEADOS, emptySet()) ?: emptySet()
    }

    fun limpiarTodo(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .clear()
            .apply()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaInventarioVehiculo(
    alVolver: () -> Unit = {},
    alConfirmar: () -> Unit = {},
    alDetalle: () -> Unit = {},
    rutaViewModel: RutaViewModel = viewModel()
) {
    val context = LocalContext.current
    val rutaState by rutaViewModel.rutaState.observeAsState()
    val isLoading by rutaViewModel.isLoading.observeAsState(false)

    // Estado del botón (persistente)
    var estadoJornada by remember {
        mutableStateOf(InventarioPrefs.obtenerEstadoJornada(context))
    }

    // Items checkeados (persistente)
    var itemsCheckeados by remember {
        mutableStateOf(InventarioPrefs.obtenerItemsCheckeados(context))
    }

    // Guardar estado cuando cambia
    LaunchedEffect(estadoJornada) {
        InventarioPrefs.guardarEstadoJornada(context, estadoJornada)
    }

    LaunchedEffect(itemsCheckeados) {
        InventarioPrefs.guardarItemsCheckeados(context, itemsCheckeados)
    }

    // Cargar ruta al iniciar
    LaunchedEffect(Unit) {
        rutaViewModel.obtenerRutaDelDia(context)
    }

    // Observar cambios en el estado
    LaunchedEffect(rutaState) {
        rutaState?.let { result ->
            result.onFailure { error ->
                Toast.makeText(
                    context,
                    error.message ?: "Error al cargar inventario",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Inventario del Vehículo",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = alVolver) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            // Reiniciar estado al recargar
                            InventarioPrefs.limpiarTodo(context)
                            estadoJornada = EstadoJornada.SALIDA_BODEGA
                            itemsCheckeados = emptySet()
                            rutaViewModel.obtenerRutaDelDia(context)
                        },
                        enabled = !isLoading
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Recargar",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ColoresInventario.naranja,
                    titleContentColor = Color.White
                )
            )
        }
    ) { relleno ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(relleno)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = ColoresInventario.naranja
                )
            } else {
                rutaState?.getOrNull()?.let { response ->
                    if (response.carga != null && response.carga.isNotEmpty()) {
                        // Filtrar solo items con cantidad positiva para el conteo
                        val itemsPositivos = response.carga.filter { item ->
                            (item.cantidadCarga?.toDoubleOrNull() ?: 0.0) > 0
                        }

                        val todosCheckeados = itemsPositivos.all { item ->
                            val itemId = "${item.tipoProducto}_${item.nombreArticulo}"
                            itemsCheckeados.contains(itemId)
                        }

                        ContenidoInventario(
                            items = response.carga,
                            estadoJornada = estadoJornada,
                            itemsCheckeados = itemsCheckeados,
                            todosCheckeados = todosCheckeados,
                            cantidadItemsPositivos = itemsPositivos.size,
                            onToggleItem = { item ->
                                val itemId = "${item.tipoProducto}_${item.nombreArticulo}"
                                itemsCheckeados = if (itemsCheckeados.contains(itemId)) {
                                    itemsCheckeados - itemId
                                } else {
                                    itemsCheckeados + itemId
                                }
                            },
                            onClickBoton = {
                                when (estadoJornada) {
                                    EstadoJornada.SALIDA_BODEGA -> {
                                        if (todosCheckeados) {
                                            estadoJornada = EstadoJornada.LLEGADA_BODEGA
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "Debes marcar todos los artículos antes de salir",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                    EstadoJornada.LLEGADA_BODEGA -> {
                                        estadoJornada = EstadoJornada.JORNADA_TERMINADA
                                    }
                                    EstadoJornada.JORNADA_TERMINADA -> {
                                        // No hace nada, está deshabilitado
                                    }
                                }
                            }
                        )
                    } else {
                        InventarioVacio(
                            onRecargar = { rutaViewModel.obtenerRutaDelDia(context) }
                        )
                    }
                } ?: run {
                    InventarioVacio(
                        onRecargar = { rutaViewModel.obtenerRutaDelDia(context) }
                    )
                }
            }
        }
    }
}

@Composable
fun ContenidoInventario(
    items: List<ItemCarga>,
    estadoJornada: EstadoJornada,
    itemsCheckeados: Set<String>,
    todosCheckeados: Boolean,
    cantidadItemsPositivos: Int,
    onToggleItem: (ItemCarga) -> Unit,
    onClickBoton: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header informativo
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = ColoresInventario.fondoTarjeta
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "RESUMEN DE CARGA",
                            color = ColoresInventario.naranja,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Artículos a cargar en el vehículo",
                            fontSize = 12.sp,
                            color = ColoresInventario.grisClaro
                        )
                    }

                    // Indicador de progreso
                    if (estadoJornada == EstadoJornada.SALIDA_BODEGA) {
                        Surface(
                            color = if (todosCheckeados)
                                ColoresInventario.verde.copy(alpha = 0.2f)
                            else
                                ColoresInventario.naranja.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                "${itemsCheckeados.size}/${cantidadItemsPositivos}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (todosCheckeados)
                                    ColoresInventario.verde
                                else
                                    ColoresInventario.naranja,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // Lista agrupada por tipo de producto
        val itemsAgrupados = items.groupBy { it.tipoProducto ?: "Sin categoría" }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            itemsAgrupados.forEach { (tipoProducto, itemsDelTipo) ->
                // Header del tipo de producto
                item {
                    Column {
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = ColoresInventario.naranja.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                tipoProducto.uppercase(),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = ColoresInventario.fondoOscuro,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                letterSpacing = 0.8.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                // Items de este tipo
                items(itemsDelTipo) { item ->
                    val itemId = "${item.tipoProducto}_${item.nombreArticulo}"
                    val isChecked = itemsCheckeados.contains(itemId)
                    val cantidadNumero = item.cantidadCarga?.toDoubleOrNull() ?: 0.0
                    val esCantidadPositiva = cantidadNumero > 0

                    TarjetaItemCarga(
                        item = item,
                        isChecked = isChecked,
                        mostrarCheckbox = estadoJornada == EstadoJornada.SALIDA_BODEGA && esCantidadPositiva,
                        esCantidadNegativa = cantidadNumero < 0,
                        onToggle = {
                            if (esCantidadPositiva) {
                                onToggleItem(item)
                            }
                        }
                    )
                }
            }
        }

        // Footer con botón de estado
        PieInventario(
            estadoJornada = estadoJornada,
            todosCheckeados = todosCheckeados,
            onClickBoton = onClickBoton
        )
    }
}

@Composable
fun TarjetaItemCarga(
    item: ItemCarga,
    isChecked: Boolean,
    mostrarCheckbox: Boolean,
    esCantidadNegativa: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (isChecked && mostrarCheckbox) {
                    Modifier.border(
                        width = 3.dp,
                        color = ColoresInventario.verdeFosforescente,
                        shape = RoundedCornerShape(12.dp)
                    )
                } else if (esCantidadNegativa) {
                    Modifier.border(
                        width = 2.dp,
                        color = ColoresInventario.grisClaro.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp)
                    )
                } else {
                    Modifier
                }
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isChecked && mostrarCheckbox)
                ColoresInventario.verdeFosforescente.copy(alpha = 0.1f)
            else if (esCantidadNegativa)
                ColoresInventario.grisClaro.copy(alpha = 0.1f)
            else
                ColoresInventario.blanco
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isChecked && mostrarCheckbox) 4.dp else 1.dp
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox (si está en modo carga y es cantidad positiva)
            if (mostrarCheckbox) {
                IconButton(
                    onClick = onToggle,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        if (isChecked) Icons.Filled.CheckCircle else Icons.Outlined.Circle,
                        contentDescription = if (isChecked) "Marcado" else "No marcado",
                        tint = if (isChecked)
                            ColoresInventario.verdeFosforescente
                        else
                            ColoresInventario.grisClaro,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))
            }

            // Indicador visual para items negativos
            if (esCantidadNegativa && !mostrarCheckbox) {
                Text(
                    "↓",
                    fontSize = 24.sp,
                    color = ColoresInventario.grisClaro,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            // Nombre del artículo
            Text(
                item.nombreArticulo ?: "Sin nombre",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = if (esCantidadNegativa)
                    ColoresInventario.grisClaro
                else
                    ColoresInventario.fondoOscuro,
                modifier = Modifier.weight(1f),
                textDecoration = if (isChecked && mostrarCheckbox)
                    TextDecoration.LineThrough
                else
                    TextDecoration.None
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Cantidad a cargar
            Surface(
                color = if (esCantidadNegativa)
                    ColoresInventario.grisClaro
                else
                    ColoresInventario.naranja,
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    item.cantidadCarga?.toDoubleOrNull()?.toInt()?.toString() ?: "0",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun PieInventario(
    estadoJornada: EstadoJornada,
    todosCheckeados: Boolean,
    onClickBoton: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = ColoresInventario.blanco,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Mensaje si faltan items
            if (estadoJornada == EstadoJornada.SALIDA_BODEGA && !todosCheckeados) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    color = ColoresInventario.naranja.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "Tenés que marcar todos los artículos para salir de la bodega",
                        fontSize = 12.sp,
                        color = ColoresInventario.naranja,
                        modifier = Modifier.padding(12.dp),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Botón de estado
            Button(
                onClick = onClickBoton,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = when (estadoJornada) {
                        EstadoJornada.SALIDA_BODEGA -> ColoresInventario.naranja
                        EstadoJornada.LLEGADA_BODEGA -> ColoresInventario.verde
                        EstadoJornada.JORNADA_TERMINADA -> ColoresInventario.gris
                    },
                    disabledContainerColor = ColoresInventario.gris
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = when (estadoJornada) {
                    EstadoJornada.SALIDA_BODEGA -> todosCheckeados
                    EstadoJornada.LLEGADA_BODEGA -> true
                    EstadoJornada.JORNADA_TERMINADA -> false
                }
            ) {
                Text(
                    when (estadoJornada) {
                        EstadoJornada.SALIDA_BODEGA -> "SALIDA DE BODEGA"
                        EstadoJornada.LLEGADA_BODEGA -> "LLEGADA A BODEGA"
                        EstadoJornada.JORNADA_TERMINADA -> "JORNADA TERMINADA"
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun InventarioVacio(onRecargar: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("📦", fontSize = 64.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "No hay artículos para cargar",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "El inventario está vacío",
            fontSize = 12.sp,
            color = ColoresInventario.grisClaro
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onRecargar,
            colors = ButtonDefaults.buttonColors(
                containerColor = ColoresInventario.naranja
            )
        ) {
            Icon(Icons.Default.Refresh, "Recargar")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Recargar")
        }
    }
}
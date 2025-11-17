package com.limaa.proyectofinal.ui.Pantallas

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.limaa.proyectofinal.PedidoDetalleViewModel
import com.limaa.proyectofinal.DetallePedido
import com.limaa.proyectofinal.Pedido

object ColoresDetalle {
    val naranja = Color(0xFFFF7A3D)
    val fondoOscuro = Color(0xFF1A1A2E)
    val fondoTarjeta = Color(0xFFF5F5F5)
    val grisClaro = Color(0xFF9E9E9E)
    val blanco = Color.White
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetallePedido(
    pedidoId: String,
    onNavigateBack: () -> Unit = {},
    detalleViewModel: PedidoDetalleViewModel = viewModel()
) {
    val context = LocalContext.current
    val detalleState by detalleViewModel.detalleState.observeAsState()
    val isLoading by detalleViewModel.isLoading.observeAsState(false)

    // Obtener información del pedido desde la ruta
    val rutaViewModel: com.limaa.proyectofinal.RutaViewModel = viewModel()
    val rutaState by rutaViewModel.rutaState.observeAsState()
    val pedidoInfo = rutaState?.getOrNull()?.pedidos?.find { it.id == pedidoId }

    LaunchedEffect(pedidoId) {
        detalleViewModel.obtenerDetallePedido(context, pedidoId)
    }

    LaunchedEffect(detalleState) {
        detalleState?.let { result ->
            result.onFailure { error ->
                Toast.makeText(
                    context,
                    error.message ?: "Error al cargar detalle",
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
                        "Detalle del Pedido",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { detalleViewModel.obtenerDetallePedido(context, pedidoId) },
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
                    containerColor = ColoresDetalle.naranja,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = ColoresDetalle.naranja
                )
            } else {
                detalleState?.getOrNull()?.let { response ->
                    if (response.detalle != null && response.detalle.isNotEmpty()) {
                        ContenidoDetalle(
                            pedidoInfo = pedidoInfo,
                            items = response.detalle
                        )
                    } else {
                        DetalleVacio()
                    }
                } ?: run {
                    DetalleVacio()
                }
            }
        }
    }
}

@Composable
fun ContenidoDetalle(
    pedidoInfo: Pedido?,
    items: List<DetallePedido>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {

        // Lista agrupada por parte
        val itemsAgrupados = items.groupBy { it.parte ?: "Sin categoría" }

        itemsAgrupados.forEach { (parte, itemsDeLaParte) ->
            // Header de la parte
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    color = ColoresDetalle.naranja.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        parte.uppercase(),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = ColoresDetalle.fondoOscuro,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        letterSpacing = 0.8.sp
                    )
                }
            }

            // Items de esta parte
            items(itemsDeLaParte) { item ->
                TarjetaDetalleItem(item)
            }
        }
    }
}

@Composable
fun TarjetaDetalleItem(item: DetallePedido) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = ColoresDetalle.blanco
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Nombre del artículo
            Text(
                item.nombre ?: "Sin nombre",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = ColoresDetalle.fondoOscuro
            )

            // Cantidad
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Cantidad:",
                    fontSize = 12.sp,
                    color = ColoresDetalle.grisClaro
                )
                Text(
                    item.cantidad?.toDoubleOrNull()?.toInt()?.toString() ?: "0",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColoresDetalle.naranja
                )
            }

            // Extras si existen
            if (!item.extras.isNullOrBlank() && item.extras != "0") {
                Surface(
                    color = ColoresDetalle.fondoTarjeta,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        "Extras: ${item.extras}",
                        fontSize = 12.sp,
                        color = ColoresDetalle.fondoOscuro,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DetalleVacio() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("📋", fontSize = 64.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Sin detalles",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "No se encontraron artículos en este pedido",
            fontSize = 14.sp,
            color = ColoresDetalle.grisClaro
        )
    }
}
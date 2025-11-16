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

    // Cargar detalle al iniciar
    LaunchedEffect(pedidoId) {
        detalleViewModel.obtenerDetallePedido(context, pedidoId)
    }

    // Observar cambios en el estado
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
                            pedidoId = pedidoId,
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
    pedidoId: String,
    items: List<DetallePedido>
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header con info del pedido
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = ColoresDetalle.fondoTarjeta
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    "PEDIDO #$pedidoId",
                    color = ColoresDetalle.naranja,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "${items.size} artículos en este pedido",
                    fontSize = 12.sp,
                    color = ColoresDetalle.grisClaro
                )
            }
        }

        // Lista agrupada por parte
        val itemsAgrupados = items.groupBy { it.parte ?: "Sin categoría" }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsAgrupados.forEach { (parte, itemsDeLaParte) ->
                // Header de la parte
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
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

        // Footer con total
        FooterTotal(items = items)
    }
}

@Composable
fun TarjetaDetalleItem(item: DetallePedido) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = ColoresDetalle.blanco
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Nombre del artículo
            Text(
                item.nombre ?: "Sin nombre",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = ColoresDetalle.fondoOscuro
            )

            // Código del artículo
            if (item.articulo != null) {
                Text(
                    "Código: ${item.articulo}",
                    fontSize = 12.sp,
                    color = ColoresDetalle.grisClaro
                )
            }

            Divider(
                modifier = Modifier.padding(vertical = 4.dp),
                color = ColoresDetalle.grisClaro.copy(alpha = 0.3f)
            )

            // Detalles numéricos
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "Cantidad",
                        fontSize = 11.sp,
                        color = ColoresDetalle.grisClaro
                    )
                    Text(
                        item.cantidad ?: "0",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColoresDetalle.naranja
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "Precio Unit.",
                        fontSize = 11.sp,
                        color = ColoresDetalle.grisClaro
                    )
                    Text(
                        "Q ${item.precio ?: "0.00"}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "Subtotal",
                        fontSize = 11.sp,
                        color = ColoresDetalle.grisClaro
                    )
                    Text(
                        "Q ${item.subtotal ?: "0.00"}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColoresDetalle.fondoOscuro
                    )
                }
            }

            // Extras si existen
            if (!item.extras.isNullOrBlank() && item.extras != "0") {
                Spacer(modifier = Modifier.height(4.dp))
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
fun FooterTotal(items: List<DetallePedido>) {
    val total = items.sumOf {
        it.subtotal?.toDoubleOrNull() ?: 0.0
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = ColoresDetalle.blanco,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "TOTAL DEL PEDIDO",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColoresDetalle.fondoOscuro,
                    letterSpacing = 0.5.sp
                )

                Text(
                    "Q ${String.format("%.2f", total)}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColoresDetalle.naranja
                )
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
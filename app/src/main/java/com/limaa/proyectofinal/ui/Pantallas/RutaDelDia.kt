package com.limaa.proyectofinal.ui.Pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.limaa.proyectofinal.Pedido
import com.limaa.proyectofinal.RutaViewModel

// Creado por: Ivan Morataya

object Colores {
    val Naranja = Color(0xFFFF7A3D)
    val Naranja2 = Color(0xFFFFEBE5)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutaPantalla(
    onNavigateBack: () -> Unit = {},
    onViewOrder: (Pedido) -> Unit = {},
    viewModel: RutaViewModel = viewModel()
) {
    val context = LocalContext.current
    val rutaState by viewModel.rutaState.observeAsState()
    val isLoading by viewModel.isLoading.observeAsState(false)

    LaunchedEffect(Unit) {
        viewModel.obtenerRutaDelDia(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Mis Rutas",
                        color = Color.Black,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator(
                                color = Colores.Naranja
                            )
                            Text(
                                "Cargando rutas...",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }

                rutaState?.isFailure == true -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Text(
                                "Error al cargar las rutas",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                rutaState?.exceptionOrNull()?.message ?: "Error desconocido",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                            Button(
                                onClick = { viewModel.obtenerRutaDelDia(context) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Colores.Naranja
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Reintentar")
                            }
                        }
                    }
                }

                rutaState?.getOrNull()?.pedidos.isNullOrEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Text(
                                "No hay rutas disponibles",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                "Aún no tienes pedidos asignados",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                            Button(
                                onClick = { viewModel.obtenerRutaDelDia(context) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Colores.Naranja
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Actualizar")
                            }
                        }
                    }
                }

                else -> {
                    val pedidos = rutaState?.getOrNull()?.pedidos ?: emptyList()

                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Colores.Naranja2
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "Rutas de hoy",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Colores.Naranja
                                    )
                                    Text(
                                        "${pedidos.size} ${if (pedidos.size == 1) "pedido" else "pedidos"}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(pedidos) { pedido ->
                                DeliveryStopCard(
                                    pedido = pedido,
                                    onMarkArrival = {
                                    },
                                    onViewOrder = { onViewOrder(pedido) }
                                )
                            }

                            item {
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DeliveryStopCard(
    pedido: Pedido,
    onMarkArrival: () -> Unit = {},
    onViewOrder: () -> Unit = {}
) {
    val estadoColor = try {
        if (pedido.color != null && pedido.color.startsWith("#")) {
            Color(android.graphics.Color.parseColor(pedido.color))
        } else {
            Color.LightGray
        }
    } catch (e: Exception) {
        Color.LightGray
    }

    val yaEntregado = pedido.estado?.contains("Recoger", ignoreCase = true) == true

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                pedido.cliente ?: "Cliente desconocido",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    pedido.condominio?.let { condominio ->
                        if (condominio.isNotBlank()) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Ubicación:",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Gray
                                )
                                Text(
                                    condominio,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.Black
                                )
                            }
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Pedido:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray
                        )
                        Text(
                            pedido.id ?: "N/A",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }

                    pedido.direccion?.let { direccion ->
                        if (direccion.isNotBlank()) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    "Dirección:",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Gray
                                )
                                Text(
                                    direccion,
                                    fontSize = 14.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    pedido.informacionAcceso?.let { info ->
                        if (info.isNotBlank()) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    "Acceso:",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Gray
                                )
                                Text(
                                    info,
                                    fontSize = 13.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    pedido.telefono?.let { tel ->
                        if (tel.isNotBlank()) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Tel:",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Gray
                                )
                                Text(
                                    tel,
                                    fontSize = 14.sp,
                                    color = Colores.Naranja,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    pedido.horaEntrega?.let { hora ->
                        Text(
                            hora.substring(0, 5),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Colores.Naranja
                        )
                    }

                    pedido.estado?.let { estado ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = estadoColor.copy(alpha = 0.2f)
                        ) {
                            Text(
                                estado,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = estadoColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onMarkArrival,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (yaEntregado) Colores.Naranja else Color.LightGray
                    ),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        if (yaEntregado) "Marcar Recoger" else "Marcar Entrega",
                        fontSize = 12.sp
                    )
                }

                OutlinedButton(
                    onClick = onViewOrder,
                    modifier = Modifier.weight(0.7f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Colores.Naranja
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        Colores.Naranja
                    )
                ) {
                    Text("Ver detalles", fontSize = 12.sp)
                }
            }
        }
    }
}
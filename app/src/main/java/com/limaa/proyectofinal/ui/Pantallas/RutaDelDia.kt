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
import androidx.compose.material.icons.filled.LocationOn
import android.content.Intent
import android.net.Uri
import android.content.Context
import androidx.compose.material.icons.filled.CameraAlt
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

// Creado por: Ivan Morataya

object Colores {
    val Naranja = Color(0xFFFF7A3D)
    val Naranja2 = Color(0xFFFFEBE5)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutaPantalla(
    onNavigateBack: () -> Unit = {},
    onViewOrder: (String) -> Unit = {},
    viewModel: RutaViewModel = viewModel()
) {
    val context = LocalContext.current
    val rutaState by viewModel.rutaState.observeAsState()
    val isLoading by viewModel.isLoading.observeAsState(false)

    LaunchedEffect(Unit) {
        if (rutaState == null) {
            viewModel.obtenerRutaDelDia(context)
        }
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
                                    onViewOrder = { onViewOrder(pedido.id ?: "") },
                                    onOpenMaps = {
                                        pedido.coordenadas?.let { coords ->
                                            if (coords.isNotBlank() && coords != "null") {
                                                val intent = Intent(
                                                    Intent.ACTION_VIEW,
                                                    Uri.parse("geo:0,0?q=$coords(${pedido.cliente ?: "Ubicación"})")
                                                )
                                                intent.setPackage("com.google.android.apps.maps")

                                                try {
                                                    context.startActivity(intent)
                                                } catch (e: Exception) {
                                                    val webIntent = Intent(
                                                        Intent.ACTION_VIEW,
                                                        Uri.parse("https://www.google.com/maps/search/?api=1&query=$coords")
                                                    )
                                                    context.startActivity(webIntent)
                                                }
                                            }
                                        }
                                    }
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
    onViewOrder: () -> Unit = {},
    onOpenMaps: () -> Unit = {}
) {
    val context = LocalContext.current
    val sharedPrefs = context.getSharedPreferences("pedidos_estado", Context.MODE_PRIVATE)

    val estadoPedido = remember { mutableStateOf(sharedPrefs.getInt(pedido.id ?: "", 0)) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            Toast.makeText(context, "✓ Comprobación completada", Toast.LENGTH_SHORT).show()
        }
    }

    val estadoColor = try {
        if (pedido.color != null && pedido.color.startsWith("#")) {
            Color(android.graphics.Color.parseColor(pedido.color))
        } else {
            Color.LightGray
        }
    } catch (e: Exception) {
        Color.LightGray
    }

    val tieneCoordenadasValidas = !pedido.coordenadas.isNullOrBlank() &&
            pedido.coordenadas != "null"

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
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    pedido.cliente ?: "Cliente desconocido",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    pedido.telefono?.let { tel ->
                        if (tel.isNotBlank()) {
                            Text(
                                tel,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Colores.Naranja
                            )
                        }
                    }

                    pedido.estado?.let { estado ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = estadoColor.copy(alpha = 0.2f)
                        ) {
                            Text(
                                estado,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = estadoColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    pedido.condominio?.let { condominio ->
                        if (condominio.isNotBlank()) {
                            Text(
                                "Sector: $condominio",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black
                            )
                        }
                    }

                    pedido.direccion?.let { direccion ->
                        if (direccion.isNotBlank()) {
                            Text(
                                "Dir: $direccion",
                                fontSize = 11.sp,
                                color = Color.Gray,
                                maxLines = 2
                            )
                        }
                    }

                    pedido.informacionAcceso?.let { info ->
                        if (info.isNotBlank()) {
                            Text(
                                "Acceso: $info",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (tieneCoordenadasValidas) {
                        IconButton(
                            onClick = onOpenMaps,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Abrir en Maps",
                                tint = Colores.Naranja,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            val uri = Uri.parse("content://temp")
                            cameraLauncher.launch(uri)
                        },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Tomar foto",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        val nuevoEstado = when (estadoPedido.value) {
                            0 -> 1
                            1 -> 2
                            else -> 2
                        }
                        estadoPedido.value = nuevoEstado
                        sharedPrefs.edit().putInt(pedido.id ?: "", nuevoEstado).apply()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = when (estadoPedido.value) {
                            0 -> Colores.Naranja
                            1 -> Color(0xFF4CAF50)
                            else -> Color.LightGray
                        }
                    ),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    enabled = estadoPedido.value != 2,
                    contentPadding = PaddingValues(vertical = 10.dp)
                ) {
                    Text(
                        when (estadoPedido.value) {
                            0 -> "Marcar llegada"
                            1 -> "Marcar salida"
                            else -> "Finalizado"
                        },
                        fontSize = 12.sp
                    )
                }

                OutlinedButton(
                    onClick = onViewOrder,
                    modifier = Modifier.weight(0.8f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Colores.Naranja
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Colores.Naranja),
                    contentPadding = PaddingValues(vertical = 10.dp)
                ) {
                    Text("Ver detalles", fontSize = 12.sp)
                }
            }
        }
    }
}
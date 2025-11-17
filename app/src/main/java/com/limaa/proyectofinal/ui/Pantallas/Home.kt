package com.limaa.proyectofinal.ui.Pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.limaa.proyectofinal.RutaViewModel

// Creado por Arturo Lima

@Composable
fun RouteModScreen(
    onVerMasInventario: () -> Unit = {},
    onVerMasRuta: () -> Unit = {},
    onCerrarSesion: () -> Unit = {},
    viewModel: RutaViewModel = viewModel()
) {
    val context = LocalContext.current
    val rutaState by viewModel.rutaState.observeAsState()

    //  Cargar TODOS los datos de la API (solo si no hay datos)
    LaunchedEffect(Unit) {
        if (rutaState == null) {
            viewModel.obtenerRutaDelDia(context)
        }
    }

    // Obtener los primeros 3 pedidos (rutas)
    val pedidos = rutaState?.getOrNull()?.pedidos ?: emptyList()
    val primerosTresPedidos = pedidos.take(3)

    //  Obtener los primeros 3 items de inventario (solo con cantidad positiva)
    val itemsCarga = rutaState?.getOrNull()?.carga ?: emptyList()
    val itemsPositivos = itemsCarga.filter { item ->
        (item.cantidadCarga?.toDoubleOrNull() ?: 0.0) > 0
    }
    val primerosTresItems = itemsPositivos.take(3)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F5FA))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header con botón de cerrar sesión en la esquina
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFFFF7622),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ROUTEMOD",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                // Botón de cerrar sesión (reemplaza el ícono de usuario)
                Button(
                    onClick = onCerrarSesion,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFC6AE)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(
                        text = "CERRAR SESIÓN",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(88.dp))

            // Sección de Inventario
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Inventario",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(Color(0xFF121223))
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Mostrar los primeros 3 items o mensaje de carga
                if (primerosTresItems.isEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Cargando...",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.Gray
                        )
                        Text(
                            text = "",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.Gray
                        )
                    }
                } else {
                    primerosTresItems.forEachIndexed { index, item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = item.nombreArticulo?.ifBlank { "Sin nombre" }
                                    ?: "Sin nombre",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                modifier = Modifier.weight(1f)
                            )

                            Text(
                                text = "Cantidad ${item.cantidadCarga?.toDoubleOrNull()?.toInt() ?: 0}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color.Gray
                            )
                        }

                        if (index < primerosTresItems.size - 1) {
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onVerMasInventario,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .height(36.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF7622)
                    ),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text(
                        text = "VER MAS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(Color(0xFF121223))
                )
            }

            Spacer(modifier = Modifier.height(88.dp))

            // Sección de Ruta Diaria
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Ruta Diaria",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(Color(0xFF121223))
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (primerosTresPedidos.isEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Cargando...",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.Gray
                        )
                        Text(
                            text = "",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.Gray
                        )
                    }
                } else {
                    primerosTresPedidos.forEachIndexed { index, pedido ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = pedido.condominio?.ifBlank { "Sin ubicación" }
                                    ?: "Sin ubicación",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal
                            )

                            Text(
                                text = pedido.telefono?.ifBlank { "Sin teléfono" }
                                    ?: "Sin teléfono",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color.Gray
                            )
                        }

                        if (index < primerosTresPedidos.size - 1) {
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onVerMasRuta,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .height(36.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF7622)
                    ),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text(
                        text = "VER MAS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(Color(0xFF121223))
                )
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RouteModScreenPreview() {
    RouteModScreen()
}
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.limaa.proyectofinal.RutaViewModel
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.ContentScale
import com.limaa.proyectofinal.R

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

    LaunchedEffect(Unit) {
        if (rutaState == null) {
            viewModel.obtenerRutaDelDia(context)
        }
    }

    val pedidos = rutaState?.getOrNull()?.pedidos ?: emptyList()
    val primerosTresPedidos = pedidos.take(3)

    val itemsCarga = rutaState?.getOrNull()?.carga ?: emptyList()
    val itemsPositivos = itemsCarga.filter { (it.cantidadCarga?.toDoubleOrNull() ?: 0.0) > 0 }
    val primerosTresItems = itemsPositivos.take(3)

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFF7622))
        )
        Image(
            painter = painterResource(id = R.drawable.segundointentocamion1),
            contentDescription = "Camión grande en fondo naranja",
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(1.0f),
            contentScale = ContentScale.FillBounds
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.68f)) // opacidad ajustada para legibilidad
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFF7622), RoundedCornerShape(12.dp))
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

                Button(
                    onClick = onCerrarSesion,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A1A2E)),
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

            Spacer(modifier = Modifier.height(24.dp))

            // Inventario
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.92f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Inventario", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = Color.Black)

                    Box(Modifier.fillMaxWidth().height(2.dp).background(Color(0xFF121223)))

                    Spacer(Modifier.height(16.dp))

                    if (primerosTresItems.isEmpty()) {
                        Text("Cargando...", fontSize = 14.sp, color = Color.Gray)
                    } else {
                        primerosTresItems.forEachIndexed { index, item ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = item.nombreArticulo?.ifBlank { "Sin nombre" } ?: "Sin nombre",
                                    fontSize = 14.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "Cantidad ${item.cantidadCarga?.toDoubleOrNull()?.toInt() ?: 0}",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            }
                            if (index < primerosTresItems.size - 1) Spacer(Modifier.height(12.dp))
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = onVerMasInventario,
                        modifier = Modifier.align(Alignment.CenterHorizontally).height(36.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7622)),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Text("VER MÁS", fontSize = 12.sp)
                    }

                    Spacer(Modifier.height(8.dp))
                    Box(Modifier.fillMaxWidth().height(2.dp).background(Color(0xFF121223)))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            //  Ruta diaria
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.92f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Ruta Diaria", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = Color.Black)

                    Box(Modifier.fillMaxWidth().height(2.dp).background(Color(0xFF121223)))

                    Spacer(Modifier.height(16.dp))

                    if (primerosTresPedidos.isEmpty()) {
                        Text("Cargando...", fontSize = 14.sp, color = Color.Gray)
                    } else {
                        primerosTresPedidos.forEachIndexed { index, pedido ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = pedido.condominio?.ifBlank { "Sin ubicación" } ?: "Sin ubicación",
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = pedido.telefono?.ifBlank { "Sin teléfono" } ?: "Sin teléfono",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            }
                            if (index < primerosTresPedidos.size - 1) Spacer(Modifier.height(12.dp))
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = onVerMasRuta,
                        modifier = Modifier.align(Alignment.CenterHorizontally).height(36.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7622)),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Text("VER MÁS", fontSize = 12.sp)
                    }

                    Spacer(Modifier.height(8.dp))
                    Box(Modifier.fillMaxWidth().height(2.dp).background(Color(0xFF121223)))
                }
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
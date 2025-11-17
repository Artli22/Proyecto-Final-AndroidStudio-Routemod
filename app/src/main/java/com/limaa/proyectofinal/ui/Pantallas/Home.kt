package com.limaa.proyectofinal.ui.Pantallas

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.limaa.proyectofinal.RutaViewModel
import androidx.compose.foundation.Image
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import com.limaa.proyectofinal.R
import kotlinx.coroutines.delay

@Composable
fun RouteModScreen(
    onVerMasInventario: () -> Unit = {},
    onVerMasRuta: () -> Unit = {},
    onCerrarSesion: () -> Unit = {},
    viewModel: RutaViewModel = viewModel()
) {
    val context = LocalContext.current
    val rutaState by viewModel.rutaState.observeAsState()

    // Estados de animación
    var showCarga by remember { mutableStateOf(false) }
    var showRuta by remember { mutableStateOf(false) }
    var headerScale by remember { mutableStateOf(0.8f) }

    LaunchedEffect(Unit) {
        if (rutaState == null) {
            viewModel.obtenerRutaDelDia(context)
        }

        // Secuencia de animaciones
        animate(0.8f, 1f, animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )) { value, _ ->
            headerScale = value
        }

        delay(200)
        showCarga = true
        delay(300)
        showRuta = true
    }

    val pedidos = rutaState?.getOrNull()?.pedidos ?: emptyList()
    val primerosTresPedidos = pedidos.take(3)

    val itemsCarga = rutaState?.getOrNull()?.carga ?: emptyList()
    val itemsPositivos = itemsCarga.filter { (it.cantidadCarga?.toDoubleOrNull() ?: 0.0) > 0 }
    val primerosTresItems = itemsPositivos.take(3)

    Box(modifier = Modifier.fillMaxSize()) {
        // Fondo con imagen
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
                .background(Color.Black.copy(alpha = 0.68f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header con animación de escala
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(headerScale)
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

            // Carga con animación desde la izquierda
            AnimatedVisibility(
                visible = showCarga,
                enter = slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                ) + fadeIn(animationSpec = tween(400))
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.92f)),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Carga de ruta",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )

                        Box(Modifier.fillMaxWidth().height(2.dp).background(Color(0xFF121223)))

                        Spacer(Modifier.height(16.dp))

                        if (primerosTresItems.isEmpty()) {
                            Text("Cargando...", fontSize = 14.sp, color = Color.Gray)
                        } else {
                            primerosTresItems.forEachIndexed { index, item ->
                                // Animación individual de cada item
                                var itemVisible by remember { mutableStateOf(false) }

                                LaunchedEffect(Unit) {
                                    delay(100L * index)
                                    itemVisible = true
                                }

                                AnimatedVisibility(
                                    visible = itemVisible,
                                    enter = fadeIn(animationSpec = tween(300)) +
                                            expandVertically(animationSpec = tween(300))
                                ) {
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
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Ruta diaria con animación desde la derecha
            AnimatedVisibility(
                visible = showRuta,
                enter = slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                ) + fadeIn(animationSpec = tween(400))
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.92f)),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Ruta Diaria",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )

                        Box(Modifier.fillMaxWidth().height(2.dp).background(Color(0xFF121223)))

                        Spacer(Modifier.height(16.dp))

                        if (primerosTresPedidos.isEmpty()) {
                            Text("Cargando...", fontSize = 14.sp, color = Color.Gray)
                        } else {
                            primerosTresPedidos.forEachIndexed { index, pedido ->
                                // Animación individual de cada pedido
                                var pedidoVisible by remember { mutableStateOf(false) }

                                LaunchedEffect(Unit) {
                                    delay(100L * index)
                                    pedidoVisible = true
                                }

                                AnimatedVisibility(
                                    visible = pedidoVisible,
                                    enter = fadeIn(animationSpec = tween(300)) +
                                            expandVertically(animationSpec = tween(300))
                                ) {
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
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
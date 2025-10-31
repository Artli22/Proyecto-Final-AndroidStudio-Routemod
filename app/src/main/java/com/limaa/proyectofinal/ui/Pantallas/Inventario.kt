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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Creado por: Ivan Morataya
data class Producto(
    val nombre: String,
    val cantidad: String,
    val tamano: String
)

object ColoresInventario {
    val naranja = Color(0xFFFF7A3D)
    val fondoOscuro = Color(0xFF1A1A2E)
    val fondoTarjeta = Color(0xFF25253A)
    val grisClaro = Color(0xFF9E9E9E)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaInventarioVehiculo(
    alVolver: () -> Unit = {},
    alConfirmar: () -> Unit = {},
    alDetalle: () -> Unit = {}
) {
    val productos = remember {
        listOf(
            Producto("Producto A", "Cantidad X", "14\""),
            Producto("Producto B", "Cantidad X", "14\""),
            Producto("Producto C", "Cantidad X", "14\"")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Inventario del Vehículo",
                        fontSize = 16.sp,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = alVolver) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors()
            )
        },
    ) { relleno ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(relleno)
        ) {
            Text(
                "INVENTARIO A SUBIR AL VEHÍCULO",
                modifier = Modifier.padding(16.dp),
                color = ColoresInventario.naranja,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(productos) { producto ->
                    TarjetaProducto(producto)
                }
            }

            PieInventario(
                totalTotal = "X",
                alDetalle = alDetalle,
                alConfirmar = alConfirmar
            )
        }
    }
}

@Composable
fun TarjetaProducto(producto: Producto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Yellow)
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    producto.nombre,
                    fontSize = 14.sp
                )
                Text(
                    producto.cantidad,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    producto.tamano,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun PieInventario(
    totalTotal: String,
    alDetalle: () -> Unit,
    alConfirmar: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "TOTAL DE COSAS:",
                fontSize = 12.sp,
                letterSpacing = 0.5.sp
            )
            Text(
                totalTotal,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = alDetalle) {
                Text(
                    "DETALLE",
                    color = ColoresInventario.naranja,
                    fontSize = 12.sp
                )
            }
        }
        Button(
            onClick = alConfirmar,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ColoresInventario.naranja
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                "CONFIRMAR",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true, name = "Pantalla Inventario Vehículo")
@Composable
fun PreviewPantallaInventarioVehiculo() {
    MaterialTheme {
        PantallaInventarioVehiculo(
            alVolver = {},
            alConfirmar = {},
            alDetalle = {}
        )
    }
}
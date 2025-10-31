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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Creado por: Ivan Morataya
data class Paradas(
    val nombre: String,
    val lugar: String,
    val direccion: String,
    val tiempo: String,
    val codigo: String,
    val llegada: Boolean = false
)

object Colores {
    val Naranja = Color(0xFFFF7A3D)
    val Naranja2 = Color(0xFFFFEBE5)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutaPantalla(
    onNavigateBack: () -> Unit = {},
    onViewOrder: (Paradas) -> Unit = {}
) {
    val stops = remember {
        listOf(
            Paradas(
                nombre = "Carlos Méndez",
                lugar = "Zona 10",
                direccion = "15 Avenida 12-34",
                tiempo = "09:00AM",
                codigo = "Código 1308",
                llegada = true
            ),
            Paradas(
                nombre = "Rita Gonzáles",
                lugar = "Los Manantiales",
                direccion = "7ma Calle 5-26",
                tiempo = "10:30AM",
                codigo = "Código 1411",
                llegada = true
            ),
            Paradas(
                nombre = "María López",
                lugar = "San Cristóbal",
                direccion = "3ra Calle 8-15",
                tiempo = "11:45AM",
                codigo = "Código 1523",
                llegada = false
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Ruta del día",
                        color = Color.LightGray,
                        fontSize = 14.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver"
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
            Text(
                "20/09",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                color = Colores.Naranja,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(stops) { stop ->
                    DeliveryStopCard(
                        stop = stop,
                        onMarkArrival = {  },
                        onViewOrder = { onViewOrder(stop) }
                    )
                }
            }
        }
    }
}

@Composable
fun DeliveryStopCard(
    stop: Paradas,
    onMarkArrival: () -> Unit = {},
    onViewOrder: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                stop.nombre,
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            stop.lugar,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            stop.codigo,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Text(
                            stop.direccion,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }

                Text(
                    stop.tiempo,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
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
                        containerColor = if (stop.llegada) Colores.Naranja else Color.LightGray
                    ),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "Marcar Llegada/Salida",
                        fontSize = 12.sp
                    )
                }

                OutlinedButton(
                    onClick = onViewOrder,
                    modifier = Modifier.weight(0.6f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Colores.Naranja
                    )
                ) {
                    Text("Ver pedido", fontSize = 12.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Ruta del Día Screen")
@Composable
fun PreviewRoutaPantalla() {
    MaterialTheme {
        RoutaPantalla(
            onNavigateBack = {
            },
            onViewOrder = { parada ->
            }
        )
    }
}
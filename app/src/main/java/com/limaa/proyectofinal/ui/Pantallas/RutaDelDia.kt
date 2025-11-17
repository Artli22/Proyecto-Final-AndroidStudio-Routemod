package com.limaa.proyectofinal.ui.Pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Phone
import android.content.Intent
import android.net.Uri
import android.content.Context
import androidx.compose.material.icons.filled.CameraAlt
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.material.icons.filled.Refresh

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
    var resetKey by remember { mutableStateOf(0) }

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
                actions = {
                    IconButton(
                        onClick = {
                            val sharedPrefs = context.getSharedPreferences("pedidos_estado", Context.MODE_PRIVATE)
                            val editor = sharedPrefs.edit()
                            val pedidos = rutaState?.getOrNull()?.pedidos ?: emptyList()

                            pedidos.forEach { pedido ->
                                editor.remove(pedido.id ?: "")
                            }
                            editor.apply()
                            resetKey++

                            Toast.makeText(context, "Estados de pedidos reiniciados", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reiniciar estados",
                            tint = Colores.Naranja
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
                            items(
                                items = pedidos,
                                key = { "${it.id}_$resetKey" }
                            ) { pedido ->
                                DeliveryStopCard(
                                    pedido = pedido,
                                    resetTrigger = resetKey,
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
                                    },
                                    onCall = {
                                        pedido.telefono?.let { telefono ->
                                            if (telefono.isNotBlank()) {
                                                val intent = Intent(
                                                    Intent.ACTION_DIAL,
                                                    Uri.parse("tel:$telefono")
                                                )
                                                try {
                                                    context.startActivity(intent)
                                                } catch (e: Exception) {
                                                    Toast.makeText(
                                                        context,
                                                        "No se pudo abrir la app de llamadas",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
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
    resetTrigger: Int = 0,
    onViewOrder: () -> Unit = {},
    onOpenMaps: () -> Unit = {},
    onCall: () -> Unit = {}
) {
    val context = LocalContext.current
    val sharedPrefs = context.getSharedPreferences("pedidos_estado", Context.MODE_PRIVATE)

    val estadoPedido = remember(resetTrigger) {
        mutableStateOf(sharedPrefs.getInt(pedido.id ?: "", 0))
    }

    val fusedLocationClient = remember {
        com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(context)
    }

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

    val tieneTelefonoValido = !pedido.telefono.isNullOrBlank()

    fun marcarEstadoConCoordenadas(nuevoEstado: Int) {
        if (nuevoEstado == 1) {
            if (android.Manifest.permission.ACCESS_FINE_LOCATION.let { perm ->
                    androidx.core.content.ContextCompat.checkSelfPermission(context, perm)
                } == android.content.pm.PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(context, "Obteniendo ubicación...", Toast.LENGTH_SHORT).show()

                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        val coordenadas = "${location.latitude},${location.longitude}"

                        sharedPrefs.edit()
                            .putInt(pedido.id ?: "", nuevoEstado)
                            .putString("${pedido.id}_coords", coordenadas)
                            .putLong("${pedido.id}_timestamp", System.currentTimeMillis())
                            .apply()

                        estadoPedido.value = nuevoEstado
                        enviarCoordenadasAAPI(context, pedido.id ?: "", coordenadas, "llegada")
                        Toast.makeText(context, "✓ Llegada registrada", Toast.LENGTH_SHORT).show()
                    } else {
                        val locationRequest = com.google.android.gms.location.LocationRequest.Builder(
                            com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
                            10000
                        ).build()

                        val locationCallback = object : com.google.android.gms.location.LocationCallback() {
                            override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
                                locationResult.lastLocation?.let { newLocation ->
                                    val coordenadas = "${newLocation.latitude},${newLocation.longitude}"

                                    sharedPrefs.edit()
                                        .putInt(pedido.id ?: "", nuevoEstado)
                                        .putString("${pedido.id}_coords", coordenadas)
                                        .putLong("${pedido.id}_timestamp", System.currentTimeMillis())
                                        .apply()

                                    estadoPedido.value = nuevoEstado
                                    enviarCoordenadasAAPI(context, pedido.id ?: "", coordenadas, "llegada")
                                    Toast.makeText(context, "✓ Llegada registrada", Toast.LENGTH_SHORT).show()

                                    fusedLocationClient.removeLocationUpdates(this)
                                }
                            }
                        }

                        try {
                            fusedLocationClient.requestLocationUpdates(
                                locationRequest,
                                locationCallback,
                                android.os.Looper.getMainLooper()
                            )

                            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                fusedLocationClient.removeLocationUpdates(locationCallback)
                                if (estadoPedido.value != nuevoEstado) {
                                    Toast.makeText(context, "No se pudo obtener ubicación", Toast.LENGTH_LONG).show()
                                }
                            }, 15000)
                        } catch (e: SecurityException) {
                            Toast.makeText(context, "Error de permisos", Toast.LENGTH_SHORT).show()
                        }
                    }
                }.addOnFailureListener {
                    Toast.makeText(context, "Error al obtener ubicación", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Permiso de ubicación requerido", Toast.LENGTH_SHORT).show()
            }
        } else if (nuevoEstado == 2) {
            if (android.Manifest.permission.ACCESS_FINE_LOCATION.let { perm ->
                    androidx.core.content.ContextCompat.checkSelfPermission(context, perm)
                } == android.content.pm.PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(context, "Obteniendo ubicación...", Toast.LENGTH_SHORT).show()

                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        val coordenadas = "${location.latitude},${location.longitude}"

                        sharedPrefs.edit()
                            .putInt(pedido.id ?: "", nuevoEstado)
                            .putString("${pedido.id}_coords_salida", coordenadas)
                            .putLong("${pedido.id}_timestamp_salida", System.currentTimeMillis())
                            .apply()

                        estadoPedido.value = nuevoEstado
                        enviarCoordenadasAAPI(context, pedido.id ?: "", coordenadas, "salida")
                        Toast.makeText(context, "✓ Salida registrada", Toast.LENGTH_SHORT).show()
                    } else {
                        val locationRequest = com.google.android.gms.location.LocationRequest.Builder(
                            com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
                            10000
                        ).build()

                        val locationCallback = object : com.google.android.gms.location.LocationCallback() {
                            override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
                                locationResult.lastLocation?.let { newLocation ->
                                    val coordenadas = "${newLocation.latitude},${newLocation.longitude}"

                                    sharedPrefs.edit()
                                        .putInt(pedido.id ?: "", nuevoEstado)
                                        .putString("${pedido.id}_coords_salida", coordenadas)
                                        .putLong("${pedido.id}_timestamp_salida", System.currentTimeMillis())
                                        .apply()

                                    estadoPedido.value = nuevoEstado
                                    enviarCoordenadasAAPI(context, pedido.id ?: "", coordenadas, "salida")
                                    Toast.makeText(context, "✓ Salida registrada", Toast.LENGTH_SHORT).show()

                                    fusedLocationClient.removeLocationUpdates(this)
                                }
                            }
                        }

                        try {
                            fusedLocationClient.requestLocationUpdates(
                                locationRequest,
                                locationCallback,
                                android.os.Looper.getMainLooper()
                            )

                            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                fusedLocationClient.removeLocationUpdates(locationCallback)
                                if (estadoPedido.value != nuevoEstado) {
                                    Toast.makeText(context, "No se pudo obtener ubicación", Toast.LENGTH_LONG).show()
                                }
                            }, 15000)
                        } catch (e: SecurityException) {
                            Toast.makeText(context, "Error de permisos", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                estadoPedido.value = nuevoEstado
                sharedPrefs.edit().putInt(pedido.id ?: "", nuevoEstado).apply()
                Toast.makeText(context, "Salida marcada sin ubicación", Toast.LENGTH_SHORT).show()
            }
        }
    }

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
            // Encabezado: Nombre del cliente y estado
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        pedido.cliente ?: "Cliente desconocido",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    // Teléfono con icono a la izquierda
                    if (tieneTelefonoValido) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier
                                .clickable(onClick = onCall)
                                .padding(vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = "Teléfono",
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                pedido.telefono ?: "",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF4CAF50)
                            )
                        }
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
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Información de dirección y acceso
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                pedido.condominio?.let { condominio ->
                    if (condominio.isNotBlank()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Colores.Naranja.copy(alpha = 0.1f)
                            ) {
                                Text(
                                    "Sector",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Colores.Naranja,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                condominio,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black
                            )
                        }
                    }
                }

                pedido.direccion?.let { direccion ->
                    if (direccion.isNotBlank()) {
                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(14.dp).padding(top = 2.dp)
                            )
                            Text(
                                direccion,
                                fontSize = 11.sp,
                                color = Color.Gray,
                                maxLines = 2,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                pedido.informacionAcceso?.let { info ->
                    if (info.isNotBlank()) {
                        Text(
                            "ℹ️ $info",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(start = 2.dp)
                        )
                    }
                }
            }

            // Íconos de acción y botones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Íconos de acción rápida
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (tieneCoordenadasValidas) {
                        FilledIconButton(
                            onClick = onOpenMaps,
                            modifier = Modifier.size(40.dp),
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = Colores.Naranja.copy(alpha = 0.1f)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Abrir Maps",
                                tint = Colores.Naranja,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    FilledIconButton(
                        onClick = {
                            val uri = Uri.parse("content://temp")
                            cameraLauncher.launch(uri)
                        },
                        modifier = Modifier.size(40.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = Color(0xFF2196F3).copy(alpha = 0.1f)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Tomar foto",
                            tint = Color(0xFF2196F3),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Botón de ver detalles
                OutlinedButton(
                    onClick = onViewOrder,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Colores.Naranja
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Colores.Naranja),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("Ver detalles", fontSize = 12.sp)
                }
            }

            // Botón de estado (llegada/salida)
            Button(
                onClick = {
                    val nuevoEstado = when (estadoPedido.value) {
                        0 -> 1
                        1 -> 2
                        else -> 2
                    }
                    marcarEstadoConCoordenadas(nuevoEstado)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = when (estadoPedido.value) {
                        0 -> Colores.Naranja
                        1 -> Color(0xFF4CAF50)
                        else -> Color.LightGray
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                shape = RoundedCornerShape(8.dp),
                enabled = estadoPedido.value != 2,
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                Text(
                    when (estadoPedido.value) {
                        0 -> "Marcar llegada"
                        1 -> "Marcar salida"
                        else -> "Finalizado"
                    },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

fun enviarCoordenadasAAPI(
    context: Context,
    pedidoId: String,
    coordenadas: String,
    tipo: String
) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val token = com.limaa.proyectofinal.TokenManager.getToken(context)

            if (token == null) {
                android.util.Log.e("GPS_TRACKING", "No hay token disponible")
                return@launch
            }

            val api = com.limaa.proyectofinal.ApiClient.retrofit.create(com.limaa.proyectofinal.ApiService::class.java)

            val request = com.limaa.proyectofinal.RegistrarUbicacionRequest(
                accion = "coordenadas",
                token = token,
                pedido = pedidoId,
                coordenadas = coordenadas,
                tipo = tipo,
                timestamp = System.currentTimeMillis().toString()
            )

            android.util.Log.d("GPS_TRACKING", "Enviando coordenadas: $coordenadas para pedido $pedidoId ($tipo)")

            val response = api.registrarUbicacion(request)

            android.util.Log.d("GPS_TRACKING", "Response code: ${response.code()}")
            android.util.Log.d("GPS_TRACKING", "Response body: ${response.body()}")

            if (response.isSuccessful) {
                val body = response.body()
                if (body?.ok == true) {
                    android.util.Log.d("GPS_TRACKING", "Coordenadas registradas exitosamente")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "✓ Ubicación registrada en servidor", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    android.util.Log.e("GPS_TRACKING", "Error del servidor: ${body?.error}")
                }
            } else {
                android.util.Log.e("GPS_TRACKING", "HTTP Error: ${response.code()}")
            }

        } catch (e: com.google.gson.JsonSyntaxException) {
            android.util.Log.e("GPS_TRACKING", "JSON malformado del servidor", e)
        } catch (e: Exception) {
            android.util.Log.e("GPS_TRACKING", "Error: ${e.message}", e)
        }
    }
}
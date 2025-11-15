package com.limaa.proyectofinal.Utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import android.widget.Toast

@Composable
fun rememberLocationPermissionLauncher(
    onGranted: () -> Unit,
    onDenied: () -> Unit = {},
    onPermanentlyDenied: () -> Unit = {}
): () -> Unit {
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true -> {
                onGranted()
            }
            else -> {
                // Verifica si el usuario marcó "No volver a preguntar"
                val shouldShowRationale = permissions.entries.any { entry ->
                    androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale(
                        context as androidx.activity.ComponentActivity,
                        entry.key
                    )
                }

                if (shouldShowRationale) {
                    onDenied()
                } else {
                    onPermanentlyDenied()
                    Toast.makeText(
                        context,
                        "Ve a Ajustes > Permisos y activa Ubicación",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    return {
        launcher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
}

// Helper para verificar si ya tiene permiso
fun Context.hasLocationPermission(): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
}
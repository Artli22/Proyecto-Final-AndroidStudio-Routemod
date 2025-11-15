package com.limaa.proyectofinal.ui.Pantallas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.limaa.proyectofinal.R
import kotlin.math.cos
import kotlin.math.sin

// Creado por: Jorge Villeda
object ColoresDelivery {
    val azulClaro = Color(0xFFE8F4F8)
    val melocoton = Color(0xFFFFF5F0)
    val naranja = Color(0xFFFF9B6B)
}

@Composable
fun PantallaDelivery(
    onNavigateToLogin: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        ColoresDelivery.azulClaro,
                        ColoresDelivery.melocoton
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.TopStart)
                .offset(x = (-20).dp, y = (-20).dp)
        ) {
            val lineas = 15
            val pasoAngulo = 90f / lineas

            for (i in 0..lineas) {
                val angulo = i * pasoAngulo
                val radianes = Math.toRadians(angulo.toDouble())
                val longitud = size.width * 1.2f

                drawLine(
                    color = ColoresDelivery.naranja.copy(alpha = 0.3f),
                    start = Offset(0f, 0f),
                    end = Offset(
                        (longitud * cos(radianes)).toFloat(),
                        (longitud * sin(radianes)).toFloat()
                    ),
                    strokeWidth = 3.dp.toPx()
                )
            }
        }

        Box(
            modifier = Modifier
                .size(280.dp)
                .clip(RoundedCornerShape(140.dp))
                .background(ColoresDelivery.naranja)
                .clickable { onNavigateToLogin() }
        ) {
            Image(
                painter = painterResource(id = R.drawable.segundointentocamion1),
                contentDescription = "Logo de la app - Toca para continuar",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Canvas(
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 40.dp, y = 40.dp)
        ) {
            val lineas = 20
            val pasoAngulo = 90f / lineas
            val centroX = 0f
            val centroY = size.height

            for (i in 0..lineas) {
                val angulo = 180f + i * pasoAngulo
                val radianes = Math.toRadians(angulo.toDouble())
                val longitud = size.width * 1.5f

                drawLine(
                    color = ColoresDelivery.naranja.copy(alpha = 0.4f),
                    start = Offset(centroX, centroY),
                    end = Offset(
                        centroX + (longitud * cos(radianes)).toFloat(),
                        centroY + (longitud * sin(radianes)).toFloat()
                    ),
                    strokeWidth = 4.dp.toPx()
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Pantalla Inicio Delivery")
@Composable
fun PreviewPantallaDelivery() {
    PantallaDelivery()
}








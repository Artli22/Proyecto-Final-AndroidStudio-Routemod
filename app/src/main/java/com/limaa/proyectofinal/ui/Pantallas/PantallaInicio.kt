package com.limaa.proyectofinal.ui.Pantallas

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.limaa.proyectofinal.R
import kotlinx.coroutines.delay
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
    // Estados de animación
    var showTopLines by remember { mutableStateOf(false) }
    var showBottomLines by remember { mutableStateOf(false) }
    var logoScale by remember { mutableStateOf(0f) }
    var circlesVisible by remember { mutableStateOf(false) }

    // Animación infinita de rotación para círculos concéntricos
    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotate"
    )

    // Animación de pulsación del logo
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Secuencia de entrada
    LaunchedEffect(Unit) {
        delay(100)
        showTopLines = true
        delay(200)
        showBottomLines = true
        delay(300)
        circlesVisible = true

        animate(0f, 1f, animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )) { value, _ ->
            logoScale = value
        }
    }

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
        // Líneas superiores izquierdas con animación
        if (showTopLines) {
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

                    // Animación de aparición gradual
                    val alpha = (i.toFloat() / lineas) * 0.3f

                    drawLine(
                        color = ColoresDelivery.naranja.copy(alpha = alpha),
                        start = Offset(0f, 0f),
                        end = Offset(
                            (longitud * cos(radianes)).toFloat(),
                            (longitud * sin(radianes)).toFloat()
                        ),
                        strokeWidth = 3.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
            }
        }

        // Círculos concéntricos giratorios detrás del logo
        if (circlesVisible) {
            Canvas(
                modifier = Modifier
                    .size(350.dp)
                    .rotate(rotation)
            ) {
                val numCircles = 8
                val maxRadius = size.minDimension / 2

                for (i in 1..numCircles) {
                    val radius = (maxRadius / numCircles) * i
                    val alpha = 0.15f - (i * 0.015f)

                    // Círculos completos
                    drawCircle(
                        color = ColoresDelivery.naranja.copy(alpha = alpha),
                        radius = radius,
                        style = Stroke(width = 2.dp.toPx())
                    )

                    // Segmentos de círculos para efecto de movimiento
                    if (i % 2 == 0) {
                        drawArc(
                            color = ColoresDelivery.naranja.copy(alpha = alpha * 2),
                            startAngle = 0f,
                            sweepAngle = 120f,
                            useCenter = false,
                            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                }
            }
        }

        // Logo central con animación de entrada y pulsación
        Box(
            modifier = Modifier
                .size(280.dp)
                .scale(logoScale * pulseScale)
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

        // Líneas inferiores derechas con animación
        if (showBottomLines) {
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

                    // Animación de aparición gradual inversa
                    val alpha = ((lineas - i).toFloat() / lineas) * 0.4f

                    drawLine(
                        color = ColoresDelivery.naranja.copy(alpha = alpha),
                        start = Offset(centroX, centroY),
                        end = Offset(
                            centroX + (longitud * cos(radianes)).toFloat(),
                            centroY + (longitud * sin(radianes)).toFloat()
                        ),
                        strokeWidth = 4.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
            }
        }

        // Círculos decorativos adicionales en las esquinas
        if (circlesVisible) {
            // Esquina superior derecha
            Canvas(
                modifier = Modifier
                    .size(80.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 20.dp, y = (-10).dp)
                    .rotate(-rotation * 0.5f)
            ) {
                for (i in 1..3) {
                    drawCircle(
                        color = ColoresDelivery.naranja.copy(alpha = 0.1f),
                        radius = (size.minDimension / 3) * i,
                        style = Stroke(width = 2.dp.toPx())
                    )
                }
            }

            // Esquina inferior izquierda
            Canvas(
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.BottomStart)
                    .offset(x = (-30).dp, y = 30.dp)
                    .rotate(rotation * 0.7f)
            ) {
                for (i in 1..4) {
                    drawCircle(
                        color = ColoresDelivery.naranja.copy(alpha = 0.08f),
                        radius = (size.minDimension / 4) * i,
                        style = Stroke(width = 1.5.dp.toPx())
                    )
                }
            }
        }
    }
}
package com.limaa.proyectofinal.ui.Pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    onBackClick: () -> Unit = {},
    onSendCodeClick: (String) -> Unit = {} // Ya no se usa pero lo dejo por compatibilidad
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Olvidé mi contraseña",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LoginColors.Orange,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icono grande de información
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Información",
                modifier = Modifier.size(120.dp),
                tint = LoginColors.Orange
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Mensaje principal
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = LoginColors.Orange.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Si olvidaste tu contraseña",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = LoginColors.DarkBackground,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "Comunícate con el administrador de tu empresa",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = LoginColors.Orange,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Divider(
                        color = LoginColors.Orange.copy(alpha = 0.3f),
                        thickness = 1.dp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "El administrador podrá restablecer tu contraseña de forma segura.",
                        fontSize = 14.sp,
                        color = LoginColors.LightGray,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Botón para volver
            Button(
                onClick = onBackClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LoginColors.Orange
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "VOLVER AL LOGIN",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
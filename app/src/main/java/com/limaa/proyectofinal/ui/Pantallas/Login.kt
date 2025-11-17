package com.limaa.proyectofinal.ui.Pantallas

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.limaa.proyectofinal.LoginViewModel
import kotlinx.coroutines.launch
import androidx.compose.runtime.livedata.observeAsState
import com.limaa.proyectofinal.TokenManager
import android.util.Log
import com.limaa.proyectofinal.RutaViewModel
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.material.icons.Icons
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.layout.fillMaxWidth


object LoginColors {
    val Orange = Color(0xFFFF7A3D)
    val DarkBackground = Color(0xFF1A1A2E)
    val LightGray = Color(0xFF9E9E9E)
    val TextFieldBackground = Color(0xFFF5F5F5)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: (String) -> Unit = {},
    onForgotPasswordClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {},
    loginViewModel: LoginViewModel = viewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }


    val loginState by loginViewModel.loginState.observeAsState()

    LaunchedEffect(loginState) {
        loginState?.let { result ->
            isLoading = false
            result.onSuccess { response ->
                if (response.error != null) {
                    Toast.makeText(
                        context,
                        response.error,
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (response.ok == true) {
                    // GUARDAR TOKEN Y USUARIO
                    TokenManager.saveToken(
                        context,
                        response.token ?: "",
                        response.usuario ?: ""
                    )

                    Toast.makeText(
                        context,
                        response.mensaje ?: "Login correcto",
                        Toast.LENGTH_SHORT
                    ).show()

                    //Llamar a obtener ruta
                    Log.d("LoginScreen", "Iniciando prueba de obtener ruta...")
                    val rutaViewModel = RutaViewModel()
                    rutaViewModel.obtenerRutaDelDia(context)
                    Log.d("LoginScreen", "Llamada a obtenerRutaDelDia ejecutada")


                    onLoginSuccess(response.token ?: "")
                } else {
                    Toast.makeText(
                        context,
                        "Error desconocido",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }.onFailure { error ->
                Toast.makeText(
                    context,
                    error.message ?: "Error en login",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
// UI de login con validación, loading y navegación
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            colors = CardDefaults.cardColors(
                containerColor = LoginColors.DarkBackground
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Log In",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Por favor, inicia sesión en tu cuenta existente",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            "USUARIO",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    "Ejemplo funcional: C1",
                    color = LoginColors.LightGray,
                    fontSize = 14.sp
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = LoginColors.TextFieldBackground,
                focusedContainerColor = LoginColors.TextFieldBackground,
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = LoginColors.Orange
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            "CONTRASEÑA",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    "Contraseña funcional: Slv2025",
                    color = LoginColors.LightGray,
                    fontSize = 14.sp
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = LoginColors.TextFieldBackground,
                focusedContainerColor = LoginColors.TextFieldBackground,
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = LoginColors.Orange
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            visualTransformation = if (passwordVisible)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible)
                            Icons.Default.Visibility
                        else
                            Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible)
                            "Ocultar contraseña"
                        else
                            "Mostrar contraseña",
                        tint = LoginColors.LightGray
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = rememberMe,
                    onCheckedChange = { rememberMe = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = LoginColors.Orange,
                        uncheckedColor = LoginColors.LightGray
                    )
                )
                Text(
                    "Recuérdame",
                    fontSize = 12.sp,
                    color = Color.Black
                )
            }

            TextButton(onClick = onForgotPasswordClick) {
                Text(
                    "Olvidé mi contraseña",
                    color = LoginColors.Orange,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                coroutineScope.launch {
                    isLoading = true
                    loginViewModel.login(email.trim(), password.trim())
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = LoginColors.Orange
            ),
            shape = RoundedCornerShape(12.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 3.dp
                )
            } else {
                Text(
                    "INICIAR SESIÓN",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Si aún no tienes credenciales de usuario, solicítalas a tu administrador",
                fontSize = 12.sp,
                color = LoginColors.LightGray,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview(showBackground = true, name = "Login Screen")
@Composable
fun PreviewLoginScreen() {
    MaterialTheme {
        LoginScreen()
    }
}

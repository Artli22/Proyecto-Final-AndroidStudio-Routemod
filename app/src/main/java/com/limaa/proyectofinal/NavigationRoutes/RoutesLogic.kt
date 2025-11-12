package com.limaa.proyectofinal.NavigationRoutes

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.limaa.proyectofinal.ui.Pantallas.EditProfileScreen
import com.limaa.proyectofinal.ui.Pantallas.ForgotPasswordScreen
import com.limaa.proyectofinal.ui.Pantallas.LocationAccessScreen
import com.limaa.proyectofinal.ui.Pantallas.LoginScreen
import com.limaa.proyectofinal.ui.Pantallas.PantallaInventarioVehiculo
import com.limaa.proyectofinal.ui.Pantallas.RegistrarseScreen
import com.limaa.proyectofinal.ui.Pantallas.RouteModScreen
import com.limaa.proyectofinal.ui.Pantallas.RoutaPantalla
import com.limaa.proyectofinal.ui.pantallas.PantallaDelivery

@Composable
fun AppNavigation(
    navController: NavHostController,
    innerPadding: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = PantallaInicio,
        modifier = Modifier.padding(innerPadding)
    ) {
        composable<PantallaInicio> {
            PantallaDelivery(
                onNavigateToLogin = {
                    navController.navigate(Login) {
                        popUpTo(PantallaInicio) { inclusive = true }
                    }
                }
            )
        }

        composable<Login> {
            LoginScreen(
                onLoginSuccess = { token ->
                    navController.navigate(LocationAccess)
                },
                onForgotPasswordClick = {
                    navController.navigate(OlvideContrasena)
                },
                onRegisterClick = {
                    navController.navigate(SignUp)
                }
            )
        }

        composable<OlvideContrasena> {
            ForgotPasswordScreen(
                onSendCodeClick = { _ ->
                    navController.popBackStack()
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable<SignUp> {
            RegistrarseScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onRegisterClick = {
                    navController.popBackStack()
                }
            )
        }

        composable<LocationAccess> {
            LocationAccessScreen(
                onAccederUbicacion = {
                    navController.navigate(Home) {
                        popUpTo(Login) { inclusive = false }
                    }
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable<Home> {
            RouteModScreen(
                onVerMasInventario = {
                    navController.navigate(Inventario)
                },
                onVerMasRuta = {
                    navController.navigate(RutaDelDia)
                },
                onPerfilClick = {
                    navController.navigate(EditProfile)
                },
                onCerrarSesion = {
                    navController.navigate(Login) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable<Inventario> {
            PantallaInventarioVehiculo(
                alVolver = {
                    navController.popBackStack()
                },
                alConfirmar = {
                    navController.popBackStack()
                },
                alDetalle = {
                }
            )
        }

        composable<RutaDelDia> {
            RoutaPantalla(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onViewOrder = { _ ->
                }
            )
        }

        composable<EditProfile> {
            EditProfileScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onSaveClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}


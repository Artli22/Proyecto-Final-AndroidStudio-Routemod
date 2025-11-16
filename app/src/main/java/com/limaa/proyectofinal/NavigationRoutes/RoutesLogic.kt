package com.limaa.proyectofinal.NavigationRoutes

import com.limaa.proyectofinal.ui.Pantallas.PantallaDetallePedido
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.limaa.proyectofinal.Utils.hasLocationPermission
import com.limaa.proyectofinal.ui.Pantallas.EditProfileScreen
import com.limaa.proyectofinal.ui.Pantallas.ForgotPasswordScreen
import com.limaa.proyectofinal.ui.Pantallas.LocationAccessScreen
import com.limaa.proyectofinal.ui.Pantallas.LoginScreen
import com.limaa.proyectofinal.ui.Pantallas.PantallaInventarioVehiculo
import com.limaa.proyectofinal.ui.Pantallas.RegistrarseScreen
import com.limaa.proyectofinal.ui.Pantallas.RouteModScreen
import com.limaa.proyectofinal.ui.Pantallas.RoutaPantalla
import com.limaa.proyectofinal.ui.Pantallas.PantallaDelivery
import androidx.navigation.toRoute
import androidx.lifecycle.viewmodel.compose.viewModel
import com.limaa.proyectofinal.RutaViewModel

@Composable
fun AppNavigation(
    navController: NavHostController,
    innerPadding: PaddingValues
) {
    val rutaViewModel: RutaViewModel = viewModel()

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
            val context = LocalContext.current

            LaunchedEffect(Unit) {
                if (context.hasLocationPermission()) {
                    navController.navigate(Home) {
                        popUpTo(LocationAccess) { inclusive = true }
                    }
                }
            }

            LocationAccessScreen(
                onPermissionGranted = {
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
                },
                viewModel = rutaViewModel
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
                onViewOrder = { pedidoId ->
                    navController.navigate(DetallePedido(pedidoId = pedidoId))
                },
                viewModel = rutaViewModel
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

        composable<DetallePedido> { backStackEntry ->
            val route = backStackEntry.toRoute<DetallePedido>()
            PantallaDetallePedido(
                pedidoId = route.pedidoId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}


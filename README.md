# Proyecto-Final-AndroidStudio-Routemod
Proyecto final para la clase de Plataformas Moviles 2025.

RouteMod es una aplicación móvil diseñada para optimizar la gestión de rutas de entrega y el control de inventario para conductores y repartidores. La aplicación permite a los usuarios gestionar sus entregas diarias, mantener un control preciso del inventario de sus vehículos, y acceder a información detallada sobre cada parada en su ruta.
La app facilita el proceso de entrega mediante una interfaz intuitiva que permite a los conductores:

Visualizar su ruta diaria con todas las paradas programadas
Gestionar el inventario del vehículo antes de iniciar las entregas
Marcar llegadas y salidas en cada punto de entrega
Acceder a la información de los clientes y sus pedidos
Mantener un perfil actualizado con su información y tipo de transporte

Servicios
Base de Datos y Datos

Base de Datos Local (Room): Se implementará una base de datos local utilizando Room para almacenar:

Información de usuarios (conductores) - datos de perfil
Inventario de productos del vehículo
Rutas diarias y paradas programadas
Historial de entregas completadas
Configuraciones de la aplicación

Esta base de datos permitirá que la aplicación funcione completamente offline una vez que los datos hayan sido cargados inicialmente.
API/Servicio de Datos (A Desarrollar): 

Autenticación

Sistema de Autenticación Local: Implementación propia de un sistema de login que:

Valida credenciales almacenadas localmente en Room
Gestiona sesiones de usuario
Maneja recuperación de contraseña mediante códigos almacenados localmente
No requiere conexión a internet una vez configurada la cuenta



Ubicación (Futuro)

Google Maps/Servicios de Ubicación: En una futura iteración se planea integrar:

Solicitud de permisos de ubicación (ya implementada la pantalla LocationAccess)
Visualización de rutas en mapa
Sin embargo, actualmente esta funcionalidad solo muestra la UI de solicitud de permisos



Librerías
Navegación y UI

Jetpack Compose Navigation (androidx.navigation:navigation-compose):

Manejo de la navegación entre las 9 pantallas de la aplicación
Implementa navegación type-safe usando objetos serializables
Gestiona el flujo de navegación desde inicio de sesión hasta las pantallas principales


Kotlinx Serialization (org.jetbrains.kotlinx:kotlinx-serialization-json):

Serialización de rutas de navegación
Permite pasar objetos complejos entre pantallas de forma segura


Material3 Components:

Proporciona componentes de UI modernos (Scaffold, TopAppBar, Cards, Buttons, etc.)
Usado extensivamente en todas las pantallas para mantener consistencia visual
Sistema de temas y colores



Base de Datos Local

Room (androidx.room:room-runtime y androidx.room:room-ktx):

Almacenará todos los datos de la aplicación (usuarios, productos, rutas, entregas)
Proporciona seguridad de tipos y validación en tiempo de compilación
Integración con Coroutines para operaciones asíncronas 



Componentes de Android

AndroidX Core KTX: Extensiones de Kotlin para APIs de Android, mejorando la legibilidad del código
Activity Compose: Integración de Jetpack Compose con Activities

Utilidades de UI

Material Icons: Conjunto de iconos utilizados en toda la aplicación (ArrowBack, Person, LocationOn, Edit, Visibility, etc.)


# Proyecto-Final-AndroidStudio-Routemod

Proyecto final para la clase de Plataformas Móviles 2025.

**RouteMod** es una aplicación móvil diseñada para optimizar la gestión de rutas de entrega y el control de inventario para conductores y repartidores. La aplicación permite a los usuarios gestionar sus entregas diarias, mantener un control preciso del inventario de sus vehículos, y acceder a información detallada sobre cada parada en su ruta.

La app facilita el proceso de entrega mediante una interfaz intuitiva que permite a los conductores:
- Visualizar su ruta diaria con todas las paradas programadas
- Gestionar el inventario del vehículo antes de iniciar las entregas
- Marcar llegadas y salidas en cada punto de entrega con envío de coordenadas GPS
- Acceder a la información de los clientes y sus pedidos
- Mantener un perfil actualizado con su información y tipo de transporte

## Servicios

### 1. **API Externa (apilot.php)**
- **URL Base**: `https://www.solucionesviables.com/desarrollo/apilot.php`
- **Tecnología**: Retrofit + Gson
- **Autenticación**: Bearer Token (JWT) enviado en header `Authorization`
- **Endpoints utilizados**:
  - `POST /apilot.php` → `accion: login`  
    → Autenticación de usuario. Retorna `token` y `usuario`.
  - `POST /apilot.php` → `accion: ruta`  
    → Obtiene la ruta del día con pedidos y carga de inventario.
  - `POST /apilot.php` → `accion: pedido`  
    → Obtiene el detalle completo de un pedido (artículos, cantidades, extras).
  - `POST /apilot.php` → `accion: coordenadas`  
    → Registra coordenadas GPS al marcar "llegada" o "salida" en una entrega.

> **Nota**: Todas las llamadas requieren conexión a internet y token válido.

### 2. **Base de Datos Local (Room)**
- **Uso**: Almacenamiento offline de:
  - Datos del usuario (perfil, credenciales encriptadas si aplica)
  - Inventario del vehículo (estado de check-in/check-out)
  - Historial de entregas completadas
  - Configuraciones locales (preferencias, estados de UI)
- **Integración**: Operaciones asíncronas con Coroutines
- **Persistencia**: Permite funcionamiento parcial sin internet tras la primera carga

### 3. **SharedPreferences (TokenManager)**
- **Uso**: Persistencia segura del token JWT y nombre de usuario
- **Métodos clave**:
  - `saveToken()`, `getToken()`, `clearToken()`, `isLoggedIn()`
- **Seguridad**: Almacenado en modo privado, borrado al cerrar sesión

---

## Librerías

| Librería | Uso |
|--------|-----|
| **Retrofit** | Cliente HTTP para consumir la API externa (`apilot.php`). Maneja JSON, headers, timeouts y logging. |
| **Gson** | Serialización/deserialización automática de respuestas JSON (`LoginResponse`, `RutaResponse`, etc.). |
| **OkHttp** | Cliente subyacente de Retrofit. Incluye interceptor de logging y autenticación automática con token. |
| **Jetpack Compose Navigation** | Navegación type-safe entre pantallas usando rutas serializables (`@Serializable`). |
| **Kotlinx Serialization** | Serialización segura de parámetros de navegación (ej: `DetallePedido(pedidoId)`). |
| **Material3 Components** | UI moderna: `Scaffold`, `TopAppBar`, `Card`, `Button`, `Checkbox`, `CircularProgressIndicator`, etc. |
| **Room** | Base de datos local (planificada para persistencia offline). |
| **AndroidX Core KTX** | Extensiones de Kotlin para Android (mejora legibilidad). |
| **Activity Compose** | Integración de Compose con `ComponentActivity`. |
| **Material Icons** | Iconos vectoriales (`ArrowBack`, `LocationOn`, `Visibility`, etc.). |
| **ViewModel + LiveData** | Gestión de estado reactivo en pantallas (`LoginViewModel`, `RutaViewModel`, etc.). |
| **Coroutines** | Operaciones asíncronas (llamadas API, animaciones, GPS). |

---

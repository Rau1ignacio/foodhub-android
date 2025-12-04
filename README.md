# 🛒 FoodHub – Marketplace de Alimentos Móvil

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.22-purple.svg)](https://kotlinlang.org/)
[![Android](https://img.shields.io/badge/Android-7.0%2B-green.svg)](https://developer.android.com/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-2024.05.00-blue.svg)](https://developer.android.com/jetpack/compose)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

Aplicación móvil Android para la gestión de pedidos en minimarkets, diseñada para conectar pequeños comercios y emprendedores con clientes que buscan productos frescos y convenientes. Sistema completo con aplicación móvil (Kotlin + Jetpack Compose) y microservicio backend (Spring Boot 3 + Kotlin).

---

## 📋 Tabla de Contenidos

- [Características Principales](#-características-principales)
- [Tecnologías y Arquitectura](#-tecnologías-y-arquitectura)
- [Capturas de Pantalla](#-capturas-de-pantalla)
- [Requisitos del Sistema](#-requisitos-del-sistema)
- [Instalación y Configuración](#-instalación-y-configuración)
- [Arquitectura del Proyecto](#-arquitectura-del-proyecto)
- [API Endpoints](#-api-endpoints)
- [Integrantes del Equipo](#-integrantes-del-equipo)
- [Aprendizajes Clave](#-aprendizajes-clave)
- [Licencia](#-licencia)

---

## ✨ Características Principales

### 📱 Módulo Cliente

- **Autenticación Segura**
  - Registro de nuevos usuarios con validación de campos
  - Inicio de sesión con email y contraseña
  - Gestión de sesiones con roles diferenciados (CLIENT/ADMIN)

- **Catálogo de Productos**
  - Visualización intuitiva de productos disponibles
  - Búsqueda en tiempo real por nombre o descripción
  - Filtrado dinámico por categorías (Frutas, Verduras, Lácteos, Bebidas, Otros)
  - Indicadores visuales de stock y disponibilidad

- **Carrito de Compras**
  - Agregar productos con control de cantidad
  - Modificación de cantidades con validación de stock
  - Cálculo automático de totales
  - Interfaz de confirmación de pedido

- **Historial de Pedidos**
  - Vista completa de pedidos anteriores
  - Detalle de productos por orden
  - Información de fecha, total y estado

### 🔧 Módulo Administrador

- **Gestión de Productos (CRUD Completo)**
  - Crear nuevos productos con todos los detalles
  - Editar información de productos existentes
  - Eliminar productos del catálogo
  - Control de stock e inventario
  - Subir imágenes desde galería o cámara
  - Asignación de categorías

### 🎨 Experiencia de Usuario

- Diseño moderno con Material Design 3
- Navegación fluida entre pantallas
- Validación de formularios en tiempo real
- Mensajes de error descriptivos
- Indicadores de carga y estados

---

## 💻 Tecnologías y Arquitectura

### Frontend (Android)

- **Lenguaje:** Kotlin 1.9.22
- **UI Framework:** Jetpack Compose
- **Mínimo SDK:** 24 (Android 7.0)
- **Target SDK:** 34 (Android 14)

### Arquitectura MVVM

```
📦 com.example.foodhub
├── 📂 core
│   ├── nav/          # Navegación y rutas
│   └── utils/        # Validadores y utilidades
├── 📂 data
│   ├── local/        # Room Database (DAOs, Entities)
│   ├── remote/       # Retrofit API Client
│   └── repository/   # Capa de datos unificada
├── 📂 domain
│   └── models/       # Modelos de dominio
└── 📂 ui
    ├── auth/         # Pantallas de autenticación
    ├── home/         # Catálogo de productos
    ├── cart/         # Carrito de compras
    ├── detail/       # Detalle de producto
    ├── history/      # Historial de pedidos
    ├── admin/        # Panel de administración
    └── viewmodels/   # ViewModels (MVVM)
```

### Principales Dependencias

| Librería | Versión | Propósito |
|----------|---------|-----------|
| Jetpack Compose | 2024.05.00 | UI declarativa |
| Navigation Compose | 2.7.7 | Navegación entre pantallas |
| Room | 2.6.1 | Base de datos local |
| Retrofit | 2.9.0 | Cliente HTTP |
| Coil | 2.6.0 | Carga de imágenes |
| ViewModel | 2.8.0 | Gestión de estado UI |
| Material Icons Extended | - | Iconos de Material Design |

### Backend

- **Framework:** Spring Boot 3
- **Lenguaje:** Kotlin
- **Base de Datos:** Relacional (configurada en el microservicio)
- **Arquitectura:** RESTful API

---

## 📸 Capturas de Pantalla

*(Agrega aquí capturas de pantalla de tu aplicación: Login, Home, Carrito, Admin, etc.)*

---

## 🔧 Requisitos del Sistema

### Para Desarrolladores

- **Android Studio:** Hedgehog o superior (recomendado)
- **JDK:** 17
- **Gradle:** 8.7
- **Dispositivo/Emulador:** Android 7.0 (API 24) o superior
- **Backend:** Microservicio Spring Boot corriendo localmente o en servidor

### Para Usuarios Finales

- Dispositivo Android 7.0 o superior
- Conexión a Internet (para sincronización con backend)
- Permisos: Cámara, Almacenamiento, Internet

---

## 🚀 Instalación y Configuración

### 1. Clonar el Repositorio

```bash
git clone https://github.com/tu-usuario/foodhub-android.git
cd foodhub-android
```

### 2. Configurar el Backend

Antes de ejecutar la app, asegúrate de que el microservicio backend esté corriendo.

**Opción A: Backend Local (Emulador)**

En `app/src/main/java/com/example/foodhub/data/remote/RetrofitClient.kt`:

```kotlin
private const val BASE_URL = "http://10.0.2.2:8080/"
```

**Opción B: Backend en Servidor AWS**

```kotlin
private const val BASE_URL = "http://TU_IP_AWS:8080/"
```

### 3. Abrir el Proyecto

1. Abre Android Studio
2. Selecciona "Open an Existing Project"
3. Navega a la carpeta `foodhub-android`
4. Espera a que Gradle sincronice las dependencias

### 4. Ejecutar la Aplicación

1. Conecta un dispositivo Android o inicia un emulador
2. Presiona el botón "Run" (▶️) en Android Studio
3. Selecciona tu dispositivo de destino

### 5. Usuarios de Prueba

Puedes registrar nuevos usuarios desde la app o usar estos de prueba (si están configurados en tu backend):

```
Cliente:
Email: cliente@test.com
Password: 123456

Administrador:
Email: admin@test.com
Password: 123456
```

---

## 🏗️ Arquitectura del Proyecto

### Patrón MVVM

La aplicación implementa el patrón Model-View-ViewModel para separar la lógica de negocio de la UI:

- **Model:** Entidades de Room y DTOs de red
- **View:** Composables de Jetpack Compose
- **ViewModel:** Gestión de estado y lógica de UI

### Flujo de Datos

```
UI (Composable) → ViewModel → Repository → [Room DB | Retrofit API]
                     ↓
                StateFlow
                     ↓
              UI se actualiza
```

### Gestión de Estado

- **StateFlow:** Para observar cambios reactivos
- **MutableStateFlow:** Para actualizar estado desde ViewModels
- **LaunchedEffect:** Para operaciones asíncronas en Composables

### Navegación

Sistema de navegación basado en `NavController` con rutas definidas:

```kotlin
sealed class Route(val route: String) {
    object Home : Route("home")
    object Cart : Route("cart")
    object Detail : Route("detail/{id}")
    object Admin : Route("admin_list")
    // ...
}
```

---

## 🌐 API Endpoints

### Autenticación

| Método | Endpoint | Descripción | Body |
|--------|----------|-------------|------|
| POST | `/api/auth/register` | Registrar nuevo usuario | `User` |
| POST | `/api/auth/login` | Iniciar sesión | `LoginRequestDto` |

### Productos

| Método | Endpoint | Descripción | Permisos |
|--------|----------|-------------|----------|
| GET | `/api/products` | Listar todos los productos | Público |
| GET | `/api/products/{id}` | Obtener producto específico | Público |
| POST | `/api/products` | Crear producto | ADMIN |
| PUT | `/api/products/{id}` | Actualizar producto | ADMIN |
| DELETE | `/api/products/{id}` | Eliminar producto | ADMIN |

### Carrito

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/cart/{userId}` | Obtener carrito del usuario |
| POST | `/api/cart/add` | Agregar producto al carrito |
| PUT | `/api/cart/{itemId}` | Actualizar cantidad |
| DELETE | `/api/cart/{itemId}` | Eliminar ítem del carrito |

### Pedidos

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/orders` | Crear nuevo pedido |
| GET | `/api/orders/user/{userId}` | Obtener pedidos del usuario |

---

## 👥 Integrantes del Equipo

| Nombre | GitHub | Rol |
|--------|--------|-----|
| Martin Mora | [@MartinMDevv](https://github.com/MartinMDevv) | Desarrollador Android / Líder Técnico |
| Raúl Ignacio | [@Rau1ignacio](https://github.com/Rau1ignacio) | Desarrollador Backend / DevOps |

---

## 🎓 Aprendizajes Clave

### Jetpack Compose
- Desarrollo de UI declarativa moderna
- Gestión de estado con `remember` y `mutableStateOf`
- Composables reutilizables y navegación

### Arquitectura MVVM
- Separación clara de responsabilidades
- ViewModels para lógica de UI
- Flujos reactivos con StateFlow

### Gestión de Datos
- **Room:** Persistencia local robusta con DAOs y Entities
- **Retrofit:** Consumo eficiente de API REST
- Sincronización entre datos locales y remotos

### Patrones de Diseño
- **Singleton:** Para instancias únicas (RetrofitClient)
- **Repository Pattern:** Abstracción de fuentes de datos
- **Factory Pattern:** Creación de ViewModels con dependencias

### Funcionalidades Android
- Permisos de cámara y almacenamiento
- FileProvider para compartir archivos
- Navegación multi-pantalla compleja

### Validación de Datos
- Validadores reutilizables para formularios
- Manejo de errores en tiempo real
- Feedback visual al usuario

---

## 📝 Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo [LICENSE](LICENSE) para más detalles.

```
MIT License

Copyright (c) 2025 FoodHub Team

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

## 🤝 Contribuciones

Las contribuciones son bienvenidas. Para cambios importantes:

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

---

## 🙏 Agradecimientos

- Material Design Guidelines por el sistema de diseño
- JetBrains por Kotlin
- Google por Android y Jetpack Compose
- Comunidad de desarrolladores Android por las mejores prácticas

---

**Hecho con ❤️ por el equipo FoodHub**

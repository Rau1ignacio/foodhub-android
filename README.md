# FoodHub – App móvil + Microservicio backend

## 1. Nombre del proyecto

**FoodHub** – Aplicación móvil para la gestión de pedidos en minimarkets, con backend en **Spring Boot (Kotlin)** y frontend en **Android (Kotlin + Jetpack Compose)**{index=1}  

---

## 2. Integrantes

- Nombre 1 – Rol (por ejemplo: Desarrollador Android / Líder Técnico)
- Nombre 2 – Rol (por ejemplo: Desarrollador Backend)
- Nombre 3 – Rol (por ejemplo: QA / Soporte DevOps)

> **Recuerda:** aquí reemplaza con los nombres reales de tu equipo.

---

## 3. Funcionalidades principales

La solución completa (app + microservicio) implementa las siguientes funcionalidades:

### App móvil (Android – Kotlin + Jetpack Compose)

- **Autenticación de usuarios**
  - Registro de usuario nuevo (`RegisterScreen.kt`)
  - Login de usuarios existentes (`LoginScreen.kt`) 
- **Módulo cliente**
  - Visualización de catálogo de productos (`HomeScreen.kt`)
  - Vista de detalle de un producto (`DetailScreen.kt`)
  - Agregar productos al carrito (`CartScreen.kt`)
  - Ver resumen del pedido antes de confirmar (`OrderSummaryScreen.kt`)
  - Historial de pedidos realizados (`OrderHistoryScreen.kt`) 
- **Módulo administrador**
  - Listado de productos administrables (`AdminListsScreen.kt`)
  - Creación / edición de productos (`AdminProductFormScreen.kt`) 
- **Arquitectura**
  - Arquitectura **MVVM** con ViewModels dedicados: `AuthVM`, `HomeVM`, `DetailVM`, `CartVM`, `OrderHistoryVM`, `AdminVM`, `SessionVM`. :contentReference[oaicite:5]{index=5}  
  - Capa de datos con **Room** (`AppDatabase`, DAOs y Entities) y consumo de API vía **Retrofit** (`FoodApi.kt`, `RetrofitClient`).

### Backend (Microservicio – Spring Boot 3 + Kotlin)

- **Gestión de usuarios (Auth)**
  - Registro y login básico de usuarios.
  - CRUD completo de usuarios con roles (`CLIENT` / `ADMIN`). 
- **Gestión de productos**
  - CRUD completo de productos: crear, listar, actualizar y eliminar.
  - Modelo `Product` con campos: `name`, `description`, `price`, `imageUrl`, `category`, `stock`, `available`. 
- **Carrito de compras**
  - Agregar productos al carrito.
  - Listar carrito por usuario.
  - Actualizar cantidad de un ítem.
  - Eliminar ítems y limpiar carrito al generar una orden. 
- **Órdenes de compra**
  - Generación de una orden a partir del carrito del usuario.
  - Validación de stock, descuento de inventario y cálculo del total.
  - Consulta de órdenes por usuario.

---

## 4. Endpoints utilizados (microservicio y API externa)

### 4.1. Endpoints del microservicio backend (Spring Boot)

#### Auth – `/api/auth` 

- `POST /api/auth/register`  
  Registra un nuevo usuario.

- `POST /api/auth/login`  
  Inicia sesión con `email` y `password`.

- `GET /api/auth`  
  Obtiene todos los usuarios.

- `GET /api/auth/{id}`  
  Obtiene un usuario por su `id`.

- `PUT /api/auth/{id}`  
  Actualiza los datos de un usuario.

- `DELETE /api/auth/{id}`  
  Elimina un usuario.

---

#### Productos – `/api/products` 

- `GET /api/products`  
  Lista todos los productos.

- `GET /api/products/{id}`  
  Obtiene un producto específico por `id`.

- `POST /api/products`  
  Crea un nuevo producto.

- `PUT /api/products/{id}`  
  Actualiza un producto existente.

- `DELETE /api/products/{id}`  
  Elimina un producto por `id`.

---

#### Carrito – `/api/cart` 

- `GET /api/cart/{userId}`  
  Obtiene el carrito del usuario con `userId`.

- `POST /api/cart/add`  
  Agrega un producto al carrito.  
  **Body (JSON)**:
  ```json
  {
    "userId": 1,
    "productId": 10,
    "quantity": 2
  }

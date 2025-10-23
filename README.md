# Food Hub 🛒🥑

**Food Hub** es una aplicación móvil nativa para Android, desarrollada como un marketplace de alimentos locales. Conecta a pequeños comercios y emprendedores con clientes que buscan productos frescos, permitiendo una gestión de inventario local y un proceso de compra simplificado.

Este proyecto fue desarrollado utilizando **Jetpack Compose** (UI declarativa moderna) y una arquitectura limpia **MVVM + Clean Architecture**, enfocada en la escalabilidad, mantenibilidad y testabilidad del código.

## 🌟 Funcionalidades Clave

La aplicación se divide en dos roles principales con flujos de usuario diferenciados:

### 👤 Flujo de Cliente
* **Autenticación:** Sistema completo de Login y Registro de usuarios.
* **Catálogo de Productos:** Pantalla principal (`HomeScreen`) donde se listan todos los productos disponibles.
* **Detalle de Producto:** Vista detallada (`DetailScreen`) con información completa y botón para añadir al carrito.
* **Carrito de Compras:** Gestión del carrito (`CartScreen`) para modificar cantidades, eliminar productos y confirmar el pedido.
* **Resumen de Pedido:** Pantalla de confirmación (`OrderSummaryScreen`) al finalizar una compra.

### 👨‍💼 Flujo de Administrador
* **Gestión de Inventario:** Pantalla de administración (`AdminListScreen`) para ver, editar o eliminar productos existentes.
* **Formulario CRUD:** Un formulario único (`AdminProductFormScreen`) para **Crear** y **Actualizar** productos.
* **Validación de Formularios:** El formulario implementa lógica de validación robusta (en `domain/models/ProductForm.kt`) que muestra errores en tiempo real antes de permitir guardar.
* **Uso de Recursos Nativos:** El administrador puede asignar una imagen al producto usando:
    * La **Cámara** del dispositivo.
    * La **Galería** de fotos.

---

## 🏗️ Arquitectura y Stack Tecnológico

El proyecto sigue los principios de **Clean Architecture** y **MVVM (Model-View-ViewModel)**, separando las responsabilidades en capas bien definidas.

* **`ui` (Capa de Presentación):** Contiene los **Composables** (Pantallas), **ViewModels** (gestionan el estado y la lógica de UI) y **Navegación** (`AppNav.kt`).
    * **Jetpack Compose:** UI 100% nativa y declarativa.
    * **Material 3:** Sistema de diseño moderno de Google.
    * **Navigation Compose:** Para la navegación entre pantallas.
    * **StateFlow / collectAsState:** Gestión de estado reactiva.
* **`domain` (Capa de Dominio):** Contiene la lógica de negocio pura y los modelos (ej: `ProductForm.kt`), sin dependencias de Android. Aquí reside la lógica de validación de formularios.
* **`data` (Capa de Datos):** Gestiona el origen de los datos.
    * **Room (sobre SQLite):** Se utiliza Room como la capa de abstracción (ORM) sobre la base de datos **SQLite** local. Gestiona la persistencia de productos, usuarios y pedidos.
    * **Repository Pattern:** Expone los datos (desde Room) al resto de la app.
* **`core` (Capa Núcleo):** Contiene utilidades transversales como `Validators.kt`, constantes de navegación y el tema de la app.

### Stack Tecnológico Principal
* **Kotlin:** Lenguaje principal.
* **Jetpack Compose:** UI Toolkit.
* **MVVM:** Patrón de arquitectura.
* **Room (SQLite):** Persistencia local (Base de Datos) mediante el ORM Room sobre una base de datos SQLite.
* **Jetpack Navigation:** Flujo de pantallas.
* **Coroutines & Flow:** Para operaciones asíncronas y reactivas.
* **Coil:** Para la carga de imágenes (desde URI de Cámara/Galería).

---

## 🚀 Instalación y Ejecución

1.  Clona el repositorio:
    ```bash
    git clone [https://github.com/tu-usuario/food-hub.git](https://github.com/tu-usuario/food-hub.git)
    ```
2.  Abre el proyecto con [Android Studio](https://developer.android.com/studio) (versión Flamingo o superior recomendada).
3.  Espera a que Gradle sincronice todas las dependencias.
    (Usar Gradle JDK bjr-17 porque es el usamos para el proyecto)
4.  Ejecuta la aplicación en un emulador o dispositivo físico.
    * (Nota: Para probar las funciones de **Cámara**, se requiere un dispositivo físico o un emulador configurado con una cámara virtual).

---

## 👥 Autores

* **Raúl Bustamante**
* **Martín Mora**

*(Proyecto desarrollado para la asignatura Desarrollo de Aplicaciones Móviles DSY1105)*
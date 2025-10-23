// --- BLOQUE DE PLUGINS ---
// Define las herramientas de compilación que usará el proyecto.
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.devtools.ksp)    // Procesador de anotaciones (necesario para Room).
}

// --- BLOQUE DE CONFIGURACIÓN DE ANDROID ---
android {
    namespace = "com.example.foodhubtest" // Identificador único del código fuente (R.java).
    compileSdk = 34                      // API contra la que se compila (Android 14).

    // Configuración base aplicada a todas las versiones de la app.
    defaultConfig {
        applicationId = "com.example.foodhubtest" // ID único en la Play Store.
        minSdk = 24                         // Versión mínima de Android soportada (Android 7.0).
        targetSdk = 34                      // Versión objetivo probada (Android 14).
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner" // Runner para pruebas.

        // Soporte para gráficos vectoriales (SVG) en APIs antiguas.
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    // Configuración para las versiones de lanzamiento (producción).
    buildTypes {
        release {
            isMinifyEnabled = false // Deshabilita la ofuscación y reducción de código (Proguard/R8).
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    // Configuración de compatibilidad de Java y Kotlin.
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17 // Usa sintaxis de Java 17.
        targetCompatibility = JavaVersion.VERSION_17 // Produce bytecode de Java 17.
    }
    kotlinOptions {
        jvmTarget = "17" // El bytecode de Kotlin debe ser compatible con JVM 17.
    }

    // Habilita características específicas de compilación.
    buildFeatures {
        compose = true // ¡Fundamental! Activa Jetpack Compose para la UI.
    }
    // Opciones específicas para el compilador de Compose.
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }

    // Resuelve conflictos de empaquetado del APK.
    packaging {
        resources {
            // Evita errores por archivos de licencia duplicados en las dependencias.
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

// --- BLOQUE DE DEPENDENCIAS ---
// Lista de todas las bibliotecas externas que necesita la app.
dependencies {

    // --- Núcleo y Utilidades de Jetpack ---
    implementation(libs.androidx.core.ktx)           // Extensiones de Kotlin para Android.
    implementation(libs.androidx.lifecycle.runtime.ktx) // Manejo del ciclo de vida (Coroutines, etc.).
    implementation(libs.androidx.activity.compose)    // Integración de Activity con Compose.

    // --- Jetpack Compose (UI) ---
    // BOM (Bill of Materials): Asegura versiones compatibles de Compose.
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)                            // Componentes base de UI (Row, Column, Text).
    implementation(libs.androidx.ui.graphics)                   // Primitivas gráficas.
    implementation(libs.androidx.ui.tooling.preview)            // Soporte para @Preview en el IDE.
    implementation(libs.androidx.material3)                     // Componentes de Material Design 3.
    implementation(libs.androidx.material.icons.extended)       // Iconos de Material.

    // --- Arquitectura (MVVM, Datos y Navegación) ---
    implementation(libs.androidx.navigation.compose)            // Para navegar entre pantallas (Composables).
    implementation(libs.coil.compose)                           // Carga eficiente de imágenes (ej. desde URLs).
    implementation(libs.androidx.room.runtime)                  // Base de datos local (Room).
    implementation(libs.androidx.room.ktx)                      // Extensiones de Kotlin para Room (Coroutines).
    ksp(libs.androidx.room.compiler)                            // Compilador de Room (genera el código de la BD).
    implementation(libs.androidx.lifecycle.viewmodel.compose)   // Integra ViewModel con Compose.
    implementation(libs.androidx.lifecycle.viewmodel.ktx)       // Utilidades para ViewModel.

    // --- Dependencias de Pruebas (Testing) ---
    // Pruebas unitarias (locales, en JVM).
    testImplementation(libs.junit)
    // Pruebas instrumentadas (en dispositivo/emulador).
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))   // BOM para pruebas de Compose.
    androidTestImplementation(libs.androidx.ui.test.junit4)          // Herramientas para probar UI de Compose.
    debugImplementation(libs.androidx.ui.test.manifest)              // Manifiesto para pruebas de debug.
}
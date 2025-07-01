plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.robinmp.listadependientes"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.robinmp.listadependientes"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }

    // ✨ CONFIGURACIÓN DE LINT PARA ANÁLISIS DE SEGURIDAD ✨
    lint {
        // Generar reportes en diferentes formatos
        htmlReport = true
        xmlReport = true
        textReport = true

        // Ubicación personalizada de reportes
        htmlOutput = file("$buildDir/reports/lint/lint-security-report.html")
        xmlOutput = file("$buildDir/reports/lint/lint-security-report.xml")
        textOutput = file("$buildDir/reports/lint/lint-security-report.txt")

        // Checks específicos de seguridad - ACTIVAR TODOS
        checkOnly += setOf(
            "HardcodedValues",           // Valores hardcodeados (contraseñas, URLs)
            "SecureRandom",              // Uso de Random inseguro
            "TrustAllX509TrustManager",  // Confianza en todos los certificados
            "BadHostnameVerifier",       // Verificación de hostname insegura
            "AllowAllHostnameVerifier",  // Permitir todos los hostnames
            "SSLCertificateSocketFactoryCreateSocket", // SSL inseguro
            "WorldReadableFiles",        // Archivos legibles por todos
            "WorldWriteableFiles",       // Archivos escribibles por todos
            "SetWorldReadable",          // Permisos de lectura mundial
            "SetWorldWriteable",         // Permisos de escritura mundial
            "PackageManagerGetSignatures", // Verificación de firmas
            "UsesPermissionSdkM",        // Permisos peligrosos
            "PrivateKey",                // Claves privadas hardcodeadas
            "ExternalStorageRoot",       // Almacenamiento externo inseguro
            "SdCardPath",                // Rutas de SD card hardcodeadas
            "SQLiteString",              // Queries SQL inseguros
            "LogConditional",            // Logs que pueden exponer datos
            "LogTagMismatch",            // Tags de log inconsistentes
            "StopShip",                  // Comentarios TODO/STOPSHIP
            "AndroidGradlePluginVersion", // Versión del plugin
            "GradleDependency",          // Dependencias desactualizadas
            "ObsoleteSdkInt",            // Versiones obsoletas del SDK
            "MinSdkTooLow",              // SDK mínimo muy bajo
            "TargetSdkTooLow"            // SDK objetivo muy bajo
        )

        // Configuraciones adicionales de seguridad
        abortOnError = false  // No detener build, solo reportar
        warningsAsErrors = false  // Por ahora solo advertencias
        ignoreWarnings = false
        quiet = false

        // Incluir código generado en el análisis
        checkGeneratedSources = true
        checkDependencies = true

        // Configurar nivel de detalle
        explainIssues = true
        noLines = false
        showAll = true

        // Baseline para ignorar issues existentes (opcional)
        // baseline = file("lint-baseline.xml")

        // Ignorar algunos checks que pueden dar falsos positivos
        disable += setOf(
            "GoogleAppIndexingWarning",  // Indexing de Google
            "MissingTranslation"         // Traducciones faltantes
        )
    }
}

dependencies {
    // TUS DEPENDENCIAS EXISTENTES (NO TOCAR)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui.text.google.fonts)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.navigation.runtime.android)
    implementation(libs.firebase.crashlytics.buildtools)
    implementation("com.google.code.gson:gson:2.11.0")

    implementation(platform("com.google.firebase:firebase-bom:33.15.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.android.gms:play-services-auth:21.0.0")
    implementation(libs.firebase.firestore.ktx)

    // ✨ DEPENDENCIAS DE SEGURIDAD ✨
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    // Para hash seguro de contraseñas
    implementation("org.mindrot:jbcrypt:0.4")

    // Para resolver el error de certificados de Google Fonts
    implementation("androidx.compose.ui:ui-text-google-fonts:1.5.4")

    // TUS DEPENDENCIAS DE TESTING (NO TOCAR)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

// ✨ TASKS PERSONALIZADAS PARA ANÁLISIS DE SEGURIDAD ✨
tasks.register("lintSecurity") {
    group = "verification"
    description = "Ejecuta análisis de seguridad con Android Lint"
    dependsOn("lint")

    doLast {
        println("📋 Análisis de seguridad completado!")
        println("📊 Reportes generados en:")
        println("   • HTML: build/reports/lint/lint-security-report.html")
        println("   • XML:  build/reports/lint/lint-security-report.xml")
        println("   • TXT:  build/reports/lint/lint-security-report.txt")
        println("🔍 Abre el archivo HTML en tu navegador para ver los resultados detallados")
    }
}

tasks.register("securityAnalysis") {
    group = "verification"
    description = "Análisis completo de seguridad (Lint + Tests)"
    dependsOn("lintSecurity", "test")

    doLast {
        println("🔐 Análisis completo de seguridad finalizado")
        println("📝 Revisa los reportes para identificar vulnerabilidades")
    }
}
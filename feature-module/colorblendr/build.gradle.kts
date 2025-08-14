plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.dokka)
    alias(libs.plugins.spotless)
}

// Enable K2 compiler for better performance
// Note: This is experimental in Kotlin 2.2.20-Beta2
kotlin {
    jvmToolchain(libs.versions.java.toolchain.get().toInt())
    
    compilerOptions {
        freeCompilerArgs.addAll(
            "-opt-in=kotlin.RequiresOptIn",
            "-Xjvm-default=all",
            "-Xcontext-receivers"
        )
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.fromTarget(libs.versions.jvmTarget.get())
    }
}

android {
    namespace = "dev.aurakai.auraframefx.colorblendr"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        vectorDrawables.useSupportLibrary = true
        
        // Enable multiDex if needed
        multiDexEnabled = true
        
        // Enable Java 24 bytecode
        compileSdkExtension = libs.versions.compileSdk.get()
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
        // Enable view binding if needed
        // viewBinding = true
    }

    compileOptions {
        // Java 24 compatibility
        sourceCompatibility = JavaVersion.VERSION_24
        targetCompatibility = JavaVersion.VERSION_24
        // Enable core library desugaring
        isCoreLibraryDesugaringEnabled = true
    }
    
    // Configure Java toolchain for this module
    kotlin {
        jvmToolchain(libs.versions.java.toolchain.get().toInt())
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
        // Enable experimental features if needed
        // kotlinCompilerExtensionVersion += "-experimental"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // ===== MODULE DEPENDENCIES =====
    implementation(project(":core-module"))
    implementation(project(":app"))

    // ===== ANDROX & COMPOSE =====
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
    
    // ===== KOTLIN COROUTINES =====
    implementation(libs.bundles.coroutines)
    
    // ===== ANDROIDX CORE =====
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.appcompat)
    
    // ===== HILT =====
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)
    
    // ===== NETWORKING =====
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.kotlinx.serialization)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    
    // ===== UTILITIES =====
    implementation(libs.timber)
    implementation(libs.coil.compose)
    implementation(libs.commons.io)
    implementation(libs.commons.compress)
    
    // ===== CORE LIBRARY DESUGARING =====
    coreLibraryDesugaring(libs.coreLibraryDesugaring)
    
    // ===== TESTING =====
    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testImplementation(libs.mockk.android)
    
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
    
    // ===== HILT TESTING =====
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.compiler)
    
    // ===== DEBUG =====
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    
    // ===== OPTIONAL: LEAK CANARY =====
    // debugImplementation(libs.leakcanary.android)
}

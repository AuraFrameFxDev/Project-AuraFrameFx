import org.jetbrains.kotlin.gradle.dsl.JvmTarget

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
        jvmTarget = JvmTarget.fromTarget(libs.versions.jvmTarget.get())
    }
}

android {
    namespace = "dev.aurakai.auraframefx.coremodule"
    compileSdk = libs.versions.compileSdk.get().toInt()
    ndkVersion = libs.versions.ndkVersion.get()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        
        // Enable multiDex if needed
        multiDexEnabled = true
        
        // Enable Java 24 bytecode
        compileSdkExtension = libs.versions.compileSdk.get()
        
        // NDK configuration for native code (if any)
        ndk {
            abiFilters.addAll(listOf("arm64-v8a", "armeabi-v7a", "x86_64"))
        }
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
    
    // Native build configuration (if CMakeLists.txt exists)
    externalNativeBuild {
        cmake {
            version = libs.versions.cmakeVersion.get()
            path = file("src/main/cpp/CMakeLists.txt")
        }
    }
        targetCompatibility = JavaVersion.VERSION_24
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    // Native build configuration (if any native code exists)
    externalNativeBuild {
        cmake {
            version = libs.versions.cmakeVersion.get()
        }
    }
}

dependencies {
    // ===== ANDROID & COMPOSE =====
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
    
    // ===== ROOM DATABASE =====
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    
    // ===== WORK MANAGER =====
    implementation(libs.work.runtime.ktx)
    
    // ===== FIREBASE =====
    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)
    
    // ===== XPOSED FRAMEWORK =====
    implementation(libs.bundles.xposed)
    
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
    // Xposed Framework (for core hooks)
    implementation(files("${project.rootDir}/Libs/api-82.jar"))
    implementation(files("${project.rootDir}/Libs/api-82-sources.jar"))
}

// ✅ Kotlin compiler options will be auto-provisioned from root build.gradle.kts
// No need for manual kotlinOptions block - it's deprecated!

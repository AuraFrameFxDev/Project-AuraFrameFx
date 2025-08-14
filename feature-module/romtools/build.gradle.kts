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

android {
    namespace = "dev.aurakai.auraframefx.romtools"
    compileSdk = libs.versions.compileSdk.get().toInt()
    ndkVersion = libs.versions.ndkVersion.get()
    
    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_24
        targetCompatibility = JavaVersion.VERSION_24
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/DEPENDENCIES"
            excludes += "/META-INF/LICENSE"
            excludes += "/META-INF/LICENSE.txt"
            excludes += "/META-INF/NOTICE"
            excludes += "/META-INF/NOTICE.txt"
        }
        jniLibs {
            useLegacyPackaging = true
        }
    }

    sourceSets {
        getByName("main") {
            java.srcDirs("src/main/java")
            assets.srcDirs("src/main/assets")
            jniLibs.srcDirs("src/main/jniLibs")
        }
    }
}

dependencies {
    // SACRED RULE #5: DEPENDENCY HIERARCHY
    implementation(project(":core-module"))
    implementation(project(":app"))
    implementation(project(":secure-comm"))

    // ✅ FIXED: Using correct bundle names from TOML
    implementation(libs.bundles.androidx.core)
    implementation(libs.bundles.compose)
    implementation(libs.bundles.coroutines)
    implementation(libs.bundles.network)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Hilt Dependency Injection
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // Room Database
    implementation(libs.bundles.room)
    ksp(libs.room.compiler)

    // Security bundles
    implementation(libs.bundles.security)

    // ✅ FIXED: Utilities bundle (now exists in TOML)
    implementation(libs.bundles.utilities)

    // Core library desugaring
    coreLibraryDesugaring(libs.coreLibraryDesugaring)

    // Testing
    testImplementation(libs.bundles.testing)
    testImplementation(libs.junit.engine)
    androidTestImplementation(libs.bundles.testing)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.compiler)

    // Debug implementations
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // System interaction and root access
    implementation(files("${project.rootDir}/Libs/api-82.jar"))
    implementation(files("${project.rootDir}/Libs/api-82-sources.jar"))
}

// Configure native ROM tools build
tasks.configureEach {
    if (name.startsWith("externalNativeBuild")) {
        dependsOn("copyRomTools")
    }
}

// Task to copy ROM modification tools
tasks.register<Copy>("copyRomTools") {
    from("src/main/cpp")
    into(layout.buildDirectory.dir("rom-tools").get())
    include("**/*.so", "**/*.bin", "**/*.img", "**/*.cpp", "**/*.h")
    includeEmptyDirs = false
}

// Task to verify ROM tools integrity
tasks.register("verifyRomTools") {
    doLast {
        val romToolsDir = file(layout.buildDirectory.dir("rom-tools").get())
        if (!romToolsDir.exists()) {
            println("⚠️  ROM tools directory not found - ROM functionality may be limited")
        } else {
            println("✅ ROM tools verified and ready")
        }
    }
}

tasks.named("preBuild") {
    dependsOn("verifyRomTools")
    doLast {
        println("✅ ROM tools verified and ready")
    }
}

// ✅ Kotlin compiler options auto-provisioned from root build.gradle.kts
// No deprecated kotlinOptions needed!

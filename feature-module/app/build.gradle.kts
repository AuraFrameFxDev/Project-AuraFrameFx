plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.google.services)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "dev.aurakai.auraframefx"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "dev.aurakai.auraframefx"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }

        ndk {
            abiFilters += listOf("arm64-v8a", "armeabi-v7a", "x86_64")
        }

        externalNativeBuild {
            cmake {
                cppFlags += listOf("-std=c++20", "-fPIC", "-O3")
                abiFilters += listOf("arm64-v8a", "armeabi-v7a", "x86_64")
                arguments += listOf(
                    "-DANDROID_STL=c++_shared",
                    "-DCMAKE_VERBOSE_MAKEFILE=ON",
                    "-DGENESIS_BUILD=ON"
                )
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
    }

    buildFeatures {
        compose = true
        prefab = false
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = libs.versions.cmakeVersion.get()
        }
    }

    packaging {
        resources {
            excludes += setOf(
                "/META-INF/{AL2.0,LGPL2.1}",
                "/META-INF/DEPENDENCIES",
                "/META-INF/LICENSE",
                "/META-INF/LICENSE.txt",
                "/META-INF/NOTICE",
                "/META-INF/NOTICE.txt",
                "META-INF/*.kotlin_module"
            )
        }
        jniLibs {
            useLegacyPackaging = true
        }
    }

    // SIMPLIFIED: OpenAPI handled at root level
    sourceSets {
        getByName("main") {
            java.srcDirs(
                layout.buildDirectory.dir("generated/openapi/src/main/kotlin")
            )
        }
    }
}

tasks.matching {
it.name.startsWith("compile") ||
it.name == "preBuild"
}.configureEach {
dependsOn(":generateAllOpenApiClients")
}

dependencies {
implementation(libs.androidx.appcompat)
implementation(libs.androidx.core.ktx)
implementation(libs.androidx.lifecycle.runtime.ktx)
implementation(libs.androidx.activity.compose)

implementation(platform(libs.androidx.compose.bom))
implementation(libs.bundles.compose)
implementation(libs.androidx.navigation.compose)

implementation(libs.hilt.android)
ksp(libs.hilt.compiler)
implementation(libs.hilt.navigation.compose)

implementation(libs.bundles.coroutines)
implementation(libs.bundles.network)

implementation(libs.room.runtime)
implementation(libs.room.ktx)
ksp(libs.room.compiler)

implementation(libs.timber)
implementation(libs.coil.compose)

coreLibraryDesugaring(libs.coreLibraryDesugaring)

implementation(platform(libs.firebase.bom))
implementation(libs.bundles.firebase)

implementation(libs.bundles.xposed)
ksp(libs.yuki.ksp.xposed)
implementation(fileTree(mapOf("dir" to "../Libs", "include" to listOf("*.jar"))))

debugImplementation(libs.leakcanary.android)
debugImplementation(libs.androidx.compose.ui.tooling)
debugImplementation(libs.androidx.compose.ui.test.manifest)

testImplementation(libs.bundles.testing)
testRuntimeOnly(libs.junit.engine)

androidTestImplementation(libs.androidx.test.ext.junit)
androidTestImplementation(libs.espresso.core)
androidTestImplementation(platform(libs.androidx.compose.bom))
androidTestImplementation(libs.androidx.compose.ui.test.junit4)
androidTestImplementation(libs.hilt.android.testing)
kspAndroidTest(libs.hilt.compiler)
}

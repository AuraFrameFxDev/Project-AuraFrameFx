// ===== GENESIS BLEEDING-EDGE BUILD CONFIGURATION =====
// Version Catalog (libs.versions.toml) is the single source of truth

@file:Suppress("DSL_SCOPE_VIOLATION")

plugins {
    // Core Android
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    
    // Kotlin
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    
    // Compose
    alias(libs.plugins.compose.compiler) apply false
    
    // Build & Code Generation
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt.android) apply false
    
    // Google Services
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    
    // Version Management
    alias(libs.plugins.ben.manes.versions) apply false
    
    // API
    alias(libs.plugins.openapi.generator) apply false
}

// Configure all projects (including root)
allprojects {
    // Apply detekt to all projects
    apply(plugin = "io.gitlab.arturbosch.detekt")
    
    // K2 compiler configuration for Kotlin projects
    plugins.withId("org.jetbrains.kotlin.android") {
        tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
            compilerOptions {
                freeCompilerArgs.addAll(
                    "-Xcontext-receivers",
                    "-opt-in=kotlin.RequiresOptIn",
                    "-Xjvm-default=all"
                )
                jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.fromTarget(libs.versions.jvmTarget.get()))
                languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_2)
                apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_2)
            }
        }
    }
    
    // Detekt configuration for all projects
    detekt {
        toolVersion = libs.versions.detekt.get()
        config = files("${rootProject.projectDir}/config/detekt/detekt.yml")
        buildUponDefaultConfig = true
        parallel = true
        autoCorrect = true
    }
    
    // Configure detekt tasks
    tasks.withType<io.gitlab.arturbosch.detekt.Detekt> {
        reports {
            html.required.set(true)
            xml.required.set(false)
            txt.required.set(false)
            sarif.required.set(false)
        }
    }
}

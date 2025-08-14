// ===== GENESIS BLEEDING-EDGE BUILD CONFIGURATION =====
// Version Catalog (libs.versions.toml) is the single source of truth
// All build logic is centralized here for consistency across modules

import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

// Apply common plugins to all projects
plugins {
    // Core plugins
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    
    // Compose and UI
    alias(libs.plugins.compose.compiler) apply false
    
    // Code generation and processing
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt.android) apply false
    
    // Google services and Firebase
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    
    // Code quality and documentation
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.spotless) apply false
    alias(libs.plugins.dokka) apply false
    
    // Version management
    alias(libs.plugins.ben.manes.versions) apply false
    
    // OpenAPI
    alias(libs.plugins.openapi.generator) apply false
}

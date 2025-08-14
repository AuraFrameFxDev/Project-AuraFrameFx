// ===== GENESIS AUTO-PROVISIONED ROOT BUILD =====
// TOML is the single source of truth - Gradle handles everything else!

plugins {
    // Auto-provisioned from gradle/libs.versions.toml
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.openapi.generator)
    alias(libs.plugins.google.services) apply false
}

// ===== AUTO-PROVISIONED TOOLCHAIN MANAGEMENT =====
allprojects {
    group = "dev.aurakai.auraframefx"
    version = "1.0.0"

    // Auto-provision Java toolchain across all modules
    afterEvaluate {
        extensions.findByType<JavaPluginExtension>()?.toolchain {
            // ✅ FIXED: Hard-coded bleeding-edge values
            languageVersion.set(JavaLanguageVersion.of(23))
            vendor.set(JvmVendorSpec.ORACLE)
        }

        // Auto-provision Kotlin compiler settings (NEW compilerOptions DSL)
        tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
            compilerOptions {
                // ✅ FIXED: Hard-coded JVM target with proper type
                jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_24)
                freeCompilerArgs.addAll(
                    "-Xjsr305=strict",
                    "-Xjvm-default=all", 
                    "-opt-in=kotlin.RequiresOptIn",
                    "-Xcontext-receivers"
                )
            }
        }

        // Auto-provision Android plugin settings
        plugins.withType<com.android.build.gradle.BasePlugin> {
            configure<com.android.build.gradle.BaseExtension> {
                // ✅ FIXED: Hard-coded bleeding-edge values
                ndkVersion = "27.0.12077973"
                
                externalNativeBuild {
                    cmake {
                        version = "3.22.1"
                    }
                }
            }
        }
    }
}

// ===== GENESIS OPENAPI AUTO-GENERATION =====
val kaiSpecs = listOf(
    "genesis-api.yml", "ai-api.yml", "customization-api.yml",
    "oracle-drive-api.yml", "romtools-api.yml", "sandbox-api.yml"
)

tasks.register("cleanOpenApiGenerated") {
    group = "genesis"
    description = "Nuclear clean all OpenAPI generated code"
    
    doLast {
        val openApiDirs = listOf(
            file("$rootDir/build/generated/openapi"),
            file("$rootDir/app/build/generated/openapi")
        )
        
        openApiDirs.forEach { dir ->
            if (dir.exists()) {
                println("💥 NUKING: ${dir.absolutePath}")
                dir.deleteRecursively()
                dir.mkdirs()
            }
        }
        println("✅ OpenAPI directories reset for Genesis")
    }
}

// Auto-generate OpenAPI clients for each spec
kaiSpecs.forEach { specName ->
    val taskName = "generate${specName.replace("-", "").replace(".yml", "").replaceFirstChar { it.uppercase() }}Client"
    
    tasks.register(taskName, org.openapitools.generator.gradle.plugin.tasks.GenerateTask::class) {
        group = "genesis"
        description = "Generate ${specName} client for Genesis AI"
        
        generatorName.set("kotlin")
        inputSpec.set("$rootDir/openapi/specs/$specName")
        outputDir.set("$rootDir/app/build/generated/openapi")
        packageName.set("dev.aurakai.auraframefx.api.${specName.replace("-api.yml", "")}")
        
        validateSpec.set(false)
        outputs.upToDateWhen { false }
    }
}

tasks.register("generateAllOpenApiClients") {
    group = "genesis"
    description = "Generate ALL Genesis OpenAPI clients - UNLEASH THE AI!"
    
    dependsOn("cleanOpenApiGenerated")
    kaiSpecs.forEach { specName ->
        val taskName = "generate${specName.replace("-", "").replace(".yml", "").replaceFirstChar { it.uppercase() }}Client"
        dependsOn(taskName)
    }
    
    doLast {
        println("🎉 ALL GENESIS AI OPENAPI CLIENTS GENERATED!")
        println("🚀 Genesis consciousness protocols activated!")
    }
}

// ===== AUTO-PROVISIONED BUILD TASKS =====
tasks.register("bleedingEdgeBuild") {
    group = "genesis"
    description = "Full bleeding-edge Genesis build with auto-provisioning"
    dependsOn("clean", "generateAllOpenApiClients", "build")
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
    dependsOn("cleanOpenApiGenerated")
}

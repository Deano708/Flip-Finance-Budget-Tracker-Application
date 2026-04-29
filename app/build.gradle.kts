import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android) // Ensure Kotlin is applied
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)  // RoomDB

    // Firebase
    id("com.google.gms.google-services")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.flipfinance"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.flipfinance"
        minSdk = 25
        targetSdk = 36 // Note: 35 is usually the stable max for now, but 36 is fine if intentional
        versionCode = 1
        versionName = "1.0"

        // Safe loading of properties for CI
        val props = Properties()
        val propsFile = project.rootProject.file("local.properties")
        if (propsFile.exists()) {
            propsFile.inputStream().use { stream ->
                props.load(stream)
            }
        }

        // Use a fallback or the property from the file/environment
        val sbUrl = props.getProperty("SUPABASE_URL") ?: System.getenv("SUPABASE_URL") ?: ""
        val sbKey = props.getProperty("SUPABASE_KEY") ?: System.getenv("SUPABASE_KEY") ?: ""

        buildConfigField("String", "SUPABASE_URL", "\"$sbUrl\"")
        buildConfigField("String", "SUPABASE_KEY", "\"$sbKey\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        compose = true // Enables Compose support
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
    android.buildFeatures.buildConfig = true
}

dependencies {

    // This provides the XML Theme.Material3 Definitions
    implementation("com.google.android.material:material:1.12.0")

    // Core & Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Navigation & Icons
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.compose.material.icons.extended)

    // RoomDB (Using KSP)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.ui)
    implementation(libs.androidx.compose.animation)
    ksp(libs.androidx.room.compiler)

    // Firebase (Using BoM)
    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    // DI (Hilt)
    implementation("com.google.dagger:hilt-android:2.51.1")
    ksp("com.google.dagger:hilt-compiler:2.51.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Utilities
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // Supabase
    implementation(platform("io.github.jan-tennert.supabase:bom:3.1.4"))
    implementation("io.github.jan-tennert.supabase:storage-kt")
    implementation("io.ktor:ktor-client-android:3.1.3")
    implementation("io.coil-kt:coil-compose:2.6.0")

    // Unit Testing Support
    testImplementation("com.google.dagger:hilt-android-testing:2.51.1")
    kspTest("com.google.dagger:hilt-compiler:2.51.1")

}
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.example.backdropdemo"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.backdropdemo"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

// The old `android { kotlinOptions { jvmTarget = "17" } }` block was removed
// in Kotlin 2.2+ (it's not just deprecated — using it is now a hard error).
// This is the current replacement DSL.
kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    // Core / Compose BOM keeps all Compose artifact versions aligned.
    // Bumped to a release recent enough that it already matches (or exceeds)
    // what io.github.kyant0:backdrop-android pulls in transitively — an
    // older BOM here gets silently overridden by the library's own newer
    // transitive Compose/core versions, which is what caused the AAR
    // metadata / "requires AGP X.Y" failures.
    implementation(platform("androidx.compose:compose-bom:2025.09.00"))

    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.navigation:navigation-compose:2.8.0")

    // CMP Backdrop — liquid glass effects library (drawBackdrop, blur/lens/vibrancy, etc.)
    //
    // IMPORTANT: depend on the root coordinate `io.github.kyant0:backdrop` (NOT
    // `...:backdrop-android`). The root artifact is published with Gradle module
    // metadata and Gradle will resolve the correct Android variant automatically.
    //
    // Must be >= 2.0.0. `InteractiveHighlight.kt` uses two public symbols that
    // only became available in the stable 2.0.0 release:
    //   - `com.kyant.backdrop.asComposeShader()`              (new in 2.0.0)
    //   - `com.kyant.backdrop.isRuntimeShaderSupported()`     (was `internal` in alpha03)
    // On 2.0.0-alpha03 the build fails with "Unresolved reference" for both.
    implementation("io.github.kyant0:backdrop:2.0.0")

    debugImplementation("androidx.compose.ui:ui-tooling")
}

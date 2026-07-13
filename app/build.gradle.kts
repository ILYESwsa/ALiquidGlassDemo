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

    kotlinOptions {
        jvmTarget = "17"
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
    implementation("io.github.kyant0:backdrop-android:2.0.0-alpha03")

    debugImplementation("androidx.compose.ui:ui-tooling")
}

plugins {
    id("com.android.application") version "8.2.0"
    kotlin("android") version "1.9.23"
}

android {
    namespace = "com.gestionmemoire.androidApp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.gestionmemoire.androidApp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }
    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
}

dependencies {
    implementation(project(":shared"))
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
}
plugins {
    kotlin("multiplatform") version "1.9.23"
}

group = "com.gestionmemoire"
version = "0.1"

kotlin {
    android()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                // Ajoutez ici les dépendances communes
            }
        }
        val androidMain by getting
        val iosMain by creating {
            dependsOn(commonMain)
        }
    }
}
plugins {
    alias(libs.plugins.android.application)  // Plugin pour Android Application
    alias(libs.plugins.kotlin.android)       // Plugin pour Kotlin Android
    alias(libs.plugins.kotlin.compose)       // Plugin pour Kotlin Compose
    id ("kotlin-kapt")
}

android {
    namespace = "fr.isen.curiecadet.isensmartcompanion"
    compileSdk = 35

    defaultConfig {
        applicationId = "fr.isen.curiecadet.isensmartcompanion"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"



    }

    buildTypes {
        release {
            isMinifyEnabled = false  // Désactivation de la minification pour la release
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true  // Activation de Jetpack Compose
        buildConfig = true
    }
}

dependencies {
    // Dépendances de base
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)



    // Room Runtime (Déclaration correcte)
    implementation(libs.androidx.room.runtime)

    // Room KTX (Extensions Kotlin pour Room)
    implementation(libs.androidx.room.ktx)

    kapt(libs.room.compiler)


    // Ajoutez cette dépendance pour utiliser lifecycleScope
    implementation(libs.androidx.lifecycle.runtime.ktx.v261)

    // Dépendances de Compose
    implementation(platform(libs.androidx.compose.bom))  // BOM pour gérer les versions des bibliothèques Compose
    implementation(libs.androidx.ui)                    // Composants de base de Compose
    implementation(libs.androidx.ui.graphics)           // Graphiques pour Compose
    implementation(libs.androidx.material3)             // Composants Material3 pour Compose
    implementation(libs.androidx.ui.tooling.preview)    // Prévisualisation dans l'IDE

    // Navigation Compose
    implementation(libs.androidx.navigation.compose)

    // Dépendances pour le réseau
    implementation(libs.retrofit)           // Retrofit pour les appels API
    implementation(libs.converter.gson)     // Convertisseur Gson pour Retrofit
    implementation(libs.gson)               // Librairie Gson pour JSON

    // Autres dépendances
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.appcompat)
    implementation(libs.material3)
    implementation(libs.accessibility.test.framework)
    implementation(libs.generativeai)
    implementation(libs.androidx.material.icons.extended)

    // Dépendances de test
    testImplementation(libs.junit)                                // JUnit pour les tests unitaires
    androidTestImplementation(libs.androidx.junit)                // JUnit pour les tests Android
    androidTestImplementation(libs.androidx.espresso.core)        // Espresso pour les tests UI
    androidTestImplementation(platform(libs.androidx.compose.bom))  // BOM pour tests Compose
    androidTestImplementation(libs.androidx.ui.test.junit4)       // Tests UI avec Compose

    // Dépendances pour le débogage
    debugImplementation(libs.androidx.ui.tooling)                 // Outils de débogage Compose
    debugImplementation(libs.androidx.ui.test.manifest)           // Manifest pour tests Compose
}
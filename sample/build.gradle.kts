@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.irisMock)
}

android {
    namespace = "dev.arildo.iris.sample"
    compileSdk = 34

    defaultConfig {
        applicationId = "dev.arildo.iris.mock"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            keyAlias = "any"
            keyPassword = "anyany"
            storeFile = file("test.jks")
            storePassword = "anyany"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.material3)
    implementation(libs.androidx.core)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.material)
    implementation(libs.retrofit)
    implementation(libs.retrofitGson)
    implementation(libs.androidx.constraintLayout)
    implementation(libs.androidx.lifecycle)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.activity.compose)
    implementation(libs.compose.ui.tooling.preview)

    debugImplementation(libs.compose.ui.tooling)
    testImplementation(libs.test.junit)
}

tasks.preBuild {
    // First of all, publish plugin locally to get all changes
    dependsOn(":iris-mock-plugin:publishToMavenLocal")
    dependsOn(":iris-mock-compiler:publishToMavenLocal")
}

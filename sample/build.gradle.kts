plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("dev.arildo.iris-mock-plugin") version "1.0.10"
}

android {
    namespace = "dev.arildo.iris.sample"
    compileSdk = 33

    defaultConfig {
        applicationId = "dev.arildo.iris.mock"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

kotlin.sourceSets.main {
    kotlin.srcDirs(
        file("build/generated/ksp/main/kotlin")
    )
}

dependencies {
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.7.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")

//    kotlinCompilerPluginClasspath(project(":iris-mock-plugin"))
//
//    implementation(project(":iris-mock"))
//    kspDebug(project(":iris-mock-ksp"))

//    implementation("dev.arildo:iris-mock:0.0.1-SNAPSHOT")
//    ksp("dev.arildo:iris-mock-compiler:0.0.1-SNAPSHOT")

    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

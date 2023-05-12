plugins {
    id("java-library")
    kotlin("jvm")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    api("com.squareup.okhttp3:okhttp:3.14.9")
    api("com.squareup.okio:okio:2.8.0")
}

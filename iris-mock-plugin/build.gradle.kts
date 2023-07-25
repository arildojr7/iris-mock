import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.gradle.plugin-publish") version "1.2.0"
    id("com.github.gmazzo.buildconfig") version "3.0.3"
    `kotlin-dsl`
    signing
    kotlin("jvm")
    kotlin("kapt")
}

group = findProperty("GROUP_ID").toString()
version = findProperty("PLUGIN_VERSION").toString()

gradlePlugin {
    website.set("https://github.com/arildojr7/iris-mock")
    vcsUrl.set("https://github.com/arildojr7/iris-mock.git")

    plugins {
        create("irisMockGradlePlugin") {
            id = findProperty("PLUGIN_ID").toString()
            displayName = "Plugin to inject iris-mock custom interceptors at OkHttp"
            description =
                "Injects custom interceptors at bytecode level in OkHttpClient. See GitHub for more info"
            tags.set(listOf("interceptor", "android", "okhttp", "mock"))
            implementationClass = "dev.arildo.iris.plugin.IrisMockGradlePlugin"
        }
    }
}

signing {
    setRequired {
        gradle.taskGraph.allTasks.any { it is PublishToMavenRepository }
    }
}

java {
    withJavadocJar()
    withSourcesJar()
}

dependencies {
    implementation("com.android.tools.build:gradle:7.2.2")
    implementation(kotlin("stdlib"))
    implementation(kotlin("gradle-plugin-api"))
    implementation("com.squareup:kotlinpoet:1.14.2")

    compileOnly("dev.gradleplugins:gradle-api:7.6")
    compileOnly("com.squareup.okhttp3:okhttp:3.14.9")
    compileOnly("org.jetbrains.kotlin:kotlin-compiler-embeddable:1.8.21")
    compileOnly("com.google.auto.service:auto-service-annotations:1.0.1")
    kapt("com.google.auto.service:auto-service:1.0.1")

    arrayOf("asm", "asm-util", "asm-commons").forEach {
        compileOnly("org.ow2.asm:$it:9.4")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += listOf(
            "-opt-in=kotlin.RequiresOptIn",
            "-opt-in=org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi"
        )
    }
}

val pluginId = findProperty("PLUGIN_ID").toString()
val pluginArtifactId = findProperty("PLUGIN_ARTIFACT_ID").toString()

buildConfig {
    packageName("dev.arildo.iris.plugin")
    buildConfigField("String", "PLUGIN_ID", "\"${pluginId}\"")
    buildConfigField("String", "PLUGIN_GROUP_ID", "\"${group}\"")
    buildConfigField("String", "PLUGIN_ARTIFACT_ID", "\"${pluginArtifactId}\"")
    buildConfigField("String", "PLUGIN_VERSION", "\"${version}\"")
}

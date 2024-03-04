import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.gradle.plugin-publish") version "1.2.0"
    id("com.github.gmazzo.buildconfig") version "3.0.3"
    `kotlin-dsl`
    signing
    kotlin("jvm")
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

val compatAgp42: SourceSet by sourceSets.creating
val compatAgp71: SourceSet by sourceSets.creating
val compatAgp72: SourceSet by sourceSets.creating
val shared: SourceSet by sourceSets.creating

dependencies {
    compatAgp72.compileOnlyConfigurationName("com.android.tools.build:gradle:7.2.2")
    compatAgp72.compileOnlyConfigurationName("dev.gradleplugins:gradle-api:7.6")
    compatAgp72.compileOnlyConfigurationName(shared.output)

    compatAgp71.compileOnlyConfigurationName("com.android.tools.build:gradle:7.1.0")
    compatAgp71.compileOnlyConfigurationName("dev.gradleplugins:gradle-api:7.6")
    compatAgp71.compileOnlyConfigurationName(shared.output)

    compatAgp42.compileOnlyConfigurationName("com.android.tools.build:gradle:4.2.0")
    compatAgp42.compileOnlyConfigurationName("dev.gradleplugins:gradle-api:7.6")
    compatAgp42.compileOnlyConfigurationName(shared.output)

    compileOnly(compatAgp42.output)
    compileOnly(compatAgp72.output)
    compileOnly(compatAgp71.output)
    compileOnly(shared.output)

    compileOnly(kotlin("gradle-plugin"))
    compileOnly("com.squareup.okhttp3:okhttp:3.14.9")

    arrayOf("asm", "asm-util", "asm-commons").forEach {
        compileOnly("org.ow2.asm:$it:9.4")
        shared.compileOnlyConfigurationName("org.ow2.asm:$it:9.4")
    }
}

tasks.withType<Jar> {
    from(compatAgp42.output)
    from(compatAgp72.output)
    from(compatAgp71.output)
    from(shared.output)
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
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

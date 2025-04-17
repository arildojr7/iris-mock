import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.gradlePublish)
    alias(libs.plugins.buildconfig)
    alias(libs.plugins.ktlint)
    `kotlin-dsl`
    signing
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
            implementationClass = "dev.arildo.irismock.plugin.IrisMockGradlePlugin"
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
    shared.compileOnlyConfigurationName(gradleApi())
    shared.compileOnlyConfigurationName(libs.agp420)
    shared.compileOnlyConfigurationName(libs.bundles.asm)

    compatAgp42.compileOnlyConfigurationName(gradleApi())
    compatAgp42.compileOnlyConfigurationName(libs.agp420)
    compatAgp42.compileOnlyConfigurationName(shared.output)

    compatAgp71.compileOnlyConfigurationName(gradleApi())
    compatAgp71.compileOnlyConfigurationName(libs.agp710)
    compatAgp71.compileOnlyConfigurationName(shared.output)

    compatAgp72.compileOnlyConfigurationName(gradleApi())
    compatAgp72.compileOnlyConfigurationName(libs.agp722)
    compatAgp72.compileOnlyConfigurationName(shared.output)

    compileOnly(compatAgp42.output)
    compileOnly(compatAgp72.output)
    compileOnly(compatAgp71.output)
    compileOnly(shared.output)

    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.okhttp)
    compileOnly(libs.bundles.asm)
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
    packageName("dev.arildo.irismock.plugin")
    buildConfigField("String", "PLUGIN_ID", "\"${pluginId}\"")
    buildConfigField("String", "PLUGIN_GROUP_ID", "\"${group}\"")
    buildConfigField("String", "PLUGIN_VERSION", "\"${version}\"")
}

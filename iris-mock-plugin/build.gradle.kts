plugins {
    id("com.gradle.plugin-publish") version "1.2.0"
    `kotlin-dsl`
    `signing`
}

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

group = findProperty("GROUP_ID").toString()
version = findProperty("PLUGIN_VERSION").toString()

gradlePlugin {
    website.set("https://github.com/arildojr7/iris-mock")
    vcsUrl.set("https://github.com/arildojr7/iris-mock.git")

    plugins {
        create("irisMockPlugin") {
            id = findProperty("PLUGIN_ID").toString()
            displayName = "Plugin to inject iris-mock custom interceptors at OkHttp"
            description = "Injects custom interceptors at bytecode level in OkHttpClient. See GitHub for more info"
            tags.set(listOf("interceptor", "android", "okhttp", "mock"))
            implementationClass = "dev.arildo.iris.plugin.IrisMockPlugin"
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
    compileOnly("dev.gradleplugins:gradle-api:7.6")
    compileOnly("com.squareup.okhttp3:okhttp:3.14.9")
    arrayOf("asm", "asm-util", "asm-commons").forEach {
        compileOnly("org.ow2.asm:$it:9.4")
    }
}

plugins {
    id("com.gradle.plugin-publish") version "1.2.0"
    `kotlin-dsl`
}

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

gradlePlugin {
    plugins {
        create("irisMockPlugin") {
            id = findProperty("PLUGIN_ID").toString()
            group = findProperty("GROUP_ID").toString()
            version = findProperty("PLUGIN_VERSION").toString()
            implementationClass = "dev.arildo.iris.plugin.IrisMockPlugin"
        }
    }
}

dependencies {
    implementation("com.android.tools.build:gradle:7.2.2")
    compileOnly("dev.gradleplugins:gradle-api:7.6")
    compileOnly("com.squareup.okhttp3:okhttp:3.14.9")
    arrayOf("asm", "asm-util", "asm-commons").forEach {
        compileOnly("org.ow2.asm:$it:9.4")
    }
}

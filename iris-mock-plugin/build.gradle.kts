plugins {
    id("com.gradle.plugin-publish") version "1.2.0"
    `kotlin-dsl`
    `signing`
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

publishing {
    repositories {
        maven {
            name = "OSSRH"
            url = uri(getMavenUrl())
            credentials {
                username = System.getenv("SONATYPE_USER")
                password = System.getenv("SONATYPE_PASSWORD")
            }
        }
    }
}
signing {
    setRequired {
        gradle.taskGraph.allTasks.any { it is PublishToMavenRepository }
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

fun getMavenUrl(): String = if (System.getenv("IS_RELEASE") == "true") {
    "" // TODO add release maven url
} else {
    "https://s01.oss.sonatype.org/content/repositories/snapshots/"
}

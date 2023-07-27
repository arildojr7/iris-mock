plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("maven-publish")
    id("signing")
    `kotlin-dsl`
    id("com.github.gmazzo.buildconfig") version "3.0.3"
}

group = findProperty("GROUP_ID").toString()
version = findProperty("PLUGIN_VERSION").toString()

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = findProperty("GROUP_ID").toString()
            artifactId = findProperty("PLUGIN_ARTIFACT_ID").toString()
            version = findProperty("PLUGIN_VERSION").toString()

            from(components["java"])

            pom {
                name.set("iris-mock")
                description.set("A kotlin-first tool to intercept android network calls, modify requests/responses and mock entire APIs")
                url.set("https://github.com/arildojr7/iris-mock")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("arildojr7")
                        name.set("Arildo Borges Jr")
                        email.set("arildo@codeinlab.io")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/arildojr7/iris-mock.git")
                    url.set("https://github.com/arildojr7/iris-mock")
                }
            }
        }
    }
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
    sign(publishing.publications["maven"])
}

java {
    withJavadocJar()
    withSourcesJar()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += listOf(
            "-opt-in=kotlin.RequiresOptIn",
            "-opt-in=org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi"
        )
    }
}

dependencies {
    implementation("com.squareup:kotlinpoet:1.14.2")
    compileOnly("org.jetbrains.kotlin:kotlin-compiler-embeddable:1.8.21")

    compileOnly("com.google.auto.service:auto-service-annotations:1.0.1")
    kapt("com.google.auto.service:auto-service:1.0.1")
}

val pluginId = findProperty("PLUGIN_ID").toString()

buildConfig {
    packageName("dev.arildo.iris.plugin")
    buildConfigField("String", "PLUGIN_ID", "\"${pluginId}\"")
}

fun getMavenUrl(): String = if (System.getenv("IS_RELEASE") == "true") {
    "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
} else {
    "https://s01.oss.sonatype.org/content/repositories/snapshots/"
}

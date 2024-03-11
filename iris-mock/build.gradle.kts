@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin.jvm)
    id("java-library")
    id("maven-publish")
    id("signing")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = findProperty("GROUP_ID").toString()
            artifactId = findProperty("LIB_ARTIFACT_ID").toString()
            version = findProperty("LIB_VERSION").toString()

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

dependencies {
    implementation(libs.okhttp)
    implementation(libs.okio)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization)
    testImplementation(libs.test.junit)
    testImplementation(libs.test.mockk)
}
tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}
fun getMavenUrl(): String = if (System.getenv("IS_RELEASE") == "true") { // todo refactor
    "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
} else {
    "https://s01.oss.sonatype.org/content/repositories/snapshots/"
}

plugins {
    kotlin("jvm")
    id("maven-publish")
    id("signing")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = findProperty("GROUP_ID").toString()
            artifactId = findProperty("COMPILER_ARTIFACT_ID").toString()
            version = findProperty("COMPILER_VERSION").toString()

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

dependencies {
    implementation("com.google.devtools.ksp:symbol-processing-api:1.8.21-1.0.11")
}

fun getMavenUrl(): String = if (System.getenv("IS_RELEASE") == "true") {
    "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
} else {
    "https://s01.oss.sonatype.org/content/repositories/snapshots/"
}

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
        }
    }
    repositories {
        maven {
            name = "OSSRH"
            url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
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
    implementation("com.google.devtools.ksp:symbol-processing-api:1.7.10-1.0.6")
}

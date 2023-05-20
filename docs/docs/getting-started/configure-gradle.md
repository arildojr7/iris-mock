In order to use iris mock, you need to add its dependencies to your gradle files. 
You can choose to configure iris mock for entire project or just for a specific build variant:

## Configuring on entire project

=== "Kotlin"

    ```kotlin
    // add plugin to app module build.gradle.kts
    plugins {
        id("com.google.devtools.ksp")
        id("dev.arildo.iris-mock-plugin") version "1.0.0"
    }

    // add dependencies
    dependencies {
        implementation("dev.arildo:iris-mock:1.0.0")
        ksp("dev.arildo:iris-mock-compiler:1.0.0")
    }
    ```

=== "Groovy"

    ```kotlin
    // todo
    ```


## Configuring in a specific build variant

=== "Kotlin"

    ```kotlin
    // add plugin to app module build.gradle.kts
    plugins {
        id("com.google.devtools.ksp")
        id("dev.arildo.iris-mock-plugin") version "1.0.0" apply false
    }

    buildTypes {
        debug {
            apply(plugin = "dev.arildo.iris-mock-plugin")
        }
    }

    dependencies {
        debugImplementation("dev.arildo:iris-mock:1.0.0")
        kspDebug("dev.arildo:iris-mock-compiler:1.0.0")
    }
    ```

=== "Groovy"

    ```kotlin
    // todo
    ```

---
You are now ready to create your own interceptors

!!! note ""

    Since iris mock uses the new `Transform API`, it only works on AGP 4.2.0+


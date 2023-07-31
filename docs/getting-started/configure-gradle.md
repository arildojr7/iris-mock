To use iris mock you need to add its dependency to your gradle files.
You can choose to configure iris mock for entire project or just for a specific build variant:

## Configuring on entire project

=== "Kotlin"

    ```kotlin
    // add plugin to app module build.gradle.kts
    plugins {
        id("dev.arildo.iris-mock-plugin") version "1.1.0-alpha04"
    }
    ```

=== "Groovy"

    ```kotlin
    // add plugin to app module build.gradle
    plugins {
        id "dev.arildo.iris-mock-plugin" version "1.1.0-alpha04"
    }
    ```
<details>
  <summary>Using legacy plugin application - older gradle</summary>

```kotlin
// build.gradle.kts
buildscript {
    repositories {
        maven { url = uri("https://plugins.gradle.org/m2/") }
    }
    dependencies {
        classpath("dev.arildo:iris-mock-plugin:1.1.0-alpha04")
    }
}

apply(plugin = "dev.arildo.iris-mock-plugin")
```
</details>

## Configuring in a specific build variant

=== "Kotlin"

    ```kotlin
    // add plugin to app module build.gradle.kts
    plugins {
        id("dev.arildo.iris-mock-plugin") version "1.1.0-alpha04" apply false
    }

    buildTypes {
        debug {
            apply(plugin = "dev.arildo.iris-mock-plugin")
        }
    }
    ```

=== "Groovy"

    ```kotlin
    // todo
    ```

---
You are now ready to create your own interceptors :sunglasses:

!!! note ""

    Since iris mock uses the new `Transform API`, it only works on AGP 4.2.0+


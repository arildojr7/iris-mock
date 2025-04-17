<p align="center">
  <img src="assets/iris-mock-header.png" /> 
  <br><br>
  <img src="https://github.com/arildojr7/iris-mock/actions/workflows/pull_request.yml/badge.svg" />
  <img src="https://shields.io/badge/mavenCentral-v1.2.0-blue" />
  <a href="https://pinterest.github.io/ktlint/"><img src="https://img.shields.io/badge/code%20style-%E2%9D%A4-FF4081.svg" alt="ktlint"></a>
  <img src="https://img.shields.io/github/license/arildojr7/iris-mock?color=0979ba" />
</p>

A kotlin tool to intercept android network calls, modify requests/responses and mock entire APIs. Also includes a cool DSL, that helps to reduce boilerplate code and simplify development.
<br><br>
btw, <b>Iris</b> is my daughter's name 🥰

## Features
- Works with Retrofit, Volley and other libs that depend on OkHttp
- Allow intercept call on 3rd-party libs
- [DSL](https://kotlinlang.org/docs/type-safe-builders.html) to avoid boilerplate

## Why use IrisMock?
- A centralized tool to log, intercept and modify requests/response
- No need to do SSL things, like inject certificate
- Simple and intuitive API
- Doesn't use reflection
- As it works at bytecode level, can be used with 3rd-party libs (plugin-only)

## Getting started

### #1 - IrisMock plugin
This is the default way of using IrisMock. Start by adding the plugin to your project:

```kotlin
// :app build.gradle.kts
plugins {
    id("dev.arildo.iris-mock-plugin") version "LATEST_VERSION"
}
```
<details>
  <summary>Using legacy plugin application - older gradle</summary>

```kotlin
// :app build.gradle
buildscript {
    repositories {
        maven {
          url = uri("https://plugins.gradle.org/m2/")
        }
    }
    dependencies {
        classpath("dev.arildo:iris-mock-plugin:LATEST_VERSION")
    }
}

apply(plugin = "dev.arildo.iris-mock-plugin")
```
</details>

Then create a class implementing the `Interceptor` interface and then use `irisMock(chain)` function:
```kotlin
class MyInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain) = irisMock(chain) {
        onGet(contains = "user/profile") mockResponse userProfileJson
        onPost(endsWith = "/login") {
            delay(2_000) // fake slow internet
            if (containsInRequestBody("validPassword")) mockResponse(successLoginJson)
            else mockResponse(errorPasswordJson)
        }
    }
}
```

Finally, we need to start IrisMock on application and pass the interceptors:
```kotlin
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startIrisMock {
            enableLogs()
            interceptors(::MyInterceptor)
        }
    }
}
```
---
### #2 - IrisMock DSL
This way, the interceptors are __not__ injected on OkHttp, so you need to it by yourself. It's specially useful
if you already have interceptors on your project and want only benefit from using IrisMock DSL.

```kotlin
// build.gradle.kts
dependencies {
    implementation("dev.arildo:iris-mock:LATEST_VERSION")
}
```

then you can use `irisMock(chain)`, as shown previously.

See [official docs](https://irismock.arildo.dev/getting-started/configure-gradle/) for further details

## Roadmap
- Add support to [Ktor](https://github.com/ktorio/ktor)
- Expand DSL

## Contributing
Feel free to open PRs and submit feature suggestions via the repository issues. Everything's welcome 😎

## Support
[!["Buy Me A Coffee"](https://www.buymeacoffee.com/assets/img/custom_images/orange_img.png)](https://bmc.link/arildojr7)

## License
```
Copyright 2023 Arildo Borges Junior

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

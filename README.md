<p align="center">
  <img src="assets/iris-mock-header.png" /> 
  <br><br>
  <img src="https://img.shields.io/github/actions/workflow/status/arildojr7/iris-mock/pull_request.yml?color=00b330" />
  <img src="https://shields.io/badge/mavenCentral-v1.1.0-blue" />
  <a href="https://pinterest.github.io/ktlint/"><img src="https://img.shields.io/badge/code%20style-%E2%9D%A4-FF4081.svg" alt="ktlint"></a>
  <img src="https://img.shields.io/endpoint?color=00b330&url=https%3A%2F%2Fhits.dwyl.com%2Farildojr7%2Firis-mock.json" />
  <img src="https://img.shields.io/github/license/arildojr7/iris-mock?color=0979ba" />
</p>

A kotlin-first tool to intercept android network calls, modify requests/responses and mock entire APIs. Also includes a cool DSL, that helps to reduce boilerplate code and simplify development.
<br><br>
btw, <b>Iris</b> is my daughter's name 🥰

## Features
- Works with Retrofit, Volley and every libs that depend on OkHttp
- Allow intercept call on 3rd-party libs
- [DSL](https://kotlinlang.org/docs/type-safe-builders.html) to avoid boilerplate

## Why use iris mock?
- A centralized tool to log, intercept and modify requests/response
- As it works at bytecode level, can be used with 3rd-party libs
- No need to apply KAPT or KSP, since it's implemented directly through KCP
- No need to do SSL things, like inject certificate

## How to use

#### build.gradle.kts

```kotlin
plugins {
  id("dev.arildo.iris-mock-plugin") version "1.1.0"
}
```

<details>
  <summary>Using legacy plugin application - older gradle</summary>
  
  ```kotlin
  buildscript {
      repositories {
          maven {
              url = uri("https://plugins.gradle.org/m2/")
          }
      }
      dependencies {
          classpath("dev.arildo:iris-mock-plugin:1.1.0-alpha04")
      }
}

apply(plugin = "dev.arildo.iris-mock-plugin")
  ```
</details>


#### Code
Just create a class implementing the `Interceptor` interface and annotate it with `@IrisMockInterceptor`. That's all. The interceptor will be automatically injected at `OkHttp`

```kotlin
@IrisMockInterceptor
class MyInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain) = irisMockScope(chain) {
        onGet(contains = "user/profile") mockResponse userProfileJson
        onPost(endsWith = "/login") then {
            delay(2_000) // supports coroutine 
            if (requestContains("validPassword")) mockResponse(successLoginJson)
            else mockResponse(errorPasswordJson)
        }
        enableLogs() // you can log requests
    }
}
```

See [official docs](https://irismock.arildo.dev/getting-started/configure-gradle/) for further details

## Roadmap
- Add support to [Ktor](https://github.com/ktorio/ktor)
- Expand DSL
- and many other cool things

## Getting started video
[![Iris Mock](https://github.com/arildojr7/iris-mock/assets/32650006/a8526d9a-923f-49b1-b58f-fe0dbd15d051)](https://www.youtube.com/watch?v=fe3bJ55tIck)

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

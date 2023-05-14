<p align="center">
  <img src="assets/iris-mock-header.png" />
</p>
A kotlin-first tool to intercept android network calls, modify requests/responses and mock entire APIs. Also includes a cool DSL, that help to reduce boilerplate code and simplify development. 
<br><br>
btw, Iris is my daughter's name 🥰

## Features
- Works with Retrofit, Volley and every libs that depend on OkHttp
- Allow intercept call on 3rd-party libs
- [DSL](https://kotlinlang.org/docs/type-safe-builders.html) to avoid boilerplate


## Why use iris mock?
- A centralized tool to log, intercept and modify requests/response
- As it works at bytecode level, can be used with 3rd-party libs
- Compiler developed using [KSP](https://github.com/google/ksp), up to 2x faster than KAPT
- No need to do SSL things, like inject certificate

## How to use

#### Dependencies

```kotlin
// add plugin to app module build.gradle.kts
plugins {
    id("com.google.devtools.ksp")
    id("dev.arildo.iris-mock-plugin") version "0.0.1-SNAPSHOT"
}

// add dependencies
dependencies {
    implementation("dev.arildo:iris-mock:0.0.1-SNAPSHOT")
    ksp("dev.arildo:iris-mock-compiler:0.0.1-SNAPSHOT")
}

// as there's no stable release yet, you need to add these lines on settings.gradle.kts
repositories {
    maven {
        url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    }
}
```

#### Code
Just create a class implementing the `Interceptor` interface and annotate it with `@IrisMockInterceptor`. That's all. The interceptor will be automatically injected at `OkHttp` 😎

```kotlin
@IrisMockInterceptor
class MyInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain) = irisMockScope(chain) {
        onGet(contains = "user/profile") mockResponse userProfileJson
        onPost(endsWith = "/login") then {
            delay(2_000) // supports coroutine 😎
            if (requestContains("validPassword")) mockResponse(successLoginJson)
            else mockResponse(errorPasswordJson)
        }
        startLogger() // you can log requests
    }
}
```

## Roadmap
- Add support to [Ktor](https://github.com/ktorio/ktor)
- Expand DSL
- and many other cool things

## Contributing
Feel free to open PRs and submit feature suggestions via the repository issues. Everything's welcome 😎

## License
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

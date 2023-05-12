<p align="center">
  <img src="assets/iris-mock-header.png" />
</p>
A kotlin-first tool to intercept android network calls, modify requests/responses and mock entire APIs.
<br><br>
btw, Iris is my daughter's name 🥰

## Features
- Works with Retrofit, Volley and every libs that depend on OkHttp
- Allow intercept call on 3rd-party libs

## Why use iris mock?
- A centralized tool to log, intercept and modify requests/response
- As it works at bytecode level, can be used with 3rd-party libs
- Compiler developed using [KSP](https://github.com/google/ksp), up to 2x faster than KAPT
- No need to do SSL things, like inject certificate

## How to use

#### Dependencies

```kotlin
// add plugin to app module `build.gradle.kts`
plugins {
    id("com.google.devtools.ksp")
    id("dev.arildo.iris-mock-plugin") version "0.0.1"
}

// add dependencies
dependencies {
    implementation("dev.arildo:iris-mock:0.0.1")
    ksp("dev.arildo:iris-mock-compiler:0.0.1")
}
```

#### Code
Just create a class implementing the `Interceptor` interface and annotate it with `@IrisMockInterceptor`. That's all. The interceptor will be automatically injected at `OkHttp`

```kotlin
@IrisMockInterceptor
class MyInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain) : Response {
        return chain.proceed(chain.request())
    }
}
```

## Roadmap
- Add support to [Ktor](https://github.com/ktorio/ktor)
- Add support to coroutines
- Add a DSL to ease modifications/mocks
- and many other cool things

## Contributing
Feel free to open PRs and submit feature suggestions via the repository issues. Everything's welcome 😎

## License
[MIT](https://choosealicense.com/licenses/mit/)

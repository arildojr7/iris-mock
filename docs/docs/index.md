#
<p align="center">
<img src="assets/logo.png" /> 
<br><br>
<img src="https://img.shields.io/github/actions/workflow/status/arildojr7/iris-mock/pull_request.yml?color=00b330" />
  <img src="https://shields.io/badge/mavenCentral-v1.0.0-blue" />
  <a href="https://pinterest.github.io/ktlint/"><img src="https://img.shields.io/badge/code%20style-%E2%9D%A4-FF4081.svg" alt="ktlint"></a>
  <img src="https://img.shields.io/endpoint?color=00b330&url=https%3A%2F%2Fhits.dwyl.com%2Farildojr7%2Firis-mock.json" />
  <img src="https://img.shields.io/github/license/arildojr7/iris-mock?color=0979ba" />
</p>
<br>
A kotlin-first tool to intercept android network calls, modify requests/responses and mock entire APIs. Also includes a cool DSL, that helps to reduce boilerplate code and simplify development.
<br><br>
btw, Iris is my daughter's name :smiling_face_with_3_hearts:

## Features
- Works with Retrofit, Volley and every libs that depend on OkHttp
- Allow intercept call on 3rd party libs
- [DSL](https://kotlinlang.org/docs/type-safe-builders.html){ target=_blank } to avoid boilerplate


## Why use iris mock?
- A centralized tool to log, intercept and modify requests/response
- As it works at bytecode level, can be used with 3rd party libs
- Compiler developed using [KSP](https://github.com/google/ksp){ target=_blank }, up to 2x faster than KAPT
- No need to do SSL things, like inject certificate


## Roadmap
- Add support to [Ktor](https://github.com/ktorio/ktor){ target=_blank }
- Expand DSL
- and many other cool things
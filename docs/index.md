#
<p align="center">
<img src="assets/logo.png" /> 
<br><br>
  <img src="https://github.com/arildojr7/iris-mock/actions/workflows/pull_request.yml/badge.svg" />
  <img src="https://shields.io/badge/mavenCentral-v1.2.0-blue" />
  <a href="https://pinterest.github.io/ktlint/"><img src="https://img.shields.io/badge/code%20style-%E2%9D%A4-FF4081.svg" alt="ktlint"></a>
  <img src="https://img.shields.io/github/license/arildojr7/iris-mock?color=0979ba" />
</p>
<br>
A kotlin tool to intercept android network calls, modify requests/responses and mock entire APIs. Also includes a cool DSL, that helps to reduce boilerplate code and simplify development.
<br><br>
btw, <b>Iris</b> is my daughter's name :smiling_face_with_3_hearts:

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

## Roadmap
- Add support to [Ktor](https://github.com/ktorio/ktor){ target=_blank }
- Expand DSL

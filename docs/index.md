#
<p align="center">
<img src="assets/logo.png" /> 
<br><br>
<img src="https://img.shields.io/github/actions/workflow/status/arildojr7/iris-mock/pull_request.yml?color=00b330" />
  <img src="https://shields.io/badge/mavenCentral-v1.1.0 alpha04-blue" />
  <a href="https://pinterest.github.io/ktlint/"><img src="https://img.shields.io/badge/code%20style-%E2%9D%A4-FF4081.svg" alt="ktlint"></a>
  <img src="https://img.shields.io/endpoint?color=00b330&url=https%3A%2F%2Fhits.dwyl.com%2Farildojr7%2Firis-mock.json" />
  <img src="https://img.shields.io/github/license/arildojr7/iris-mock?color=0979ba" />
</p>
<br>
A kotlin tool to intercept android network calls, modify requests/responses and mock entire APIs. Also includes a cool DSL, that helps to reduce boilerplate code and simplify development.
<br><br>
btw, <b>Iris</b> is my daughter's name :smiling_face_with_3_hearts:

## Features
- Works with Retrofit, Volley and every libs that depend on OkHttp
- Allow intercept call on 3rd party libs
- [DSL](https://kotlinlang.org/docs/type-safe-builders.html){ target=_blank } to avoid boilerplate

## Why use Iris Mock?
- A centralized tool to intercept, modify and log requests/response
- As it works at bytecode level, can be used with 3rd party libs
- No need to apply KAPT or KSP, since it's implemented directly through Kotlin Compiler Plugin
- No need to do SSL things, like inject certificate

## Roadmap
- Add support to [Ktor](https://github.com/ktorio/ktor){ target=_blank }
- Expand DSL
- and many other cool things
